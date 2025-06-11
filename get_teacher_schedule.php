<?php

header('Content-Type: application/json; charset=UTF-8');


include "db_connection.php";

try {
 
    if (
        !isset($_GET['teacher_id']) ||
        !is_numeric($_GET['teacher_id'])
    ) {
        throw new Exception('Invalid or missing teacher ID');
    }
    $teacher_id = (int) $_GET['teacher_id'];

    $sql = "
      SELECT day, lecture_number, class_name
      FROM teacher_schedule
      WHERE teacher_id = :tid
      ORDER BY
        FIELD(day, 'Sunday','Monday','Tuesday','Wednesday','Thursday'),
        lecture_number
    ";
    $stmt = $conn->prepare($sql);
    $stmt->execute([':tid' => $teacher_id]);
    $entries = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        'status' => 'success',
        'data'   => $entries
    ], JSON_UNESCAPED_UNICODE);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status'  => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ], JSON_UNESCAPED_UNICODE);

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode([
        'status'  => 'error',
        'message' => $e->getMessage()
    ], JSON_UNESCAPED_UNICODE);
}