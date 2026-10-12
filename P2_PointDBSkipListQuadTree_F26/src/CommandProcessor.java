/**
 * The purpose of this class is to parse a text file into its appropriate, line
 * by line commands for the format specified in Project 2.
 * 
 * @author Mahro Kim, Vinh Chau
 * @version 2026-10-04
 */
public class CommandProcessor {

    private Database data;

    /**
     * Creates and initializes the command processor and database.
     */
    public CommandProcessor() {
        data = new Database();
    }

    /**
     * Processes one command from input.
     *
     * @param line
     *            a single line from the text file
     */
    public void processor(String line) {
        line = line.trim();
        if (line.isEmpty()) {
            return;
        }

        String[] arr = line.split("\\s+");
        String command = arr[0];

        if (command.equals("insert")) {
            String name = arr[1];
            int x = Integer.parseInt(arr[2]);
            int y = Integer.parseInt(arr[3]);

            data.insert(name, x, y);
        }
        else if (command.equals("remove")) {
            int numParam = arr.length - 1;

            // Remove by name: remove <name>
            if (numParam == 1) {
                data.remove(arr[1]);
            }
            // Remove by coordinates: remove <x> <y>
            else if (numParam == 2) {
                int x = Integer.parseInt(arr[1]);
                int y = Integer.parseInt(arr[2]);
                data.remove(x, y);
            }
        }
        else if (command.equals("regionsearch")) {
            int x = Integer.parseInt(arr[1]);
            int y = Integer.parseInt(arr[2]);
            int w = Integer.parseInt(arr[3]);
            int h = Integer.parseInt(arr[4]);

            data.regionsearch(x, y, w, h);
        }
        else if (command.equals("duplicates")) {
            data.duplicates();
        }
        else if (command.equals("search")) {
            data.search(arr[1]);
        }
        else if (command.equals("dump")) {
            data.dump();
        }
        else {
            System.out.println("Unrecognized command.");
        }
    }
}