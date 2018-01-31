package br.com.familyschool.familyschool.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.model.NotaBimestre;

public class NotasAlunoAdapter extends RecyclerView.Adapter<NotasAlunoAdapter.MyViewHolder> {


    private ArrayList<String> bimestres;
    private ArrayList<NotaBimestre> notas;

    public NotasAlunoAdapter(ArrayList<String> bimestres, ArrayList<NotaBimestre> notas) {
        this.bimestres = bimestres;
        this.notas = notas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listaNotas = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_notas_aluno,parent,false);
        return new MyViewHolder(listaNotas);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        NotaBimestre notasBimestre = notas.get(position);
        String Bimestre = bimestres.get(position);

        holder.bimestre.setText(Bimestre);
        holder.nota1.setText(notasBimestre.getNota1());
        holder.nota2.setText(notasBimestre.getNota2());
        holder.media.setText(notasBimestre.getMedia());
    }

    @Override
    public int getItemCount() {
        return notas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView bimestre;
        TextView nota1;
        TextView nota2;
        TextView media;

        public MyViewHolder(View itemView) {
            super(itemView);
            bimestre = (TextView) itemView.findViewById(R.id.bimestres);
            nota1 = (TextView) itemView.findViewById(R.id.bm_nota1);
            nota2 = (TextView) itemView.findViewById(R.id.bm_nota2);
            media = (TextView) itemView.findViewById(R.id.bm_media);
        }
    }

}
