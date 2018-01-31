package br.com.familyschool.familyschool.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;
import br.com.familyschool.familyschool.helper.Base64Custom;
import br.com.familyschool.familyschool.helper.Preferencias;
import br.com.familyschool.familyschool.model.Usuario;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @InjectView(R.id.email)
    EditText emailText;
    @InjectView(R.id.senha)
    EditText passwordText;
    @InjectView(R.id.btn_login)
    Button _loginButton;
    @InjectView(R.id.link_senha)
    TextView _signupLink;
    @InjectView(R.id.link_redifinir)
    TextView _redefinirLink;
    @InjectView(R.id.link_cadastro)
    TextView cadastroLink;
    private Usuario usuario;
    private FirebaseAuth autenticacao;
    private DatabaseReference firebase;
    private String emailLogado;
    private String codigoResponsavel;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                _loginButton.setEnabled(false);

                      progressDialog = new ProgressDialog(LoginActivity.this,
                              R.style.AppTheme_Dark_Dialog);
                      progressDialog.setIndeterminate(true);
                      progressDialog.setMessage("Autenticando...");
                      progressDialog.setCancelable(false);
                      progressDialog.show();
                      usuario = new Usuario();
                      usuario.setEmail(emailText.getText().toString());
                      usuario.setSenha(passwordText.getText().toString());
                      validarLogin();

                     }

            }
        );

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), PasswordActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        _redefinirLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        cadastroLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CadastroActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    private void validarLogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    final Preferencias preferencias = new Preferencias(LoginActivity.this);
                    String identificadorUsuarioLogado = Base64Custom.codificarBase64(usuario.getEmail());
                    preferencias.salvarDados(identificadorUsuarioLogado);

                    emailLogado = usuario.getEmail();

                    firebase = ConfiguracaoFirebase.getFireBase().child("Usuario").child(identificadorUsuarioLogado);
                    firebase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Usuario usuarioLogado = dataSnapshot.getValue(Usuario.class);
                            if (usuarioLogado.getTipoPessoa().equals("Aluno")){
                                abrirTelaPrincipal();
                                progressDialog.dismiss();
                            } else if(usuarioLogado.getTipoPessoa().equals("Responsavel")) {
                                String identificadorUsuario = Base64Custom.codificarBase64(usuarioLogado.getCodigoAluno());
                                String identificadorResponsavel = Base64Custom.codificarBase64(usuarioLogado.getEmail());
                                preferencias.salvarResponsavel(identificadorResponsavel);
                                preferencias.salvarDados(identificadorUsuario);
                                responsavelTelaPrincipal();
                                progressDialog.dismiss();
                            } else {
                                professorTelaPrincipal();
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {

                    String erroLogin = "";

                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        erroLogin = "O e-mail digitado não existe ou foi desativado!";
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        erroLogin = "A senha está incorreta!";
                    } catch (Exception e){
                        erroLogin = "Falha na Internet!";
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, "Erro: " + erroLogin, Toast.LENGTH_LONG).show();
                    _loginButton.setEnabled(true);
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void abrirTelaPrincipal(){
        Intent MainIntent = new Intent(LoginActivity.this, MainActivity.class);
        MainIntent.putExtra("email", emailLogado);
        startActivity(MainIntent);
        finish();

    }

    private void professorTelaPrincipal(){
        Intent MainIntent = new Intent(LoginActivity.this, ProfessorActivity.class);
        MainIntent.putExtra("email", emailLogado);
        startActivity(MainIntent);
        finish();

    }

    private void responsavelTelaPrincipal(){
        Intent MainIntent = new Intent(LoginActivity.this, ResponsavelActivity.class);
        MainIntent.putExtra("email", emailLogado);
        startActivity(MainIntent);
        finish();

    }
}