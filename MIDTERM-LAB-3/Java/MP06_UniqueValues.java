import java.io.*;
import java.util.*;

/**
 * MP06 - Display Unique Values
 * Student: PARAON, JOSH HEIDRIC C.
 * Description: Reads a CSV dataset and displays the unique (distinct) values
 *              found in each column across all records.
 */
public class MP06_UniqueValues {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ask user for the CSV file path
        System.out.print("Enter the CSV dataset file path: ");
        String filePath = scanner.nextLine().trim();

        String[] headers = null;                                         // stores column header names
        List<LinkedHashSet<String>> uniquePerColumn = new ArrayList<>(); // unique values per column

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                String[] fields = parseCSVLine(line);

                if (isHeader) {
                    headers = fields; // save header row

                    // Initialize a LinkedHashSet for each column (preserves order, no duplicates)
                    for (int i = 0; i < headers.length; i++) {
                        uniquePerColumn.add(new LinkedHashSet<>());
                    }

                    isHeader = false;
                } else {
                    // For each field in the row, add value to the corresponding column's set
                    for (int i = 0; i < fields.length && i < uniquePerColumn.size(); i++) {
                        String value = fields[i].trim();
                        if (!value.isEmpty()) {
                            uniquePerColumn.get(i).add(value); // duplicates are ignored by Set
                        }
                    }
                }
            }

            if (headers == null) {
                System.out.println("Error: File is empty or has no headers.");
                scanner.close();
                return;
            }

            // Display unique values per column
            System.out.println("\n========================================");
            System.out.println("UNIQUE VALUES PER COLUMN");
            System.out.println("========================================");

            for (int i = 0; i < headers.length; i++) {
                Set<String> unique = uniquePerColumn.get(i);
                System.out.println("\nColumn [" + i + "]: " + headers[i].trim());
                System.out.println("  Unique Count: " + unique.size());
                System.out.println("  Values:");

                int count = 1;
                for (String val : unique) {
                    System.out.println("    " + count++ + ". " + val);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found - " + filePath);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        scanner.close();
    }

    /**
     * Parses a single CSV line, handling quoted fields with embedded commas.
     * @param line - the raw CSV line string
     * @return array of field values
     */
    static String[] parseCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        tokens.add(current.toString());
        return tokens.toArray(new String[0]);
    }
}