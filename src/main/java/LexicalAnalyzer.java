import org.w3c.dom.Attr;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 10.03.2017.
 */
public class LexicalAnalyzer {

    private Tables tables;

    private Reader reader;

    private ArrayList<Token> tokens;

    private ArrayList<LexicalError> errors;

    public LexicalAnalyzer(Reader reader) {
        this.reader = reader;
        tables = new Tables();
        tokens = new ArrayList<>();
        errors = new ArrayList<>();
    }

    public List<Token> scan() throws IOException {
        int symbol;
        int row = 0;
        int column = 0;
        ArrayList<Token> tokens = new ArrayList<>();
        symbol = reader.read();
        do {
            int tokenCode = -1;
            boolean supressOutput = false;
            StringBuilder buffer = new StringBuilder();
            Attribute attribute = tables.getSymbolAttribute((char) symbol);
            int tokenLength = 0;
            switch (attribute) {
                case BLANK:
                    while(symbol != -1) {
                        if((char) symbol == '\n') {
                            row++;
                            column = 0;
                        } else
                            column++;
                        symbol = reader.read();
                        if(tables.getSymbolAttribute((char) symbol) != Attribute.BLANK) {
                            break;
                        }
                    }
                    supressOutput = true;
                    break;

                case CONSTANT:
                    while(symbol != -1 && tables.getSymbolAttribute((char) symbol) == Attribute.CONSTANT) {
                        buffer.append((char) symbol);
                        symbol = reader.read();
                    }
                    String constant = buffer.toString();
                    tokenLength += constant.length();
                    if((tokenCode = tables.constTabSearch(constant)) == -1)
                        tokenCode = tables.putConstant(constant);
                    break;

                case IDENTIFIER:
                    while (symbol != -1 && (tables.getSymbolAttribute((char) symbol) == Attribute.IDENTIFIER ||
                                            tables.getSymbolAttribute((char) symbol) == Attribute.CONSTANT)) {
                        buffer.append((char) symbol);
                        symbol = reader.read();
                    }
                    String identifier = buffer.toString();
                    tokenLength += identifier.length();
                    if((tokenCode = tables.reservedTabSearch(identifier)) == -1)
                        if((tokenCode = tables.identifierTabSearch(identifier)) == -1)
                            tokenCode = tables.putIdentifier(identifier);
                    break;

                case DELIMITER:
                    tokenLength++;
                    tokenCode = tables.getDelimiterCode((char) symbol);
                    symbol = reader.read();
                    break;

                case COMMENT:
                    symbol = reader.read();
                    if((char) symbol != '*') {
                        if((char) symbol == '\n') {
                            row++;
                            column = 0;
                        } else
                            column++;
                        errors.add(new LexicalError("Expected '*'", row, column));
                        break;
                    } else {
                        symbol = reader.read();
                        do {
                            while (symbol != -1 && (char) symbol != '*') {
                                if((char) symbol == '\n') {
                                    row++;
                                    column = 0;
                                } else
                                    column++;
                                symbol = reader.read();
                            }
                            if(symbol == -1)
                                errors.add(new LexicalError("Expected '*'", row, column));
                            else
                                symbol = reader.read();
                        } while ((char) symbol != ')');
                        if((char) symbol == ')') {
                            supressOutput = true;
                            column++;
                        }
                        if(symbol != -1)
                            symbol = reader.read();
                    }
                    break;
                case INVALID:
                    errors.add(new LexicalError("Illegal symbol", row, column));
                    symbol = reader.read();
            }
            if(!supressOutput) {
                tokens.add(new Token(tokenCode, row, column));
                column += tokenLength;
            }
        } while (symbol != -1);
        return tokens;
    }
}
