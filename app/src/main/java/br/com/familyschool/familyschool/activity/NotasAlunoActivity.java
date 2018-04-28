package br.com.familyschool.familyschool.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import br.com.familyschool.familyschool.Adapter.NotasAlunoAdapter;
import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;
import br.com.familyschool.familyschool.helper.Preferencias;
import br.com.familyschool.familyschool.model.NotaBimestre;
import br.com.familyschool.familyschool.model.Turma;

public class NotasAlunoActivity extends AppCompatActivity {

    private RecyclerView list_bim;
    private String txtIdProfessor;
    private DatabaseReference firebase;
    private ArrayList<String> turmas;
    private String[] itens = {"1º Bimestre","2º Bimestre","3º Bimestre","4º Bimestre","Recuperação"};
    private ArrayList<NotaBimestre> notas;
    private NotasAlunoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas_aluno);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Family School");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        list_bim = (RecyclerView) findViewById(R.id.recyclerNotas);

        turmas = new ArrayList<>();
        notas = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            txtIdProfessor = bundle.getString("idProfessor");
        }

        Preferencias preferencias = new Preferencias(NotasAlunoActivity.this);
        final String identificadorUsuarioLogado = preferencias.getIdentificador();
        firebase = ConfiguracaoFirebase.getFireBase().child("Classe").child(txtIdProfessor);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                turmas.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    Turma turma = dados.getValue(Turma.class);
                    turmas.add(turma.getNomeTurma());
               }
                verificar(turmas, identificadorUsuarioLogado);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ArrayList<String> bimestres = new ArrayList<String>();
        bimestres.add("1º Bimestre");
        bimestres.add("2º Bimestre");
        bimestres.add("3º Bimestre");
        bimestres.add("4º Bimestre");
        bimestres.add("Recuperaçao");

        adapter = new NotasAlunoAdapter(bimestres, notas);

        list_bim.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);

        list_bim.setLayoutManager(layoutManager);
    }

    private void verificar (ArrayList<String> turmasColedadas,final String identificador){
       for (String turma : turmasColedadas){
            for (String bimestre : itens){
                firebase = ConfiguracaoFirebase.getFireBase().child("NotasBimestre").child(txtIdProfessor).child(turma).child(bimestre);
                firebase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot dados : dataSnapshot.getChildren()){
                            NotaBimestre notaBimestre = dados.getValue(NotaBimestre.class);
                            if (notaBimestre.getIdUsuario().equals(identificador)){
                                notas.add(notaBimestre);
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
