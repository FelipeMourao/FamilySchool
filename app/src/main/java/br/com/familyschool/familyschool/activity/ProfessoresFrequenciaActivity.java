package br.com.familyschool.familyschool.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import br.com.familyschool.familyschool.Adapter.FrequenciaAdapter;
import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;
import br.com.familyschool.familyschool.helper.Preferencias;
import br.com.familyschool.familyschool.model.Contato;
import br.com.familyschool.familyschool.model.Frequencia;
import br.com.familyschool.familyschool.model.Turma;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProfessoresFrequenciaActivity extends AppCompatActivity {

    @InjectView(R.id.list_professores) ListView listView;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerContatos;
    private ArrayList<String> contatos;
    private ArrayList<Turma> turmas;
    private ArrayAdapter adapter;
    private String identificadorUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professores_frequencia);
        ButterKnife.inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Family School");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        contatos = new ArrayList<>();
        turmas = new ArrayList<>();

        adapter = new FrequenciaAdapter(ProfessoresFrequenciaActivity.this, turmas);
        listView.setAdapter(adapter);

        Preferencias preferencias = new Preferencias(ProfessoresFrequenciaActivity.this);
        identificadorUsuarioLogado = preferencias.getIdentificador();
        firebase = ConfiguracaoFirebase.getFireBase().child("Contatos")
                .child(identificadorUsuarioLogado);

        valueEventListenerContatos = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    Contato contato = dados.getValue(Contato.class);
                    contatos.add(contato.getIdentificadorUsuario());
                }
                adicionarTurma(contatos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener(valueEventListenerContatos);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Turma turma = turmas.get(position);
                Intent intent = new Intent(ProfessoresFrequenciaActivity.this, FrequenciaAlunoActivity.class);
                intent.putExtra("idProfessor", turma.getIdUsuario());
                intent.putExtra("nomeTurma", turma.getNomeTurma());
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

    private void adicionarTurma(ArrayList<String> identificadores){
        for (final String identificador : identificadores){
            firebase = ConfiguracaoFirebase.getFireBase().child("Classe").child(identificador);
            firebase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot dados : dataSnapshot.getChildren()){
                        final Turma turma = dados.getValue(Turma.class);
                        firebase = ConfiguracaoFirebase.getFireBase().child("Frequencia").child(identificador).child(turma.getNomeTurma());
                        firebase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot dados : dataSnapshot.getChildren()){
                                    Frequencia frequencia = dados.getValue(Frequencia.class);
                                    if (frequencia.getIdUsuarioAluno().equals(identificadorUsuarioLogado)){
                                        turmas.add(turma);
                                        Log.i("Teste Turma Escolhida", "Turmas: " + turmas.size());
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

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
