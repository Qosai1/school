<?php

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: access");
header("Access-Control-Allow-Methods: POST");
header("Content-Type: application/json; charset=UTF-8");

require __DIR__ . '/db_connection.php';

$data = json_decode(file_get_contents("php://input"));

if (
    isset($data->subject_name) && 
    isset($data->subject_code) && 
    isset($data->subject_time) &&
    isset($data->subject_location) &&
    isset($data->teacher_id)
) {
    $subject_name = $data->subject_name;
    $subject_code = $data->subject_code;
    $subject_time = $data->subject_time;
    $subject_location = $data->subject_location;
    $teacher_id = $data->teacher_id;

    $insert = "INSERT INTO subjects(subject_name, subject_code, subject_time, subject_location, teacher_id) 
               VALUES('$subject_name', '$subject_code', '$subject_time', '$subject_location', '$teacher_id')";

    $result = mysqli_query($db_conn, $insert);

    if ($result) {
        echo json_encode(["success" => 1, "msg" => "Subject inserted successfully"]);
    } else {
        echo json_encode(["success" => 0, "msg" => "Error inserting subject"]);
    }

} else {
    echo json_encode(["success" => 0, "msg" => "Missing required fields"]);
}
?>
