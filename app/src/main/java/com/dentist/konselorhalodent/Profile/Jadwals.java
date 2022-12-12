package com.dentist.konselorhalodent.Profile;

import java.io.Serializable;

public class Jadwals implements Serializable {

    private String id,konselorId,dokterId,tanggal,mulai,selesai;

    public Jadwals(){

    }

    public Jadwals(String konselorId, String dokterId, String tanggal, String mulai, String selesai) {
        this.konselorId = konselorId;
        this.dokterId = dokterId;
        this.tanggal = tanggal;
        this.mulai = mulai;
        this.selesai = selesai;
    }

    public Jadwals(String id, String konselorId, String dokterId, String tanggal, String mulai, String selesai) {
        this.id = id;
        this.konselorId = konselorId;
        this.dokterId = dokterId;
        this.tanggal = tanggal;
        this.mulai = mulai;
        this.selesai = selesai;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getKonselorId() {
        return konselorId;
    }

    public void setKonselorId(String konselor_id) {
        this.konselorId = konselor_id;
    }

    public String getDokterId() {
        return dokterId;
    }

    public void setDokterId(String dokterId) {
        this.dokterId = dokterId;
    }

    public String getMulai() {
        return mulai;
    }

    public void setMulai(String mulai) {
        this.mulai = mulai;
    }

    public String getSelesai() {
        return selesai;
    }

    public void setSelesai(String selesai) {
        this.selesai = selesai;
    }
}
