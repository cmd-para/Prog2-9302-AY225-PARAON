import java.io.*;
import java.util.*;

/**
 * MP05 - Extract Values from a Column
 * Student: PARAON, JOSH HEIDRIC C.
 * Description: Prompts the user for a CSV file path and a column index,
 *              then extracts and displays all values from that column.
 */
public class MP05_ExtractColumn {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ask user for the CSV file path
        System.out.print("Enter the CSV dataset file path: ");
        String filePath = scanner.nextLine().trim();

        // Ask user which column index to extract (0-based)
        System.out.print("Enter the column index to extract (0-based): ");
        int columnIndex = Integer.parseInt(scanner.nextLine().trim());

        List<String> extractedValues = new ArrayList<>(); // stores extracted column values
        String headerName = "";                           // stores the column header name

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;
            boolean isHeader = true; // flag to identify the header row

            while ((line = reader.readLine()) != null) {
                // Split row by comma, accounting for quoted fields
                String[] fields = parseCSVLine(line);

                if (fields.length <= columnIndex) {
                    // Skip rows that don't have enough columns
                    continue;
                }

                if (isHeader) {
                    // Save the column header name
                    headerName = fields[columnIndex].trim();
                    isHeader = false;
                } else {
                    // Extract the value from the specified column
                    String value = fields[columnIndex].trim();
                    extractedValues.add(value);
                }
            }

            // Display the extracted values
            System.out.println("\n========================================");
            System.out.println("Column Extracted: " + headerName + " (Index " + columnIndex + ")");
            System.out.println("Total Values Extracted: " + extractedValues.size());
            System.out.println("========================================");

            for (int i = 0; i < extractedValues.size(); i++) {
                // Print row number and value
                System.out.printf("[%d] %s%n", i + 1, extractedValues.get(i));
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found - " + filePath);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid column index entered.");
        }

        scanner.close();
    }

    /**
     * Parses a single CSV line, handling quoted fields that may contain commas.
     * @param line - the raw CSV line string
     * @return an array of field values
     */
    static String[] parseCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false; // track whether we're inside a quoted field

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes; // toggle quote state
            } else if (c == ',' && !inQuotes) {
                tokens.add(current.toString()); // end of a field
                current.setLength(0);           // reset buffer
            } else {
                current.append(c); // append character to current field
            }
        }

        tokens.add(current.toString()); // add the last field
        return tokens.toArray(new String[0]);
    }
}