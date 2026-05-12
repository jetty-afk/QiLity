package com.example.thequality.homepage;

public class HasilDeteksi {

    private String tanggal;
    private String rgb;
    private String gray;
    private double ppb;
    private String status;
    private String sekolah;

    public HasilDeteksi() {
    }
    public HasilDeteksi(String tanggal, String rgb, String gray, double ppb, String status, String sekolah) {
        this.tanggal = tanggal;
        this.rgb = rgb;
        this.gray = gray;
        this.ppb = ppb;
        this.status = status;
        this.sekolah = sekolah;
    }
    public String getTanggal() {
        return tanggal;
    }

    public String getRgb() {
        return rgb;
    }

    public String getGray() {
        return gray;
    }

    public double getPpb() {
        return ppb;
    }

    public String getStatus() {
        return status;
    }

    public String getSekolah() {
        return sekolah;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public void setRgb(String rgb) {
        this.rgb = rgb;
    }

    public void setGray(String gray) {
        this.gray = gray;
    }

    public void setPpb(double ppb) {
        this.ppb = ppb;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSekolah(String sekolah) {
        this.sekolah = sekolah;
    }
}
