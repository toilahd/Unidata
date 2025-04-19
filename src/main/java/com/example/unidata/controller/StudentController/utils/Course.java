package com.example.unidata.controller.StudentController.utils;

public class Course {
    private String code;
    private String name;
    private int credits;
    private int lectures;
    private int labs;
    private String openCourseId; // MAMM ✅

    public String getOpenCourseId() {
        return openCourseId;
    }

    public void setOpenCourseId(String openCourseId) {
        this.openCourseId = openCourseId;
    }
    public Course(String code, String name, int credits, int lectures, int labs) {
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.lectures = lectures;
        this.labs = labs;
    }

    public String getCode() {
        return code;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getLectures() {
        return lectures;
    }

    public void setLectures(int lectures) {
        this.lectures = lectures;
    }

    public int getLabs() {
        return labs;
    }

    public void setLabs(int labs) {
        this.labs = labs;
    }
}
