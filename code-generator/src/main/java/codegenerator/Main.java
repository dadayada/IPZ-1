package codegenerator;

import lexicalanalyzer.DTO.LexicalAnalyzerOutput;
import lexicalanalyzer.core.LexicalAnalyzer;
import lexicalanalyzer.core.Token;
import syntaxanalyzer.SyntaxAnalyzer;
import syntaxanalyzer.SyntaxOut;

import java.io.BufferedReader;
import java.io.FileReader;

public class Main {

    public  static void main(String[] args) {
        String path = "program.txt";
        LexicalAnalyzer lexan = new LexicalAnalyzer();
        LexicalAnalyzerOutput lexOutput = null;
        try {
            lexOutput = lexan.scan(new BufferedReader(new FileReader(path)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Token token: lexOutput.getTokens()) {
            System.out.println(token);
        }
        SyntaxOut out = (new SyntaxAnalyzer(lexOutput.getTables(), lexOutput.getTokens())).analyze();
        System.out.println(out.getTree().strRepr("   "));
        if(out.getErrors().isEmpty()) {
            Generator gen =null ;
            try {
                gen = new Generator(out.getTree(), out.getTables());
                System.out.println(gen.generate());
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(out.getErrors());
        }
    }
}
