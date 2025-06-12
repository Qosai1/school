<?php
header('Content-Type: application/json; charset=utf-8');
include "db_connection.php";

// قراءة البيانات المرسلة من التطبيق بصيغة JSON
$data = json_decode(file_get_contents("php://input"), true);

if (!$data || !is_array($data)) {
    echo json_encode(["status" => "error", "message" => "Invalid input"]);
    exit;
}

foreach ($data as $entry) {
    $class_name = $entry['class'];
    $day = $entry['day'];
    $subjects = $entry['subjects'];

    // تأكد من وجود سجل للصف داخل جدول schedules، أو أنشئه إذا غير موجود
    $stmt = $conn->prepare("SELECT schedule_id FROM schedules WHERE class_name = ?");
    $stmt->bind_param("s", $class_name);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($row = $result->fetch_assoc()) {
        $schedule_id = $row['schedule_id'];
    } else {
        // إدراج صف جديد في جدول schedules
        $insert_stmt = $conn->prepare("INSERT INTO schedules (class_name) VALUES (?)");
        $insert_stmt->bind_param("s", $class_name);
        $insert_stmt->execute();
        $schedule_id = $insert_stmt->insert_id;
        $insert_stmt->close();
    }
    $stmt->close();

    // إضافة تفاصيل الجدول
    $lecture_number = 1;
    foreach ($subjects as $subject_entry) {
        $subject_name = $subject_entry['subject_name'];

        $insert_detail_stmt = $conn->prepare("INSERT INTO schedule_details (schedule_id, day, lecture_number, subject_name) VALUES (?, ?, ?, ?)");
        $insert_detail_stmt->bind_param("isis", $schedule_id, $day, $lecture_number, $subject_name);
        $insert_detail_stmt->execute();
        $insert_detail_stmt->close();

        $lecture_number++;
    }
}

echo json_encode(["status" => "success", "message" => "Schedule saved"]);
?>

}
?>