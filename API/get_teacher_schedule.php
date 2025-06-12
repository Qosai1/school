<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

require_once 'db_connection.php';

if (!isset($_GET['teacher_id'])) {
    echo json_encode([
        "status" => "error",
        "message" => "Missing teacher_id"
    ]);
    exit;
}

$teacher_id = $_GET['teacher_id'];

$sql = "SELECT day, lecture_number, class_name
        FROM teacher_schedule
        WHERE teacher_id = ?
        ORDER BY FIELD(day, 'Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday'), lecture_number";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $teacher_id);
$stmt->execute();
$result = $stmt->get_result();

$data = [];

while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}

$stmt->close();
$conn->close();

echo json_encode([
    "status" => "success",
    "data" => $data
]);
?>
