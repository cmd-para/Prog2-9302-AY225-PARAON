// Get form and input elements
const form = document.getElementById('gradesForm');
const attendanceInput = document.getElementById('attendanceCount');
const lab1Input = document.getElementById('lab1Grade');
const lab2Input = document.getElementById('lab2Grade');
const lab3Input = document.getElementById('lab3Grade');

// Create results display area
const resultsDiv = document.createElement('div');
resultsDiv.id = 'results';
resultsDiv.className = 'results';
document.querySelector('.container').appendChild(resultsDiv);

// Add event listeners to all inputs
[attendanceInput, lab1Input, lab2Input, lab3Input].forEach(input => {
  input.addEventListener('input', calculateGrades);
});

function calculateGrades() {
  // Get input values
  let attendanceCount = parseFloat(attendanceInput.value) || 0;
  let lab1 = parseFloat(lab1Input.value) || 0;
  let lab2 = parseFloat(lab2Input.value) || 0;
  let lab3 = parseFloat(lab3Input.value) || 0;

  // Enforce attendance limit (0-16)
  if (attendanceCount < 0) {
    attendanceCount = 0;
    attendanceInput.value = 0;
  } else if (attendanceCount > 16) {
    attendanceCount = 16;
    attendanceInput.value = 16;
  }

  // Enforce lab work grade limits (0-100)
  if (lab1 < 0) {
    lab1 = 0;
    lab1Input.value = 0;
  } else if (lab1 > 100) {
    lab1 = 100;
    lab1Input.value = 100;
  }

  if (lab2 < 0) {
    lab2 = 0;
    lab2Input.value = 0;
  } else if (lab2 > 100) {
    lab2 = 100;
    lab2Input.value = 100;
  }

  if (lab3 < 0) {
    lab3 = 0;
    lab3Input.value = 0;
  } else if (lab3 > 100) {
    lab3 = 100;
    lab3Input.value = 100;
  }

  // Check if all inputs have values
  if (!attendanceInput.value || !lab1Input.value || !lab2Input.value || !lab3Input.value) {
    resultsDiv.innerHTML = '<p class="info">Please enter all values to see results.</p>';
    return;
  }

  // Validate attendance range
  if (attendanceCount < 0 || attendanceCount > 16) {
    resultsDiv.innerHTML = '<p class="warning">⚠️ Attendance must be between 0 and 16.</p>';
    return;
  }

  // Validate lab grades range
  if (lab1 < 0 || lab1 > 100 || lab2 < 0 || lab2 > 100 || lab3 < 0 || lab3 > 100) {
    resultsDiv.innerHTML = '<p class="warning">⚠️ Lab work grades must be between 0 and 100.</p>';
    return;
  }

  // Calculate Lab Work Average
  const labWorkAverage = (lab1 + lab2 + lab3) / 3;

  // Calculate Attendance Score (16 attendances = 100%)
  const maxAttendances = 16;
  const attendanceScore = (attendanceCount / maxAttendances) * 100;

  // Calculate Class Standing
  const classStanding = (attendanceScore * 0.40) + (labWorkAverage * 0.60);

  // Calculate Required Prelim Exam Scores
  // Formula: Prelim Grade = (Prelim Exam × 0.70) + (Class Standing × 0.30)
  // Rearranged: Prelim Exam = (Prelim Grade - Class Standing × 0.30) / 0.70

  const passingGrade = 75;
  const excellentGrade = 100;

  const requiredForPassing = (passingGrade - (classStanding * 0.30)) / 0.70;
  const requiredForExcellent = (excellentGrade - (classStanding * 0.30)) / 0.70;

  // Determine student's standing
  let standing = '';
  let standingClass = '';
  
  if (classStanding >= 90) {
    standing = 'Excellent! You have a very strong class standing.';
    standingClass = 'excellent';
  } else if (classStanding >= 75) {
    standing = 'Good! You have a passing class standing.';
    standingClass = 'good';
  } else {
    standing = 'You need to improve your class standing.';
    standingClass = 'needs-improvement';
  }

  // Build results HTML
  let html = '<h2>Results</h2>';
  
  html += '<div class="result-section">';
  html += '<h3>Computed Values</h3>';
  html += `<p><strong>Attendance Score:</strong> ${attendanceScore.toFixed(2)}% (${attendanceCount} out of 16 attendances)</p>`;
  html += `<p><strong>Lab Work 1:</strong> ${lab1.toFixed(2)}</p>`;
  html += `<p><strong>Lab Work 2:</strong> ${lab2.toFixed(2)}</p>`;
  html += `<p><strong>Lab Work 3:</strong> ${lab3.toFixed(2)}</p>`;
  html += `<p><strong>Lab Work Average:</strong> ${labWorkAverage.toFixed(2)}</p>`;
  html += `<p><strong>Class Standing:</strong> ${classStanding.toFixed(2)}</p>`;
  html += '</div>';

  html += '<div class="result-section">';
  html += '<h3>Required Prelim Exam Scores</h3>';
  
  if (requiredForPassing <= 0) {
    html += `<p class="excellent"><strong>To Pass (75):</strong> You've already secured a passing grade! (Required: ${requiredForPassing.toFixed(2)})</p>`;
  } else if (requiredForPassing > 100) {
    html += `<p class="warning"><strong>To Pass (75):</strong> ${requiredForPassing.toFixed(2)} - Unfortunately, passing is mathematically impossible with your current class standing.</p>`;
  } else {
    html += `<p><strong>To Pass (75):</strong> ${requiredForPassing.toFixed(2)}</p>`;
  }

  if (requiredForExcellent <= 0) {
    html += `<p class="excellent"><strong>For Excellent (100):</strong> You've already secured an excellent grade! (Required: ${requiredForExcellent.toFixed(2)})</p>`;
  } else if (requiredForExcellent > 100) {
    html += `<p class="info"><strong>For Excellent (100):</strong> ${requiredForExcellent.toFixed(2)} - This would require more than 100% on the exam.</p>`;
  } else {
    html += `<p><strong>For Excellent (100):</strong> ${requiredForExcellent.toFixed(2)}</p>`;
  }
  html += '</div>';

  html += '<div class="result-section">';
  html += '<h3>Student Standing</h3>';
  html += `<p class="${standingClass}">${standing}</p>`;
  html += '</div>';

  resultsDiv.innerHTML = html;
}

// Initial calculation if there are default values
calculateGrades();