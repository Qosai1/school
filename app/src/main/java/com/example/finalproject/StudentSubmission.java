package com.example.finalproject;

public class StudentSubmission {
    public int student_id;
    public String student_name;
    public String attachment;
    public String submitted_at;

    // Constructor
    public StudentSubmission() {
    }

    public StudentSubmission(int student_id, String student_name, String attachment, String submitted_at) {
        this.student_id = student_id;
        this.student_name = student_name;
        this.attachment = attachment;
        this.submitted_at = submitted_at;
    }

    public boolean hasAttachment() {
        return attachment != null && !attachment.trim().isEmpty() && !attachment.equals("null");
    }

    public String getFileName() {
        if (!hasAttachment()) return "No file";

        String[] parts = attachment.split("/");
        return parts[parts.length - 1]; // Return last part (filename)
    }

    // Get file extension
    public String getFileExtension() {
        String fileName = getFileName();
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(lastDot + 1).toLowerCase();
        }
        return "";
    }
}