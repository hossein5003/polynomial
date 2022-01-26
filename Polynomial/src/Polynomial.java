import java.util.Arrays;

public class Polynomial {
    int start;
    int finish;
    static int avail = 0;
    static Node[] elements = new Node[1000];

    public Polynomial(String polynomial) {
        init(polynomial);
    }

    private void init(String polynomial) {
        if (polynomial.equals("")) {
            this.start = avail;
            this.finish = avail;

            return;
        }

        String[] polynomialAndOperators = polynomial.split(" ");

        start = avail;
        finish = avail;

        for (int i = 0; i < polynomialAndOperators.length; i++) {
            if (i % 2 == 0) {
                String[] number = polynomialAndOperators[i].split("x\\^");
                String coefficient = number[0];

                if (i != 0)
                    if (polynomialAndOperators[i - 1].equals("-"))
                        coefficient = "-" + coefficient;

                add(new BigNumber(coefficient), new BigNumber(number[1]));
            }
        }
    }

    public void add(BigNumber coefficient, BigNumber exponent) {
        Node newElement = new Node(coefficient, exponent);
        int index = 0;

        if (start == avail) {
            avail++;
            elements[start] = newElement;

            return;
        }

        for (int i = this.start; i <= this.finish; i++) {
            if (i == this.start)
                if (exponent.isBigger(elements[i].exponent)) {
                    index = i;
                    break;
                }

            if (i == this.finish) {
                index = i + 1;
            } else if (exponent.isBigger(elements[i].exponent) && elements[i - 1].exponent.isBigger(exponent)) {
                index = i;
                break;
            }
        }

        avail++;
        finish++;

        Node temp;
        for (int i = index; i <= finish; i++) {
            temp = elements[i];
            elements[i] = newElement;
            newElement = temp;
        }
    }

    public Polynomial plus(Polynomial other) {
        Polynomial result = new Polynomial("");

        int i = this.start;
        int j = other.start;

        while (i <= this.finish && j <= other.finish) {
            if (Polynomial.elements[j].exponent.isBigger(Polynomial.elements[i].exponent)) {
                result.add(Polynomial.elements[j].coefficient, Polynomial.elements[j].exponent);
                j++;
            } else if (Polynomial.elements[i].exponent.isBigger(Polynomial.elements[j].exponent)) {
                result.add(Polynomial.elements[i].coefficient, Polynomial.elements[i].exponent);
                i++;
            } else {
                result.add(Polynomial.elements[j].coefficient
                        .plus(Polynomial.elements[i].coefficient), Polynomial.elements[j].exponent);
                i++;
                j++;
            }
        }

        return getPolynomial(other, result, i, j, "plus");
    }

    @Override
    public String toString() {
        return "start  : " + start + "\n" +
                "finish : " + finish + "\n" +
                "avail  : " + avail + "\n" +
                Arrays.toString(elements);
    }

    public void remove(BigNumber coefficient, BigNumber exponent) {
        int index = -1;

        for (int i = start; i <= finish; i++)
            if (elements[i].coefficient.toString().equals(coefficient.toString()))
                if (elements[i].exponent.toString().equals(exponent.toString()))
                    index = i;

        if (index == -1)
            throw new ArithmeticException("index not found!");

        for (int i = index; i <= finish; i++)
            elements[i] = elements[i + 1];

        avail--;
        finish--;
    }

    public Polynomial minus(Polynomial other) {
        Polynomial result = new Polynomial("");

        int i = this.start;
        int j = other.start;

        BigNumber afterMinusToPolynomial;

        while (i <= this.finish && j <= other.finish) {
            if (Polynomial.elements[j].exponent.isBigger(Polynomial.elements[i].exponent)) {
                afterMinusToPolynomial = new BigNumber("-" + Polynomial.elements[j].coefficient.toString());
                result.add(afterMinusToPolynomial, Polynomial.elements[j].exponent);
                j++;
            } else if (Polynomial.elements[i].exponent.isBigger(Polynomial.elements[j].exponent)) {
                result.add(Polynomial.elements[i].coefficient, Polynomial.elements[i].exponent);
                i++;
            } else {
                result.add(Polynomial.elements[i].coefficient
                        .minus(Polynomial.elements[j].coefficient), Polynomial.elements[j].exponent);
                i++;
                j++;
            }
        }

        return getPolynomial(other, result, i, j, "minus");
    }

    private Polynomial getPolynomial(Polynomial other, Polynomial result, int i, int j, String operator) {
        if (i <= this.finish)
            for (int m = i; m <= this.finish; m++)
                result.add(Polynomial.elements[m].coefficient, Polynomial.elements[m].exponent);

        BigNumber afterMinusToPolynomial;
        if (j <= other.finish) {
            if (operator.equals("minus"))
                for (int m = j; m <= other.finish; m++) {
                    afterMinusToPolynomial = new BigNumber("-" + Polynomial.elements[m].coefficient.toString());
                    result.add(afterMinusToPolynomial, Polynomial.elements[m].exponent);
                }
            else
                for (int m = j; m <= other.finish; m++)
                    result.add(Polynomial.elements[m].coefficient, Polynomial.elements[m].exponent);
        }
        return result;
    }

    public Polynomial times(Polynomial other) {
        Polynomial result = new Polynomial("");

        for (int i = start; i <= finish; i++)
            for (int j = other.start; j <= other.finish; j++) {
                BigNumber newExponent = elements[i].exponent.plus(elements[j].exponent);
                BigNumber newCoefficient = elements[i].coefficient.times(elements[j].coefficient);

                if (result.start == avail)
                    result.add(newCoefficient, newExponent);
                else {
                    boolean alreadyExist = false;
                    for (int m = result.start; m <= result.finish; m++)
                        if (elements[m].exponent.toString().equals(newExponent.toString())) {
                            elements[m].coefficient = elements[m].coefficient.plus(newCoefficient);
                            alreadyExist = true;
                            break;
                        }

                    if (!alreadyExist)
                        result.add(newCoefficient, newExponent);
                }
            }

        return result;
    }

    public boolean isZero() {
        for (int i = start; i <= finish; i++)
            if (!elements[i].coefficient.toString().equals("0"))
                return false;

        return true;
    }

    public BigNumber getCoefficient(BigNumber exponent) {
        for (int i = start; i <= finish; i++)
            if (elements[i].exponent.toString().equals(exponent.toString()))
                return elements[i].coefficient;

        return new BigNumber("0");
    }

    public BigNumber getMaximumExponent() {
        return elements[start].exponent;
    }
}

