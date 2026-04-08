// ============================================================
// Student   : PARAON, JOSH HEIDRIC C.
// Course    : Programming 2 - BSIT-GD 1
// Section   : 9302-AY225
// Assignment: Midterm Lab Work 2 – 3×3 Matrix Determinant Solver
// School    : University of Perpetual Help System DALTA – Molino Campus
// Date      : April 8, 2026
// GitHub    : https://github.com/joshheidric/uphsd-cs-paraon-joshheidric
// Description:
//   This program computes the determinant of a 3×3 matrix assigned
//   to the student using cofactor expansion along the first row.
//   It displays the matrix, each 2×2 minor calculation, each signed
//   cofactor term, and the final determinant value step by step.
//   Run with: node determinant_solver.js
// ============================================================

// ── SECTION 1: Matrix Declaration ───────────────────────────────────
// Matrix assigned to me (#29)
// Values are hardcoded
const matrix = [
    [5, 1, 4],
    [3, 6, 2],
    [4, 2, 5]
];

// ── SECTION 2: Matrix Printer ────────────────────────────────────────
// Prints the 3×3 matrix to the console in a clean bracketed format
// so it's easy to read in the output.
function printMatrix(m) {
    console.log(`┌                ┐`);
    m.forEach(row => {
        const fmt = row.map(v => v.toString().padStart(3)).join("  ");
        console.log(`│ ${fmt}  │`);
    });
    console.log(`└                ┘`);
}

// ── SECTION 3: 2×2 Determinant Helper ───────────────────────────────
// Computes the determinant of a 2×2 matrix given four scalar values.
// Called three times during the cofactor expansion step.
// Parameters: a, b = first row; c, d = second row of the 2×2 sub-matrix.
function computeMinor(a, b, c, d) {
    // 2×2 determinant formula: ad - bc
    return (a * d) - (b * c);
}

// ── SECTION 4: Step-by-Step Determinant Solver ──────────────────────
// Main solving function. It:
//   1. Prints the assigned matrix
//   2. Computes each 2×2 minor (M₁₁, M₁₂, M₁₃) with arithmetic shown
//   3. Prints each signed cofactor term
//   4. Sums the cofactors for the final determinant
//   5. Flags if the matrix is singular (det = 0)
function solveDeterminant(m) {
    const line = "=".repeat(52);

    // Print the problem header and matrix display
    console.log(line);
    console.log("  3x3 MATRIX DETERMINANT SOLVER");
    console.log("  Student: PARAON, JOSH HEIDRIC C.");
    console.log("  Assigned Matrix:");
    console.log(line);
    printMatrix(m);
    console.log(line);

    // ── Step 1: Minor M₁₁ ──
    // Delete row 0 and column 0 → remaining elements: [1][1],[1][2],[2][1],[2][2]
    const minor11 = computeMinor(m[1][1], m[1][2], m[2][1], m[2][2]);
    console.log(
        `  Step 1 - Minor M11: det([${m[1][1]},${m[1][2]}],[${m[2][1]},${m[2][2]}])` +
        ` = (${m[1][1]}*${m[2][2]}) - (${m[1][2]}*${m[2][1]}) = ${minor11}`
    );

    // ── Step 2: Minor M₁₂ ──
    // Delete row 0 and column 1 → remaining: [1][0],[1][2],[2][0],[2][2]
    const minor12 = computeMinor(m[1][0], m[1][2], m[2][0], m[2][2]);
    console.log(
        `  Step 2 - Minor M12: det([${m[1][0]},${m[1][2]}],[${m[2][0]},${m[2][2]}])` +
        ` = (${m[1][0]}*${m[2][2]}) - (${m[1][2]}*${m[2][0]}) = ${minor12}`
    );

    // ── Step 3: Minor M₁₃ ──
    // Delete row 0 and column 2 → remaining: [1][0],[1][1],[2][0],[2][1]
    const minor13 = computeMinor(m[1][0], m[1][1], m[2][0], m[2][1]);
    console.log(
        `  Step 3 - Minor M13: det([${m[1][0]},${m[1][1]}],[${m[2][0]},${m[2][1]}])` +
        ` = (${m[1][0]}*${m[2][1]}) - (${m[1][1]}*${m[2][0]}) = ${minor13}`
    );

    // ── Signed Cofactor Terms ──
    // Row 1 sign pattern: +C₁₁, -C₁₂, +C₁₃ (alternating sign rule)
    const c11 =  m[0][0] * minor11;
    const c12 = -m[0][1] * minor12;
    const c13 =  m[0][2] * minor13;

    console.log();
    console.log(`  Cofactor C11 = (+1) * ${m[0][0]} * ${minor11} = ${c11}`);
    console.log(`  Cofactor C12 = (-1) * ${m[0][1]} * ${minor12} = ${c12}`);
    console.log(`  Cofactor C13 = (+1) * ${m[0][2]} * ${minor13} = ${c13}`);

    // ── Final Determinant ──
    // Add all three cofactor terms to get the determinant
    const det = c11 + c12 + c13;
    console.log();
    console.log(`  det(M) = ${c11} + (${c12}) + ${c13}`);
    console.log(line);
    console.log(`  DETERMINANT = ${det}`);

    // ── Singular Matrix Check ──
    // If det = 0, warn that this matrix has no inverse
    if (det === 0) {
        console.log("  The matrix is SINGULAR, it has no inverse.");
    }
    console.log(line);
}

// ── SECTION 5: Program Entry Point ──────────────────────────────────
// Kick off the solver with my assigned matrix.
solveDeterminant(matrix);