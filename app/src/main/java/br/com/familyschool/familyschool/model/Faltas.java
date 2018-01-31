package br.com.familyschool.familyschool.model;


public class Faltas {
    private String dataUsuario;
    private String nomeUsuario;
    private String TimeInMillis;

    public Faltas() {
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getDataUsuario() {
        return dataUsuario;
    }

    public void setDataUsuario(String dataUsuario) {
        this.dataUsuario = dataUsuario;
    }

    public String getTimeInMillis() {
        return TimeInMillis;
    }

    public void setTimeInMillis(String timeInMillis) {
        TimeInMillis = timeInMillis;
    }
}
