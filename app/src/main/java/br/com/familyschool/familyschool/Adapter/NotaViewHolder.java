package br.com.familyschool.familyschool.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.familyschool.familyschool.R;


public class NotaViewHolder extends RecyclerView.ViewHolder {

    TextView nome;
    TextView nota1;
    TextView nota2;
    TextView media;
    ImageView salvar;

    public NotaViewHolder(View itemView) {
        super(itemView);
        nome = (TextView) itemView.findViewById(R.id.txt_nome);
        media = (TextView) itemView.findViewById(R.id.txt_media_bim);
        nota1 = (TextView) itemView.findViewById(R.id.ed_nota1);
        nota2 = (TextView) itemView.findViewById(R.id.ed_nota2);
        salvar = (ImageView) itemView.findViewById(R.id.btn_salvar_nota);
    }
}
