package com.example.cuoiki.model;

public class Budget {

    private int id;
    private int month;
    private int year;
    private double amount;
    private int categoryId;
    private int accountId;
    private int warningPercent;

    /*
     * Hai thuộc tính chỉ dùng để hiển thị.
     * Không phải cột trong bảng Budget.
     */
    private String categoryName;
    private double spentAmount;

    public Budget(
            int id,
            int month,
            int year,
            double amount,
            int categoryId,
            int accountId,
            int warningPercent
    ) {
        this.id = id;
        this.month = month;
        this.year = year;
        this.amount = amount;
        this.categoryId = categoryId;
        this.accountId = accountId;
        this.warningPercent = warningPercent;
    }

    public Budget(
            int month,
            int year,
            double amount,
            int categoryId,
            int accountId,
            int warningPercent
    ) {
        this.month = month;
        this.year = year;
        this.amount = amount;
        this.categoryId = categoryId;
        this.accountId = accountId;
        this.warningPercent = warningPercent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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

    public int getWarningPercent() {
        return warningPercent;
    }

    public void setWarningPercent(int warningPercent) {
        this.warningPercent = warningPercent;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(double spentAmount) {
        this.spentAmount = spentAmount;
    }

    public int getUsagePercent() {

        if (amount <= 0) {
            return 0;
        }

        return (int) Math.round(
                spentAmount * 100 / amount
        );
    }

    public double getRemainingAmount() {
        return amount - spentAmount;
    }

    public boolean isExceeded() {
        return spentAmount >= amount;
    }

    public boolean isWarningReached() {
        return !isExceeded()
                && getUsagePercent() >= warningPercent;
    }
}