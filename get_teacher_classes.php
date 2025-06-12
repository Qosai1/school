<?php
header('Content-Type: application/json');
include "db_connection.php";

try {
    // التحقق من وجود teacher_id
    if (!isset($_POST['teacher_id']) || empty($_POST['teacher_id'])) {
        echo json_encode(['error' => 'teacher_id is required']);
        exit;
    }

    $teacher_id = intval($_POST['teacher_id']);

    // التحقق من صحة teacher_id
    if ($teacher_id <= 0) {
        echo json_encode(['error' => 'Invalid teacher_id']);
        exit;
    }

    // استعلام لجلب الصفوف التي يدرسها المدرس
    $sql = "SELECT DISTINCT c.classID as id, c.className as name 
            FROM teachers t 
            JOIN teacher2class tc ON t.teacher_id = tc.teacher_id 
            JOIN class c ON tc.classID = c.classID 
            WHERE t.teacher_id = ?";

    $stmt = $conn->prepare($sql);
    
    if (!$stmt) {
        echo json_encode(['error' => 'Database prepare error: ' . $conn->error]);
        exit;
    }

    $stmt->bind_param("i", $teacher_id);
    
    if (!$stmt->execute()) {
        echo json_encode(['error' => 'Query execution error: ' . $stmt->error]);
        exit;
    }

    $result = $stmt->get_result();
    $classes = [];

    while ($row = $result->fetch_assoc()) {
        $classes[] = [
            'id' => intval($row['id']),
            'name' => $row['name']
        ];
    }

    // إذا لم توجد صفوف، أرجع array فارغ
    echo json_encode($classes);

    $stmt->close();
    $conn->close();

} catch (Exception $e) {
    echo json_encode(['error' => 'Server error: ' . $e->getMessage()]);
}
?>