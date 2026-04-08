/**
 * MP05 - Extract Values from a Column
 * Student: PARAON, JOSH HEIDRIC C.
 * Description: Prompts the user for a CSV file path and a column index,
 *              then extracts and displays all values from that column.
 * Run with: node MP05_ExtractColumn.js
 */

const fs = require('fs');
const readline = require('readline');

// Set up readline interface for user input
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

/**
 * Parses a single CSV line, handling quoted fields containing commas.
 * @param {string} line - raw CSV line
 * @returns {string[]} array of field values
 */
function parseCSVLine(line) {
    const tokens = [];
    let current = '';
    let inQuotes = false; // tracks whether we're inside a quoted field

    for (const char of line) {
        if (char === '"') {
            inQuotes = !inQuotes; // toggle quote state
        } else if (char === ',' && !inQuotes) {
            tokens.push(current); // end of a field
            current = '';
        } else {
            current += char; // build current field value
        }
    }

    tokens.push(current); // push the last field
    return tokens;
}

// Step 1: Ask user for CSV file path
rl.question('Enter the CSV dataset file path: ', (filePath) => {
    filePath = filePath.trim();

    // Step 2: Ask user which column index to extract (0-based)
    rl.question('Enter the column index to extract (0-based): ', (indexInput) => {
        const columnIndex = parseInt(indexInput.trim());

        if (isNaN(columnIndex)) {
            console.log('Error: Invalid column index entered.');
            rl.close();
            return;
        }

        // Step 3: Read the CSV file using Node.js File System module
        try {
            const fileContent = fs.readFileSync(filePath, 'utf8');

            // Step 4: Parse CSV into an array of lines
            const lines = fileContent.split('\n').filter(line => line.trim() !== '');

            if (lines.length === 0) {
                console.log('Error: The file is empty.');
                rl.close();
                return;
            }

            // Step 5: Extract header name and values from the specified column
            const headerFields = parseCSVLine(lines[0]);

            if (columnIndex >= headerFields.length) {
                console.log(`Error: Column index ${columnIndex} does not exist. Dataset has ${headerFields.length} column(s).`);
                rl.close();
                return;
            }

            const headerName = headerFields[columnIndex].trim(); // get column header
            const extractedValues = [];                           // stores extracted values

            for (let i = 1; i < lines.length; i++) {
                const fields = parseCSVLine(lines[i]);

                if (fields.length > columnIndex) {
                    extractedValues.push(fields[columnIndex].trim()); // collect column value
                }
            }

            // Step 6: Display formatted output
            console.log('\n========================================');
            console.log(`Column Extracted: ${headerName} (Index ${columnIndex})`);
            console.log(`Total Values Extracted: ${extractedValues.length}`);
            console.log('========================================');

            extractedValues.forEach((value, index) => {
                console.log(`[${index + 1}] ${value}`);
            });

        } catch (err) {
            // Step 7: Handle errors
            if (err.code === 'ENOENT') {
                console.log('Error: File not found - ' + filePath);
            } else {
                console.log('Error reading file: ' + err.message);
            }
        }

        rl.close();
    });
});
