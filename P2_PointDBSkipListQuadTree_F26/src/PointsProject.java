import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Main for CS3114 Quadtree/SkipList Point project (CS3114 Spring 2026 Project
 * 2). Usage: java PointsProject <command-file>
 *
 * @author CS Staff
 * @version September 2026
 */
public class PointsProject {
    /**
     * Main: Process input parameters and invoke command file processor
     *
     * @param args
     *            The command line parameters
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: PointsProject <command-file>");
            return;
        }

        String commandFile = args[0].trim();
        File theFile = new File(commandFile);
        if (!theFile.exists()) {
            System.out.println("There is no such input file as |" + commandFile
                + "|");
            return;
        }
        CommandProcessor processor = new CommandProcessor();
        Scanner scanner = new Scanner(theFile);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                processor.processor(line);
            }
        }

        scanner.close();
    }
}
