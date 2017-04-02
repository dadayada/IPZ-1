package core;


import DTO.LexicalAnalyzerOutput;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyzer {

    private Tables tables;

    private ArrayList<Token> tokens;

    private ArrayList<LexicalError> errors;

    public LexicalAnalyzer() {}

    public LexicalAnalyzerOutput scan(Reader reader) throws IOException {
        tables = new Tables();
        tokens = new ArrayList<>();
        errors = new ArrayList<>();
        int symbol;
        int row = 0;
        int column = 0;
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
                        supressOutput = true;
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
                            if(symbol == -1) {
                                errors.add(new LexicalError("Expected '*'", row, column));
                                supressOutput = true;
                            }
                            else
                                symbol = reader.read();
                        } while ((char) symbol != ')' && symbol != -1);
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
                    supressOutput = true;
                    symbol = reader.read();
            }
            if(!supressOutput) {
                tokens.add(new Token(tokenCode, row, column));
                column += tokenLength;
            }
        } while (symbol != -1);
        return new LexicalAnalyzerOutput(tables, tokens, errors);
    }
}
