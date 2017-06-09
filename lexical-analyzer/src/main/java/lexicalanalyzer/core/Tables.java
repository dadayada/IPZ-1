package lexicalanalyzer.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Tables {

    private static final Map<String, Integer> reservedWords;

    private static final Map<Character, Integer> delimiters;

    private Map<String, Integer> identifiers;

    private Map<String, Integer> constants;

    private int curIdentifierCode;

    private int curConstantCode;

    static {
        reservedWords = new HashMap<>();
        reservedWords.put("PROGRAM", 401);
        reservedWords.put("BEGIN", 402);
        reservedWords.put("END", 403);
        reservedWords.put("LABEL", 404);
        reservedWords.put("GOTO", 405);
        reservedWords.put("ENDIF", 406);
        reservedWords.put("IF", 407);
        reservedWords.put("THEN", 408);
        reservedWords.put("ELSE", 409);
        delimiters = new HashMap<>();
        delimiters.put('.', 46);
        delimiters.put(',', 44);
        delimiters.put(':', 58);
        delimiters.put(';', 59);
        delimiters.put('=', 61);
    }

    public Tables() {
        curConstantCode = 501;
        curIdentifierCode = 1001;
        identifiers = new HashMap<>();
        constants = new HashMap<>();
    }

    public Attribute getSymbolAttribute(char symb) {
        if(Character.isWhitespace(symb))
            return Attribute.BLANK;
        if((symb >= 'A' && symb <= 'Z') || (symb >= 'a' && symb <= 'z'))
            return Attribute.IDENTIFIER;
        if(Character.isDigit(symb))
            return Attribute.CONSTANT;
        if(delimiters.containsKey(symb))
            return Attribute.DELIMITER;
        if(symb == '(')
            return Attribute.COMMENT;
        return Attribute.INVALID;
    }

    public int reservedTabSearch(String identifier) {
        Integer code = reservedWords.get(identifier);
        if(code != null) {
            return code;
        }
        return -1;
    }

    public int constTabSearch(String constant) {
        Integer code = constants.get(constant);
        if(code != null) {
            return code;
        }
        return -1;
    }

    public int identifierTabSearch(String identifier) {
        Integer code = identifiers.get(identifier);
        if(code != null) {
            return code;
        }
        return -1;
    }

    public int getDelimiterCode(char delimiter) {
        return delimiters.get(delimiter);
    }

    public int putIdentifier(String identifier) {
        identifiers.put(identifier, curIdentifierCode);
        return curIdentifierCode++;
    }

    public int putConstant(String constant) {
        constants.put(constant, curConstantCode);
        return curConstantCode++;
    }

    public String identidiersToString() {
        String identifiers = "";
        Set<Map.Entry<String, Integer>> entries = this.identifiers.entrySet();
        for(Map.Entry<String, Integer> entry: entries) {
            identifiers += "value: " + entry.getKey() + ", code: " + entry.getValue() + '\n';
        }
        return identifiers;
    }

    public String constantsTostring() {
        String constants = "";
        Set<Map.Entry<String, Integer>> entries = this.constants.entrySet();
        for(Map.Entry<String, Integer> entry: entries) {
            constants += "value: " + entry.getKey() + ", code: " + entry.getValue() + '\n';
        }
        return constants;
    }

    public String getTokenValue(int code) {
        if(code >= 46 && code <= 61) {
            for(Map.Entry<Character, Integer> entry : delimiters.entrySet()) {
                if(entry.getValue() == code)
                    return Character.toString(entry.getKey());
            }
        } else if(code >= 401 && code <= 409) {
            for(Map.Entry<String, Integer> entry : reservedWords.entrySet()) {
                if (entry.getValue() == code)
                    return entry.getKey();
            }
        } else if(code >= 501 && code <= 1000) {
            for(Map.Entry<String, Integer> entry : constants.entrySet()) {
                if (entry.getValue() == code)
                    return entry.getKey();
            }
        } else if(code >= 1001) {
            for(Map.Entry<String, Integer> entry : identifiers.entrySet()) {
                if (entry.getValue() == code)
                    return entry.getKey();
            }
        }
        return "";
    }
}
