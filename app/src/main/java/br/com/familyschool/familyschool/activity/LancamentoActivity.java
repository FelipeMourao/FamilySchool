package br.com.familyschool.familyschool.activity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.familyschool.familyschool.Adapter.SpinnerAdapter;
import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;
import br.com.familyschool.familyschool.helper.Preferencias;
import br.com.familyschool.familyschool.model.NotaLancamento;
import br.com.familyschool.familyschool.model.NotaTarefa;
import br.com.familyschool.familyschool.model.Usuario;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class LancamentoActivity extends AppCompatActivity {

    @InjectView(R.id.txt_titulo)  TextView titulo;
    @InjectView(R.id.txt_nome_aluno)  TextView nomeAluno;
    @InjectView(R.id.txt_nota_aluno)  TextView notaAluno;
    @InjectView(R.id.spinner) Spinner bimestreAluno;
    @InjectView(R.id.txt_resposta)  TextView respostaAluno;
    @InjectView(R.id.btn_resposta) Button btnResposta;
    @InjectView(R.id.btn_lancar) Button btnLancar;
    @InjectView(R.id.btn_cancelar) Button btnCancelar;
    private String txtIdAluno, txtTurma,txtNota, txtConteudo, bimestreSelecionado;
    private ArrayList<String> idAluno;
    private ProgressDialog progressDialog;
    private ArrayAdapter adapter;
    private DatabaseReference firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancamento);
        ButterKnife.inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Family School");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        idAluno = new ArrayList<>();
        ArrayList<String> bimestres = new ArrayList<String>();
        bimestres.add("Selecione o Bimetre");
        bimestres.add("1º Bimestre");
        bimestres.add("2º Bimestre");
        bimestres.add("3º Bimestre");
        bimestres.add("4º Bimestre");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            txtIdAluno = bundle.getString("idAluno");
            txtTurma = bundle.getString("turma");
            txtNota = bundle.getString("nota");
        }

        Double nota = Double.parseDouble(txtNota);
        final int notaInteito = nota.intValue();

        adapter = new SpinnerAdapter(this,R.layout.spinner_text,bimestres);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        bimestreAluno.setAdapter(adapter);


        bimestreAluno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bimestreSelecionado = bimestreAluno.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        notaAluno.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AlertDialog.Builder d = new AlertDialog.Builder(LancamentoActivity.this);
                LayoutInflater inflater = LancamentoActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
                d.setTitle("Selecione a nota:");
                d.setView(dialogView);
                final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);
                final NumberPicker numberPicker2 = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker2);
                numberPicker.setMaxValue(notaInteito);
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
                        notaAluno.setText(numberPicker.getValue() + "." + numberPicker2.getValue());
                    }
                });
                d.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog alertDialog = d.create();
                alertDialog.show();
                return false;
            }
        });

        Preferencias preferencias = new Preferencias(this);
        final String identificadorProfessorUsuario = preferencias.getIdentificador();
        firebase = ConfiguracaoFirebase.getFireBase().child("NotasTarefas").child(identificadorProfessorUsuario).child(txtTurma);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    NotaTarefa notaTarefa = dados.getValue(NotaTarefa.class);
                    if (notaTarefa.getIdAluno().equals(txtIdAluno)){
                        titulo.setText(notaTarefa.getAssuntoResposta());
                        respostaAluno.setText(notaTarefa.getResposta());
                        txtConteudo = notaTarefa.getRespostaConteudo();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        firebase = ConfiguracaoFirebase.getFireBase().child("Usuario").child(txtIdAluno);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                nomeAluno.setText(usuario.getNome());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        btnResposta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtConteudo.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(LancamentoActivity.this,R.style.MyAlertDialogStyle);
                    builder.setMessage("O Aluno não Anexo nenhuma Conteúdo!");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(txtConteudo);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setTitle(titulo.getText());
                    Long reference = downloadManager.enqueue(request);
                    Toast.makeText(LancamentoActivity.this, "Iniciano download....", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnLancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(LancamentoActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Lançando....");
                progressDialog.show();

                firebase = ConfiguracaoFirebase.getFireBase().child("NotaLancamento").child(identificadorProfessorUsuario).child(txtTurma).child(txtIdAluno);
                NotaLancamento lancamento = new NotaLancamento();
                lancamento.setIdAluno(txtIdAluno);
                lancamento.setIdProfessor(identificadorProfessorUsuario);
                lancamento.setBimestre(bimestreSelecionado);
                lancamento.setNotaLancada(notaAluno.getText().toString());

                firebase.setValue(lancamento);


                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        finish();
                    }
                },5000);
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
}
