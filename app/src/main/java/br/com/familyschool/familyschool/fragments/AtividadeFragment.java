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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.familyschool.familyschool.Adapter.TarefaAdapter;
import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.activity.TarefaProfessorActivity;
import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;
import br.com.familyschool.familyschool.helper.Preferencias;
import br.com.familyschool.familyschool.model.Tarefa;

public class AtividadeFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Tarefa> tarefas;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerTarefas;
    private Tarefa tarefaSelecionada;
    private String identificadorUsuarioLogado;

    public AtividadeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerTarefas);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerTarefas);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_atividade, container, false);

        //instancear objetos
        tarefas = new ArrayList<>();

        listView = (ListView) view.findViewById(R.id.lv_atividades);
        adapter = new TarefaAdapter(getActivity(),tarefas);
        listView.setAdapter(adapter);

        registerForContextMenu(listView);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                tarefaSelecionada = tarefas.get(position);
                return false;
            }
        });

        Preferencias preferencias = new Preferencias(getActivity());
        identificadorUsuarioLogado = preferencias.getIdentificador();
        firebase = ConfiguracaoFirebase.getFireBase().child("Tarefas").child(identificadorUsuarioLogado);

        valueEventListenerTarefas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Limpar tarefas
                tarefas.clear();

                //Listar tarefas
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Tarefa tarefa = dados.getValue(Tarefa.class);
                    tarefas.add(tarefa);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener(valueEventListenerTarefas);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tarefa tarefa = tarefas.get(position);
                Intent intent = new Intent(getActivity(), TarefaProfessorActivity.class);
                intent.putExtra("assunto", tarefa.getAssunto());
                intent.putExtra("nota",tarefa.getNota());
                intent.putExtra("data",tarefa.getDataEntrega());
                intent.putExtra("turma",tarefa.getNomeTurma());
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Selecione Opção");
        menu.add(Menu.NONE, 1, Menu.NONE, "Detalhes");
        menu.add(Menu.NONE, 2, Menu.NONE, "Excluir");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case 2:
                excluir();
                return true;
            case 1:
                detalhes();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void excluir(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Confirma a exclusão de : "+tarefaSelecionada.getAssunto()+"?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                firebase = ConfiguracaoFirebase.getFireBase().child("Tarefas").child(identificadorUsuarioLogado).child(tarefaSelecionada.getAssunto());
                firebase.removeValue();
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Não", null);
        AlertDialog dialog = builder.create();
        dialog.setTitle("Confirmação de operação");
        dialog.show();
    }

    private void detalhes(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Nome do Assunto: "+tarefaSelecionada.getAssunto()+"\nData de Entrega: "+tarefaSelecionada.getDataEntrega()+
        "\nDescrição: "+tarefaSelecionada.getDescricao());
        builder.setPositiveButton("OK",null);
        AlertDialog dialog = builder.create();
        dialog.setTitle("Detalhes");
        dialog.show();
    }
}