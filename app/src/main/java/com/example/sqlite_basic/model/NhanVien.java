package com.example.sqlite_basic.model;

public class NhanVien {
    private int id;
    private String ten;
    private String sdt;
    private byte[] hinhAnh;

    public NhanVien(int id, String ten, String sdt, byte[] hinhAnh) {
        this.id = id;
        this.ten = ten;
        this.sdt = sdt;
        this.hinhAnh = hinhAnh;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public byte[] getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(byte[] hinhAnh) {
        this.hinhAnh = hinhAnh;
    }
}
