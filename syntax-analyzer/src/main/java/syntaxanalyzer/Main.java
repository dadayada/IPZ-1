package syntaxanalyzer;

import lexicalanalyzer.DTO.LexicalAnalyzerOutput;
import lexicalanalyzer.core.LexicalAnalyzer;
import lexicalanalyzer.core.Token;

import java.io.BufferedReader;
import java.io.FileReader;

public class Main {

    public static void main(String[] args) {
        String path = "program.txt";
        LexicalAnalyzer lexan = new LexicalAnalyzer();
        LexicalAnalyzerOutput lexOutput = null;
        try {
            lexOutput = lexan.scan(new BufferedReader(new FileReader(path)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(!lexOutput.getErrors().isEmpty()) {
            System.out.println(lexOutput.getErrors());
        } else {
            SyntaxOut out = (new SyntaxAnalyzer(lexOutput.getTables(), lexOutput.getTokens())).analyze();
            if(!out.getErrors().isEmpty())
                System.out.println(out.getErrors());
            else
                System.out.println(out.getTree().strRepr("  "));
        }
    }
}
