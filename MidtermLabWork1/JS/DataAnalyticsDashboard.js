const fs = require('fs');
const readline = require('readline');

// ─── Input Setup ──────────────────────────────────────────────────────────────
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

// A simple promise wrapper to use async/await for prompt inputs
const ask = (query) => new Promise((resolve) => rl.question(query, resolve));

// ─── Data Model ───────────────────────────────────────────────────────────────
class GameRecord {
    constructor(fields) {
        this.img = (fields[0] || '').trim();
        this.title = (fields[1] || '').trim();
        this.console = (fields[2] || '').trim();
        this.genre = (fields[3] || '').trim();
        this.publisher = (fields[4] || '').trim();
        this.developer = (fields[5] || '').trim();
        this.criticScore = this.parseDouble(fields[6]);
        this.totalSales = this.parseDouble(fields[7]);
        this.naSales = this.parseDouble(fields[8]);
        this.jpSales = this.parseDouble(fields[9]);
        this.palSales = this.parseDouble(fields[10]);
        this.otherSales = this.parseDouble(fields[11]);
        this.releaseDate = fields.length > 12 ? fields[12].trim() : "";
        this.lastUpdate = fields.length > 13 ? fields[13].trim() : "";
    }

    parseDouble(s) {
        if (!s || s.trim() === "") return 0.0;
        const val = parseFloat(s.trim());
        return isNaN(val) ? 0.0 : val;
    }
}

// ─── Constants ────────────────────────────────────────────────────────────────
const EXPECTED_HEADER =
    "img,title,console,genre,publisher,developer,critic_score," +
    "total_sales,na_sales,jp_sales,pal_sales,other_sales,release_date,last_update";

// ANSI Colors
const COLORS = {
    RESET: "\x1b[0m",
    CYAN: "\x1b[1;36m",
    GREEN: "\x1b[1;32m",
    YELLOW: "\x1b[1;33m",
    RED: "\x1b[1;31m",
    BLUE: "\x1b[1;34m",
    WHITE: "\x1b[1;37m",
    PURPLE: "\x1b[1;35m"
};

// ─── Main Execution ───────────────────────────────────────────────────────────
async function main() {
    let dataset = null;
    let filePath = "";

    printBanner();

    // ── Step 1: File Validation Loop ──────────────────────────────────────
    while (true) {
        filePath = (await ask(`${COLORS.CYAN}  Enter dataset file path: ${COLORS.RESET}`)).trim();
        
        if (!fs.existsSync(filePath) || !fs.statSync(filePath).isFile()) {
            console.log(`${COLORS.RED}  [ERROR] File does not exist: "${filePath}". Please try again.\n${COLORS.RESET}`);
            continue;
        }

        try {
            fs.accessSync(filePath, fs.constants.R_OK);
        } catch (err) {
            console.log(`${COLORS.RED}  [ERROR] File is not readable. Check file permissions and try again.\n${COLORS.RESET}`);
            continue;
        }

        if (!filePath.toLowerCase().endsWith(".csv")) {
            console.log(`${COLORS.RED}  [ERROR] File is not a .csv file. Please provide a valid CSV file.\n${COLORS.RESET}`);
            continue;
        }

        // Validate CSV format
        const csvError = validateCSVFormat(filePath);
        if (csvError !== null) {
            console.log(`${COLORS.RED}  [ERROR] Invalid CSV format: ${csvError}\n${COLORS.RESET}`);
            continue;
        }

        // Load data
        console.log(`${COLORS.YELLOW}\n  Loading dataset...${COLORS.RESET}`);
        dataset = loadCSV(filePath);
        if (!dataset || dataset.length === 0) {
            console.log(`${COLORS.RED}  [ERROR] Dataset is empty or could not be parsed. Please try again.\n${COLORS.RESET}`);
            continue;
        }

        console.log(`${COLORS.GREEN}  [OK] File validated and loaded successfully! (${dataset.length} records)\n${COLORS.RESET}`);
        break;
    }

    // ── Step 2: Menu Loop ─────────────────────────────────────────────────
    while (true) {
        printMenu();
        const choice = (await ask(`${COLORS.CYAN}  Enter choice: ${COLORS.RESET}`)).trim();

        switch (choice) {
            case "1": viewDatasetSummary(dataset); break;
            case "2": monthlySales(dataset); break;
            case "3": topPublishers(dataset); break;
            case "4": categoryAnalysis(dataset); break;
            case "5":
                printFooter();
                rl.close();
                return;
            default:
                console.log(`${COLORS.RED}\n  [ERROR] Invalid choice. Please enter 1–5.\n${COLORS.RESET}`);
        }
    }
}

// ─── CSV Validation ───────────────────────────────────────────────────────────
function validateCSVFormat(filePath) {
    try {
        const fileContent = fs.readFileSync(filePath, 'utf-8');
        const lines = fileContent.split(/\r?\n/);
        
        if (lines.length === 0 || lines[0].trim() === '') {
            return "File is empty or has no header row.";
        }

        const headerLine = lines[0].trim().toLowerCase();
        if (headerLine !== EXPECTED_HEADER) {
            return `Header mismatch.\n    Expected: ${EXPECTED_HEADER}\n    Found   : ${headerLine}`;
        }

        if (lines.length < 2 || lines[1].trim() === '') {
            return "File has a header but no data rows.";
        }

        const cols = splitCSVLine(lines[1]);
        if (cols.length < 12) {
            return `Data row has too few columns (expected 14, found ${cols.length}).`;
        }

        return null;
    } catch (e) {
        return "Could not read file: " + e.message;
    }
}

// ─── CSV Loader ───────────────────────────────────────────────────────────────
function loadCSV(filePath) {
    const records = [];
    try {
        const fileContent = fs.readFileSync(filePath, 'utf-8');
        const lines = fileContent.split(/\r?\n/);

        // skip header
        for (let i = 1; i < lines.length; i++) {
            const line = lines[i];
            if (line.trim() === '') continue;

            let fields = splitCSVLine(line);
            if (fields.length >= 12) {
                // Pad to 14 fields if needed
                while (fields.length < 14) fields.push("");
                records.push(new GameRecord(fields));
            }
        }
        return records;
    } catch (e) {
        console.log(`${COLORS.RED}  [ERROR] Failed to load CSV: ${e.message}${COLORS.RESET}`);
        return null;
    }
}

// Handles quoted fields (e.g., "Yoru, Tomosu")
function splitCSVLine(line) {
    const tokens = [];
    let inQuotes = false;
    let sb = '';

    for (let i = 0; i < line.length; i++) {
        const c = line[i];
        if (c === '"') {
            inQuotes = !inQuotes;
        } else if (c === ',' && !inQuotes) {
            tokens.push(sb);
            sb = '';
        } else {
            sb += c;
        }
    }
    tokens.push(sb);
    return tokens;
}

// ─── Option 1: Dataset Summary ────────────────────────────────────────────────
function viewDatasetSummary(data) {
    printSectionHeader("DATASET SUMMARY");

    const totalRecords = data.length;
    const validSalesData = data.filter(g => g.totalSales > 0);
    const validSalesCount = validSalesData.length;
    const totalSales = data.reduce((sum, g) => sum + g.totalSales, 0);
    
    const gamesWithScores = data.filter(g => g.criticScore > 0);
    const avgScore = gamesWithScores.length > 0
        ? gamesWithScores.reduce((sum, g) => sum + g.criticScore, 0) / gamesWithScores.length
        : 0;

    const uniqueTitles = new Set(data.map(g => g.title)).size;
    const uniqueConsoles = new Set(data.map(g => g.console)).size;
    const uniqueGenres = new Set(data.map(g => g.genre)).size;
    const uniquePubs = new Set(data.map(g => g.publisher)).size;

    let bestSeller = null;
    if (validSalesData.length > 0) {
        bestSeller = validSalesData.reduce((max, g) => g.totalSales > max.totalSales ? g : max, validSalesData[0]);
    }

    printRow("Total Records", totalRecords.toString());
    printRow("Records with Sales", validSalesCount.toString());
    printRow("Unique Titles", uniqueTitles.toString());
    printRow("Unique Consoles", uniqueConsoles.toString());
    printRow("Unique Genres", uniqueGenres.toString());
    printRow("Unique Publishers", uniquePubs.toString());
    printRow("Total Sales (M)", totalSales.toFixed(2) + " M");
    printRow("Avg Critic Score", avgScore.toFixed(2) + " / 10");

    if (bestSeller) {
        printRow("Best-Selling Game", `${bestSeller.title} (${bestSeller.console}) — ${bestSeller.totalSales.toFixed(2)} M units`);
    }

    printDivider();
}

// ─── Option 2: Sales by Console ───────────────────────────────────────────────
function monthlySales(data) {
    printSectionHeader("SALES BY CONSOLE (Top 15)");

    const salesMap = {};
    data.forEach(g => {
        if (g.totalSales > 0 && g.console.trim() !== '') {
            salesMap[g.console] = (salesMap[g.console] || 0) + g.totalSales;
        }
    });

    const sortedConsoles = Object.entries(salesMap)
        .sort((a, b) => b[1] - a[1])
        .slice(0, 15);

    sortedConsoles.forEach(([consoleName, sales]) => {
        const barLen = Math.min(Math.floor(sales / 10), 40);
        const bar = "█".repeat(barLen);
        
        const paddedName = String(consoleName).padEnd(8);
        const paddedBar = String(bar).padEnd(40);
        const formattedSales = sales.toFixed(2).padStart(8);

        console.log(`  ${COLORS.YELLOW}${paddedName}${COLORS.RESET} │ ${COLORS.GREEN}${paddedBar}${COLORS.RESET} ${COLORS.WHITE}${formattedSales} M${COLORS.RESET}`);
    });

    printDivider();
}

// ─── Option 3: Top Publishers ─────────────────────────────────────────────────
function topPublishers(data) {
    printSectionHeader("TOP 10 PUBLISHERS BY TOTAL SALES");

    const pubSales = {};
    const pubTitles = {};

    data.forEach(g => {
        if (g.publisher.trim() !== '') {
            if (g.totalSales > 0) {
                pubSales[g.publisher] = (pubSales[g.publisher] || 0) + g.totalSales;
            }
            pubTitles[g.publisher] = (pubTitles[g.publisher] || 0) + 1;
        }
    });

    console.log(`  ${COLORS.CYAN}Rank  ${String("Publisher").padEnd(30)}  Total Sales    Titles${COLORS.RESET}`);
    console.log(`  ${"─".repeat(58)}`);

    const sortedPubs = Object.entries(pubSales)
        .sort((a, b) => b[1] - a[1])
        .slice(0, 10);

    sortedPubs.forEach(([publisher, sales], index) => {
        const rankStr = String(index + 1).padEnd(4);
        const pubStr = truncate(publisher, 30).padEnd(30);
        const salesStr = sales.toFixed(2).padStart(8);
        const titlesStr = String(pubTitles[publisher] || 0).padStart(8);

        console.log(`  ${COLORS.YELLOW}${rankStr}${COLORS.RESET}  ${pubStr}  ${COLORS.GREEN}${salesStr} M${COLORS.RESET}  ${titlesStr}`);
    });

    printDivider();
}

// ─── Option 4: Category Analysis ─────────────────────────────────────────────
function categoryAnalysis(data) {
    printSectionHeader("CATEGORY (GENRE) ANALYSIS");

    const byGenre = {};
    data.forEach(g => {
        if (g.genre.trim() !== '') {
            if (!byGenre[g.genre]) byGenre[g.genre] = [];
            byGenre[g.genre].push(g);
        }
    });

    console.log(`  ${COLORS.CYAN}${String("Genre").padEnd(22)}   Titles   Total Sales  Avg Score  Avg Sales${COLORS.RESET}`);
    console.log(`  ${"─".repeat(62)}`);

    const sortedGenres = Object.entries(byGenre).sort((a, b) => {
        const sumA = a[1].reduce((sum, g) => sum + g.totalSales, 0);
        const sumB = b[1].reduce((sum, g) => sum + g.totalSales, 0);
        return sumB - sumA;
    });

    sortedGenres.forEach(([genre, list]) => {
        const totalSales = list.reduce((sum, g) => sum + g.totalSales, 0);
        
        const validScores = list.filter(g => g.criticScore > 0);
        const avgScore = validScores.length > 0 
            ? validScores.reduce((sum, g) => sum + g.criticScore, 0) / validScores.length 
            : 0;

        const validSales = list.filter(g => g.totalSales > 0);
        const avgSales = validSales.length > 0 
            ? validSales.reduce((sum, g) => sum + g.totalSales, 0) / validSales.length 
            : 0;

        const genreStr = truncate(genre, 22).padEnd(22);
        const titlesStr = String(list.length).padStart(7);
        const totalSalesStr = totalSales.toFixed(2).padStart(8);
        const avgScoreStr = avgScore.toFixed(2).padStart(8);
        const avgSalesStr = avgSales.toFixed(2).padStart(6);

        console.log(`  ${genreStr}  ${COLORS.WHITE}${titlesStr}${COLORS.RESET}  ${COLORS.GREEN}${totalSalesStr} M${COLORS.RESET}  ${COLORS.YELLOW}${avgScoreStr}${COLORS.RESET}  ${COLORS.PURPLE}${avgSalesStr} M${COLORS.RESET}`);
    });

    printDivider();
}

// ─── UI Helpers ───────────────────────────────────────────────────────────────
function printBanner() {
    console.log(COLORS.CYAN);
    console.log("  ╔══════════════════════════════════════════════════════╗");
    console.log("  ║         MINI DATA ANALYTICS CONSOLE DASHBOARD        ║");
    console.log("  ║          Note: Monthly Sales replaced with           ║");
    console.log("  ║          Sales by Console because dataset            ║");
    console.log("  ║          has no data on monthly sales :(             ║");
    console.log("  ╚══════════════════════════════════════════════════════╝");
    console.log(COLORS.RESET);
}

function printMenu() {
    console.log(COLORS.BLUE);
    console.log("  ┌─────────────────────────────────────┐");
    console.log("  │              MAIN MENU              │");
    console.log("  ├─────────────────────────────────────┤");
    console.log("  │  1 - View Dataset Summary           │");
    console.log("  │  2 - Sales by Console               │");
    console.log("  │  3 - Top Publishers                 │");
    console.log("  │  4 - Category Analysis              │");
    console.log("  │  5 - Exit                           │");
    console.log("  └─────────────────────────────────────┘");
    process.stdout.write(COLORS.RESET);
}

function printSectionHeader(title) {
    console.log();
    console.log(`${COLORS.CYAN}  ╔══════════════════════════════════════════════════════╗`);
    console.log(`  ║  ${String(title).padEnd(52)}║`);
    console.log(`  ╚══════════════════════════════════════════════════════╝${COLORS.RESET}`);
}

function printRow(label, value) {
    console.log(`  ${COLORS.YELLOW}${String(label).padEnd(22)}${COLORS.RESET} : ${COLORS.WHITE}${value}${COLORS.RESET}`);
}

function printDivider() {
    console.log(`${COLORS.BLUE}\n  ${"─".repeat(54)}${COLORS.RESET}\n`);
}

function printFooter() {
    console.log(COLORS.GREEN);
    console.log("  ╔══════════════════════════════════════════════════════╗");
    console.log("  ║         Thank you for using the Dashboard!           ║");
    console.log("  ║               Paraon, Josh Heidric C.                ║");
    console.log("  ║                     23-0013-121                      ║");
    console.log("  ╚══════════════════════════════════════════════════════╝");
    console.log(COLORS.RESET);
}

function truncate(s, max) {
    return s.length <= max ? s : s.substring(0, max - 1) + "…";
}

// Start Application
main();