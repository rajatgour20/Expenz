package com.example.s;

public class CourseModal {

    // variables for our coursename,
    // description, tracks and duration, id.
    private String title;
    private String amout;
    private String day;
    private String month;
    private String year;
    private String transactionType;
    private String category;
    int id;

    public CourseModal() {
    }

    public CourseModal(String title, String amout, String day, String month, String year, String transactionType, String category) {
        this.title = title;
        this.amout = amout;
        this.day = day;
        this.month = month;
        this.year = year;
        this.transactionType = transactionType;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAmout() {
        return amout;
    }

    public void setAmout(String amout) {
        this.amout = amout;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



}

