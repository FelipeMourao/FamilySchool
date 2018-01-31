package br.com.familyschool.familyschool.Adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.model.Turma;


public class FrequenciaAdapter extends ArrayAdapter<Turma> {

    private ArrayList<Turma> turmas;
    private Context context;

    public FrequenciaAdapter(@NonNull Context cont, @NonNull ArrayList<Turma> objects) {
        super(cont,0, objects);
        this.turmas = objects;
        this.context = cont;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;
        if (turmas != null){
            //inicializar objeto para montagem das views
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            //montar layout
            view = inflater.inflate(R.layout.lista_frequencia,parent,false);
            //recuperar elementos para exibição
            TextView nomeTurma = (TextView) view.findViewById(R.id.tv_turma);

            Turma turma = turmas.get(position);
            nomeTurma.setText(turma.getNomeTurma());
        }

        return view;
    }
}
