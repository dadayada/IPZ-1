package core;

public class Token {

    private final int code;

    private final int row;

    private final int column;

    public Token(int code, int row, int column) {
        this.code = code;
        this.row = row;
        this.column = column;
    }

    public int getCode() {
        return code;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "(row: " + this.row + ", column: " + this.column + ", code " + this.code + ")";
    }
}
