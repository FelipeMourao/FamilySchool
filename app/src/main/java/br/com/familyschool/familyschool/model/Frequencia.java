package br.com.familyschool.familyschool.model;


public class Frequencia {

    private String idUsuarioAluno;
    private String nomeUsuario;

    public Frequencia() {
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getIdUsuarioAluno() {
        return idUsuarioAluno;
    }

    public void setIdUsuarioAluno(String idUsuarioAluno) {
        this.idUsuarioAluno = idUsuarioAluno;
    }
}
