package codegenerator;

import lexicalanalyzer.core.Tables;
import lexicalanalyzer.core.Token;
import org.omg.IOP.TAG_ORB_TYPE;
import syntaxanalyzer.Node;
import syntaxanalyzer.NodeType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Generator {

    private Node tree;
    private Tables tables;
    private Set<Token> declaredLabels;
    private Set<Token> labelsIncode;
    private Set<Token> labelsToJump;
    private int internalLabelCOunter;

    public Generator(Node tree, Tables tables) {
        this.tree = tree;
        this.tables = tables;
        this.declaredLabels = new HashSet<>();
        this.labelsIncode = new HashSet<>();
        this.labelsToJump = new HashSet<>();
        this.internalLabelCOunter = 0;
    }

    private void error(String message) {
        throw new RuntimeException(message);
    }

    public String generate() {
        return generate(tree);
    }

    private String generate(Node node) {
        NodeType type = node.type;
        List<Node> children = node.children;
        if(type == NodeType.SIGNAL_PROGRAM) {
            String code =  generate(children.get(0));
            for (Token token: labelsToJump) {
                boolean flag1 = false  ;
                for (Token token1: labelsIncode) {
                    if(token.getCode() == token1.getCode()) {
                        flag1 = true;
                    }
                }
                if(flag1 == false) {
                    error("Label doesn't exist in code");
                }
            }
            return code;
        } else if(type == NodeType.PROGRAM) {
            return generate(children.get(3));
        } else if(type == NodeType.BLOCK) {
            generate(children.get(0));
            return generate(children.get(2));
        } else if(type == NodeType.DECLARATIONS) {
            return generate(children.get(0));
        } else if(type == NodeType.LABEL_DECLARATIONS) {
            if(children.get(0).type == NodeType.EMPTY) {
                return "";
            }
            declaredLabels.add(children.get(1).children.get(0).value);
            return generate(children.get(2));
        } else if(type == NodeType.LABELS_LIST) {
            if(children.get(0).type == NodeType.EMPTY) {
                return "";
            }
            declaredLabels.add(children.get(1).children.get(0).value);
            return generate(children.get(2));
        } else if(type == NodeType.STATEMENTS_LIST) {
            String code = "";
            if(children.get(0).type == NodeType.EMPTY) {
                return code;
            }
            code += generate(children.get(0));
            code += generate(children.get(1));
            return code;
        } else if(type == NodeType.STATEMENT) {
            String code = "";
            if(children.get(0).type == NodeType.LEXEM && children.get(0).value.getCode() == 59) {
                return code;
            }
            if(children.get(0).type == NodeType.LEXEM && children.get(0).value.getCode() == 405) {
                code = "JMP ";
                Token label =  children.get(1).children.get(0).value;
                boolean flag = false;
                for(Token token: declaredLabels) {
                    if(token.getCode() == label.getCode()) {
                        flag = true;
                    }
                }
                if(!flag) {
                    error("error: undeclared label");
                }
                code += tables.getTokenValue(label.getCode()) + "\n";
                labelsToJump.add(label);
                return code;
            }
            if(children.get(0).type == NodeType.UNSIGNED_INTEGER) {
                Token label =  children.get(0).children.get(0).value;
                boolean flag = false;
                for(Token token: declaredLabels) {
                    if(token.getCode() == label.getCode()) {
                        flag = true;
                    }
                }
                if(!flag) {
                    error("error: undeclared label");
                }
                code += tables.getTokenValue(label.getCode()) + ": ";
                code += generate(children.get(2));
                labelsIncode.add(label);
                return code;
            }
            if(children.get(0).type == NodeType.CONDITION_STATEMENT) {
                children = children.get(0).children;
                Node incompleteCondStat = children.get(0);
                Node altepPart = children.get(1);
                Node condExpr = incompleteCondStat.children.get(1);
                Node variableIdentifier = condExpr.children.get(0);
                Node identifier = variableIdentifier.children.get(0);
                Node number = condExpr.children.get(2);
                code += "MOV AX, " + tables.getTokenValue(identifier.children.get(0).value.getCode()) + "\n";
                code += "MOV BX, " + tables.getTokenValue(number.children.get(0).value.getCode()) + "\n";
                code += "CMP AX, BX\n";
                String ifFalseLabel = "?L" + this.internalLabelCOunter++;
                String endLabel = "?L" + this.internalLabelCOunter++;
                code += "JNE " + ifFalseLabel + "\n";
                code += generate(incompleteCondStat.children.get(3));
                code += "JMP " + endLabel + "\n";
                code += ifFalseLabel + ": NOP\n";
                if(altepPart.children.get(0).type != NodeType.EMPTY) {
                    code += generate(altepPart.children.get(1));
                }
                code += endLabel + ": NOP\n";
                return code;
            }
        }
        return "";
    }
}
