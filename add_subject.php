<?php
// Database configuration
$host = 'localhost';
$dbname = 'schooll';
$username = 'root';
$password = '';

try {
    // Create database connection
    $dsn = "mysql:host=$host;dbname=$dbname;charset=utf8";
    $pdo = new PDO($dsn, $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $pdo->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);
    

    // Check if POST data exists
    if (!isset($_POST['subject_id']) || !isset($_POST['subject_name'])) {
        echo "Missing required fields";
        exit;
    }

    // Get POST data from the Android app
    $subject_id = $_POST['subject_id'];
    $subject_name = $_POST['subject_name'];

    // Validate input
    if (empty($subject_id) || empty($subject_name)) {
        echo "All fields are required";
        exit;
    }

    // Prepare SQL statement
    $sql = "INSERT INTO subjects (subject_id, subject_name) VALUES (:subject_id, :subject_name)";
    $stmt = $pdo->prepare($sql);

    // Bind parameters
    $stmt->bindParam(':subject_id', $subject_id);
    $stmt->bindParam(':subject_name', $subject_name);

    // Execute the statement
    if ($stmt->execute()) {
        echo "New subject added successfully";
    } else {
        echo "Failed to add subject";
    }
} catch (PDOException $e) {
    echo "Database error: " . $e->getMessage();
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
?>