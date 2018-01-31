package br.com.familyschool.familyschool.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rtoshiro.util.format.MaskFormatter;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.pattern.MaskPattern;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import br.com.familyschool.familyschool.Adapter.FrequenciaAdapter;
import br.com.familyschool.familyschool.Adapter.SpinnerAdapter;
import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;
import br.com.familyschool.familyschool.helper.Base64Custom;
import br.com.familyschool.familyschool.helper.NotificationUtil;
import br.com.familyschool.familyschool.helper.Preferencias;
import br.com.familyschool.familyschool.model.Tarefa;
import br.com.familyschool.familyschool.model.Turma;
import br.com.familyschool.familyschool.model.Usuario;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class TarefaActivity extends AppCompatActivity {

    @InjectView(R.id.edit_assunto)   EditText assunto;
    @InjectView(R.id.edit_descricao) EditText descricao;
    @InjectView(R.id.txt_data) TextView txtData;
    @InjectView(R.id.txt_nota) TextView txtNota;
    @InjectView(R.id.btn_enviar)  Button botaoEnviar;
    @InjectView(R.id.btn_cancelar) Button botaoCancelar;
    @InjectView(R.id.spinner) Spinner spinner;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private ProgressDialog progressDialog;
    private ArrayAdapter adapter;
    private ArrayList<String> turmas;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerTurmas;
    private static final int DATE_DIALOG_ID = 0;
    private String sppinerSelect, urlConteudo,identificadorUsuarioLogado;
    Calendar calendario = Calendar.getInstance();
    private String emailLogado;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarefa);
        ButterKnife.inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Family School");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        turmas = new ArrayList<>();

        adapter = new SpinnerAdapter(this,R.layout.spinner_text,turmas);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sppinerSelect = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //recuperar turmas
        Preferencias preferencias = new Preferencias(this);
        identificadorUsuarioLogado = preferencias.getIdentificador();
        firebase = ConfiguracaoFirebase.getFireBase().child("Classe").child(identificadorUsuarioLogado);

        //Listener para turmas
        valueEventListenerTurmas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Limpar lista
                turmas.clear();

                //Listar Turmas
                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    Turma turma = dados.getValue(Turma.class);
                    turmas.add(turma.getNomeTurma());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener(valueEventListenerTurmas);

        botaoCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtData.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDialog(DATE_DIALOG_ID);
                return false;
            }
        });

        botaoEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(TarefaActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Enviando....");
                progressDialog.show();


                AlertDialog.Builder builder = new AlertDialog.Builder(TarefaActivity.this,R.style.MyAlertDialogStyle);
                builder.setTitle("Geração de Tarefa.");
                builder.setMessage("Você está criando uma tarefa, gostaria de anexar algum arquivo em seu conteudo?");
                builder.setCancelable(false);
                builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        opednFolder();
                    }
                });

                builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String notaRecebida = txtNota.getText().toString();
                        String dataRecebida = txtData.getText().toString();
                        String assuntoRecebido = assunto.getText().toString();
                        String descricaoRecebido = descricao.getText().toString();

                        Tarefa tarefa = new Tarefa();
                        tarefa.setAssunto(assuntoRecebido);
                        tarefa.setDescricao(descricaoRecebido);
                        tarefa.setIdProfessor(identificadorUsuarioLogado);
                        tarefa.setNota(notaRecebida);
                        tarefa.setNomeTurma(sppinerSelect);
                        tarefa.setDataEntrega(dataRecebida);
                        tarefa.setUrlConteudo("");

                        firebase = ConfiguracaoFirebase.getFireBase();
                        firebase = firebase.child("Tarefas").child(identificadorUsuarioLogado).child(assuntoRecebido);
                        firebase.setValue(tarefa);

                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                finish();
                            }
                        },3000);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        txtNota.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                OnclickNota();
                return false;
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

    @Override
    protected Dialog onCreateDialog(int id){
        int ano = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        switch (id){
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, ano, mes,dia);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendario.set(Calendar.YEAR,year);
            calendario.set(Calendar.MONTH,month);
            calendario.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            updateLabel();
        }
    };

    private void updateLabel(){
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));
        txtData.setText(sdf.format(calendario.getTime()));
    }

    public void opednFolder(){
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK && requestCode == 1){
            StorageReference storageReference = storage.getReferenceFromUrl("gs://familyschool-aaa4b.appspot.com");
            Uri file = data.getData();
            Log.e("file",file.getPath());

            StorageReference fileRef = storageReference.child(sppinerSelect).child(assunto.getText().toString());

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
                    String notaRecebida = txtNota.getText().toString();
                    String dataRecebida = txtData.getText().toString();
                    String assuntoRecebido = assunto.getText().toString();
                    String descricaoRecebido = descricao.getText().toString();
                    urlConteudo = downloadUri.toString();

                    Tarefa tarefa = new Tarefa();
                    tarefa.setAssunto(assuntoRecebido);
                    tarefa.setDescricao(descricaoRecebido);
                    tarefa.setIdProfessor(identificadorUsuarioLogado);
                    tarefa.setNota(notaRecebida);
                    tarefa.setDataEntrega(dataRecebida);
                    tarefa.setNomeTurma(sppinerSelect);
                    tarefa.setUrlConteudo(urlConteudo);

                    firebase = ConfiguracaoFirebase.getFireBase();
                    firebase = firebase.child("Tarefas").child(identificadorUsuarioLogado).child(assuntoRecebido);
                    firebase.setValue(tarefa);

                    progressDialog.dismiss();
                    finish();
                }
            });
        }
    }

    private void OnclickNota(){
        final AlertDialog.Builder d = new AlertDialog.Builder(TarefaActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
        d.setTitle("Selecione a nota:");
        d.setView(dialogView);
        final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);
        final NumberPicker numberPicker2 = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker2);
        numberPicker.setMaxValue(10);
        numberPicker.setMinValue(0);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                Log.i("NUMBERPICKRER", "onValueChange: ");
            }
        });
        numberPicker2.setMaxValue(9);
        numberPicker2.setMinValue(0);
        numberPicker2.setWrapSelectorWheel(false);
        numberPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                Log.e("NUMBERPICKRER", "onValueChange: ");
            }
        });
        d.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                txtNota.setText(numberPicker.getValue() + "." + numberPicker2.getValue());
            }
        });
        d.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog alertDialog = d.create();
        alertDialog.show();
    }
}
