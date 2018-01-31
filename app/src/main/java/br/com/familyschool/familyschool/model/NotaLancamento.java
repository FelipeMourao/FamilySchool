package br.com.familyschool.familyschool.model;

public class NotaLancamento {

    private String idProfessor;
    private String idAluno;
    private String notaLancada;
    private String bimestre;

    public NotaLancamento() {
    }

    public String getIdProfessor() {
        return idProfessor;
    }

    public void setIdProfessor(String idProfessor) {
        this.idProfessor = idProfessor;
    }

    public String getIdAluno() {
        return idAluno;
    }

    public void setIdAluno(String idAluno) {
        this.idAluno = idAluno;
    }

    public String getNotaLancada() {
        return notaLancada;
    }

    public void setNotaLancada(String notaLancada) {
        this.notaLancada = notaLancada;
    }

    public String getBimestre() {
        return bimestre;
    }

    public void setBimestre(String bimestre) {
        this.bimestre = bimestre;
    }
}
