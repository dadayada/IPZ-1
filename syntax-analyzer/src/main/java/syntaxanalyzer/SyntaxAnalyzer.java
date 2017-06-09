package syntaxanalyzer;

import lexicalanalyzer.core.Tables;
import lexicalanalyzer.core.Token;

import java.util.*;

public class SyntaxAnalyzer {

    private Tables tables;

    private Queue<Token> tokens;

    private List<SyntaxError> errors;

    public SyntaxAnalyzer(Tables tables, List<Token> tokens) {
        this.tables = tables;
        this.tokens = new ArrayDeque<>(tokens);
        this.errors = new ArrayList<>();
    }

    public SyntaxOut analyze() {
        Node root = new Node(NodeType.SIGNAL_PROGRAM);
        Node programNode = new Node(NodeType.PROGRAM);
        root.addChild(programNode);
        SyntaxOut out;
        try {
            program(programNode);
        } catch (NoSuchElementException | NullPointerException e) {

        } finally {
            out = new SyntaxOut(tables, root, errors);
        }
        return out;
    }

    private void error(Token token, String message) {
        tokens = new ArrayDeque<>();
        errors.add(new SyntaxError(message, token.getRow(), token.getColumn()));
    }

    private void program(Node parent) {
        Token token = tokens.remove();
        if(token.getCode() != 401) {
            error(token, "'PROGRAM' expected");
        }
        parent.addChild(new Node(NodeType.LEXEM, token));
        parent.addChild(procedureIdentifier(new Node(NodeType.PROCEDURE_IDENTIFIER)));
        token = tokens.remove();
        if(token.getCode() != 59) {
            error(token, "';' expected");
        }
        parent.addChild(new Node(NodeType.LEXEM, token));
        parent.addChild(block(new Node(NodeType.BLOCK)));
        token = tokens.remove();
        if(token.getCode() != 46) {
            error(token, "'.' expected");
        }
        parent.addChild(new Node(NodeType.LEXEM, token));
    }

    private Node procedureIdentifier(Node parent) {
        parent.addChild(identifier(new Node(NodeType.IDENTIFIER)));
        return parent;
    }

    private Node block(Node parent) {
        parent.addChild(declarations(new Node(NodeType.DECLARATIONS)));
        Token token = tokens.remove();
        if(token.getCode() != 402) {
            error(token, "'BEGIN' expected");
        }
        parent.addChild(new Node(NodeType.LEXEM, token));
        parent.addChild(statementsList(new Node(NodeType.STATEMENTS_LIST)));
        token = tokens.remove();
        if(token.getCode() != 403) {
            error(token, "'END' expected");
        }
        parent.addChild(new Node(NodeType.LEXEM, token));
        return parent;
    }

    private Node statementsList(Node parent) {
        Token token = tokens.peek();
        int code = token.getCode();
        if(code == 405 || code == 407 || code == 59 || (code < 1001 && code >= 501)) {
            parent.addChild(statement(new Node(NodeType.STATEMENT)));
            parent.addChild(statementsList(new Node(NodeType.STATEMENTS_LIST)));
        } else {
            parent.addChild(new Node(NodeType.EMPTY));
        }
        return parent;
    }

    private Node statement(Node parent) {
        Token token = tokens.peek();
        if(token.getCode() == 59) {
            token = tokens.remove();
            parent.addChild(new Node(NodeType.LEXEM, token));
        } else if(token.getCode() == 405) {
            token = tokens.remove();
            parent.addChild(new Node(NodeType.LEXEM, token));
            parent.addChild(unsignedInteger(new Node(NodeType.UNSIGNED_INTEGER)));
            token = tokens.remove();
            if (token.getCode() == 59) {
                parent.addChild(new Node(NodeType.LEXEM, token));
            } else {
                error(token, "Not a statemant");
            }
        } else if(token.getCode() == 407) {
            parent.addChild(conditionStatemant(new Node(NodeType.CONDITION_STATEMENT)));
            token = tokens.remove();
            if(token.getCode() == 406) {
                parent.addChild(new Node(NodeType.LEXEM, token));
                token = tokens.remove();
                if(token.getCode() == 59) {
                    parent.addChild(new Node(NodeType.LEXEM, token));
                } else {
                    error(token, "';' expected");
                }
            } else {
                error(token, "'ENDIF' expected");
            }
        } else if(token.getCode() >= 501 && token.getCode() < 1001) {
            parent.addChild(unsignedInteger(new Node(NodeType.UNSIGNED_INTEGER)));
            token = tokens.remove();
            if(token.getCode() == 58) {
                parent.addChild(new Node(NodeType.LEXEM, token));
            } else {
                error(token, "':' expected");
            }
            parent.addChild(statement(new Node(NodeType.STATEMENT)));
        }
        return parent;
    }

    private Node conditionStatemant(Node parent) {
        parent.addChild(incompleteCondStat(new Node(NodeType.INCOMPLETE_CONDITION_STATEMENT)));
        parent.addChild(alternativePart(new Node(NodeType.ALTERNATIVE_PART)));
        return parent;
    }

    private Node incompleteCondStat(Node parent) throws NoSuchElementException {
        Token token = tokens.remove();
        if(token.getCode() == 407) {
            parent.addChild(new Node(NodeType.LEXEM, token));
            parent.addChild(condExpr(new Node(NodeType.CONDITIONAL_EXPRESSION)));
            token = tokens.remove();
            if(token.getCode() == 408) {
                parent.addChild(new Node(NodeType.LEXEM, token));
            } else {
                error(token, "'THEN' expected");
            }
            parent.addChild(statementsList(new Node(NodeType.STATEMENTS_LIST)));
        } else {
            error(token, "'IF' expected");
        }
        return parent;
    }

    private Node condExpr(Node parent) {
        Token token = tokens.peek();
        if(token.getCode() >= 1001) {
            parent.addChild(varIdentifier(new Node(NodeType.VARIABLE_IDENTIFIER)));
        } else {
            error(token, "variable expected");
        }
        token = tokens.remove();
        if(token.getCode() == 61) {
            parent.addChild(new Node(NodeType.LEXEM, token));
        } else {
            error(token, "'=' expected");
        }
        parent.addChild(unsignedInteger(new Node(NodeType.UNSIGNED_INTEGER)));
        return parent;
    }

    private Node varIdentifier(Node parent) throws NoSuchElementException {
        parent.addChild(identifier(new Node(NodeType.IDENTIFIER)));
        return parent;
    }

    private Node alternativePart(Node parent) {
        Token token = tokens.peek();
        if(token.getCode() == 409) {
            token = tokens.remove();
            parent.addChild(new Node(NodeType.LEXEM, token));
            parent.addChild(statementsList(new Node(NodeType.STATEMENTS_LIST)));
        } else {
            parent.addChild(new Node(NodeType.EMPTY));
        }
        return parent;
    }

    private Node declarations(Node parent) {
        parent.addChild(labelDeclarations(new Node(NodeType.LABEL_DECLARATIONS)));
        return parent;
    }

    private Node labelDeclarations(Node parent) {
        Token token = tokens.peek();
        if(token.getCode() == 404) {
            token = tokens.remove();
            parent.addChild(new Node(NodeType.LEXEM, token));
            Node label = unsignedInteger(new Node(NodeType.UNSIGNED_INTEGER));
            parent.addChild(label);
            parent.addChild(labelsList(new Node(NodeType.LABELS_LIST)));
            token = tokens.remove();
            if(token.getCode() == 59) {
                parent.addChild(new Node(NodeType.LEXEM, token));
            } else {
                error(token, "';' expected");
            }
        } else {
            parent.addChild(new Node(NodeType.EMPTY));;
        }
        return parent;
    }

    private Node labelsList(Node parent) {
        Token token = tokens.peek();
        if(token.getCode() == 44) {
            token = tokens.remove();
            parent.addChild(new Node(NodeType.LEXEM, token));
            Node label = unsignedInteger(new Node(NodeType.UNSIGNED_INTEGER));
            parent.addChild(label);
            parent.addChild(labelsList(new Node(NodeType.LABELS_LIST)));
        } else {
            parent.addChild(new Node(NodeType.EMPTY));
        }
        return parent;
    }

    private Node identifier(Node parent) {
        Token token = tokens.remove();
        if(token.getCode() >= 1001) {
            parent.addChild(new Node(NodeType.LEXEM, token));
        } else {
            error(token, "Identifier expected");
        }
        return parent;
    }

    private Node unsignedInteger(Node parent) {
        Token token = tokens.remove();
        if(token.getCode() >= 501 && token.getCode() < 1001) {
            parent.addChild(new Node(NodeType.LEXEM, token));
        } else {
            error(token, "Unsigned integer expected");
        }
        return parent;
    }
}
