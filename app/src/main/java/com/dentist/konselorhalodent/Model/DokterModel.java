package com.dentist.konselorhalodent.Model;

public class DokterModel {

    private String id,nama,email,photo,nip,str;

    public DokterModel() {

    }

    public DokterModel(String id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    public DokterModel(String id, String nama, String email, String photo, String nip, String str) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.photo = photo;
        this.nip = nip;
        this.str = str;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }
}
