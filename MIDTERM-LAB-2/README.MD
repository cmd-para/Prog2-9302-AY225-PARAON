# uphsd-cs-paraon-joshheidric

## Student Information
- **Name:** PARAON, JOSH HEIDRIC C.
- **Section:** BSIT-GD
- **Course:** Math 101 – Linear Algebra | Programming 2
- **School:** University of Perpetual Help System DALTA – Molino Campus
- **Academic Year:** 2025–2026

---

## Assignment: Programming Assignment 1 – 3×3 Matrix Determinant Solver

### Assigned Matrix (Student #29)

```
| 5  1  4 |
| 3  6  2 |
| 4  2  5 |
```

---

## Repository Structure

```
uphsd-cs-paraon-joshheidric/
├── linear-algebra/
│   └── assignment-01/
│       ├── DeterminantSolver.java      ← Java solution
│       ├── determinant_solver.js       ← JavaScript solution
│       └── README.md                   ← This file
└── README.md                           ← Root README
```

---

## How to Run

### Java
```bash
javac DeterminantSolver.java
java DeterminantSolver
```

### JavaScript (Node.js)
```bash
node determinant_solver.js
```

---

## Sample Output

```
====================================================
  3x3 MATRIX DETERMINANT SOLVER
  Student: PARAON, JOSH HEIDRIC C.
  Assigned Matrix:
====================================================
┌               ┐
│   5    1    4  │
│   3    6    2  │
│   4    2    5  │
└               ┘
====================================================

Expanding along Row 1 (cofactor expansion):

  Step 1 — Minor M₁₁: det([6,2],[2,5]) = (6×5) - (2×2) = 26
  Step 2 — Minor M₁₂: det([3,2],[4,5]) = (3×5) - (2×4) = 7
  Step 3 — Minor M₁₃: det([3,6],[4,2]) = (3×2) - (6×4) = -18

  Cofactor C₁₁ = (+1) × 5 × 26 = 130
  Cofactor C₁₂ = (-1) × 1 × 7 = -7
  Cofactor C₁₃ = (+1) × 4 × -18 = -72

  det(M) = 130 + (-7) + (-72)
====================================================
  ✓  DETERMINANT = 51
====================================================
```

---

## Final Determinant Value

**det(M) = 51**

The matrix is **non-singular** (invertible), since the determinant is not zero.