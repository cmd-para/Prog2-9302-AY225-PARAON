import java.io.*;
import java.util.*;

/**
 * MP07 - Sort Entire Dataset Alphabetically
 * Student: PARAON, JOSH HEIDRIC C.
 * Description: Prompts for a CSV file and sorts all records alphabetically 
 * by comparing every column in the dataset. Includes a retry loop for file input.
 */
public class MP07_SortRecords {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String[]> records = new ArrayList<>();
        String[] headers = null;
        boolean fileLoaded = false;

        // Loop until a valid file is provided
        while (!fileLoaded) {
            System.out.print("Enter the CSV dataset file path: ");
            String filePath = scanner.nextLine().trim();

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                boolean isHeader = true;

                // Clear previous attempts if any
                records.clear();

                while ((line = reader.readLine()) != null) {
                    String[] fields = parseCSVLine(line);
                    if (isHeader) {
                        headers = fields;
                        isHeader = false;
                    } else {
                        records.add(fields);
                    }
                }

                if (headers == null) {
                    System.out.println("Error: The file is empty. Please try a different file.");
                } else {
                    fileLoaded = true; // Success! Break the loop
                }

            } catch (FileNotFoundException e) {
                System.out.println("Error: File not found at '" + filePath + "'. Please check the path and try again.");
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }
        }

        // Sort records alphabetically by comparing the entire row content
        records.sort((row1, row2) -> {
            int minLength = Math.min(row1.length, row2.length);
            for (int i = 0; i < minLength; i++) {
                int comparison = row1[i].trim().compareToIgnoreCase(row2[i].trim());
                if (comparison != 0) return comparison;
            }
            return Integer.compare(row1.length, row2.length);
        });

        // Display the sorted records
        System.out.println("\n========================================");
        System.out.println("Records Sorted: Alphabetically by Entire Dataset");
        System.out.println("Total Records: " + records.size());
        System.out.println("========================================");

        // Print header row
        System.out.println(String.join(" | ", headers));
        System.out.println("-".repeat(60));

        // Print each alphabetically sorted record
        for (int i = 0; i < records.size(); i++) {
            String[] row = records.get(i);
            System.out.printf("[%d] %s%n", i + 1, String.join(" | ", row));
        }

        scanner.close();
    }

    /**
     * Parses a single CSV line handling quoted fields.
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