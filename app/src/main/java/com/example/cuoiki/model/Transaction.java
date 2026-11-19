package com.example.cuoiki.model;

public class Transaction {

    private int id;
    private String title;
    private double amount;
    private String date;
    private String note;
    private String type;
    private int categoryId;
    private int accountId;
    private String createdAt;

    public Transaction() {
    }

    public Transaction(
            String title,
            double amount,
            String date,
            String note,
            String type,
            int categoryId,
            int accountId,
            String createdAt
    ) {
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.note = note;
        this.type = type;
        this.categoryId = categoryId;
        this.accountId = accountId;
        this.createdAt = createdAt;
    }

    public Transaction(
            int id,
            String title,
            double amount,
            String date,
            String note,
            String type,
            int categoryId,
            int accountId,
            String createdAt
    ) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.note = note;
        this.type = type;
        this.categoryId = categoryId;
        this.accountId = accountId;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    private String categoryName;

}