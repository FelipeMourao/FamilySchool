package br.com.familyschool.familyschool.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import br.com.familyschool.familyschool.R;

public class PasswordActivity extends AppCompatActivity {

    private EditText email;
    private Button _enviarEmail;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        email = (EditText) findViewById(R.id.email_enviar);
        _enviarEmail = (Button) findViewById(R.id.btnEnviarEmail);

        autenticacao = FirebaseAuth.getInstance();

        _enviarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _enviarEmail.setEnabled(false);

                final ProgressDialog progressDialog = new ProgressDialog(PasswordActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Enviando ...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                String emailValido = email.getText().toString();

                if (TextUtils.isEmpty(emailValido)) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Insira o seu email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                autenticacao.sendPasswordResetEmail(emailValido).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(PasswordActivity.this,R.style.MyAlertDialogStyle);
                            builder.setMessage("O e-mail de redefinição de senha foi enviado com Sucesso!");
                            builder.setCancelable(false);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        } else{
                            String erroLogin = "";

                            try{
                                throw task.getException();
                            }catch (FirebaseAuthInvalidUserException e){
                                erroLogin = "O e-mail não cadastrado. Por favor Procura a Secretaria!";
                            } catch (Exception e){
                                erroLogin = "Falha na Internet!";
                                e.printStackTrace();
                            }
                            Toast.makeText(PasswordActivity.this, "Erro: " + erroLogin, Toast.LENGTH_LONG).show();
                            _enviarEmail.setEnabled(true);
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });
    }
}
