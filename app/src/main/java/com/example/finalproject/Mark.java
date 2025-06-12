package com.example.finalproject;

public class Mark {
    private int markId;
    private int subjectId;
    private String subjectName;
    private String examName;
    private double mark;

    // Constructor
    public Mark() {
    }

    public Mark(int markId, int subjectId, String subjectName, String examName, double mark) {
        this.markId = markId;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.examName = examName;
        this.mark = mark;
    }

    public int getMarkId() {
        return markId;
    }

    public void setMarkId(int markId) {
        this.markId = markId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public double getMark() {
        return mark;
    }

    public void setMark(double mark) {
        this.mark = mark;
    }

    @Override
    public String toString() {
        return "Mark{" +
                "markId=" + markId +
                ", subjectId=" + subjectId +
                ", subjectName='" + subjectName + '\'' +
                ", examName='" + examName + '\'' +
                ", mark=" + mark +
                '}';
    }
}