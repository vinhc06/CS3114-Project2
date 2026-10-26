
/**
 * Reads and processes commands for the PointDB project.
 * Uses Database to perform SkipList and QuadTree operations.
 *
 * @author Mahro Kim, Vinh Chau
 * @version 2026-10-25
 */
public class CommandProcessor {

    private Database data;

    /**
     * Creates a command processor with an empty database.
     */
    public CommandProcessor() {
        data = new Database();
    }


    /**
     * Processes a single command from the input file.
     *
     * @param line
     *            command to process
     */
    public void processor(String line) {
        if (line == null || line.trim().isEmpty()) {
            return;
        }

        String[] arr = line.trim().split("\\s+");
        String command = arr[0];

        try {
            if (command.equals("insert")) {
                if (arr.length != 4) {
                    return;
                }

                String name = arr[1];
                int x = Integer.parseInt(arr[2]);
                int y = Integer.parseInt(arr[3]);

                data.insert(name, x, y);
            }
            else if (command.equals("remove")) {
                if (arr.length == 2) {
                    data.remove(arr[1]);
                }
                else if (arr.length == 3) {
                    int x = Integer.parseInt(arr[1]);
                    int y = Integer.parseInt(arr[2]);
                    data.remove(x, y);
                }
                else {
                    return;
                }
            }
            else if (command.equals("regionsearch")) {
                if (arr.length != 5) {
                    return;
                }

                int x = Integer.parseInt(arr[1]);
                int y = Integer.parseInt(arr[2]);
                int w = Integer.parseInt(arr[3]);
                int h = Integer.parseInt(arr[4]);

                data.regionsearch(x, y, w, h);
            }
            else if (command.equals("duplicates")) {
                if (arr.length != 1) {
                    return;
                }

                data.duplicates();
            }
            else if (command.equals("search")) {
                if (arr.length != 2) {
                    return;
                }

                data.search(arr[1]);
            }
            else if (command.equals("dump")) {
                if (arr.length != 1) {
                    return;
                }

                data.dump();
            }
            else {
                System.out.println("Unrecognized command.");
            }
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid command arguments.");
        }
    }
}
