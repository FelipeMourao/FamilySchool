package br.com.familyschool.familyschool.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.model.Tarefa;


public class TarefaAlunoAdapter extends ArrayAdapter<Tarefa> {

    private ArrayList<Tarefa> tarefas;
    private Context context;

    public TarefaAlunoAdapter(@NonNull Context cont, @NonNull ArrayList<Tarefa> objects) {
        super(cont,0, objects);
        this.tarefas = objects;
        this.context = cont;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;
        //Verificar se as tarefas está vazia
        if (tarefas != null){
            //iniciar a montagem
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            //Monta a view
            view = inflater.inflate(R.layout.lista_atividades_aluno,parent,false);
            //recupera elementos de exibição
            TextView assuntoTarefa = (TextView) view.findViewById(R.id.tv_assunto);

            Tarefa tarefa = tarefas.get(position);
            assuntoTarefa.setText(tarefa.getAssunto());
        }
        return view;
    }
}
