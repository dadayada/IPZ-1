/**
 * Created by pc on 12.03.2017.
 */
public class LexicalError {

    private final String message;
    private  final int row;
    private  final int column;

    public LexicalError(String message, int row, int column) {
        this.message = message;
        this.row = row;
        this.column = column;
    }
}
