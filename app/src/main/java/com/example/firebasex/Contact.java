package com.example.firebasex;

public class Contact {
    private String name, phone, UId;

    public Contact(String name, String phone, String UId) {
        this.name = name;
        this.phone = phone;
        this.UId = UId;
    }

    public String getUId() {
        return UId;
    }

    public void setUId(String UId) {
        this.UId = UId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
