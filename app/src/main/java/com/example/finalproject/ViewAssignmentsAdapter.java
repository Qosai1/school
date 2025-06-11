package com.example.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ViewAssignmentsAdapter extends RecyclerView.Adapter<ViewAssignmentsAdapter.AssignmentViewHolder> {

    private List<Assignment> assignmentList;
    private int student_id;
    private Activity activity;
    private static final int FILE_PICKER_REQUEST = 123;
    private int currentAssignmentId = -1;
    private AssignmentViewHolder currentHolder = null;

    public ViewAssignmentsAdapter(List<Assignment> assignmentList, int student_id, Activity activity) {
        this.assignmentList = assignmentList;
        this.student_id = student_id;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_assignment, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Assignment assignment = assignmentList.get(position);

        holder.subject.setText("subject " + assignment.subject_name);
        holder.title.setText("Address " + assignment.title);
        holder.description.setText("Description " + assignment.description);
        holder.dueDate.setText("Due Date " + assignment.due_date);


        if (assignment.attachment != null && !assignment.attachment.trim().isEmpty()) {
            holder.assignmentAttachmentSection.setVisibility(View.VISIBLE);
            holder.assignmentAttachmentName.setText(getFileNameFromPath(assignment.attachment));


            setFileIcon(holder.assignmentFileIcon, assignment.attachment);


            holder.assignmentAttachmentSection.setOnClickListener(v -> openFile(assignment.attachment));
            holder.btnViewAttachment.setOnClickListener(v -> viewFile(assignment.attachment));
            holder.btnDownloadAttachment.setOnClickListener(v -> downloadFile(assignment.attachment));
        } else {
            holder.assignmentAttachmentSection.setVisibility(View.GONE);
        }


        holder.fileSelectionLayout.setVisibility(View.GONE);
        holder.btnSubmit.setText("اختر ملف");
        holder.btnSubmit.setEnabled(true);

        holder.btnSubmit.setOnClickListener(v -> {
            if (holder.fileSelectionLayout.getVisibility() == View.GONE) {

                currentAssignmentId = assignment.assignment_id;
                currentHolder = holder;
                openFilePicker();
            } else {

                if (currentAssignmentId != -1) {
                    submitSelectedFile();
                }
            }
        });

        holder.btnCancelFile.setOnClickListener(v -> {

            resetFileSelection(holder);
        });
    }


    private String getFileNameFromPath(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "Unknown File";
        }


        String fileName = filePath;
        if (fileName.contains("/")) {
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        }
        if (fileName.contains("\\")) {
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
        }

        return fileName.isEmpty() ? "Unknown File" : fileName;
    }


    private void setFileIcon(ImageView imageView, String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();

        switch (extension) {
            case "pdf":
                break;
            case "doc":
            case "docx":
                break;
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
                break;
            case "txt":
                break;
            default:
                break;
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private void openFile(String fileName) {
        String fileUrl = "http://10.0.2.2/file_handler.php?file=" + fileName;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(fileUrl));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(activity, "Cannot open file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void viewFile(String fileName) {
        String fileUrl = "http://10.0.2.2/file_handler.php?file=" + fileName;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(fileUrl));

        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        } else {
            Toast.makeText(activity, "No app available to view this file", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadFile(String fileName) {
        String fileUrl = "http://10.0.2.2/file_handler.php?file=" + fileName;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(fileUrl));

        try {
            activity.startActivity(intent);
            Toast.makeText(activity, "Opening file for download...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(activity, "Error downloading file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        String[] mimeTypes = {
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "text/plain",
                "image/jpeg",
                "image/png"
        };
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        try {
            activity.startActivityForResult(
                    Intent.createChooser(intent, "اختر ملف التسليم"),
                    FILE_PICKER_REQUEST
            );
        } catch (Exception e) {
            Toast.makeText(activity, "خطأ في فتح منتقي الملفات", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void handleFileSelection(Uri fileUri) {
        if (fileUri != null && currentAssignmentId != -1 && currentHolder != null) {
            try {
                String fileName = getFileName(fileUri);

                // عرض معلومات الملف المختار
                currentHolder.selectedFileName.setText("الملف المختار: " + fileName);
                currentHolder.fileSelectionLayout.setVisibility(View.VISIBLE);
                currentHolder.btnSubmit.setText("إرسال التسليم");

                // حفظ Uri للاستخدام لاحقاً
                currentHolder.selectedFileUri = fileUri;

            } catch (Exception e) {
                Toast.makeText(activity, "خطأ في معالجة الملف: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                resetFileSelection(currentHolder);
            }
        }
    }

    private void submitSelectedFile() {
        if (currentHolder != null && currentHolder.selectedFileUri != null) {
            try {
                currentHolder.btnSubmit.setEnabled(false);
                currentHolder.btnSubmit.setText("جاري الإرسال...");

                submitAssignmentWithFile(currentAssignmentId, currentHolder.selectedFileUri);
            } catch (Exception e) {
                Toast.makeText(activity, "خطأ في إرسال الملف: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                resetFileSelection(currentHolder);
            }
        }
    }

    private void resetFileSelection(AssignmentViewHolder holder) {
        holder.fileSelectionLayout.setVisibility(View.GONE);
        holder.btnSubmit.setText("اختر ملف");
        holder.btnSubmit.setEnabled(true);
        holder.selectedFileUri = null;
        holder.selectedFileName.setText("");
        currentAssignmentId = -1;
        currentHolder = null;
    }

    private void submitAssignmentWithFile(int assignmentId, Uri fileUri) throws IOException {
        // الحصول على اسم الملف
        String fileName = getFileName(fileUri);

        // قراءة محتوى الملف
        InputStream inputStream = activity.getContentResolver().openInputStream(fileUri);
        if (inputStream == null) {
            throw new IOException("لا يمكن قراءة الملف");
        }

        byte[] fileBytes = readBytes(inputStream);
        inputStream.close();

        // إنشاء multipart request محسن
        MultipartRequest multipartRequest = new MultipartRequest(
                Request.Method.POST,
                "http://10.0.2.2/submit_assignment.php",
                response -> {
                    activity.runOnUiThread(() -> {
                        try {
                            Toast.makeText(activity, "تم إرسال التسليم بنجاح", Toast.LENGTH_SHORT).show();
                            if (currentHolder != null) {
                                resetFileSelection(currentHolder);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                },
                volleyError -> {
                    activity.runOnUiThread(() -> {
                        String errorMessage = "فشل في الإرسال";

                        if (volleyError instanceof TimeoutError) {
                            errorMessage = "انتهت مهلة الاتصال - تحقق من الإنترنت";
                        } else if (volleyError instanceof NoConnectionError) {
                            errorMessage = "لا يوجد اتصال بالإنترنت";
                        } else if (volleyError instanceof AuthFailureError) {
                            errorMessage = "خطأ في التوثيق";
                        } else if (volleyError instanceof ServerError) {
                            errorMessage = "خطأ في الخادم";
                        } else if (volleyError instanceof NetworkError) {
                            errorMessage = "خطأ في الشبكة";
                        } else if (volleyError instanceof ParseError) {
                            errorMessage = "خطأ في معالجة البيانات";
                        }

                        // إضافة كود الخطأ إذا كان متوفر
                        if (volleyError.networkResponse != null) {
                            int statusCode = volleyError.networkResponse.statusCode;
                            errorMessage += " (كود: " + statusCode + ")";

                            // رسائل مخصصة حسب كود الخطأ
                            switch (statusCode) {
                                case 400:
                                    errorMessage = "طلب غير صحيح (400)";
                                    break;
                                case 401:
                                    errorMessage = "غير مخول للوصول (401)";
                                    break;
                                case 403:
                                    errorMessage = "ممنوع الوصول (403)";
                                    break;
                                case 404:
                                    errorMessage = "الصفحة غير موجودة (404)";
                                    break;
                                case 500:
                                    errorMessage = "خطأ في الخادم (500)";
                                    break;
                                case 503:
                                    errorMessage = "الخدمة غير متوفرة (503)";
                                    break;
                            }

                            // طباعة تفاصيل الخطأ للتشخيص
                            try {
                                String responseBody = new String(volleyError.networkResponse.data, "UTF-8");
                                System.out.println("Status Code: " + statusCode);
                                System.out.println("Server Response: " + responseBody);
                                System.out.println("Headers: " + volleyError.networkResponse.headers);
                            } catch (Exception e) {
                                System.out.println("Error reading response: " + e.getMessage());
                                e.printStackTrace();
                            }
                        } else {
                            // لا يوجد network response - غالباً مشكلة اتصال
                            System.out.println("No network response - connection issue");
                            System.out.println("Error message: " + volleyError.getMessage());
                        }

                        // عرض الرسالة للمستخدم
                        Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show();

                        // إعادة تعيين التحديد
                        if (currentHolder != null) {
                            resetFileSelection(currentHolder);
                        }
                    });
                }
        );

        // إضافة المعاملات
        multipartRequest.addStringParam("student_id", String.valueOf(student_id));
        multipartRequest.addStringParam("assignment_id", String.valueOf(assignmentId));

        // إضافة الملف
        multipartRequest.addFileParam("attachment", fileName, fileBytes);

        // إضافة إلى queue
        Volley.newRequestQueue(activity).add(multipartRequest);
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }
        return result != null ? result : "unknown_file";
    }

    private byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static int getFilePickerRequestCode() {
        return FILE_PICKER_REQUEST;
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView subject, title, description, dueDate, selectedFileName;
        Button btnSubmit, btnCancelFile;
        LinearLayout fileSelectionLayout;
        Uri selectedFileUri;

        // عناصر مرفق الواجب (من الأستاذ)
        LinearLayout assignmentAttachmentSection;
        TextView assignmentAttachmentName;
        ImageView assignmentFileIcon;
        Button btnViewAttachment, btnDownloadAttachment;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.assignmentSubject);
            title = itemView.findViewById(R.id.assignmentTitle);
            description = itemView.findViewById(R.id.assignmentDescription);
            dueDate = itemView.findViewById(R.id.assignmentDueDate);
            btnSubmit = itemView.findViewById(R.id.btnSubmitAssignment);

            // العناصر الجديدة للملف المختار (تسليم الطالب)
            selectedFileName = itemView.findViewById(R.id.selectedFileName);
            btnCancelFile = itemView.findViewById(R.id.btnCancelFile);
            fileSelectionLayout = itemView.findViewById(R.id.fileSelectionLayout);

            // عناصر مرفق الواجب (من الأستاذ)
            assignmentAttachmentSection = itemView.findViewById(R.id.assignmentAttachmentSection);
            assignmentAttachmentName = itemView.findViewById(R.id.assignmentAttachmentName);
            assignmentFileIcon = itemView.findViewById(R.id.assignmentFileIcon);
            btnViewAttachment = itemView.findViewById(R.id.btnViewAttachment);
            btnDownloadAttachment = itemView.findViewById(R.id.btnDownloadAttachment);
        }
    }
}