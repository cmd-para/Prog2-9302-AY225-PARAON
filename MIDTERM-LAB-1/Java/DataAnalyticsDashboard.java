import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class DataAnalyticsDashboard {

    // ─── Data Model ───────────────────────────────────────────────────────────
    static class GameRecord {
        String img, title, console, genre, publisher, developer;
        double criticScore, totalSales, naSales, jpSales, palSales, otherSales;
        String releaseDate, lastUpdate;

        GameRecord(String[] fields) {
            img          = fields[0].trim();
            title        = fields[1].trim();
            console      = fields[2].trim();
            genre        = fields[3].trim();
            publisher    = fields[4].trim();
            developer    = fields[5].trim();
            criticScore  = parseDouble(fields[6]);
            totalSales   = parseDouble(fields[7]);
            naSales      = parseDouble(fields[8]);
            jpSales      = parseDouble(fields[9]);
            palSales     = parseDouble(fields[10]);
            otherSales   = parseDouble(fields[11]);
            releaseDate  = fields.length > 12 ? fields[12].trim() : "";
            lastUpdate   = fields.length > 13 ? fields[13].trim() : "";
        }

        private double parseDouble(String s) {
            try { return s == null || s.isBlank() ? 0.0 : Double.parseDouble(s.trim()); }
            catch (NumberFormatException e) { return 0.0; }
        }
    }

    // ─── Expected CSV Header ──────────────────────────────────────────────────
    static final String EXPECTED_HEADER =
        "img,title,console,genre,publisher,developer,critic_score," +
        "total_sales,na_sales,jp_sales,pal_sales,other_sales,release_date,last_update";

    // ─── ANSI Colors ──────────────────────────────────────────────────────────
    static final String RESET  = "\033[0m";
    static final String CYAN   = "\033[1;36m";
    static final String GREEN  = "\033[1;32m";
    static final String YELLOW = "\033[1;33m";
    static final String RED    = "\033[1;31m";
    static final String BLUE   = "\033[1;34m";
    static final String WHITE  = "\033[1;37m";
    static final String PURPLE = "\033[1;35m";

    // ─── Main ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<GameRecord> dataset = null;
        String filePath = "";

        printBanner();

        // ── Step 1: File Validation Loop ──────────────────────────────────────
        while (true) {
            System.out.print(CYAN + "  Enter dataset file path: " + RESET);
            filePath = resolvePath(scanner.nextLine().trim());



            File file = new File(filePath);

            if (!file.exists()) {
                System.out.println(RED + "  [ERROR] File does not exist: \"" + filePath + "\". Please try again.\n" + RESET);
                continue;
            }
            if (file.isDirectory()) {
                System.out.println(RED + "  [ERROR] That path is a folder, not a file: \"" + filePath + "\". Please try again.\n" + RESET);
                continue;
            }
            if (!file.canRead()) {
                System.out.println(RED + "  [ERROR] File is not readable. Check file permissions and try again.\n" + RESET);
                continue;
            }
            if (!filePath.toLowerCase().endsWith(".csv")) {
                System.out.println(RED + "  [ERROR] File is not a .csv file. Please provide a valid CSV file.\n" + RESET);
                continue;
            }

            // Validate CSV format
            String csvError = validateCSVFormat(file);
            if (csvError != null) {
                System.out.println(RED + "  [ERROR] Invalid CSV format: " + csvError + "\n" + RESET);
                continue;
            }

            // Load data
            System.out.println(YELLOW + "\n  Loading dataset..." + RESET);
            dataset = loadCSV(file);
            if (dataset == null || dataset.isEmpty()) {
                System.out.println(RED + "  [ERROR] Dataset is empty or could not be parsed. Please try again.\n" + RESET);
                continue;
            }

            System.out.println(GREEN + "  [OK] File validated and loaded successfully! (" + dataset.size() + " records)\n" + RESET);
            break;
        }

        // ── Step 2: Menu Loop ─────────────────────────────────────────────────
        while (true) {
            printMenu();
            System.out.print(CYAN + "  Enter choice: " + RESET);
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> viewDatasetSummary(dataset);
                case "2" -> monthlySales(dataset);
                case "3" -> topPublishers(dataset);
                case "4" -> categoryAnalysis(dataset);
                case "5" -> {
                    printFooter();
                    scanner.close();
                    return;
                }
                default -> System.out.println(RED + "\n  [ERROR] Invalid choice. Please enter 1–5.\n" + RESET);
            }
        }
    }

    // ─── Path Resolver ────────────────────────────────────────────────────────
    static String resolvePath(String input) {
        // Strip surrounding quotes (single or double) that users sometimes paste
        if ((input.startsWith("\"") && input.endsWith("\"")) ||
            (input.startsWith("'")  && input.endsWith("'"))) {
            input = input.substring(1, input.length() - 1).trim();
        }
        // Expand ~ to the user's home directory
        if (input.startsWith("~")) {
            String home = System.getProperty("user.home");
            input = home + input.substring(1);
        }
        // Use canonical path to fully resolve symlinks and clean up the path
        try {
            return Paths.get(input).toRealPath().toString();
        } catch (IOException e) {
            // File may not exist yet — fall back to absolute path
            try {
                return new File(input).getCanonicalPath();
            } catch (IOException ex) {
                return new File(input).getAbsolutePath();
            }
        }
    }

    // ─── CSV Validation ───────────────────────────────────────────────────────
    static String validateCSVFormat(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String headerLine = br.readLine();
            if (headerLine == null || headerLine.isBlank())
                return "File is empty or has no header row.";

            // Normalize and compare headers
            String normalizedHeader = headerLine.trim().toLowerCase();
            if (!normalizedHeader.equals(EXPECTED_HEADER))
                return "Header mismatch.\n    Expected: " + EXPECTED_HEADER +
                       "\n    Found   : " + headerLine.trim();

            // Check at least one data row
            String firstData = br.readLine();
            if (firstData == null || firstData.isBlank())
                return "File has a header but no data rows.";

            // Check that data row has correct column count
            String[] cols = splitCSVLine(firstData);
            if (cols.length < 12)
                return "Data row has too few columns (expected 14, found " + cols.length + ").";

            return null; // valid
        } catch (IOException e) {
            return "Could not read file: " + e.getMessage();
        }
    }

    // ─── CSV Loader ───────────────────────────────────────────────────────────
    static List<GameRecord> loadCSV(File file) {
        List<GameRecord> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] fields = splitCSVLine(line);
                if (fields.length >= 12) {
                    // Pad to 14 fields if needed
                    if (fields.length < 14) fields = Arrays.copyOf(fields, 14);
                    records.add(new GameRecord(fields));
                }
            }
        } catch (IOException e) {
            System.out.println(RED + "  [ERROR] Failed to load CSV: " + e.getMessage() + RESET);
            return null;
        }
        return records;
    }

    // Handles quoted fields (e.g., "Yoru, Tomosu")
    static String[] splitCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }

    // ─── Option 1: Dataset Summary ────────────────────────────────────────────
    static void viewDatasetSummary(List<GameRecord> data) {
        printSectionHeader("DATASET SUMMARY");

        long totalRecords   = data.size();
        long validSales     = data.stream().filter(g -> g.totalSales > 0).count();
        double totalSales   = data.stream().mapToDouble(g -> g.totalSales).sum();
        double avgScore     = data.stream().filter(g -> g.criticScore > 0)
                                  .mapToDouble(g -> g.criticScore).average().orElse(0);
        long uniqueTitles   = data.stream().map(g -> g.title).distinct().count();
        long uniqueConsoles = data.stream().map(g -> g.console).distinct().count();
        long uniqueGenres   = data.stream().map(g -> g.genre).distinct().count();
        long uniquePubs     = data.stream().map(g -> g.publisher).distinct().count();

        Optional<GameRecord> bestSeller = data.stream()
            .filter(g -> g.totalSales > 0)
            .max(Comparator.comparingDouble(g -> g.totalSales));

        printRow("Total Records",       String.valueOf(totalRecords));
        printRow("Records with Sales",  String.valueOf(validSales));
        printRow("Unique Titles",       String.valueOf(uniqueTitles));
        printRow("Unique Consoles",     String.valueOf(uniqueConsoles));
        printRow("Unique Genres",       String.valueOf(uniqueGenres));
        printRow("Unique Publishers",   String.valueOf(uniquePubs));
        printRow("Total Sales (M)",     String.format("%.2f M", totalSales));
        printRow("Avg Critic Score",    String.format("%.2f / 10", avgScore));
        bestSeller.ifPresent(g ->
            printRow("Best-Selling Game", g.title + " (" + g.console + ") — " +
                     String.format("%.2f M units", g.totalSales))
        );

        printDivider();
    }

    // ─── Option 2: Sales by Console ───────────────────────────────────────────
    static void monthlySales(List<GameRecord> data) {
        printSectionHeader("ALL CONSOLES BY TOTAL SALES");

        Map<String, Double> salesByConsole = data.stream()
            .filter(g -> g.totalSales > 0 && !g.console.isBlank())
            .collect(Collectors.groupingBy(g -> g.console,
                     Collectors.summingDouble(g -> g.totalSales)));

        List<Map.Entry<String, Double>> sorted = salesByConsole.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .collect(Collectors.toList());

        int total      = sorted.size();
        int pageSize   = 20;
        int totalPages = (int) Math.ceil((double) total / pageSize);
        int[] page     = {0};

        Scanner sc = new Scanner(System.in);

        while (true) {
            int from = page[0] * pageSize;
            int to   = Math.min(from + pageSize, total);

            System.out.printf("%n  " + CYAN + "%-4s  %-10s  %-40s  %10s" + RESET + "%n",
                "Rank", "Console", "Sales Chart", "Total Sales");
            System.out.println("  " + "─".repeat(70));

            for (int i = from; i < to; i++) {
                Map.Entry<String, Double> e = sorted.get(i);
                int barLen = (int) Math.min(e.getValue() / 10, 40);
                String bar = "█".repeat(barLen);
                System.out.printf("  " + YELLOW + "%-4d" + RESET +
                    "  %-10s  " + GREEN + "%-40s" + RESET +
                    "  " + WHITE + "%8.2f M" + RESET + "%n",
                    i + 1, e.getKey(), bar, e.getValue());
            }

            System.out.printf("%n  " + BLUE + "Page %d of %d  (%d consoles total)" + RESET + "%n",
                page[0] + 1, totalPages, total);
            System.out.println("  " + CYAN + "[N] Next   [P] Previous   [Q] Back to menu" + RESET);
            System.out.print(CYAN + "  Enter choice: " + RESET);

            String nav = sc.nextLine().trim().toUpperCase();
            switch (nav) {
                case "N" -> { if (page[0] < totalPages - 1) page[0]++; }
                case "P" -> { if (page[0] > 0) page[0]--; }
                case "Q" -> { printDivider(); return; }
                default  -> System.out.println(RED + "  [ERROR] Invalid input. Use N, P, or Q." + RESET);
            }
        }
    }

    // ─── Option 3: All Publishers ─────────────────────────────────────────────
    static void topPublishers(List<GameRecord> data) {
        printSectionHeader("ALL PUBLISHERS BY TOTAL SALES");

        Map<String, Double> pubSales = data.stream()
            .filter(g -> g.totalSales > 0 && !g.publisher.isBlank())
            .collect(Collectors.groupingBy(g -> g.publisher,
                     Collectors.summingDouble(g -> g.totalSales)));

        Map<String, Long> pubTitles = data.stream()
            .filter(g -> !g.publisher.isBlank())
            .collect(Collectors.groupingBy(g -> g.publisher, Collectors.counting()));

        List<Map.Entry<String, Double>> sorted = pubSales.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .collect(Collectors.toList());

        int total     = sorted.size();
        int pageSize  = 20;
        int totalPages = (int) Math.ceil((double) total / pageSize);
        int[] page    = {0};

        Scanner sc = new Scanner(System.in);

        while (true) {
            int from = page[0] * pageSize;
            int to   = Math.min(from + pageSize, total);

            System.out.printf("%n  " + CYAN + "%-4s  %-30s  %10s  %8s" + RESET + "%n",
                "Rank", "Publisher", "Total Sales", "Titles");
            System.out.println("  " + "─".repeat(58));

            for (int i = from; i < to; i++) {
                Map.Entry<String, Double> e = sorted.get(i);
                System.out.printf("  " + YELLOW + "%-4d" + RESET +
                    "  %-30s  " + GREEN + "%8.2f M" + RESET + "  %8d%n",
                    i + 1,
                    truncate(e.getKey(), 30),
                    e.getValue(),
                    pubTitles.getOrDefault(e.getKey(), 0L));
            }

            System.out.printf("%n  " + BLUE + "Page %d of %d  (%d publishers total)" + RESET + "%n",
                page[0] + 1, totalPages, total);
            System.out.println("  " + CYAN + "[N] Next   [P] Previous   [Q] Back to menu" + RESET);
            System.out.print(CYAN + "  Enter choice: " + RESET);

            String nav = sc.nextLine().trim().toUpperCase();
            switch (nav) {
                case "N" -> { if (page[0] < totalPages - 1) page[0]++; }
                case "P" -> { if (page[0] > 0) page[0]--; }
                case "Q" -> { printDivider(); return; }
                default  -> System.out.println(RED + "  [ERROR] Invalid input. Use N, P, or Q." + RESET);
            }
        }
    }

    // ─── Option 4: Category Analysis ─────────────────────────────────────────
    static void categoryAnalysis(List<GameRecord> data) {
        printSectionHeader("CATEGORY (GENRE) ANALYSIS");

        Map<String, List<GameRecord>> byGenre = data.stream()
            .filter(g -> !g.genre.isBlank())
            .collect(Collectors.groupingBy(g -> g.genre));

        System.out.printf("  " + CYAN + "%-22s  %7s  %10s  %8s  %8s" + RESET + "%n",
            "Genre", "Titles", "Total Sales", "Avg Score", "Avg Sales");
        System.out.println("  " + "─".repeat(62));

        byGenre.entrySet().stream()
            .sorted((a, b) -> {
                double sa = a.getValue().stream().mapToDouble(g -> g.totalSales).sum();
                double sb = b.getValue().stream().mapToDouble(g -> g.totalSales).sum();
                return Double.compare(sb, sa);
            })
            .forEach(e -> {
                List<GameRecord> list = e.getValue();
                double totalSales = list.stream().mapToDouble(g -> g.totalSales).sum();
                double avgScore   = list.stream().filter(g -> g.criticScore > 0)
                                        .mapToDouble(g -> g.criticScore).average().orElse(0);
                double avgSales   = list.stream().filter(g -> g.totalSales > 0)
                                        .mapToDouble(g -> g.totalSales).average().orElse(0);

                System.out.printf("  %-22s  " + WHITE + "%7d" + RESET +
                    "  " + GREEN + "%8.2f M" + RESET +
                    "  " + YELLOW + "%8.2f" + RESET +
                    "  " + PURPLE + "%6.2f M" + RESET + "%n",
                    truncate(e.getKey(), 22),
                    list.size(),
                    totalSales,
                    avgScore,
                    avgSales);
            });

        printDivider();
    }

    // ─── UI Helpers ───────────────────────────────────────────────────────────
    static void printBanner() {
        System.out.println(CYAN);
        System.out.println("  ╔══════════════════════════════════════════════════════╗");
        System.out.println("  ║         MINI DATA ANALYTICS CONSOLE DASHBOARD        ║");
        System.out.println("  ║          Note: Monthly Sales replaced with           ║");
        System.out.println("  ║          Sales by Console because dataset            ║");
        System.out.println("  ║          has no data on monthly sales :(             ║");
        System.out.println("  ╚══════════════════════════════════════════════════════╝");
        System.out.println(RESET);
    }

    static void printMenu() {
        System.out.println(BLUE);
        System.out.println("  ┌─────────────────────────────────────┐");
        System.out.println("  │              MAIN MENU              │");
        System.out.println("  ├─────────────────────────────────────┤");
        System.out.println("  │  1 - View Dataset Summary           │");
        System.out.println("  │  2 - Sales by Console               │");
        System.out.println("  │  3 - Top Publishers                 │");
        System.out.println("  │  4 - Category Analysis              │");
        System.out.println("  │  5 - Exit                           │");
        System.out.println("  └─────────────────────────────────────┘");
        System.out.print(RESET);
    }

    static void printSectionHeader(String title) {
        System.out.println();
        System.out.println(CYAN + "  ╔══════════════════════════════════════════════════════╗");
        System.out.printf ("  ║  %-52s║%n", title);
        System.out.println("  ╚══════════════════════════════════════════════════════╝" + RESET);
    }

    static void printRow(String label, String value) {
        System.out.printf("  " + YELLOW + "%-22s" + RESET + " : " + WHITE + "%s" + RESET + "%n",
            label, value);
    }

    static void printDivider() {
        System.out.println(BLUE + "\n  " + "─".repeat(54) + RESET + "\n");
    }

    static void printFooter() {
        System.out.println(GREEN);
        System.out.println("  ╔══════════════════════════════════════════════════════╗");
        System.out.println("  ║         Thank you for using the Dashboard!           ║");
        System.out.println("  ║               Paraon, Josh Heidric C.                ║");
        System.out.println("  ║                     23-0013-121                      ║");
        System.out.println("  ╚══════════════════════════════════════════════════════╝");
        System.out.println(RESET);
    }

    static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}