<?php

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: access");
header("Access-Control-Allow-Methods: POST");
header("Content-Type: application/json; charset=UTF-8");

require __DIR__ . '/db_connection.php';

$data = json_decode(file_get_contents("php://input"));

if (isset($data->student_id) && isset($data->subject_id)) {
    $student_id = $data->student_id;
    $subject_id = $data->subject_id;

    try {
        $query = "SELECT mark_type, mark_value 
                  FROM marks 
                  WHERE student_id = :student_id AND subject_id = :subject_id";
                  
        $stmt = $pdo->prepare($query);
        $stmt->execute([
            ':student_id' => $student_id,
            ':subject_id' => $subject_id
        ]);

        $marks = $stmt->fetchAll(PDO::FETCH_ASSOC);

        if ($marks) {
            echo json_encode(["success" => 1, "marks" => $marks]);
        } else {
            echo json_encode(["success" => 0, "msg" => "لا توجد علامات لهذا الطالب في هذه المادة."]);
        }

    } catch (PDOException $e) {
        echo json_encode(["success" => 0, "msg" => "خطأ في قاعدة البيانات: " . $e->getMessage()]);
    }

} else {
    echo json_encode(["success" => 0, "msg" => "يرجى إرسال student_id و subject_id"]);
}
?>
