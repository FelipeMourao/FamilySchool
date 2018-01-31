package br.com.familyschool.familyschool.model;

public class NotaTarefa {

    private String idProfessor;
    private String resposta;
    private String respostaConteudo;
    private String idAluno;
    private String assuntoResposta;

    public NotaTarefa() {
    }

    public String getIdProfessor() {
        return idProfessor;
    }

    public void setIdProfessor(String idProfessor) {
        this.idProfessor = idProfessor;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public String getRespostaConteudo() {
        return respostaConteudo;
    }

    public void setRespostaConteudo(String respostaConteudo) {
        this.respostaConteudo = respostaConteudo;
    }

    public String getIdAluno() {
        return idAluno;
    }

    public void setIdAluno(String idAluno) {
        this.idAluno = idAluno;
    }

    public String getAssuntoResposta() {
        return assuntoResposta;
    }

    public void setAssuntoResposta(String assuntoResposta) {
        this.assuntoResposta = assuntoResposta;
    }
}
