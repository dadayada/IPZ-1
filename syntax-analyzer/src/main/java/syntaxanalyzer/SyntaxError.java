package syntaxanalyzer;

public class SyntaxError {

    private final String message;
    private  final int row;
    private  final int column;

    public SyntaxError(String message, int row, int column) {
        this.message = message;
        this.row = row;
        this.column = column;
    }

    @Override
    public String toString() {
        return "(row: " + this.row + ", column: " + this.column + ", message: " + this.message +  ")\n";
    }

}
