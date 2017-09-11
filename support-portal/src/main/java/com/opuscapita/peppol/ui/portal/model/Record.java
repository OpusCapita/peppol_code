package com.opuscapita.peppol.ui.portal.model;

public class Record {
    String name;
    String surname;
    String phone;
    String data;

    public Record() {
    }

    public Record(String name, String surname, String phone, String data) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
