package id.co.kamil.pertagasmonitoring;

public class Regresi {
    String id_wilayah, nama_wilayah, coefficients,stdErr,coefP,RSquare,SSE,SSR,SSTO,F,TStats,PValues, barrel,week,predict,dollar;

    public String getDollar() {
        return dollar;
    }

    public void setDollar(String dollar) {
        this.dollar = dollar;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getBarrel() {
        return barrel;
    }

    public void setBarrel(String barrel) {
        this.barrel = barrel;
    }

    @Override
    public String toString() {
        return "Regresi{" +
                "id_wilayah='" + id_wilayah + '\'' +
                ", nama_wilayah='" + nama_wilayah + '\'' +
                ", coefficients='" + coefficients + '\'' +
                ", stdErr='" + stdErr + '\'' +
                ", coefP='" + coefP + '\'' +
                ", RSquare='" + RSquare + '\'' +
                ", SSE='" + SSE + '\'' +
                ", SSR='" + SSR + '\'' +
                ", SSTO='" + SSTO + '\'' +
                ", F='" + F + '\'' +
                ", TStats='" + TStats + '\'' +
                ", PValues='" + PValues + '\'' +
                '}';
    }

    public String getPredict() {
        return predict;
    }

    public void setPredict(String predict) {
        this.predict = predict;
    }

    public String getId_wilayah() {
        return id_wilayah;
    }

    public void setId_wilayah(String id_wilayah) {
        this.id_wilayah = id_wilayah;
    }

    public String getNama_wilayah() {
        return nama_wilayah;
    }

    public void setNama_wilayah(String nama_wilayah) {
        this.nama_wilayah = nama_wilayah;
    }

    public String getCoefficients() {
        return coefficients;
    }

    public void setCoefficients(String coefficients) {
        this.coefficients = coefficients;
    }

    public String getStdErr() {
        return stdErr;
    }

    public void setStdErr(String stdErr) {
        this.stdErr = stdErr;
    }

    public String getCoefP() {
        return coefP;
    }

    public void setCoefP(String coefP) {
        this.coefP = coefP;
    }

    public String getRSquare() {
        return RSquare;
    }

    public void setRSquare(String RSquare) {
        this.RSquare = RSquare;
    }

    public String getSSE() {
        return SSE;
    }

    public void setSSE(String SSE) {
        this.SSE = SSE;
    }

    public String getSSR() {
        return SSR;
    }

    public void setSSR(String SSR) {
        this.SSR = SSR;
    }

    public String getSSTO() {
        return SSTO;
    }

    public void setSSTO(String SSTO) {
        this.SSTO = SSTO;
    }

    public String getF() {
        return F;
    }

    public void setF(String f) {
        F = f;
    }

    public String getTStats() {
        return TStats;
    }

    public void setTStats(String TStats) {
        this.TStats = TStats;
    }

    public String getPValues() {
        return PValues;
    }

    public void setPValues(String PValues) {
        this.PValues = PValues;
    }
}
