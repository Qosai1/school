<?php
// ملف: file_handler.php
// هذا الملف للتعامل مع عرض وتحميل الملفات المرفوعة

header('Content-Type: application/json; charset=utf-8');

if (!isset($_GET['file']) || empty($_GET['file'])) {
    http_response_code(400);
    echo json_encode(["error" => "File parameter is required"]);
    exit;
}

$filename = $_GET['file'];
$uploadDir = __DIR__ . '/uploads/assignments/';
$filePath = $uploadDir . $filename;

// التحقق من وجود الملف
if (!file_exists($filePath)) {
    http_response_code(404);
    echo json_encode([
        "error" => "File not found",
        "requested_file" => $filename,
        "full_path" => $filePath,
        "upload_dir_exists" => is_dir($uploadDir)
    ]);
    exit;
}

// التحقق من أن الملف داخل مجلد الرفع (أمان)
$realPath = realpath($filePath);
$realUploadDir = realpath($uploadDir);

if (strpos($realPath, $realUploadDir) !== 0) {
    http_response_code(403);
    echo json_encode(["error" => "Access denied"]);
    exit;
}

// الحصول على معلومات الملف
$fileInfo = pathinfo($filePath);
$mimeType = mime_content_type($filePath);
$fileSize = filesize($filePath);

// تحديد Content-Type بناء على نوع الملف
switch (strtolower($fileInfo['extension'])) {
    case 'pdf':
        header('Content-Type: application/pdf');
        break;
    case 'doc':
        header('Content-Type: application/msword');
        break;
    case 'docx':
        header('Content-Type: application/vnd.openxmlformats-officedocument.wordprocessingml.document');
        break;
    case 'jpg':
    case 'jpeg':
        header('Content-Type: image/jpeg');
        break;
    case 'png':
        header('Content-Type: image/png');
        break;
    case 'txt':
        header('Content-Type: text/plain');
        break;
    default:
        header('Content-Type: application/octet-stream');
}

// إعداد headers لعرض الملف في المتصفح
header('Content-Length: ' . $fileSize);
header('Content-Disposition: inline; filename="' . $fileInfo['basename'] . '"');
header('Cache-Control: public, max-age=3600');

// قراءة وإرسال الملف
readfile($filePath);
exit;
?>