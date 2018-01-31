package br.com.familyschool.familyschool.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferencias {

    private Context contexto;
    private SharedPreferences preferences;
    private final String NOME_ARQUIVO = "familySchool.preferencias";
    private final int MODE = 0;
    private SharedPreferences.Editor editor;

    private final String CHAVE_IDENTIFICADOR = "identificadorUsuarioLogado";
    private final String CHAVE_CODIGO = "token";
    private final String CHAVE_RESPONSAVEL = "responsavel";

    public Preferencias( Context contextoParametro){

        contexto = contextoParametro;
        preferences = contexto.getSharedPreferences(NOME_ARQUIVO, MODE );
        editor = preferences.edit();

    }

    public void salvarDados( String identificadorUsuario ){

        editor.putString(CHAVE_IDENTIFICADOR, identificadorUsuario);
        editor.commit();

    }

    public void salvarToken( String token ){

        editor.putString(CHAVE_CODIGO, token);
        editor.commit();

    }

    public void salvarResponsavel(String responsavel ){

        editor.putString(CHAVE_RESPONSAVEL, responsavel);
        editor.commit();

    }

    public String getIdentificador(){
        return preferences.getString(CHAVE_IDENTIFICADOR, null);
    }
    public String getToken(){
        return preferences.getString(CHAVE_CODIGO, null);
    }
    public String getIDResponsavel(){
        return preferences.getString(CHAVE_RESPONSAVEL, null);
    }
}
