<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");
header("Content-Type: application/json; charset=UTF-8");

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}


$data = json_decode(file_get_contents("php://input"), true);

// التحقق من الحقول المطلوبة
if (
    isset($data['name']) && isset($data['email']) &&
    isset($data['phone']) && isset($data['birth_date']) &&
    isset($data['gender']) && isset($data['address']) &&
    isset($data['classID'])
) {
    $name = trim($data['name']);
    $email = trim($data['email']);
    $phone = trim($data['phone']);
    $birth_date = $data['birth_date'];
    $gender = $data['gender'];
    $address = trim($data['address']);
    $classID = $data['classID'];

    $conn = new mysqli("localhost", "root", "", "schooll");

    if ($conn->connect_error) {
        echo json_encode(["error" => "Database connection failed", "details" => $conn->connect_error]);
        exit();
    }

    $conn->autocommit(FALSE);

    try {
       $username = strtolower(str_replace(' ', '', $name));
        
        $checkUser = $conn->prepare("SELECT user_id FROM users WHERE username = ?");
        $checkUser->bind_param("s", $username);
        $checkUser->execute();
        $result = $checkUser->get_result();
        
        $counter = 1;

        $originalUsername = $username;
        while ($result->num_rows > 0) {
            $username = $originalUsername . $counter;
            $checkUser->bind_param("s", $username);
            $checkUser->execute();
            $result = $checkUser->get_result();
            $counter++;
        }
        $checkUser->close();

        // 1. إضافة المستخدم إلى جدول users أولاً
        $password = "123321"; // كلمة المرور الافتراضية
        $role = "student"; // نوع المستخدم
        
        $insertUser = $conn->prepare("INSERT INTO users (username, password, role) VALUES (?, ?, ?)");
        $insertUser->bind_param("sss", $username, $password, $role);
        
        if (!$insertUser->execute()) {
            throw new Exception("Failed to create user account: " . $insertUser->error);
        }
        
        // الحصول على user_id الذي تم إنشاؤه
        $user_id = $conn->insert_id;
        $insertUser->close();

        // 2. تحديث reference_id في جدول users (سيتم تحديثه بعد إضافة الطالب)
        
        // 3. إضافة الطالب إلى جدول students مع user_id
        $insertStudent = $conn->prepare("INSERT INTO students (name, email, phone, birth_date, gender, address, classID, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        $insertStudent->bind_param("ssssssii", $name, $email, $phone, $birth_date, $gender, $address, $classID, $user_id);
        
        if (!$insertStudent->execute()) {
            throw new Exception("Failed to add student: " . $insertStudent->error);
        }
        
        // الحصول على student_id الذي تم إنشاؤه
        $student_id = $conn->insert_id;
        $insertStudent->close();

        // 4. تحديث reference_id في جدول users ليشير إلى student_id
        $updateUser = $conn->prepare("UPDATE users SET reference_id = ? WHERE user_id = ?");
        $updateUser->bind_param("ii", $student_id, $user_id);
        
        if (!$updateUser->execute()) {
            throw new Exception("Failed to update user reference: " . $updateUser->error);
        }
        $updateUser->close();

        // تأكيد المعاملة (Commit Transaction)
        $conn->commit();
        
        echo json_encode([
            "success" => true,
            "message" => "Student and user account added successfully",
            "student_id" => $student_id,
            "user_id" => $user_id,
            "username" => $username,
            "password" => $password
        ]);

    } catch (Exception $e) {
        // إلغاء المعاملة في حالة الخطأ (Rollback Transaction)
        $conn->rollback();
        echo json_encode([
            "error" => "Transaction failed",
            "details" => $e->getMessage()
        ]);
    }

    $conn->close();

} else {
    // إظهار الحقول المفقودة للمساعدة في التشخيص
    $missing_fields = [];
    $required_fields = ['name', 'email', 'phone', 'birth_date', 'gender', 'address', 'classID'];
    
    foreach ($required_fields as $field) {
        if (!isset($data[$field]) || empty(trim($data[$field]))) {
            $missing_fields[] = $field;
        }
    }
    
    echo json_encode([
        "error" => "Missing required fields",
        "missing_fields" => $missing_fields,
        "received_data" => $data
    ]);
}
?>