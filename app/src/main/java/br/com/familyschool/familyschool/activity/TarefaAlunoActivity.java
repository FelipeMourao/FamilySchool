package br.com.familyschool.familyschool.activity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;
import br.com.familyschool.familyschool.helper.Preferencias;
import br.com.familyschool.familyschool.model.NotaTarefa;
import br.com.familyschool.familyschool.model.Tarefa;
import br.com.familyschool.familyschool.model.Usuario;
import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TarefaAlunoActivity extends AppCompatActivity {

    @InjectView(R.id.txt_assunto) TextView assunto;
    @InjectView(R.id.txt_professor) TextView professor;
    @InjectView(R.id.txt_descricao) TextView descricao;
    @InjectView(R.id.txt_notaTarefa) TextView nota;
    @InjectView(R.id.txt_data) TextView dataEntrega;
    @InjectView(R.id.btn_arquivo) Button btnArquivo;
    @InjectView(R.id.btn_anexar) Button btnAnexar;
    @InjectView(R.id.btn_enviar) Button btnEnviar;
    @InjectView(R.id.btn_cancelar) Button btnCancelar;
    @InjectView(R.id.edit_descricao) EditText respostaAluno;
    private DatabaseReference firebase;
    private String txtAssunto,txtProfessor,txtDescricao,txtNota,txtEntrega,txtConteudo,turma,downloadAnexo;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private ProgressDialog progressDialog;
    private DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarefa_aluno);
        ButterKnife.inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Family School");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            txtAssunto = bundle.getString("assunto");
            txtProfessor = bundle.getString("professor");
            txtDescricao = bundle.getString("descricao");
            txtNota = bundle.getString("nota");
            txtEntrega = bundle.getString("data");
            txtConteudo = bundle.getString("urlConteudo");
            turma = bundle.getString("turma");
        }

        assunto.setText(txtAssunto);

        downloadAnexo = "";

        firebase = ConfiguracaoFirebase.getFireBase().child("Usuario").child(txtProfessor);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                professor.setText(usuario.getNome());
                }

            @Override
                public void onCancelled(DatabaseError databaseError) {

                  }
        });

        descricao.setText(txtDescricao);

        nota.setText("Nota: " + txtNota);

        dataEntrega.setText("Data da Entrega: " + txtEntrega);

        if (txtConteudo.isEmpty()){
            btnArquivo.setEnabled(false);
        } else {
            btnArquivo.setEnabled(true);
        };

        btnArquivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(txtConteudo);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                Long reference = downloadManager.enqueue(request);
                Toast.makeText(TarefaAlunoActivity.this, "Iniciano download....",Toast.LENGTH_LONG).show();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAnexar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(TarefaAlunoActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Anexando....");
                progressDialog.show();


                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(TarefaAlunoActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Enviando....");
                progressDialog.show();

                Preferencias preferencias = new Preferencias(TarefaAlunoActivity.this);
                String identificadorUsuarioLogado = preferencias.getIdentificador();

                String respostaEdit = respostaAluno.getText().toString();
                firebase = ConfiguracaoFirebase.getFireBase().child("NotasTarefas").child(txtProfessor).child(turma).child(identificadorUsuarioLogado);
                NotaTarefa notaTarefa = new NotaTarefa();
                notaTarefa.setIdAluno(identificadorUsuarioLogado);
                notaTarefa.setIdProfessor(txtProfessor);
                notaTarefa.setAssuntoResposta(txtAssunto);
                notaTarefa.setResposta(respostaEdit);
                notaTarefa.setRespostaConteudo(downloadAnexo);
                firebase.setValue(notaTarefa);

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(TarefaAlunoActivity.this,R.style.MyAlertDialogStyle);
                        builder.setMessage("Tarefa Enviada com Sucesso!");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.dismiss();
                                finish();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                },3000);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK && requestCode == 1){
            StorageReference storageReference = storage.getReferenceFromUrl("gs://familyschool-aaa4b.appspot.com");
            Uri file = data.getData();
            Log.e("file",file.getPath());

            Preferencias preferencias = new Preferencias(TarefaAlunoActivity.this);
            String identificadorUsuarioLogado = preferencias.getIdentificador();
            StorageReference fileRef = storageReference.child("TarefaEntrega").child(turma).child(identificadorUsuarioLogado).child(assunto.getText().toString());

            UploadTask uploadTask = fileRef.putFile(file);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("uploadFail","" + e);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    downloadAnexo = downloadUri.toString();

                    AlertDialog.Builder builder = new AlertDialog.Builder(TarefaAlunoActivity.this,R.style.MyAlertDialogStyle);
                    builder.setMessage("Arquivo Anexado com Sucesso!");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        } else {
            progressDialog.dismiss();
        }
    }
}
