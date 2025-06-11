<?php
$host = "localhost";
$db = "schooll";
$user = "root";
$pass = "";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    http_response_code(500); // فقط نرسل كود خطأ بدون JSON
    exit;
}
?>
