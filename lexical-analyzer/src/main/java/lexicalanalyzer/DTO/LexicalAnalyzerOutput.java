package lexicalanalyzer.DTO;

import lexicalanalyzer.core.LexicalError;
import lexicalanalyzer.core.Tables;
import lexicalanalyzer.core.Token;

import java.util.List;

/**
 * Created by pc on 12.03.2017.
 */
public class LexicalAnalyzerOutput {

    private final Tables tables;

    private final List<Token> tokens;

    private final List<LexicalError> errors;

    public LexicalAnalyzerOutput(Tables tables, List<Token> tokens, List<LexicalError> errors) {
        this.tables = tables;
        this.tokens = tokens;
        this.errors = errors;
    }

    public Tables getTables() {
        return tables;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public List<LexicalError> getErrors() {
        return errors;
    }
}
