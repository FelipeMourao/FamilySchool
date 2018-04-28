package br.com.familyschool.familyschool.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;
import br.com.familyschool.familyschool.helper.Preferencias;
import br.com.familyschool.familyschool.model.Faltas;
import br.com.familyschool.familyschool.model.Frequencia;
import br.com.familyschool.familyschool.model.Usuario;

public class FrequenciaActivity extends AppCompatActivity {

    private TextView txtData;
    private Button botaoSalvar, botatoCancelar;
    Calendar calendario = Calendar.getInstance();
    private static final int DATE_DIALOG_ID = 0;
    private String identificadorProfessorUsuario,turmaAlunoUsuario, TimeInMillis;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> alunos,selecaoPresencas,idUsuarios;
    private ArrayList<String> selectedItens = new ArrayList<>();
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerAlunos;
    private ListView listView;
    private ProgressDialog progressDialog;

    @Override
    protected void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerAlunos);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerAlunos);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frequencia);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Family School");
        setSupportActionBar(toolbar);

        txtData = (TextView) findViewById(R.id.txt_data);
        botaoSalvar = (Button) findViewById(R.id.btn_salvar);
        botatoCancelar = (Button) findViewById(R.id.btn_cancelar);

        FirebaseMessaging.getInstance().subscribeToTopic("Notificacao");

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        alunos = new ArrayList<>();
        selecaoPresencas = new ArrayList<>();
        idUsuarios = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            identificadorProfessorUsuario = bundle.getString("IdProfessor");
            turmaAlunoUsuario = bundle.getString("Turma");
        }

        txtData.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDialog(DATE_DIALOG_ID);
                return false;
            }
        });


        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtData.getText().toString().isEmpty()){
                    Toast.makeText(FrequenciaActivity.this,"Selecione a Data",Toast.LENGTH_LONG).show();
                } else {
                    progressDialog = new ProgressDialog(FrequenciaActivity.this,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Salvando...");
                    progressDialog.show();

                    Preferencias preferencias = new Preferencias(FrequenciaActivity.this);
                    String identificadorUsuarioLogado = preferencias.getIdentificador();
                    String data = txtData.getText().toString();
                    for(String usuarioEncontrado : idUsuarios){
                        for (String Usuario : selecaoPresencas){
                            if (Usuario.equals(usuarioEncontrado)){
                                firebase = ConfiguracaoFirebase.getFireBase().child("Presenca").child(identificadorUsuarioLogado);
                                firebase = firebase.child(turmaAlunoUsuario).child(usuarioEncontrado).child(TimeInMillis);

                                Faltas faltas = new Faltas();
                                faltas.setDataUsuario(data);
                                faltas.setNomeUsuario(usuarioEncontrado);
                                faltas.setTimeInMillis(TimeInMillis);

                                firebase.setValue(faltas);
                            }
                        }
                    }

                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(FrequenciaActivity.this,R.style.MyAlertDialogStyle);
                            builder.setMessage("Frêquencia Salva com Sucesso!");
                            builder.setCancelable(false);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    },5000);
                }
            }
        });

        botatoCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebase = ConfiguracaoFirebase.getFireBase().child("Usuario");
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);
                    idUsuarios.add(usuario.getNome());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        firebase = ConfiguracaoFirebase.getFireBase().child("Frequencia").child(identificadorProfessorUsuario).child(turmaAlunoUsuario);
        valueEventListenerAlunos = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Limpar listar de alunos
                alunos.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    Frequencia frequencia = dados.getValue(Frequencia.class);
                    alunos.add(frequencia.getNomeUsuario());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener(valueEventListenerAlunos);


        listView = (ListView) findViewById(R.id.lv_alunos);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        adapter = new ArrayAdapter<String>(this, R.layout.lista_alunos, R.id.txt_alunos, alunos);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectItem = ((TextView)view).getText().toString();
                if (selectedItens.contains(selectItem)){
                    selecaoPresencas.remove(selectItem);
                } else {
                    selecaoPresencas.add(selectItem);
                }
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
        TimeInMillis = Long.toString(calendario.getTimeInMillis());
    }
}
