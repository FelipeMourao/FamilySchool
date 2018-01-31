package br.com.familyschool.familyschool.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.familyschool.familyschool.Adapter.NotasAdapter;
import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;
import br.com.familyschool.familyschool.helper.Preferencias;
import br.com.familyschool.familyschool.helper.SimpleDividerItemDecoration;
import br.com.familyschool.familyschool.model.Frequencia;
import br.com.familyschool.familyschool.model.NotaLancamento;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class NotasActivity extends AppCompatActivity {

    @InjectView(R.id.btn_cancelar) Button btnCancelar;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerAlunos;
    private ArrayList<Frequencia> alunos;
    private String turmaAlunoUsuario,bimestreUsuario,lancamento;
    private NotasAdapter adapter;

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
        setContentView(R.layout.activity_notas);
        ButterKnife.inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Family School");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        alunos = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null){
            turmaAlunoUsuario = bundle.getString("Turma");
            bimestreUsuario = bundle.getString("bimestre");
        }

        Preferencias preferencias = new Preferencias(this);
        String identificadorUsuario = preferencias.getIdentificador();
        firebase = ConfiguracaoFirebase.getFireBase().child("Frequencia").child(identificadorUsuario).child(turmaAlunoUsuario);
        valueEventListenerAlunos = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Limpar listar de alunos
                alunos.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    Frequencia frequencia = dados.getValue(Frequencia.class);
                    alunos.add(frequencia);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener(valueEventListenerAlunos);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerAlunos);

        adapter = new NotasAdapter(alunos,this,turmaAlunoUsuario,bimestreUsuario,identificadorUsuario);

        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
