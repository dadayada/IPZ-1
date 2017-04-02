import DTO.LexicalAnalyzerOutput;
import core.LexicalAnalyzer;
import core.LexicalError;
import core.Token;

import java.io.*;

/**
 * Created by pc on 12.03.2017.
 */
public class ConsoleController {

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("usage: SIGNAL-la <path>");
            return;
        }
        String path = args[0];
        try {
            LexicalAnalyzer analyzer = new LexicalAnalyzer();
            LexicalAnalyzerOutput output = analyzer.scan(new BufferedReader(new FileReader(path)));
            BufferedWriter writer = new BufferedWriter(new FileWriter("la-output.txt"));
            writer.write("Tokens:\n");
            for (Token token: output.getTokens()) {
                writer.write(token.toString() + '\n');
            }
            writer.write("Errors:\n");
            for (LexicalError error: output.getErrors()) {
                writer.write(error.toString());
            }
            writer.write("Identifiers table:\n");
            writer.write(output.getTables().identidiersToString());
            writer.write("Constants table:");
            writer.write(output.getTables().constantsTostring());
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
