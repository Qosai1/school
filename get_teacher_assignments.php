<?php
// get_teacher_assignments.php
include 'db_connection.php';

$teacher_id = $_GET['teacher_id'];

$sql = "SELECT a.assignment_id, a.title, a.description, a.due_date, a.attachment, s.subject_name 
        FROM assignments a
        JOIN subjects s ON a.subject_id = s.subject_id
        WHERE a.teacher_id = ?";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $teacher_id);
$stmt->execute();
$result = $stmt->get_result();

$assignments = array();

while ($row = $result->fetch_assoc()) {
    $assignments[] = $row;
}

header('Content-Type: application/json');
echo json_encode($assignments);
?>
