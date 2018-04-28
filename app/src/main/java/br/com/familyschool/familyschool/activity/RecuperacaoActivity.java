package br.com.familyschool.familyschool.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import br.com.familyschool.familyschool.model.Frequencia;
import br.com.familyschool.familyschool.model.NotaBimestre;

public class RecuperacaoActivity extends AppCompatActivity {

    private Button btnCancelar;
    private String[] itens = {"1º Bimestre","2º Bimestre","3º Bimestre","4º Bimestre","Recuperação"};
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerAlunos;
    private ArrayList<Frequencia> alunos;
    private String turmaAlunoUsuario,bimestreUsuario;
    private NotasAdapter adapter;
    private Double mediaFinal, media;
    private ArrayList<String> idAlunos;

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
        setContentView(R.layout.activity_recuperacao);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Family School");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        btnCancelar = (Button) findViewById(R.id.btn_cancelar);

        alunos = new ArrayList<>();
        idAlunos = new ArrayList<>();
        mediaFinal = 0.0;

        Bundle bundle = getIntent().getExtras();

        if (bundle != null){
            turmaAlunoUsuario = bundle.getString("Turma");
            bimestreUsuario = bundle.getString("bimestre");
        }

        Preferencias preferencias = new Preferencias(this);
        final String identificadorUsuarioLogado = preferencias.getIdentificador();
        firebase = ConfiguracaoFirebase.getFireBase().child("Frequencia").child(identificadorUsuarioLogado).child(turmaAlunoUsuario);
        valueEventListenerAlunos = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Limpar listar de alunos
                alunos.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    Frequencia frequencia = dados.getValue(Frequencia.class);
                    idAlunos.add(frequencia.getIdUsuarioAluno());
                }
                //adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener(valueEventListenerAlunos);

        for (final String usuario : idAlunos){
            for (String bimestre : itens){
                firebase = ConfiguracaoFirebase.getFireBase().child("NotasBimestre").child(identificadorUsuarioLogado).child(turmaAlunoUsuario).child(bimestre);
                firebase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dados : dataSnapshot.getChildren()){
                            NotaBimestre notaBimestre = dados.getValue(NotaBimestre.class);
                            if (usuario.equals(notaBimestre.getIdUsuario())){
                                media = Double.parseDouble(notaBimestre.getMedia());
                                double aux = 0.0;
                                if (!mediaFinal.equals(0.0)){
                                    aux = mediaFinal;
                                }
                                mediaFinal = aux + media;
                            }
                        }
                        Toast.makeText(RecuperacaoActivity.this, "Media Final: " + mediaFinal, Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        }
    }

        //RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerAlunos);

        //adapter = new NotasAdapter(alunos,this,turmaAlunoUsuario,bimestreUsuario);

        //recyclerView.setAdapter(adapter);

        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);

        //recyclerView.setLayoutManager(layoutManager);

        //recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

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
