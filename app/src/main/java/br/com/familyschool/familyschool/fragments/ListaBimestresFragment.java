package br.com.familyschool.familyschool.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.familyschool.familyschool.Adapter.FrequenciaAdapter;
import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.activity.NotasActivity;
import br.com.familyschool.familyschool.activity.RecuperacaoActivity;
import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;
import br.com.familyschool.familyschool.helper.Preferencias;
import br.com.familyschool.familyschool.model.Turma;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListaBimestresFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Turma> turmas;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerTurmas;

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerTurmas);

    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerTurmas);
    }


    public ListaBimestresFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_lista_bimestres, container, false);

        turmas = new ArrayList<>();

        listView = (ListView) view.findViewById(R.id.lv_turmas);
        adapter = new FrequenciaAdapter(getActivity(),turmas);
        listView.setAdapter(adapter);

        final String bimestre = getArguments().getString("bimestre");

        Preferencias preferencias = new Preferencias(getActivity());
        String identificadorUsuarioLogado = preferencias.getIdentificador();
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
                    turmas.add(turma);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener(valueEventListenerTurmas);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Turma turma = turmas.get(position);
                if (bimestre.equals("Recuperação")){
                    Intent RecupIntent = new Intent(getActivity(), RecuperacaoActivity.class);
                    RecupIntent.putExtra("Turma",turma.getNomeTurma());
                    RecupIntent.putExtra("bimestre",bimestre);
                    startActivity(RecupIntent);
                } else {
                    Intent intent = new Intent(getActivity(), NotasActivity.class);
                    intent.putExtra("Turma",turma.getNomeTurma());
                    intent.putExtra("bimestre",bimestre);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

}
