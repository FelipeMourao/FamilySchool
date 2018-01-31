package br.com.familyschool.familyschool.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;
import br.com.familyschool.familyschool.helper.Base64Custom;
import br.com.familyschool.familyschool.helper.Preferencias;
import br.com.familyschool.familyschool.model.Usuario;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class CadastroActivity extends AppCompatActivity {

    @InjectView(R.id.radiogroup) RadioGroup group;
    @InjectView(R.id.email_aluno) EditText editText;
    @InjectView(R.id.email_cadastro) EditText emailCadastro;
    @InjectView(R.id.nome_cadastro) EditText nomeCadastro;
    @InjectView(R.id.senha_cadastro) EditText senhaCadastro;
    @InjectView(R.id.btn_enviar) Button enviarCadastro;
    @InjectView(R.id.btn_cancelar) Button cancelarCadastro;
    private String radioTipo;
    private Usuario usuario;
    private FirebaseAuth autenticacao;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        ButterKnife.inject(this);
        editText.setVisibility(View.GONE);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton button = (RadioButton) group.findViewById(checkedId);
                switch (checkedId){
                    case R.id.radioAluno:
                        radioTipo = button.getText().toString();
                        editText.setVisibility(View.GONE);
                        break;
                    case R.id.radioResponsavel:
                        radioTipo = button.getText().toString();
                        editText.setVisibility(View.VISIBLE);
                        break;
                    case R.id.radioProfessor:
                        radioTipo = button.getText().toString();
                        editText.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }
        });
        enviarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(CadastroActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Cadastrando...");
                progressDialog.show();

                usuario = new Usuario();
                usuario.setEmail(emailCadastro.getText().toString());
                usuario.setNome(nomeCadastro.getText().toString());
                usuario.setSenha(senhaCadastro.getText().toString());
                usuario.setTipoPessoa(radioTipo);
                if (radioTipo.equals("Responsavel")){
                    usuario.setCodigoAluno(editText.getText().toString());
                } else {
                    usuario.setCodigoAluno("");
                }
                cadastrarUsuario();
            }
        });

        cancelarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void cadastrarUsuario(){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if( task.isSuccessful() ){

                    Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar Usuario", Toast.LENGTH_LONG ).show();

                    String identificadorUsuario = Base64Custom.codificarBase64( usuario.getEmail() );
                    usuario.setId( identificadorUsuario );
                    usuario.salvar();

                    Preferencias preferencias = new Preferencias(CadastroActivity.this);
                    preferencias.salvarDados(identificadorUsuario);

                    abrirLoginUsuario();

                }else{

                    String erro = "";
                    try{
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erro = "Escolha uma senha que contenha, letras e números.";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erro = "Email indicado não é válido.";
                    } catch (FirebaseAuthUserCollisionException e) {
                        erro = "Já existe uma conta com esse e-mail.";
                    } catch (Exception e) {
                        erro = "Falha na Internet!";
                    }

                    Toast.makeText(CadastroActivity.this, "Erro ao cadastrar Usuario: " + erro, Toast.LENGTH_LONG ).show();
                    progressDialog.dismiss();
                }

            }
        });

    }

    public void abrirLoginUsuario(){
        autenticacao.signOut();
        Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        progressDialog.dismiss();
    }

}
