public class Node {
    BigNumber coefficient;
    BigNumber exponent;

    public Node() {
        this.coefficient = new BigNumber("0");
        this.exponent = new BigNumber("0");
    }

    public Node(BigNumber coefficient, BigNumber expon) {
        this.exponent = expon;
        this.coefficient = coefficient;
    }

    @Override
    public String toString() {
        return "Node{" +
                "coefficient=" + coefficient +
                ", exponent=" + exponent +
                '}';
    }
}
