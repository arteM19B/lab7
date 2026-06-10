package network;

public class IntegerArgument implements CommandArgument {
    private final int value;

    public IntegerArgument(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
