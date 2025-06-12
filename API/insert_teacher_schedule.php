<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

include "db_connection.php";

// استقبال البيانات بصيغة JSON
$data = json_decode(file_get_contents("php://input"), true);

if (!$data || !is_array($data)) {
    echo json_encode(["status" => "fail", "message" => "Invalid input"]);
    exit;
}

// إعداد استعلام الإدخال
$stmt = $conn->prepare("INSERT INTO teacher_schedule (teacher_id, day, lecture_number, class_name) VALUES (?, ?, ?, ?)");

// التحقق من التحضير
if (!$stmt) {
    echo json_encode(["status" => "fail", "message" => "DB Error: " . $conn->error]);
    exit;
}

foreach ($data as $entry) {
    $teacher_id = $entry["teacher_id"];
    $day = $entry["day"];
    $lecture_number = $entry["lecture_number"];
    $class_name = $entry["class_name"];

    $stmt->bind_param("isis", $teacher_id, $day, $lecture_number, $class_name);
    $stmt->execute();
}

$stmt->close();
$conn->close();

echo json_encode(["status" => "success", "message" => "Schedule saved"]);
?>
