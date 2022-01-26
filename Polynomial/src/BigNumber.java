public class BigNumber {
    private final String number;
    private boolean isNegative = false;

    public BigNumber(String number) {
        if (number.startsWith("-")) {
            this.isNegative = true;
            this.number = number.substring(1);
        } else
            this.number = number;
    }

    public BigNumber toThePowerOf(BigNumber other) {
        BigNumber result = new BigNumber(this.toString());
        BigNumber level = new BigNumber(other.toString());

        if (level.number.equals("0"))
            return new BigNumber("1");

        while (!level.number.equals("1")) {
            result = result.times(this);
            level = level.decrease();
        }

        return result;
    }

    public BigNumber shiftRight() {
        if (this.number.length() == 1)
            return new BigNumber("0");

        String newBigNumber = this.toString().substring(0, this.toString().length() - 1);
        return new BigNumber(newBigNumber);
    }

    public BigNumber shiftRight(int amount) {
        if (amount < 0)
            return this.shiftLeft(amount * -1);

        String number = this.number;

        if (amount >= number.length())
            return new BigNumber("0");

        int currentLength = this.toString().length() - amount;
        number = this.toString().substring(0, currentLength);

        return new BigNumber(number);
    }

    public BigNumber shiftLeft() {
        return shiftLeft(1);
    }

    public BigNumber shiftLeft(int amount) {
        if (amount < 0)
            return this.shiftRight(amount * -1);

        String rest = "";
        for (int i = 0; i < amount; i++)
            rest += "0";

        return new BigNumber(this + rest);
    }

    public BigNumber increase() {
        return this.plus(new BigNumber("1"));
    }

    public BigNumber decrease() {
        return this.minus(new BigNumber("1"));
    }

    public BigNumber times(BigNumber other) {
        String result = this.timesPositive(other);

        if (!((other.isNegative && this.isNegative) || (!other.isNegative && !this.isNegative)))
            result = "-" + result;

        return new BigNumber(result);
    }

    public BigNumber plus(BigNumber other) {
        String result;

        if ((!this.isNegative && !other.isNegative) || ((this.isNegative && other.isNegative))) {
            result = plusTwoPositive(other);

            if (this.isNegative)
                result = "-" + result;
        } else {
            if (isCurrentBigger(other)) {
                result = minusFirstBigger(this.convertToByteArray(), other.convertToByteArray());

                if (this.isNegative)
                    result = "-" + result;
            } else {
                result = minusFirstBigger(other.convertToByteArray(), this.convertToByteArray());

                if (!this.isNegative)
                    result = "-" + result;
            }
        }

        return new BigNumber(result);
    }

    public BigNumber minus(BigNumber other) {
        String result;

        if ((this.isNegative && other.isNegative) || (!this.isNegative && !other.isNegative)) {
            if (this.isCurrentBigger(other)) {
                result = this.minusFirstBigger(this.convertToByteArray(), other.convertToByteArray());

                if (this.isNegative)
                    result = "-" + result;
            } else {
                result = this.minusFirstBigger(other.convertToByteArray(), this.convertToByteArray());

                if (!this.isNegative)
                    result = "-" + result;
            }
        } else {
            result = plusTwoPositive(other);

            if (this.isNegative)
                result = "-" + result;
        }

        return new BigNumber(result);
    }

    public boolean isBigger(BigNumber number) {
        if (this.toString().equals(number.toString()))
            return false;

        if (this.isNegative && number.isNegative)
            return !this.isCurrentBigger(number);

        else if (!this.isNegative && !number.isNegative)
            return this.isCurrentBigger(number);

        else return !this.isNegative;
    }

    @Override
    public String toString() {
        if (this.isNegative)
            return "-" + this.number;

        return this.number;
    }

    //private needed methods

    private String timesPositive(BigNumber other) {
        byte[] currentArray = this.convertToByteArray();
        byte[] otherArray = other.convertToByteArray();

        int carry;
        BigNumber result = new BigNumber("0");
        StringBuilder resultOfEachRow;

        for (int i = otherArray.length - 1; i >= 0; i--) {
            resultOfEachRow = new StringBuilder();

            for (int m = 0; m < Math.max(0, otherArray.length - i - 1); m++)
                resultOfEachRow.append("0");

            carry = 0;

            for (int r = currentArray.length - 1; r >= 0; r--) {
                byte singleTime = (byte) (otherArray[i] * currentArray[r] + carry);
                carry = singleTime / 10;
                resultOfEachRow.insert(0, "" + singleTime % 10);
            }

            if (carry > 0)
                resultOfEachRow.insert(0, "" + carry);

            result = result.plus(new BigNumber(resultOfEachRow.toString()));
        }

        return result.number;
    }

    private String minusFirstBigger(byte[] bigger, byte[] smaller) {
        smaller = this.addZeroToArray(smaller, bigger.length);

        byte[] result = new byte[bigger.length];

        for (int i = result.length - 1; i >= 0; i--) {
            if (bigger[i] < smaller[i]) {
                for (int r = i - 1; r >= 0; r--) {
                    if (bigger[r] > 0) {
                        for (int m = r; m < i; m++) {
                            bigger[m] -= 1;
                            bigger[m + 1] += 10;
                        }

                        break;
                    }
                }
            }

            result[i] = (byte) (bigger[i] - smaller[i]);
        }

        int indexOfUselessZeros = 0;
        for (int i = 0; i < result.length - 1; i++)
            if (result[i] != 0) {
                indexOfUselessZeros = i;
                break;
            }

        return arrayToString(result).substring(indexOfUselessZeros);
    }

    private String plusTwoPositive(BigNumber other) {
        byte[] currentNumberArray = this.convertToByteArray();
        byte[] otherNumberArray = other.convertToByteArray();

        int maxLength = Math.max(currentNumberArray.length, otherNumberArray.length);
        byte[] result = new byte[maxLength + 1];

        currentNumberArray = this.addZeroToArray(currentNumberArray, maxLength);
        otherNumberArray = this.addZeroToArray(otherNumberArray, maxLength);

        int carry = 0;
        int m;
        for (int i = maxLength - 1; i >= 0; i--) {
            m = currentNumberArray[i] + otherNumberArray[i] + carry;
            result[i + 1] = (byte) (m % 10);
            carry = m / 10;
        }

        if (carry > 0)
            result[0] = (byte) carry;

        return arrayToString(result);
    }

    private String arrayToString(byte[] result) {
        StringBuilder number = new StringBuilder();

        for (byte b : result)
            number.append(b);

        if (result[0] == 0)
            return number.substring(1);

        return number.toString();
    }

    private byte[] convertToByteArray() {
        byte[] array = new byte[this.number.length()];

        for (int i = 0; i < array.length; i++)
            array[i] = (byte) Character.getNumericValue(this.number.charAt(i));

        return array;
    }

    private byte[] addZeroToArray(byte[] array, int neededLength) {
        int remainLength = neededLength - array.length;

        if (remainLength == 0)
            return array;

        byte[] result = new byte[neededLength];
        System.arraycopy(array, 0, result, remainLength, array.length);

        return result;
    }

    private boolean isCurrentBigger(BigNumber other) {
        if (this.number.length() == other.number.length()) {
            byte[] currentArray = this.convertToByteArray();
            byte[] otherArray = other.convertToByteArray();

            for (int i = 0; i < currentArray.length; i++) {
                if (currentArray[i] > otherArray[i])
                    return true;

                else if (currentArray[i] < otherArray[i])
                    return false;
            }

            return false;
        } else
            return this.number.length() > other.number.length();
    }
}
