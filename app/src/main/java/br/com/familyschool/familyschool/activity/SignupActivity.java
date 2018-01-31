package br.com.familyschool.familyschool.activity;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;
import br.com.familyschool.familyschool.model.Usuario;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @InjectView(R.id.email_rd)
    EditText _email;
    @InjectView(R.id.senha_atual)
    EditText _senhaAtual;
    @InjectView(R.id.senha_nova)
    EditText _senhaNova;
    @InjectView(R.id.senha_confirmar)
    EditText _senhaConfirmar;
    @InjectView(R.id.btn_signup)
    Button _signupButton;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _signupButton.setEnabled(false);

                final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Redefinindo Senha...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                autenticacao.signInWithEmailAndPassword(
                        _email.getText().toString(),
                        _senhaAtual.getText().toString()
                ).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String senhaNova = _senhaNova.getText().toString();
                            if (user != null) {
                                user.updatePassword(senhaNova).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this,R.style.MyAlertDialogStyle);
                                            builder.setMessage("Senha Redefinida com Sucesso!");
                                            builder.setCancelable(false);
                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    autenticacao.signOut();
                                                    finish();
                                                }
                                            });
                                            AlertDialog alertDialog = builder.create();
                                            alertDialog.show();
                                        }
                                        else {
                                            String erroLogin = "";

                                            try{
                                                throw task.getException();
                                            } catch (FirebaseAuthWeakPasswordException e) {
                                                erroLogin = "Digite uma senha mais forte, contendo mais caracteres e com letras e numeros";
                                            } catch (Exception e){
                                                erroLogin = "Falha na Internet!";
                                                e.printStackTrace();
                                            }
                                            Toast.makeText(SignupActivity.this, "Erro: " + erroLogin, Toast.LENGTH_LONG).show();
                                            _signupButton.setEnabled(true);
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        } else {
                            String erroLogin = "";

                            try{
                                throw task.getException();
                            }catch (FirebaseAuthInvalidUserException e){
                                erroLogin = "O e-mail digitado não existe ou foi desativado!";
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                erroLogin = "A senha atual está incorreta!";
                            } catch (Exception e){
                                erroLogin = "Falha na Internet!";
                                e.printStackTrace();
                            }
                            Toast.makeText(SignupActivity.this, "Erro: " + erroLogin, Toast.LENGTH_LONG).show();
                            _signupButton.setEnabled(true);
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });
    }
}