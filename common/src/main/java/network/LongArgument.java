package network;

public class LongArgument implements CommandArgument {
    private final long value;

    public LongArgument(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
