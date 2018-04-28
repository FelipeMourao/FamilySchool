package br.com.familyschool.familyschool.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;
import br.com.familyschool.familyschool.helper.Preferencias;
import br.com.familyschool.familyschool.model.Frequencia;
import br.com.familyschool.familyschool.model.NotaTarefa;

public class TarefaProfessorActivity extends AppCompatActivity {

    private TextView assunto,dataEntrega,nota;
    private ListView listaAlunos;
    private String txtAssunto, txtNota,txtData,txtTurma;
    private DatabaseReference firebase;
    private ArrayList<String> alunos, alunosEntrega;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarefa_professor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Family School");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        assunto = (TextView) findViewById(R.id.txt_assunto_professor);
        dataEntrega = (TextView) findViewById(R.id.txt_data_professor);
        nota = (TextView) findViewById(R.id.txt_nota_professor);
        listaAlunos = (ListView) findViewById(R.id.list_alunos);

        alunos = new ArrayList<>();
        alunosEntrega = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            txtAssunto = bundle.getString("assunto");
            txtNota = bundle.getString("nota");
            txtData = bundle.getString("data");
            txtTurma = bundle.getString("turma");
        }

        assunto.setText(txtAssunto);
        dataEntrega.setText("Data de Entrega: " + txtData);
        nota.setText("Nota: " + txtNota);

        Preferencias preferencias = new Preferencias(this);
        final String identificadorProfessorUsuario = preferencias.getIdentificador();
        firebase = ConfiguracaoFirebase.getFireBase().child("NotasTarefas").child(identificadorProfessorUsuario).child(txtTurma);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                alunosEntrega.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    NotaTarefa notaTarefa = dados.getValue(NotaTarefa.class);
                    if (notaTarefa.getAssuntoResposta().equals(txtAssunto)){
                        alunosEntrega.add(notaTarefa.getIdAluno());
                    }
                }
                adicionarAlunos(alunosEntrega, identificadorProfessorUsuario);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new ArrayAdapter<String>(this,R.layout.lista_frequencia,R.id.tv_turma,alunos);
        listaAlunos.setAdapter(adapter);

        listaAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String idAluno = alunosEntrega.get(position);
                Intent intent = new Intent(TarefaProfessorActivity.this, LancamentoActivity.class);
                intent.putExtra("idAluno", idAluno);
                intent.putExtra("turma", txtTurma);
                intent.putExtra("nota",txtNota);
                startActivity(intent);
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

    private void adicionarAlunos(ArrayList<String> alunosEntrega, String identificadorProfessorUsuario){
        for (final String aluno : alunosEntrega) {
            firebase = ConfiguracaoFirebase.getFireBase().child("Frequencia").child(identificadorProfessorUsuario).child(txtTurma);
            firebase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        Frequencia frequencia = dados.getValue(Frequencia.class);
                        if (frequencia.getIdUsuarioAluno().equals(aluno)){
                            alunos.add(frequencia.getNomeUsuario());
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
