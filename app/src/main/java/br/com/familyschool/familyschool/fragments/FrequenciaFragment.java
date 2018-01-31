package br.com.familyschool.familyschool.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.familyschool.familyschool.Adapter.FrequenciaAdapter;
import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.activity.FrequenciaActivity;
import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;
import br.com.familyschool.familyschool.helper.Base64Custom;
import br.com.familyschool.familyschool.helper.Preferencias;
import br.com.familyschool.familyschool.model.Turma;

/**
 * A simple {@link Fragment} subclass.
 */
public class FrequenciaFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Turma> turmas;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerTurmas;
    private Turma turmaSelecionada;
    private String identificadorUsuarioLogado;

    public FrequenciaFragment() {
        // Required empty public constructor
    }

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frequencia, container, false);

        //TextView textView = (TextView) view.findViewById(R.id.txt_nada);

        turmas = new ArrayList<>();

        listView = (ListView) view.findViewById(R.id.lv_frequencia);
        adapter = new FrequenciaAdapter(getActivity(),turmas);
        listView.setAdapter(adapter);

        registerForContextMenu(listView);

        //if (turmas == null){
        //    textView.setEnabled(true);
        //} else {
         //   textView.setEnabled(false);
        //}
            //recuperar turmas
             Preferencias preferencias = new Preferencias(getActivity());
             identificadorUsuarioLogado = preferencias.getIdentificador();
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
                    Intent intent = new Intent(getActivity(), FrequenciaActivity.class);
                    intent.putExtra("Turma",turma.getNomeTurma());
                    intent.putExtra("IdProfessor",turma.getIdUsuario());
                    startActivity(intent);
                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    turmaSelecionada = turmas.get(position);
                    return false;
                }
            });

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Selecione Opçao");
        menu.add(0, v.getId(), 0, "Detalhes");
        menu.add(0,v.getId(),0,"Excluir");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle()=="Detalhes"){
            detalhesFrequencia();
        }
        else if (item.getTitle()=="Excluir"){
            excluirFrequencia();
        }
        return super.onContextItemSelected(item);
    }

    private void excluirFrequencia(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Confirma a exclusão de : "+turmaSelecionada.getNomeTurma()+"?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                firebase = ConfiguracaoFirebase.getFireBase().child("Classe").child(identificadorUsuarioLogado).child(turmaSelecionada.getNomeTurma());
                firebase.removeValue();

                firebase = ConfiguracaoFirebase.getFireBase().child("Frequencia").child(identificadorUsuarioLogado).child(turmaSelecionada.getNomeTurma());
                firebase.removeValue();

                firebase = ConfiguracaoFirebase.getFireBase().child("Tarefas").child(identificadorUsuarioLogado);
                firebase.removeValue();

                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Não", null);
        AlertDialog dialog = builder.create();
        dialog.setTitle("Confirmação de operação");
        dialog.show();
    }

    private void detalhesFrequencia(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String CodigoDecodificado = Base64Custom.decodificarBase64(turmaSelecionada.getCodigoTurma());
        builder.setMessage("Nome da Turma: "+turmaSelecionada.getNomeTurma()+"\nCodigo da Turma: "+CodigoDecodificado);
        builder.setPositiveButton("OK",null);
        AlertDialog dialog = builder.create();
        dialog.setTitle("Detalhes");
        dialog.show();
    }
}
