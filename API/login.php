<?php 
header("Access-Control-Allow-Origin: *"); 
header("Content-Type: application/json; charset=UTF-8");  

include "db_connection.php";  

$data = json_decode(file_get_contents("php://input"), true);  

$username = $data['username'] ?? ''; 
$password = $data['password'] ?? '';  

$response = [];  

// استعلام التحقق من اسم المستخدم وكلمة المرور والدور 
$stmt = $conn->prepare("SELECT user_id, role FROM users WHERE username=? AND password=?"); 
$stmt->bind_param("ss", $username, $password); 
$stmt->execute(); 
$result = $stmt->get_result();  

if ($row = $result->fetch_assoc()) {     
    $response['status'] = "success";     
    $response['role'] = $row['role'];     
    $userId = $row['user_id'];      

    // لو الدور مدرس نجيب teacher_id و subject_id من جدول teachers     
    if ($row['role'] === 'teacher') {         
        $stmt2 = $conn->prepare("SELECT teacher_id, name, subject_id FROM teachers WHERE user_id = ?");         
        $stmt2->bind_param("i", $userId);         
        $stmt2->execute();         
        $result2 = $stmt2->get_result();          

        if ($row2 = $result2->fetch_assoc()) {             
            $response['teacher_id'] = $row2['teacher_id'];             
            $response['teacher_name'] = $row2['name'] ?? '';
            $response['subject_id'] = $row2['subject_id'] ?? null;

        } else {             
            $response['teacher_id'] = null;             
            $response['teacher_name'] = '';
            $response['subject_id'] = null;

        }         
        $stmt2->close();     
    }          

    // لو الدور طالب نجيب student_id و classID من جدول students     
    else if ($row['role'] === 'student') {         
        $stmt3 = $conn->prepare("SELECT student_id, name, classID FROM students WHERE user_id = ?");         
        $stmt3->bind_param("i", $userId);         
        $stmt3->execute();         
        $result3 = $stmt3->get_result();          

        if ($row3 = $result3->fetch_assoc()) {             
            $response['student_id'] = $row3['student_id'];             
            $response['student_name'] = $row3['name'] ?? '';   
            $response['classID'] = $row3['classID']; 

            // إضافة اسم الصف من جدول class
            $classID = $row3['classID'];
            $stmt4 = $conn->prepare("SELECT className FROM class WHERE classID = ?");
            $stmt4->bind_param("i", $classID);
            $stmt4->execute();
            $result4 = $stmt4->get_result();

            if ($row4 = $result4->fetch_assoc()) {
                $response['class_name'] = trim($row4['className']);
            } else {
                $response['class_name'] = "";
            }

            $stmt4->close();

        } else {             
            $response['student_id'] = null;             
            $response['student_name'] = '';    
            $response['classID'] = null;
            $response['class_name'] = "";
        }         
        $stmt3->close();     
    }          

    else if ($row['role'] === 'registrar') {         
        $response['registrar_id'] = $userId;     
    }      

} else {     
    $response['status'] = "fail";     
    $response['message'] = "Invalid username or password"; 
}  

echo json_encode($response);  

$stmt->close(); 
$conn->close(); 
?>
