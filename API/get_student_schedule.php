
<?php
header('Content-Type: application/json; charset=utf-8');
include "db_connection.php";

if (!isset($_GET['class_name'])) {
    echo json_encode(["status" => "error", "message" => "Missing class_name"]);
    exit;
}

$class_name = $_GET['class_name'];

$sql = "SELECT s.day, s.lecture_number, s.subject_name
        FROM schedule_details s
        JOIN schedules sch ON s.schedule_id = sch.schedule_id
        WHERE sch.class_name = ?";

$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $class_name);
$stmt->execute();

$result = $stmt->get_result();
$data = [];

while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}

$stmt->close();

echo json_encode([
    "status" => "success",
    "data" => $data
]);
?>
