<?php
header('Content-Type: application/json; charset=UTF-8');

include "db_connection.php";


try {
    
    $stmt = $conn->query("SELECT teacher_id, name FROM teachers");
    $teachers = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        'status' => 'success',
        'data'   => $teachers
    ]);
} catch (PDOException $e) {

    echo json_encode([
        'status'  => 'error',
        'message' => 'Query error: ' . $e->getMessage()
    ]);
    exit();
}