package br.com.familyschool.familyschool.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;
import br.com.familyschool.familyschool.helper.Preferencias;
import br.com.familyschool.familyschool.model.Faltas;
import br.com.familyschool.familyschool.model.Usuario;

public class FrequenciaAlunoActivity extends AppCompatActivity {

    private CalendarView mCalendarView;
    private List<EventDay> mEventDays;
    private String txtIdProfessor,nomeTurma,nomeAluno,identificadorUsuarioLogado;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerEvents;
    private ArrayList<Usuario> idAlunos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frequencia_aluno);

        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            txtIdProfessor = bundle.getString("idProfessor");
            nomeTurma = bundle.getString("nomeTurma");
        }

        mEventDays = new ArrayList<>();
        idAlunos = new ArrayList<>();



        Preferencias preferencias = new Preferencias(this);
        identificadorUsuarioLogado = preferencias.getIdentificador();
        firebase = ConfiguracaoFirebase.getFireBase().child("Usuario").child(identificadorUsuarioLogado);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               Usuario usuario = dataSnapshot.getValue(Usuario.class);
                firebase = ConfiguracaoFirebase.getFireBase().child("Presenca").child(txtIdProfessor).child(nomeTurma).child(usuario.getNome());
                firebase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dados : dataSnapshot.getChildren()) {
                            Faltas faltas = dados.getValue(Faltas.class);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(Long.parseLong(faltas.getTimeInMillis()));
                            mEventDays.add(new EventDay(calendar,R.drawable.ic_star));
                            mCalendarView.setEvents(mEventDays);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
