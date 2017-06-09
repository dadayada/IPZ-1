package syntaxanalyzer;

import lexicalanalyzer.core.Token;

import java.util.ArrayList;

public class Node {

    public NodeType type;

    public Token value;

    public ArrayList<Node> children;

    public Node(NodeType type) {
        this.type = type;
        this.children = new ArrayList<>();
    }

    public Node(NodeType type, Token value) {
        this.type = type;
        this.value = value;
        this.children = new ArrayList<>();
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public String strRepr(String padding) {
        String str = "";
        String childrenStr = "";
        str += type;
        if(value != null) {
            str += value;
        }
        str += "\n";
        for(Node child: children) {
            childrenStr += padding + child.strRepr(padding + "  ");
        }
        str += childrenStr;
        return str;
    }
}
