<?php

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: access");
header("Access-Control-Allow-Methods: POST");
header("Content-Type: application/json; charset=UTF-8");

// استدعاء ملف الاتصال
require __DIR__ . '/db_connection.php';

class AddTeacher {

    private $pdo;

    public function __construct($pdo) {
        $this->pdo = $pdo;
    }

    public function insertTeacher($data) {
        if (
            isset($data->teacher_name) &&
            isset($data->teacher_email) &&
            isset($data->teacher_password)
        ) {
            $name = $data->teacher_name;
            $email = $data->teacher_email;
            $password = password_hash($data->teacher_password, PASSWORD_DEFAULT);

            try {
                $query = "INSERT INTO teachers (teacher_name, teacher_email, teacher_password) 
                          VALUES (:name, :email, :password)";
                $stmt = $this->pdo->prepare($query);
                $stmt->execute([
                    ":name" => $name,
                    ":email" => $email,
                    ":password" => $password
                ]);

                return ["success" => 1, "msg" => "تمت إضافة المعلم بنجاح"];
            } catch (PDOException $e) {
                return ["success" => 0, "msg" => "خطأ أثناء الإدخال: " . $e->getMessage()];
            }
        } else {
            return ["success" => 0, "msg" => "الرجاء تعبئة جميع الحقول المطلوبة"];
        }
    }
}

// تنفيذ العملية
$data = json_decode(file_get_contents("php://input"));

$teacherHandler = new AddTeacher($pdo);
$response = $teacherHandler->insertTeacher($data);
echo json_encode($response);

?>
