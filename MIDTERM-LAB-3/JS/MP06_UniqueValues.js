/**
 * MP06 - Display Unique Values
 * Student: PARAON, JOSH HEIDRIC C.
 * Description: Reads a CSV dataset and displays the unique (distinct) values
 *              found in each column across all records.
 * Run with: node MP06_UniqueValues.js
 */

const fs = require('fs');
const readline = require('readline');

// Set up readline interface for console input
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

/**
 * Parses a single CSV line, correctly handling quoted fields with commas.
 * @param {string} line - raw CSV line
 * @returns {string[]} array of field values
 */
function parseCSVLine(line) {
    const tokens = [];
    let current = '';
    let inQuotes = false; // tracks whether we are inside a quoted field

    for (const char of line) {
        if (char === '"') {
            inQuotes = !inQuotes;
        } else if (char === ',' && !inQuotes) {
            tokens.push(current);
            current = '';
        } else {
            current += char;
        }
    }

    tokens.push(current); // push the last field
    return tokens;
}

// Step 1: Ask user for the CSV file path
rl.question('Enter the CSV dataset file path: ', (filePath) => {
    filePath = filePath.trim();

    // Step 2: Read the dataset file using Node.js File System module
    try {
        const fileContent = fs.readFileSync(filePath, 'utf8');

        // Step 3: Split into lines and filter out blank lines
        const lines = fileContent.split('\n').filter(line => line.trim() !== '');

        if (lines.length === 0) {
            console.log('Error: The file is empty.');
            rl.close();
            return;
        }

        // Step 4: Parse headers from first row
        const headers = parseCSVLine(lines[0]);

        // Initialize a Set for each column to collect unique values
        const uniquePerColumn = headers.map(() => new Set());

        // Step 5: Process each data row and populate the Sets
        for (let i = 1; i < lines.length; i++) {
            const fields = parseCSVLine(lines[i]);

            fields.forEach((value, colIndex) => {
                const trimmed = value.trim();
                if (trimmed !== '' && colIndex < uniquePerColumn.length) {
                    uniquePerColumn[colIndex].add(trimmed); // Set ignores duplicates automatically
                }
            });
        }

        // Step 6: Display unique values per column
        console.log('\n========================================');
        console.log('UNIQUE VALUES PER COLUMN');
        console.log('========================================');

        headers.forEach((header, i) => {
            const unique = Array.from(uniquePerColumn[i]); // convert Set to array for display
            console.log(`\nColumn [${i}]: ${header.trim()}`);
            console.log(`  Unique Count: ${unique.length}`);
            console.log('  Values:');
            unique.forEach((val, idx) => {
                console.log(`    ${idx + 1}. ${val}`);
            });
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