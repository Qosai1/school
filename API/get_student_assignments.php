<?php
header('Content-Type: application/json');
include "db_connection.php";

if (!isset($_POST['classID'])) {
    echo json_encode(["status" => "error", "message" => "Missing class_id"]);
    exit;
}

$class_id = intval($_POST['classID']);

$sql = "SELECT 
            a.assignment_id, 
            a.title, 
            a.description, 
            a.due_date, 
            a.attachment, 
            s.subject_name
        FROM 
            assignments a
        JOIN 
            subjects s ON a.subject_id = s.subject_id
        WHERE 
            a.classID = ?
        ORDER BY 
            a.due_date ASC";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $class_id);
$stmt->execute();

$result = $stmt->get_result();

$assignments = [];

while ($row = $result->fetch_assoc()) {
    $assignments[] = $row;
}

echo json_encode($assignments);

$stmt->close();
$conn->close();
?>