package com.example.cuoiki.model;

public class Debt {

    private int id;
    private String person;
    private double amount;
    private String deadline;
    private String status;
    private String type;
    private int accountId;

    public Debt(
            int id,
            String person,
            double amount,
            String deadline,
            String status,
            String type,
            int accountId
    ) {
        this.id = id;
        this.person = person;
        this.amount = amount;
        this.deadline = deadline;
        this.status = status;
        this.type = type;
        this.accountId = accountId;
    }

    public Debt(
            String person,
            double amount,
            String deadline,
            String status,
            String type,
            int accountId
    ) {
        this.person = person;
        this.amount = amount;
        this.deadline = deadline;
        this.status = status;
        this.type = type;
        this.accountId = accountId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
}