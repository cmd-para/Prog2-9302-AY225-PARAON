#!/usr/bin/env node

const fs = require("fs");
const path = require("path");
const readline = require("readline");
const os = require("os");

// ─── ANSI Colors ──────────────────────────────────────────────────────────────
const RESET  = "\x1b[0m";
const CYAN   = "\x1b[1;36m";
const GREEN  = "\x1b[1;32m";
const YELLOW = "\x1b[1;33m";
const RED    = "\x1b[1;31m";
const BLUE   = "\x1b[1;34m";
const WHITE  = "\x1b[1;37m";
const PURPLE = "\x1b[1;35m";

// ─── Expected CSV Header ──────────────────────────────────────────────────────
const EXPECTED_HEADER =
  "img,title,console,genre,publisher,developer,critic_score," +
  "total_sales,na_sales,jp_sales,pal_sales,other_sales,release_date,last_update";

// ─── Data Model ───────────────────────────────────────────────────────────────
function parseGameRecord(fields) {
  const parseDouble = (s) => {
    if (!s || s.trim() === "") return 0.0;
    const n = parseFloat(s.trim());
    return isNaN(n) ? 0.0 : n;
  };

  return {
    img:         (fields[0]  || "").trim(),
    title:       (fields[1]  || "").trim(),
    console:     (fields[2]  || "").trim(),
    genre:       (fields[3]  || "").trim(),
    publisher:   (fields[4]  || "").trim(),
    developer:   (fields[5]  || "").trim(),
    criticScore: parseDouble(fields[6]),
    totalSales:  parseDouble(fields[7]),
    naSales:     parseDouble(fields[8]),
    jpSales:     parseDouble(fields[9]),
    palSales:    parseDouble(fields[10]),
    otherSales:  parseDouble(fields[11]),
    releaseDate: fields.length > 12 ? (fields[12] || "").trim() : "",
    lastUpdate:  fields.length > 13 ? (fields[13] || "").trim() : "",
  };
}

// ─── Path Resolver ────────────────────────────────────────────────────────────
function resolvePath(input) {
  // Strip surrounding quotes
  if (
    (input.startsWith('"') && input.endsWith('"')) ||
    (input.startsWith("'") && input.endsWith("'"))
  ) {
    input = input.slice(1, -1).trim();
  }
  // Expand ~
  if (input.startsWith("~")) {
    input = os.homedir() + input.slice(1);
  }
  try {
    return fs.realpathSync(path.resolve(input));
  } catch {
    return path.resolve(input);
  }
}

// ─── CSV Split (handles quoted fields) ───────────────────────────────────────
function splitCSVLine(line) {
  const tokens = [];
  let inQuotes = false;
  let sb = "";
  for (let i = 0; i < line.length; i++) {
    const c = line[i];
    if (c === '"') {
      inQuotes = !inQuotes;
    } else if (c === "," && !inQuotes) {
      tokens.push(sb);
      sb = "";
    } else {
      sb += c;
    }
  }
  tokens.push(sb);
  return tokens;
}

// ─── CSV Validation ───────────────────────────────────────────────────────────
function validateCSVFormat(filePath) {
  let content;
  try {
    content = fs.readFileSync(filePath, "utf8");
  } catch (e) {
    return "Could not read file: " + e.message;
  }

  const lines = content.split(/\r?\n/);
  if (!lines[0] || lines[0].trim() === "") return "File is empty or has no header row.";

  const normalizedHeader = lines[0].trim().toLowerCase();
  if (normalizedHeader !== EXPECTED_HEADER)
    return (
      "Header mismatch.\n    Expected: " +
      EXPECTED_HEADER +
      "\n    Found   : " +
      lines[0].trim()
    );

  const firstData = lines[1];
  if (!firstData || firstData.trim() === "") return "File has a header but no data rows.";

  const cols = splitCSVLine(firstData);
  if (cols.length < 12)
    return `Data row has too few columns (expected 14, found ${cols.length}).`;

  return null; // valid
}

// ─── CSV Loader ───────────────────────────────────────────────────────────────
function loadCSV(filePath) {
  const records = [];
  try {
    const content = fs.readFileSync(filePath, "utf8");
    const lines = content.split(/\r?\n/);
    for (let i = 1; i < lines.length; i++) {
      const line = lines[i];
      if (!line || line.trim() === "") continue;
      let fields = splitCSVLine(line);
      if (fields.length >= 12) {
        // Pad to 14 if needed
        while (fields.length < 14) fields.push("");
        records.push(parseGameRecord(fields));
      }
    }
  } catch (e) {
    console.log(RED + "  [ERROR] Failed to load CSV: " + e.message + RESET);
    return null;
  }
  return records;
}

// ─── UI Helpers ───────────────────────────────────────────────────────────────
function printBanner() {
  console.log(CYAN);
  console.log("  ╔══════════════════════════════════════════════════════╗");
  console.log("  ║         MINI DATA ANALYTICS CONSOLE DASHBOARD        ║");
  console.log("  ║          Note: Monthly Sales replaced with           ║");
  console.log("  ║          Sales by Console because dataset            ║");
  console.log("  ║          has no data on monthly sales :(             ║");
  console.log("  ╚══════════════════════════════════════════════════════╝");
  console.log(RESET);
}

function printMenu() {
  console.log(BLUE);
  console.log("  ┌─────────────────────────────────────┐");
  console.log("  │              MAIN MENU              │");
  console.log("  ├─────────────────────────────────────┤");
  console.log("  │  1 - View Dataset Summary           │");
  console.log("  │  2 - Sales by Console               │");
  console.log("  │  3 - Top Publishers                 │");
  console.log("  │  4 - Category Analysis              │");
  console.log("  │  5 - Exit                           │");
  console.log("  └─────────────────────────────────────┘");
  process.stdout.write(RESET);
}

function printSectionHeader(title) {
  console.log();
  console.log(CYAN + "  ╔══════════════════════════════════════════════════════╗");
  console.log("  ║  " + title.padEnd(52) + "║");
  console.log("  ╚══════════════════════════════════════════════════════╝" + RESET);
}

function printRow(label, value) {
  console.log("  " + YELLOW + label.padEnd(22) + RESET + " : " + WHITE + value + RESET);
}

function printDivider() {
  console.log(BLUE + "\n  " + "─".repeat(54) + RESET + "\n");
}

function printFooter() {
  console.log(GREEN);
  console.log("  ╔══════════════════════════════════════════════════════╗");
  console.log("  ║         Thank you for using the Dashboard!           ║");
  console.log("  ║               Paraon, Josh Heidric C.                ║");
  console.log("  ║                     23-0013-121                      ║");
  console.log("  ╚══════════════════════════════════════════════════════╝");
  console.log(RESET);
}

function truncate(s, max) {
  return s.length <= max ? s : s.substring(0, max - 1) + "…";
}

// ─── Option 1: Dataset Summary ────────────────────────────────────────────────
function viewDatasetSummary(data) {
  printSectionHeader("DATASET SUMMARY");

  const totalRecords   = data.length;
  const validSales     = data.filter((g) => g.totalSales > 0).length;
  const totalSales     = data.reduce((s, g) => s + g.totalSales, 0);
  const scoredGames    = data.filter((g) => g.criticScore > 0);
  const avgScore       = scoredGames.length
    ? scoredGames.reduce((s, g) => s + g.criticScore, 0) / scoredGames.length
    : 0;
  const uniqueTitles   = new Set(data.map((g) => g.title)).size;
  const uniqueConsoles = new Set(data.map((g) => g.console)).size;
  const uniqueGenres   = new Set(data.map((g) => g.genre)).size;
  const uniquePubs     = new Set(data.map((g) => g.publisher)).size;

  const bestSeller = data
    .filter((g) => g.totalSales > 0)
    .reduce((best, g) => (!best || g.totalSales > best.totalSales ? g : best), null);

  printRow("Total Records",      String(totalRecords));
  printRow("Records with Sales", String(validSales));
  printRow("Unique Titles",      String(uniqueTitles));
  printRow("Unique Consoles",    String(uniqueConsoles));
  printRow("Unique Genres",      String(uniqueGenres));
  printRow("Unique Publishers",  String(uniquePubs));
  printRow("Total Sales (M)",    totalSales.toFixed(2) + " M");
  printRow("Avg Critic Score",   avgScore.toFixed(2) + " / 10");
  if (bestSeller) {
    printRow(
      "Best-Selling Game",
      `${bestSeller.title} (${bestSeller.console}) — ${bestSeller.totalSales.toFixed(2)} M units`
    );
  }

  printDivider();
}

// ─── Option 2: Sales by Console ──────────────────────────────────────────────
async function monthlySales(data, rl) {
  printSectionHeader("ALL CONSOLES BY TOTAL SALES");

  const salesMap = {};
  data
    .filter((g) => g.totalSales > 0 && g.console.trim() !== "")
    .forEach((g) => {
      salesMap[g.console] = (salesMap[g.console] || 0) + g.totalSales;
    });

  const sorted = Object.entries(salesMap).sort((a, b) => b[1] - a[1]);
  const total      = sorted.length;
  const pageSize   = 20;
  const totalPages = Math.ceil(total / pageSize);
  let page         = 0;

  while (true) {
    const from = page * pageSize;
    const to   = Math.min(from + pageSize, total);

    console.log(
      `\n  ${CYAN}${"Rank".padEnd(4)}  ${"Console".padEnd(10)}  ${"Sales Chart".padEnd(40)}  ${"Total Sales".padStart(10)}${RESET}`
    );
    console.log("  " + "─".repeat(70));

    for (let i = from; i < to; i++) {
      const [consoleName, sales] = sorted[i];
      const barLen = Math.min(Math.floor(sales / 10), 40);
      const bar    = "█".repeat(barLen);
      console.log(
        `  ${YELLOW}${String(i + 1).padEnd(4)}${RESET}  ${consoleName.padEnd(10)}  ${GREEN}${bar.padEnd(40)}${RESET}  ${WHITE}${sales.toFixed(2).padStart(8)} M${RESET}`
      );
    }

    console.log(`\n  ${BLUE}Page ${page + 1} of ${totalPages}  (${total} consoles total)${RESET}`);
    console.log("  " + CYAN + "[N] Next   [P] Previous   [Q] Back to menu" + RESET);

    const nav = await prompt(rl, CYAN + "  Enter choice: " + RESET);
    switch (nav.trim().toUpperCase()) {
      case "N": if (page < totalPages - 1) page++; break;
      case "P": if (page > 0) page--; break;
      case "Q": printDivider(); return;
      default: console.log(RED + "  [ERROR] Invalid input. Use N, P, or Q." + RESET);
    }
  }
}

// ─── Option 3: All Publishers ─────────────────────────────────────────────────
async function topPublishers(data, rl) {
  printSectionHeader("ALL PUBLISHERS BY TOTAL SALES");

  const salesMap  = {};
  const titlesMap = {};

  data
    .filter((g) => g.publisher.trim() !== "")
    .forEach((g) => {
      titlesMap[g.publisher] = (titlesMap[g.publisher] || 0) + 1;
      if (g.totalSales > 0) {
        salesMap[g.publisher] = (salesMap[g.publisher] || 0) + g.totalSales;
      }
    });

  const sorted     = Object.entries(salesMap).sort((a, b) => b[1] - a[1]);
  const total      = sorted.length;
  const pageSize   = 20;
  const totalPages = Math.ceil(total / pageSize);
  let page         = 0;

  while (true) {
    const from = page * pageSize;
    const to   = Math.min(from + pageSize, total);

    console.log(
      `\n  ${CYAN}${"Rank".padEnd(4)}  ${"Publisher".padEnd(30)}  ${"Total Sales".padStart(10)}  ${"Titles".padStart(8)}${RESET}`
    );
    console.log("  " + "─".repeat(58));

    for (let i = from; i < to; i++) {
      const [publisher, sales] = sorted[i];
      const titles = titlesMap[publisher] || 0;
      console.log(
        `  ${YELLOW}${String(i + 1).padEnd(4)}${RESET}  ${truncate(publisher, 30).padEnd(30)}  ${GREEN}${sales.toFixed(2).padStart(8)} M${RESET}  ${String(titles).padStart(8)}`
      );
    }

    console.log(`\n  ${BLUE}Page ${page + 1} of ${totalPages}  (${total} publishers total)${RESET}`);
    console.log("  " + CYAN + "[N] Next   [P] Previous   [Q] Back to menu" + RESET);

    const nav = await prompt(rl, CYAN + "  Enter choice: " + RESET);
    switch (nav.trim().toUpperCase()) {
      case "N": if (page < totalPages - 1) page++; break;
      case "P": if (page > 0) page--; break;
      case "Q": printDivider(); return;
      default: console.log(RED + "  [ERROR] Invalid input. Use N, P, or Q." + RESET);
    }
  }
}

// ─── Option 4: Category Analysis ─────────────────────────────────────────────
function categoryAnalysis(data) {
  printSectionHeader("CATEGORY (GENRE) ANALYSIS");

  const byGenre = {};
  data
    .filter((g) => g.genre.trim() !== "")
    .forEach((g) => {
      if (!byGenre[g.genre]) byGenre[g.genre] = [];
      byGenre[g.genre].push(g);
    });

  console.log(
    `  ${CYAN}${"Genre".padEnd(22)}  ${"Titles".padStart(7)}  ${"Total Sales".padStart(10)}  ${"Avg Score".padStart(8)}  ${"Avg Sales".padStart(8)}${RESET}`
  );
  console.log("  " + "─".repeat(62));

  Object.entries(byGenre)
    .sort((a, b) => {
      const sa = a[1].reduce((s, g) => s + g.totalSales, 0);
      const sb = b[1].reduce((s, g) => s + g.totalSales, 0);
      return sb - sa;
    })
    .forEach(([genre, list]) => {
      const totalSales = list.reduce((s, g) => s + g.totalSales, 0);
      const scored     = list.filter((g) => g.criticScore > 0);
      const avgScore   = scored.length
        ? scored.reduce((s, g) => s + g.criticScore, 0) / scored.length
        : 0;
      const withSales  = list.filter((g) => g.totalSales > 0);
      const avgSales   = withSales.length
        ? withSales.reduce((s, g) => s + g.totalSales, 0) / withSales.length
        : 0;

      console.log(
        `  ${truncate(genre, 22).padEnd(22)}  ${WHITE}${String(list.length).padStart(7)}${RESET}  ${GREEN}${totalSales.toFixed(2).padStart(8)} M${RESET}  ${YELLOW}${avgScore.toFixed(2).padStart(8)}${RESET}  ${PURPLE}${avgSales.toFixed(2).padStart(6)} M${RESET}`
      );
    });

  printDivider();
}

// ─── Prompt Helper ────────────────────────────────────────────────────────────
function prompt(rl, question) {
  return new Promise((resolve) => {
    rl.question(question, resolve);
  });
}

// ─── Main ─────────────────────────────────────────────────────────────────────
async function main() {
  const rl = readline.createInterface({
    input:  process.stdin,
    output: process.stdout,
  });

  printBanner();

  // ── Step 1: File Validation Loop ──────────────────────────────────────────
  let dataset  = null;

  while (true) {
    const rawInput  = await prompt(rl, CYAN + "  Enter dataset file path: " + RESET);
    const filePath  = resolvePath(rawInput.trim());

    if (!fs.existsSync(filePath)) {
      console.log(RED + `  [ERROR] File does not exist: "${filePath}". Please try again.\n` + RESET);
      continue;
    }
    const stat = fs.statSync(filePath);
    if (stat.isDirectory()) {
      console.log(RED + `  [ERROR] That path is a folder, not a file: "${filePath}". Please try again.\n` + RESET);
      continue;
    }
    try {
      fs.accessSync(filePath, fs.constants.R_OK);
    } catch {
      console.log(RED + "  [ERROR] File is not readable. Check file permissions and try again.\n" + RESET);
      continue;
    }
    if (!filePath.toLowerCase().endsWith(".csv")) {
      console.log(RED + "  [ERROR] File is not a .csv file. Please provide a valid CSV file.\n" + RESET);
      continue;
    }

    const csvError = validateCSVFormat(filePath);
    if (csvError) {
      console.log(RED + "  [ERROR] Invalid CSV format: " + csvError + "\n" + RESET);
      continue;
    }

    console.log(YELLOW + "\n  Loading dataset..." + RESET);
    dataset = loadCSV(filePath);
    if (!dataset || dataset.length === 0) {
      console.log(RED + "  [ERROR] Dataset is empty or could not be parsed. Please try again.\n" + RESET);
      continue;
    }

    console.log(GREEN + `  [OK] File validated and loaded successfully! (${dataset.length} records)\n` + RESET);
    break;
  }

  // ── Step 2: Menu Loop ─────────────────────────────────────────────────────
  while (true) {
    printMenu();
    const choice = await prompt(rl, CYAN + "  Enter choice: " + RESET);

    switch (choice.trim()) {
      case "1": viewDatasetSummary(dataset); break;
      case "2": await monthlySales(dataset, rl); break;
      case "3": await topPublishers(dataset, rl); break;
      case "4": categoryAnalysis(dataset); break;
      case "5":
        printFooter();
        rl.close();
        return;
      default:
        console.log(RED + "\n  [ERROR] Invalid choice. Please enter 1–5.\n" + RESET);
    }
  }
}

main().catch((err) => {
  console.error(RED + "Unhandled error: " + err.message + RESET);
  process.exit(1);
});