package com.example.thequality.homepage.riwayat;

public class DeteksiModel {
    private String tanggal, sekolah, status;
    private double co2, bakteri;

    public DeteksiModel() {
        // Wajib untuk Firebase
    }

    public DeteksiModel(String tanggal, String sekolah, double co2, double bakteri, String status) {
        this.tanggal = tanggal;
        this.sekolah = sekolah;
        this.co2 = co2;
        this.bakteri = bakteri;
        this.status = status;
    }

    public String getTanggal() { return tanggal; }
    public String getSekolah() { return sekolah; }
    public double getCo2() { return co2; }
    public double getBakteri() { return bakteri; }
    public String getStatus() { return status; }
}
