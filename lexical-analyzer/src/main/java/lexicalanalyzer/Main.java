package lexicalanalyzer;

import lexicalanalyzer.DTO.LexicalAnalyzerOutput;
import lexicalanalyzer.core.LexicalAnalyzer;

import java.io.FileReader;
import java.io.Reader;

public class Main {

    public static void main(String[] args) {
        LexicalAnalyzer analyzer = new LexicalAnalyzer();
        String path = "program.txt"; //path to file with source code
        LexicalAnalyzerOutput out = null;
        try {
            Reader reader = new FileReader(path);
            out = analyzer.scan(reader);
            if(!out.getErrors().isEmpty()) {
                System.out.println(out.getErrors());
            } else {
                System.out.println(out.getTokens());
                System.out.println("Identifiers: \n");
                System.out.println(out.getTables().identidiersToString());
                System.out.println("Constatnts: \n");
                System.out.println(out.getTables().constantsTostring());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
