// ============================================================
// Student   : PARAON, JOSH HEIDRIC C.
// Course    : Programming 2 - BSIT-GD 1
// Section   : 9302-AY225
// Assignment: Midterm Lab Work 2 – 3x3 Matrix Determinant Solver
// School    : University of Perpetual Help System DALTA – Molino Campus
// Date      : April 8, 2026
// GitHub    : https://github.com/
// Description:
//   This program computes the determinant of a 3×3 matrix assigned
//   to the student using cofactor expansion along the first row.
//   It displays the matrix, each 2×2 minor calculation, each signed
//   cofactor term, and the final determinant value step by step.
// ============================================================

public class DeterminantSolver {

    // ── SECTION 1: Matrix Declaration ───────────────────────────────────
    // Matrix assigned to me (#29)
    // Values are hardcoded
    static int[][] matrix = {
        {5, 1, 4},
        {3, 6, 2},
        {4, 2, 5}
    };

    // ── SECTION 2: Main Entry Point ──────────────────────────────────────
    // Execution starts here. We simply call the solver with our matrix.
    public static void main(String[] args) {
        solveDeterminant(matrix);
    }

    // ── SECTION 3: Matrix Printer ────────────────────────────────────────
    // Displays the 3×3 matrix in a bracketed, readable format to the console.
    public static void printMatrix(int[][] m) {
        System.out.println("┌                 ┐");
        for (int[] row : m) {
            String fmt = String.format("│ %3d  %3d  %3d   │", row[0], row[1], row[2]);
            System.out.println(fmt);
        }
        System.out.println("└                 ┘");
    }

    // ── SECTION 4: 2×2 Determinant Helper ───────────────────────────────
    // Calculates the determinant of a 2×2 sub-matrix using the formula ad - bc.
    // This helper is called three times during the cofactor expansion step.
    // Parameters: a, b = first row; c, d = second row of the 2×2 sub-matrix.
    public static int computeMinor(int a, int b, int c, int d) {
        // Standard 2x2 determinant: ad - bc
        return (a * d) - (b * c);
    }

    // ── SECTION 5: Step-by-Step Determinant Solver ──────────────────────
    // Main solving method. It:
    //   1. Prints the assigned matrix clearly
    //   2. Computes each 2×2 minor (M₁₁, M₁₂, M₁₃) and shows the arithmetic
    //   3. Computes and prints each signed cofactor term
    //   4. Sums the cofactors to get the final determinant
    //   5. Checks if the matrix is singular (det = 0)
    public static void solveDeterminant(int[][] m) {
        String line = "=".repeat(52);

        // Print the problem header and matrix
        System.out.println(line);
        System.out.println("  3x3 MATRIX DETERMINANT SOLVER");
        System.out.println("  Student: PARAON, JOSH HEIDRIC C.");
        System.out.println("  Assigned Matrix:");
        System.out.println(line);
        printMatrix(m);
        System.out.println(line);

        // ── Step 1: Minor M₁₁ ──
        // Remove row 0 and column 0; the remaining 2×2 uses indices [1][1],[1][2],[2][1],[2][2]
        int minor11 = computeMinor(m[1][1], m[1][2], m[2][1], m[2][2]);
        System.out.printf(
            "  Step 1 - Minor M11: det([%d,%d],[%d,%d]) = (%d*%d) - (%d*%d) = %d%n",
            m[1][1], m[1][2], m[2][1], m[2][2],
            m[1][1], m[2][2], m[1][2], m[2][1],
            minor11
        );

        // ── Step 2: Minor M₁₂ ──
        // Remove row 0 and column 1; remaining indices: [1][0],[1][2],[2][0],[2][2]
        int minor12 = computeMinor(m[1][0], m[1][2], m[2][0], m[2][2]);
        System.out.printf(
            "  Step 2 - Minor M12: det([%d,%d],[%d,%d]) = (%d*%d) - (%d*%d) = %d%n",
            m[1][0], m[1][2], m[2][0], m[2][2],
            m[1][0], m[2][2], m[1][2], m[2][0],
            minor12
        );

        // ── Step 3: Minor M₁₃ ──
        // Remove row 0 and column 2; remaining indices: [1][0],[1][1],[2][0],[2][1]
        int minor13 = computeMinor(m[1][0], m[1][1], m[2][0], m[2][1]);
        System.out.printf(
            "  Step 3 - Minor M13: det([%d,%d],[%d,%d]) = (%d*%d) - (%d*%d) = %d%n",
            m[1][0], m[1][1], m[2][0], m[2][1],
            m[1][0], m[2][1], m[1][1], m[2][0],
            minor13
        );

        // ── Signed Cofactor Terms ──
        // The sign pattern for row 1 is: +C₁₁, -C₁₂, +C₁₃
        int c11 =  m[0][0] * minor11;
        int c12 = -m[0][1] * minor12;
        int c13 =  m[0][2] * minor13;

        System.out.println();
        System.out.printf("  Cofactor C11 = (+1) * %d * %d = %d%n", m[0][0], minor11, c11);
        System.out.printf("  Cofactor C12 = (-1) * %d * %d = %d%n", m[0][1], minor12, c12);
        System.out.printf("  Cofactor C13 = (+1) * %d * %d = %d%n", m[0][2], minor13, c13);

        // ── Final Determinant ──
        // Sum all three cofactor terms to get det(M)
        int det = c11 + c12 + c13;
        System.out.println();
        System.out.printf("  det(M) = %d + (%d) + %d%n", c11, c12, c13);
        System.out.println(line);
        System.out.printf("  DETERMINANT = %d%n", det);

        // ── Singular Matrix Check ──
        // If the determinant is zero, the matrix cannot be inverted
        if (det == 0) {
            System.out.println("  The matrix is SINGULAR, it has no inverse.");
        }
        System.out.println(line);
    }
}