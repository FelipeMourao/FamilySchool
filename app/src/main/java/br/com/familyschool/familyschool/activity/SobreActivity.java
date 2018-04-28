package br.com.familyschool.familyschool.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import br.com.familyschool.familyschool.R;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class SobreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Element versionElement = new Element();
        versionElement.setTitle("Version 1.0");

        String descricao = "Family School é um aplicativo escolar de acompanhamento entre os Professores e Resposanveis com aluno durante periodo escolar";
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.ic_familiy)
                .setDescription(descricao)
                .addItem(versionElement)
                .create();

        setContentView(aboutPage);
    }
}
