<?php
header('Content-Type: application/json; charset=utf-8');
include "db_connection.php";


if (!isset($_GET['class_name'])) {
    echo json_encode([
        "status" => "error",
        "message" => "Missing class_name parameter"
    ]);
    exit();
}

$class_name = $_GET['class_name'];

try {
    $stmt = $conn->prepare("SELECT COUNT(*) FROM schedules WHERE class_name = ?");
    $stmt->execute([$class_name]);
    $count = $stmt->fetchColumn();

    if ($count > 0) {
        echo json_encode(["status" => "exists"]);
    } else {
        echo json_encode(["status" => "not_exists"]);
    }

} catch (PDOException $e) {
    echo json_encode([
        "status" => "error",
        "message" => "Database error",
        "details" => $e->getMessage()
    ]);

}
?>