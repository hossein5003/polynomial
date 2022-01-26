import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private static String[] polynomialOperations;
    private static String[] bigNumberOperations;
    private static Polynomial[] resultOfPolynomialOperations;
    private static BigNumber[] results;
    private static int currentIndexOfResult = 0;
    private static BigNumber x;
    private static BigNumber target;

    public static void main(String[] args) {
        init();

        calculatePolynomialOperations();
        calculateBigNumberOperations();

        sortResults();

        int index = findTarget();
        System.out.println(index);
    }

    private static void init() {
        Scanner input = new Scanner(System.in);

        int n = Integer.parseInt(input.nextLine());
        polynomialOperations = new String[n];

        for (int i = 0; i < polynomialOperations.length; i++)
            polynomialOperations[i] = input.nextLine();

        int m = Integer.parseInt(input.nextLine());
        bigNumberOperations = new String[m];

        for (int i = 0; i < bigNumberOperations.length; i++)
            bigNumberOperations[i] = input.nextLine();

        x = new BigNumber(input.nextLine());
        target = new BigNumber(input.nextLine());

        resultOfPolynomialOperations = new Polynomial[n];
        results = new BigNumber[m + n];
    }

    private static void calculatePolynomialOperations() {
        int index;
        String operation;

        Polynomial firstPolynomial;
        Polynomial secondPolynomial;

        String line;

        for (int i = 0; i < polynomialOperations.length; i++) {
            line = polynomialOperations[i];

            index = line.indexOf(")");
            firstPolynomial = new Polynomial(line.substring(1, index));
            operation = String.valueOf(line.charAt(index + 2));
            secondPolynomial = new Polynomial(line.substring(index + 5, line.length() - 1));

            resultOfPolynomialOperations[i] = findAnswerOfEachPolynomialOperation(firstPolynomial, secondPolynomial, operation);
        }

        useXInResultOfPolynomials();
    }

    private static void useXInResultOfPolynomials() {
        for (Polynomial resultOfPolynomialOperation : resultOfPolynomialOperations)
            addResultOfPolynomialToResults(resultOfPolynomialOperation);
    }

    private static void addResultOfPolynomialToResults(Polynomial polynomial) {
        BigNumber result = new BigNumber("0");

        BigNumber resultOfExponential;
        BigNumber resultOfTime;

        for (int i = polynomial.start; i <= polynomial.finish; i++) {
            resultOfExponential = x.toThePowerOf(Polynomial.elements[i].exponent);
            resultOfTime = Polynomial.elements[i].coefficient.times(resultOfExponential);

            result = result.plus(resultOfTime);
        }

        results[currentIndexOfResult] = result;
        currentIndexOfResult++;
    }

    private static Polynomial findAnswerOfEachPolynomialOperation(Polynomial first, Polynomial second, String operation) {
        switch (operation) {
            case "+":
                return first.plus(second);
            case "-":
                return first.minus(second);
            default:
                return first.times(second);
        }
    }

    private static void calculateBigNumberOperations() {
        String[] array;
        BigNumber number;

        for (String bigNumberOperation : bigNumberOperations) {
            array = bigNumberOperation.split(" ");
            number = new BigNumber(array[0]);

            results[currentIndexOfResult] = findAnswerOfEachBignumberOperation(number, array[1]);
            currentIndexOfResult++;
        }
    }

    private static BigNumber findAnswerOfEachBignumberOperation(BigNumber number, String operation) {
        switch (operation) {
            case "++":
                return number.increase();
            case "--":
                return number.decrease();
            case "R":
                return number.shiftRight();
            default:
                return number.shiftLeft();
        }
    }

    private static void sortResults() {
        BigNumber temp;
        for (int i = 0; i < results.length; i++)
            for (int j = i + 1; j < results.length; j++)
                if (results[i].isBigger(results[j])) {
                    temp = results[i];
                    results[i] = results[j];
                    results[j] = temp;
                }
    }

    private static int findTarget() {
        int low = 0;
        int high = results.length - 1;
        int mid;

        while (low <= high) {
            mid = (low + high) / 2;

            if (results[mid].isBigger(target))
                high = mid - 1;
            else if (target.isBigger(results[mid]))
                low = mid + 1;
            else
                return mid;
        }

        return -1;
    }
}