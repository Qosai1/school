<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

// استلام البيانات من JSON
$data = json_decode(file_get_contents("php://input"), true);

// التحقق من الحقول المطلوبة فقط (بدون major و profile_picture)
if (
    isset($data['name']) && isset($data['email']) &&
    isset($data['phone']) && isset($data['birth_date']) &&
    isset($data['gender']) && isset($data['address'])
) {
    $name = $data['name'];
    $email = $data['email'];
    $phone = $data['phone'];
    $birth_date = $data['birth_date'];
    $gender = $data['gender'];
    $address = $data['address'];

    // الاتصال بقاعدة البيانات
    $conn = new mysqli("localhost", "root", "", "school");

    if ($conn->connect_error) {
        echo json_encode(["error" => "Database connection failed"]);
        exit();
    }

    // إدخال البيانات بدون major و profile_picture
    $stmt = $conn->prepare("INSERT INTO students (name, email, phone, birth_date, gender, address) VALUES (?, ?, ?, ?, ?, ?)");
    $stmt->bind_param("ssssss", $name, $email, $phone, $birth_date, $gender, $address);

    if ($stmt->execute()) {
        echo json_encode(["message" => "Student added successfully"]);
    } else {
        echo json_encode(["error" => "Insert failed", "details" => $stmt->error]);
    }

    $stmt->close();
    $conn->close();
} else {
    echo json_encode(["error" => "Missing required fields"]);
}
?>