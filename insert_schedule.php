<?php
header('Content-Type: application/json; charset=utf-8');
include "db_connection.php";
try {
    $input = file_get_contents("php://input");
    $data = json_decode($input, true);

    if (!$data || !is_array($data)) {
        echo json_encode(["status" => "error", "message" => "Invalid or no data received"]);
        exit();
    }

    $conn->beginTransaction();

    $class = $data[0]["class"];
    $created_at = date("Y-m-d H:i:s");

    $stmtSchedule = $conn->prepare("INSERT INTO schedules (class_name, created_at) VALUES (:class_name, :created_at)");
    $stmtSchedule->execute([':class_name' => $class, ':created_at' => $created_at]);
    $schedule_id = $conn->lastInsertId();

    $stmtDetail = $conn->prepare("INSERT INTO schedule_details (schedule_id, day, lecture_number, subject_name) VALUES (:schedule_id, :day, :lecture_number, :subject_name)");

    foreach ($data as $item) {
        $day = $item["day"];
        foreach ($item["subjects"] as $index => $subject) {
            $lecture_number = $index + 1;
            $stmtDetail->execute([
                ':schedule_id' => $schedule_id,
                ':day' => $day,
                ':lecture_number' => $lecture_number,
                ':subject_name' => $subject["subject_name"]
            ]);
        }
    }

    $conn->commit();
    echo json_encode(["status" => "success", "message" => "Schedule saved successfully"]);
} catch (Exception $e) {
    if (isset($conn)) {
        $conn->rollBack();
    }
    echo json_encode([
        "status" => "error",
        "message" => "Failed to save schedule",
        "details" => $e->getMessage()
    ]);
}
?>