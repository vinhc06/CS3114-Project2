
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Main driver for the CS3114 PointDB project.
 * Reads commands from an input file and processes
 * them using the CommandProcessor.
 *
 * @author CS Staff, Vinh Chau, Mahro Kim
 * @version 2026-10-25
 */
public class PointsProject {

    /**
     * Reads a command file and executes its commands.
     *
     * @param args
     *            command-line arguments containing the input filename
     * @throws IOException
     *             if an error occurs while reading the file
     */
    public static void main(String[] args) throws IOException {
        if (args == null || args.length != 1) {
            System.out.println("Usage: PointsProject <command-file>");
            return;
        }

        String commandFile = args[0].trim();
        File theFile = new File(commandFile);

        if (!theFile.exists() || !theFile.isFile()) {
            System.out.println("There is no such input file as |"
                + commandFile + "|");
            return;
        }

        CommandProcessor processor = new CommandProcessor();

        try (Scanner scanner = new Scanner(theFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (!line.isEmpty()) {
                    processor.processor(line);
                }
            }
        }
    }
}
