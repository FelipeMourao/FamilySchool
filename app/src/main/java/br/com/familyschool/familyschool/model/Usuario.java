package br.com.familyschool.familyschool.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;


public class Usuario {

    private String id;
    private String nome;
    private String email;
    private String senha;
    private String tipoPessoa;
    private String CodigoAluno;

    public Usuario() {
    }

    public void salvar(){
        DatabaseReference referenciaFireBase = ConfiguracaoFirebase.getFireBase();
        referenciaFireBase.child("Usuario").child(getId()).setValue(this);
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getCodigoAluno() {
        return CodigoAluno;
    }

    public void setCodigoAluno(String codigoAluno) {
        CodigoAluno = codigoAluno;
    }
}
