package com.example.cuoiki.model;

public class Goal {

    private int id;
    private String title;
    private double targetAmount;
    private double currentAmount;
    private String deadline;
    private int accountId;

    public Goal(
            int id,
            String title,
            double targetAmount,
            double currentAmount,
            String deadline,
            int accountId
    ) {
        this.id = id;
        this.title = title;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
        this.accountId = accountId;
    }

    public Goal(
            String title,
            double targetAmount,
            double currentAmount,
            String deadline,
            int accountId
    ) {
        this.title = title;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
        this.accountId = accountId;
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

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        return title;
    }
}