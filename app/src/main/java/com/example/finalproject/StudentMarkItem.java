package com.example.finalproject;

public class StudentMarkItem {
    private int studentId;
    private String studentName;
    private String mark;

    public StudentMarkItem(int studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.mark = "";
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}