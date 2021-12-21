package com.dentist.konselorhalodent.Profile;

import java.io.Serializable;

public class Jadwal implements Serializable {

    private String konselor_id,dokter_id,mulai,selesai;

    public Jadwal(){

    }

    public Jadwal(String konselor_id, String dokter_id, String mulai, String selesai) {
        this.konselor_id = konselor_id;
        this.dokter_id = dokter_id;
        this.mulai = mulai;
        this.selesai = selesai;
    }

    public String getKonselor_id() {
        return konselor_id;
    }

    public void setKonselor_id(String konselor_id) {
        this.konselor_id = konselor_id;
    }

    public String getDokter_id() {
        return dokter_id;
    }

    public void setDokter_id(String dokter_id) {
        this.dokter_id = dokter_id;
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
