<?php
header('Content-Type: application/json; charset=utf-8');

require_once 'db_connection.php';

if (!isset($_GET["class_name"]) || empty(trim($_GET["class_name"]))) {
    echo json_encode(["status" => "error", "message" => "Missing class_name"]);
    exit;
}

$class_name = trim($_GET["class_name"]);

$stmt = $conn->prepare("SELECT COUNT(*) FROM schedules WHERE class_name = ?");
$stmt->bind_param("s", $class_name);
$stmt->execute();
$stmt->bind_result($count);
$stmt->fetch();
$stmt->close();

if ($count > 0) {
    echo json_encode(["status" => "exists"]);
} else {
    echo json_encode(["status" => "not_exists"]);
}
?>
