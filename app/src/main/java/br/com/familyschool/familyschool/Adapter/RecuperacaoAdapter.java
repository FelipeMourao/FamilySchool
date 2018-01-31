package br.com.familyschool.familyschool.Adapter;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecuperacaoAdapter extends RecyclerView.Adapter<RecuperacaoAdapter.MyViewHolder> {

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nome;
        TextView nota1;
        TextView nota2;
        TextView media;
        ImageView salvar;

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

}
