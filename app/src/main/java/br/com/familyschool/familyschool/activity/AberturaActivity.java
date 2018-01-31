package br.com.familyschool.familyschool.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;
import br.com.familyschool.familyschool.helper.Base64Custom;
import br.com.familyschool.familyschool.model.Usuario;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class AberturaActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private DatabaseReference firebase;
    private String emailLogado;
    @InjectView(R.id.abert1) ImageView iv;
    @InjectView(R.id.abert2) ImageView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abertura);
        ButterKnife.inject(this);
        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.mytransition);
        tv.startAnimation(myanim);
        iv.startAnimation(myanim);
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                verificarUsuarioLogado();
            }
        }, 2000);
    }

    private void abrirTelaPrincipal(){
        Intent MainIntent = new Intent(this, MainActivity.class);
        MainIntent.putExtra("email", emailLogado);
        startActivity(MainIntent);
        finish();

    }

    private void professorTelaPrincipal(){
        Intent MainIntent = new Intent(this, ProfessorActivity.class);
        MainIntent.putExtra("email", emailLogado);
        startActivity(MainIntent);
        finish();

    }

    private void responsavelTelaPrincipal(){
        Intent MainIntent = new Intent(this, ResponsavelActivity.class);
        MainIntent.putExtra("email", emailLogado);
        startActivity(MainIntent);
        finish();

    }

    private void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        FirebaseUser user = autenticacao.getCurrentUser();
        if( user != null){
            emailLogado = user.getEmail();
            String identificadorUsuarioLogado = Base64Custom.codificarBase64(emailLogado);
            firebase = ConfiguracaoFirebase.getFireBase().child("Usuario").child(identificadorUsuarioLogado);
            firebase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Usuario usuarioLogado = dataSnapshot.getValue(Usuario.class);
                    if (usuarioLogado.getTipoPessoa().equals("Aluno")){
                        abrirTelaPrincipal();
                    } else if (usuarioLogado.getTipoPessoa().equals("Responsavel")){
                        responsavelTelaPrincipal();
                    } else {
                        professorTelaPrincipal();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Intent intent = new Intent(AberturaActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
