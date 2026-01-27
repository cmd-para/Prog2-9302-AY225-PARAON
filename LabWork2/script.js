// Predefined username and password combinations
const VALID_CREDENTIALS = [
    { username: "admin", password: "password123" },
    { username: "josh", password: "joshpogi123" },
    { username: "sirval", password: "passwordnisirval" },
    { username: "teacher", password: "teacher123" },
    { username: "inkleto", password: "seanpogi123" }
];

// Attendance records array
let attendanceRecords = [];

// Function to validate credentials against valid combinations
function isValidCredentials(username, password) {
    return VALID_CREDENTIALS.some(cred => 
        cred.username === username && cred.password === password
    );
}

// Initialize the login form
document.addEventListener('DOMContentLoaded', function() {
    // Ensure login page is shown on page load/refresh
    showLoginPage();
    
    const loginForm = document.getElementById('loginForm');
    const messageDiv = document.getElementById('message');
    const timestampDiv = document.getElementById('timestamp');
    const logoutButton = document.getElementById('logoutButton');

    // Logout button event listener
    logoutButton.addEventListener('click', function() {
        showLoginPage();
    });

    loginForm.addEventListener('submit', function(e) {
        e.preventDefault();

        // Get input values
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        // Clear previous messages
        messageDiv.innerHTML = '';
        messageDiv.className = '';
        timestampDiv.innerHTML = '';

        // Validate credentials
        if (isValidCredentials(username, password)) {
            // Login successful
            const timestamp = getCurrentTimestamp();
            
            // Add to attendance records
            attendanceRecords.push({
                username: username,
                timestamp: timestamp
            });

            // Hide login page and show welcome page
            showWelcomePage(username, timestamp);

            // Generate attendance summary file
            generateAttendanceFile();
        } else {
            // Login failed - display error message
            messageDiv.innerHTML = 'Invalid username or password. Please try again.';
            messageDiv.className = 'message error';
            // Play beep sound
            playBeepSound();
        }
    });
});

// Function to show login page
function showLoginPage() {
    const loginContainer = document.getElementById('loginContainer');
    const welcomeContainer = document.getElementById('welcomeContainer');
    
    loginContainer.classList.remove('hidden');
    welcomeContainer.classList.add('hidden');
    
    // Clear form
    document.getElementById('loginForm').reset();
    document.getElementById('message').innerHTML = '';
    document.getElementById('message').className = '';
    document.getElementById('timestamp').innerHTML = '';
}

// Function to show welcome page
function showWelcomePage(username, timestamp) {
    const loginContainer = document.getElementById('loginContainer');
    const welcomeContainer = document.getElementById('welcomeContainer');
    const welcomeUsername = document.getElementById('welcomeUsername');
    const welcomeTimestamp = document.getElementById('welcomeTimestamp');
    
    // Hide login page
    loginContainer.classList.add('hidden');
    
    // Show welcome page
    welcomeContainer.classList.remove('hidden');
    welcomeUsername.innerHTML = `<p style="font-size: 18px; color: #333; margin: 20px 0;">Welcome, <strong>${username}</strong>!</p>`;
    welcomeTimestamp.innerHTML = `<strong>Login Time:</strong> ${timestamp}`;
}

// Function to get current system time in readable format
function getCurrentTimestamp() {
    const now = new Date();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const year = now.getFullYear();
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');
    
    return `${month}/${day}/${year} ${hours}:${minutes}:${seconds}`;
}

// Function to play beep sound when login fails
function playBeepSound() {
    try {
        const beep = new Audio('beep.mp3');
        beep.volume = 0.3; // Reduce volume to 30%
        beep.play().catch(function(error) {
            console.log('Could not play beep sound:', error);
            // Fallback: Use Web Audio API to generate a beep if file is not found
            generateBeepSound();
        });
    } catch (error) {
        console.log('Error loading beep sound:', error);
        // Fallback: Use Web Audio API to generate a beep
        generateBeepSound();
    }
}

// Fallback function to generate a beep sound using Web Audio API
function generateBeepSound() {
    try {
        const audioContext = new (window.AudioContext || window.webkitAudioContext)();
        const oscillator = audioContext.createOscillator();
        const gainNode = audioContext.createGain();

        oscillator.connect(gainNode);
        gainNode.connect(audioContext.destination);

        oscillator.frequency.value = 800; // Beep frequency in Hz
        oscillator.type = 'sine';

        gainNode.gain.setValueAtTime(0.1, audioContext.currentTime); // Reduced volume to 10%
        gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.3);

        oscillator.start(audioContext.currentTime);
        oscillator.stop(audioContext.currentTime + 0.3);
    } catch (error) {
        console.log('Could not generate beep sound:', error);
    }
}

// Function to generate attendance summary file
function generateAttendanceFile() {
    let attendanceData = "";
    
    // Add each record in plain text format
    attendanceRecords.forEach((record) => {
        attendanceData += "Username: " + record.username + "\n";
        attendanceData += "Timestamp: " + record.timestamp + "\n";
        attendanceData += "---\n\n";
    });

    // Create blob and trigger download
    const blob = new Blob([attendanceData], { type: 'text/plain' });
    const link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = 'attendance_summary.txt';
    link.click();
}
