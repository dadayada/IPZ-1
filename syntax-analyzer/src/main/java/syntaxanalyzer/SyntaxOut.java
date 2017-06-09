package syntaxanalyzer;

import lexicalanalyzer.core.Tables;

import java.util.List;

public class SyntaxOut {

    private Tables tables;
    private Node tree;
    private List<SyntaxError> errors;

    public SyntaxOut(Tables tables, Node tree, List<SyntaxError> errors) {
        this.tables = tables;
        this.tree = tree;
        this.errors = errors;
    }

    public Tables getTables() {
        return tables;
    }

    public Node getTree() {
        return tree;
    }

    public List<SyntaxError> getErrors() {
        return errors;
    }
}
