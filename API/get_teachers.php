<?php
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json; charset=UTF-8');

require_once 'db_connection.php';

$sql = "SELECT teacher_id, name FROM teachers";
$result = $conn->query($sql);

$teachers = [];

if ($result && $result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $teachers[] = $row;
    }

    echo json_encode([
        "status" => "success",
        "data" => $teachers
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "No teachers found"
    ]);
}

$conn->close();
?>
