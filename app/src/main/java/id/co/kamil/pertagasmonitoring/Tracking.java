package id.co.kamil.pertagasmonitoring;

class Tracking {
    String id,tgl,id_perusahaan,perusahaan, kategori, alamat,vol,status;

    public String getVol() {
        return vol;
    }

    public void setVol(String vol) {
        this.vol = vol;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }

    public String getId_perusahaan() {
        return id_perusahaan;
    }

    public void setId_perusahaan(String id_perusahaan) {
        this.id_perusahaan = id_perusahaan;
    }

    public String getPerusahaan() {
        return perusahaan;
    }

    public void setPerusahaan(String perusahaan) {
        this.perusahaan = perusahaan;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
}
