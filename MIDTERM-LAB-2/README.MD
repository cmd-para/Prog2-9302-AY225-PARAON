# Midterm Lab Work 2 - 3x3 Matrix Determinant Solver

## Student Information
- **Name:** PARAON, JOSH HEIDRIC C.
- **Section:** BSIT-GD
- **Course:** Programming 2
- **Section:** 9302-AY225
- **School:** University of Perpetual Help System DALTA – Molino

---

## Assignment: Midterm Lab Work 2 - 3x3 Matrix Determinant Solver

### Assigned Matrix (#29)

```
| 5  1  4 |
| 3  6  2 |
| 4  2  5 |
```

---

## Repository Structure

```
└── Prog2-9302-AY225-PARAON/
    ├── Java/
    │   └── DeterminantSolver.java <-- Java file
    ├── JS/
    │   └── DeterminantSolver.js   <-- JavaScript file
    └── README.md

```

---

## How to Run

### Java
Run with Visual Studio Code using "Run Java"  button.

### JavaScript (Node.js)
```bash
node DeterminantSolver.js
```

---

## Sample Output

```
====================================================
  3x3 MATRIX DETERMINANT SOLVER
  Student: PARAON, JOSH HEIDRIC C.
  Assigned Matrix:
====================================================
┌                ┐
│   5    1    4  │
│   3    6    2  │
│   4    2    5  │
└                ┘
====================================================
  Step 1 - Minor M11: det([6,2],[2,5]) = (6*5) - (2*2) = 26
  Step 2 - Minor M12: det([3,2],[4,5]) = (3*5) - (2*4) = 7
  Step 3 - Minor M13: det([3,6],[4,2]) = (3*2) - (6*4) = -18

  Cofactor C11 = (+1) * 5 * 26 = 130
  Cofactor C12 = (-1) * 1 * 7 = -7
  Cofactor C13 = (+1) * 4 * -18 = -72

  det(M) = 130 + (-7) + -72
====================================================
  DETERMINANT = 51
====================================================
```

---

## Final Determinant Value

**det(M) = 51**

The matrix is **non-singular** (invertible), since the determinant is not zero.