package br.com.familyschool.familyschool.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;
import br.com.familyschool.familyschool.helper.Preferencias;
import br.com.familyschool.familyschool.model.Faltas;
import br.com.familyschool.familyschool.model.Frequencia;
import br.com.familyschool.familyschool.model.Turma;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class FrequenciaAlunoActivity extends AppCompatActivity {

    private String txtIdProfessor, NomeTurma,nomeAluno,nomeTurma;
    private DatabaseReference firebase;
    private ArrayList<Turma> turmas;
    private ValueEventListener valueEventListenerTurmas;
    private CompactCalendarView compactCalendarView;
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    private ArrayList<String> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frequencia_aluno);
        ButterKnife.inject(this);

        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendarView.setUseThreeLetterAbbreviation(true);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            txtIdProfessor = bundle.getString("idProfessor");
            nomeTurma = bundle.getString("nomeTurma");
        }

        turmas = new ArrayList<>();
        events = new ArrayList<>();

        Preferencias preferencias = new Preferencias(this);
        final String identificadorUsuarioLogado = preferencias.getIdentificador();
        firebase = ConfiguracaoFirebase.getFireBase().child("Presenca").child(txtIdProfessor).child(nomeTurma).child(identificadorUsuarioLogado);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    Faltas faltas = dados.getValue(Faltas.class);
                    events.add(faltas.getTimeInMillis());
                }
                localizarDatas();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                toolbar.setTitle(dateFormatForMonth.format(dateClicked));
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                toolbar.setTitle(dateFormatForMonth.format(firstDayOfNewMonth));
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

    private void localizarDatas(){
        for (String timeInMillis : events){
            long time = Long.parseLong(timeInMillis);
            Event ev1 = new Event(Color.GREEN, time);
            compactCalendarView.addEvent(ev1);
        }
    }
}
