<?php
header('Content-Type: application/json; charset=utf-8');
include "db_connection.php";


if (!isset($_GET['class_id']) || !is_numeric($_GET['class_id'])) {
    echo json_encode([
        'status'  => 'error',
        'message' => 'Missing or invalid class_id'
    ]);
    exit;
}
$class_id = intval($_GET['class_id']);

$sql = "
    SELECT 
        sd.day, 
        sd.lecture_number, 
        sd.subject_name
    FROM schedule_details AS sd
    JOIN schedules       AS sch ON sd.schedule_id = sch.schedule_id
    JOIN `class`        AS c   ON sch.class_name = c.className
    WHERE c.classID = ?
    ORDER BY 
      FIELD(sd.day, 'Sunday','Monday','Tuesday','Wednesday','Thursday'),
      sd.lecture_number
";

$stmt = $conn->prepare($sql);
if (!$stmt) {
    $err = $conn->errorInfo();
    echo json_encode([
        'status'  => 'error',
        'message' => 'Database prepare error: ' . ($err[2] ?? 'Unknown')
    ]);
    exit;
}

$stmt->execute([$class_id]);
$schedule = $stmt->fetchAll(PDO::FETCH_ASSOC);

echo json_encode([
    'status' => 'ok',
    'data'   => $schedule
]);