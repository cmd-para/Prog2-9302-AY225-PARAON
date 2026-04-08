/**
 * MP07 - Sort Records Alphabetically
 * Student: PARAON, JOSH HEIDRIC C.
 * Description: Prompts for a CSV file and sorts all records alphabetically 
 * by comparing every column in the dataset. Includes a retry loop for file input.
 */

const fs = require('fs');
const readline = require('readline/promises');

// Set up promise-based interface for async/await loop
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

/**
 * Parses a single CSV line, handling quoted fields.
 */
function parseCSVLine(line) {
    const tokens = [];
    let current = '';
    let inQuotes = false;

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
    tokens.push(current);
    return tokens;
}

async function main() {
    let lines = [];
    let headers = null;
    let fileLoaded = false;

    // Loop until a valid file is successfully loaded
    while (!fileLoaded) {
        const filePath = (await rl.question('Enter the CSV dataset file path: ')).trim();

        try {
            const fileContent = fs.readFileSync(filePath, 'utf8');
            lines = fileContent.split('\n').filter(line => line.trim() !== '');

            if (lines.length === 0) {
                console.log('Error: The file is empty. Please try a different file.');
                continue;
            }

            headers = parseCSVLine(lines[0]);
            fileLoaded = true; // Break the loop

        } catch (err) {
            if (err.code === 'ENOENT') {
                console.log(`Error: File not found at '${filePath}'. Please try again.`);
            } else {
                console.log(`Error reading file: ${err.message}`);
            }
        }
    }

    // Step 5: Parse data rows
    const records = lines.slice(1).map(line => parseCSVLine(line));

    // Sort records alphabetically by comparing the entire row content
    records.sort((row1, row2) => {
        const minLength = Math.min(row1.length, row2.length);
        
        // Compare column by column
        for (let i = 0; i < minLength; i++) {
            const val1 = row1[i].trim();
            const val2 = row2[i].trim();
            const comparison = val1.localeCompare(val2, undefined, { sensitivity: 'base' });

            // If columns are different, we have our alphabetical order
            if (comparison !== 0) {
                return comparison;
            }
        }
        // Tie-breaker: shorter row comes first if all common columns match
        return row1.length - row2.length;
    });

    // Step 6: Display formatted output
    console.log('\n========================================');
    console.log('Records Sorted: Alphabetically by Entire Dataset');
    console.log(`Total Records: ${records.length}`);
    console.log('========================================');

    // Print header
    console.log(headers.map(h => h.trim()).join(' | '));
    console.log('-'.repeat(60));

    // Print each alphabetically sorted record
    records.forEach((row, index) => {
        console.log(`[${index + 1}] ${row.map(f => f.trim()).join(' | ')}`);
    });

    rl.close();
}

main();