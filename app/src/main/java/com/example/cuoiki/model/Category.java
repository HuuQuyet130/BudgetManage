package com.example.cuoiki.model;

public class Category {

    private int id;
    private String name;
    private String type;

    // Constructor rỗng
    public Category() {
    }

    // Dùng khi thêm danh mục mới
    public Category(String name, String type) {
        this.name = name;
        this.type = type;
    }

    // Dùng khi đọc dữ liệu đầy đủ từ SQLite
    public Category(int id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }
}