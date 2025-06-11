package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StudentSubmissionAdapter extends RecyclerView.Adapter<StudentSubmissionAdapter.ViewHolder> {

    private Context context;
    private List<StudentSubmission> submissions;

    public StudentSubmissionAdapter(Context context, List<StudentSubmission> submissions) {
        this.context = context;
        this.submissions = submissions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student_submission, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentSubmission submission = submissions.get(position);

        // عرض اسم الطالب
        holder.studentName.setText(submission.student_name != null ? submission.student_name : "Unknown Student");

        // عرض تاريخ التسليم
        holder.submittedAt.setText(submission.submitted_at != null ?
                "Submitted: " + submission.submitted_at : "No Date");

        // التعامل مع المرفق
        if (submission.attachment != null && !submission.attachment.trim().isEmpty()) {
            holder.attachmentSection.setVisibility(View.VISIBLE);
            holder.attachmentName.setText(getFileNameFromPath(submission.attachment));

            // تحديد نوع الملف وإظهار الأيقونة المناسبة
            setFileIcon(holder.fileIcon, submission.attachment);

            // إضافة click listener لفتح الملف
            holder.attachmentSection.setOnClickListener(v -> openFile(submission.attachment));
            holder.btnDownload.setOnClickListener(v -> downloadFile(submission.attachment));
            holder.btnView.setOnClickListener(v -> viewFile(submission.attachment));

        } else {
            holder.attachmentSection.setVisibility(View.GONE);
            holder.noAttachment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return submissions != null ? submissions.size() : 0;
    }

    // استخراج اسم الملف من المسار
    private String getFileNameFromPath(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "Unknown File";
        }

        // إزالة المسار والاحتفاظ باسم الملف فقط
        String fileName = filePath;
        if (fileName.contains("/")) {
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        }
        if (fileName.contains("\\")) {
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
        }

        return fileName.isEmpty() ? "Unknown File" : fileName;
    }

    // تحديد أيقونة الملف بناء على النوع
    private void setFileIcon(ImageView imageView, String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();

        switch (extension) {
            case "pdf":
                //imageView.setImageResource(R.drawable.ic_pdf); // تحتاج لإضافة هذه الأيقونة
                break;
            case "doc":
            case "docx":
                //imageView.setImageResource(R.drawable.ic_doc); // تحتاج لإضافة هذه الأيقونة
                break;
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
               // imageView.setImageResource(R.drawable.ic_image); // تحتاج لإضافة هذه الأيقونة
                break;
            case "txt":
              //  imageView.setImageResource(R.drawable.ic_text); // تحتاج لإضافة هذه الأيقونة
                break;
            default:
               // imageView.setImageResource(R.drawable.ic_file); // أيقونة عامة للملفات
                break;
        }
    }

    // الحصول على امتداد الملف
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    // فتح الملف (محاولة فتحه بالتطبيقات المناسبة)
    private void openFile(String fileName) {
        String fileUrl = "http://10.0.2.2/file_handler.php?file=" + fileName;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(fileUrl));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Cannot open file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // عرض الملف في المتصفح
    private void viewFile(String fileName) {
        String fileUrl = "http://10.0.2.2/file_handler.php?file=" + fileName;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(fileUrl));

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "No app available to view this file", Toast.LENGTH_SHORT).show();
        }
    }

    // تحميل الملف
    private void downloadFile(String fileName) {
        String fileUrl = "http://10.0.2.2/file_handler.php?file=" + fileName;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(fileUrl));

        try {
            context.startActivity(intent);
            Toast.makeText(context, "Opening file for download...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Error downloading file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, submittedAt, attachmentName, noAttachment;
        LinearLayout attachmentSection;
        ImageView fileIcon;
        Button btnView, btnDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            submittedAt = itemView.findViewById(R.id.submittedAt);
            attachmentSection = itemView.findViewById(R.id.attachmentSection);
            attachmentName = itemView.findViewById(R.id.attachmentName);
            fileIcon = itemView.findViewById(R.id.fileIcon);
            btnView = itemView.findViewById(R.id.btnView);
            btnDownload = itemView.findViewById(R.id.btnDownload);
            noAttachment = itemView.findViewById(R.id.noAttachment);
        }
    }
}