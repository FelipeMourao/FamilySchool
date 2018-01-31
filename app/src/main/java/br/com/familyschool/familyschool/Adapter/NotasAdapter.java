package br.com.familyschool.familyschool.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;
import br.com.familyschool.familyschool.helper.Preferencias;
import br.com.familyschool.familyschool.model.Frequencia;
import br.com.familyschool.familyschool.model.NotaBimestre;
import br.com.familyschool.familyschool.model.NotaLancamento;

public class NotasAdapter extends RecyclerView.Adapter {

    private ArrayList<Frequencia> alunos;
    private Context context;
    private String turma,bimestre,identificadorProfessor;
    private ProgressDialog progressDialog;
    private DatabaseReference firebase;
    private Double nota1, nota2,media;

    public NotasAdapter(ArrayList<Frequencia> alunos, Context context,String turma, String bimestre, String identificadorProfessor) {
        this.alunos = alunos;
        this.turma = turma;
        this.identificadorProfessor = identificadorProfessor;
        this.bimestre = bimestre;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.lista_notas, parent, false);

        return new NotaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final NotaViewHolder viewHolder = (NotaViewHolder) holder;

        final Frequencia aluno = alunos.get(position);

        viewHolder.nota1.setText("0.0");

        Preferencias preferencias = new Preferencias(context);
        String identificadorUsuarioLogado = preferencias.getIdentificador();
        firebase = ConfiguracaoFirebase.getFireBase().child("NotasBimestre").child(identificadorUsuarioLogado).child(turma).child(bimestre);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    NotaBimestre notaBimestre = dados.getValue(NotaBimestre.class);
                    viewHolder.nota1.setText(notaBimestre.getNota1());
                    viewHolder.nota2.setText(notaBimestre.getNota2());
                    viewHolder.media.setText(notaBimestre.getMedia());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        firebase = ConfiguracaoFirebase.getFireBase().child("NotaLancamento").child(identificadorProfessor).child(turma);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    NotaLancamento notaLancamento = dados.getValue(NotaLancamento.class);
                    if (notaLancamento.getBimestre().equals(bimestre) && notaLancamento.getIdAluno().equals(aluno.getIdUsuarioAluno())) {
                        viewHolder.nota1.setText(notaLancamento.getNotaLancada());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        viewHolder.nome.setText(aluno.getNomeUsuario());

        viewHolder.nota2.setText("0.0");

        viewHolder.nota1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final AlertDialog.Builder d = new AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.number_picker_dialog, null);
                d.setTitle("Selecione a nota:");
                d.setView(dialogView);
                final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);
                final NumberPicker numberPicker2 = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker2);
                numberPicker.setMaxValue(10);
                numberPicker.setMinValue(0);
                numberPicker.setWrapSelectorWheel(false);
                numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        Log.i("NUMBERPICKRER", "onValueChange: ");
                    }
                });
                numberPicker2.setMaxValue(9);
                numberPicker2.setMinValue(0);
                numberPicker2.setWrapSelectorWheel(false);
                numberPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        Log.e("NUMBERPICKRER", "onValueChange: ");
                    }
                });
                d.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String nota2Vazio = viewHolder.nota2.getText().toString();
                        String nota1Vazio = viewHolder.nota1.getText().toString();
                        String mediaVazio  = viewHolder.media.getText().toString();
                        if (nota1Vazio.isEmpty()){
                            Toast.makeText(context,"Preenche os campos",Toast.LENGTH_LONG).show();
                        } else {
                            viewHolder.nota1.setText(numberPicker.getValue() + "." + numberPicker2.getValue());
                            nota1 = Double.parseDouble(numberPicker.getValue() + "." + numberPicker2.getValue());
                            nota2 = Double.parseDouble(nota2Vazio);
                            media = 0.0;
                            media = (nota1 + nota2);
                            String mediaFinal = media.toString();
                            viewHolder.media.setText(mediaFinal);
                        }
                    }
                });
                d.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog alertDialog = d.create();
                alertDialog.show();
                return false;
            }
        });

        viewHolder.nota2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final AlertDialog.Builder d = new AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.number_picker_dialog, null);
                d.setTitle("Selecione a nota:");
                d.setView(dialogView);
                final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);
                final NumberPicker numberPicker2 = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker2);
                numberPicker.setMaxValue(10);
                numberPicker.setMinValue(0);
                numberPicker.setWrapSelectorWheel(false);
                numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        Log.i("NUMBERPICKRER", "onValueChange: ");
                    }
                });
                numberPicker2.setMaxValue(9);
                numberPicker2.setMinValue(0);
                numberPicker2.setWrapSelectorWheel(false);
                numberPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        Log.e("NUMBERPICKRER", "onValueChange: ");
                    }
                });
                d.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String nota2Vazio = viewHolder.nota2.getText().toString();
                        String nota1Vazio = viewHolder.nota1.getText().toString();
                        String mediaVazio  = viewHolder.media.getText().toString();
                        if (nota2Vazio.isEmpty()){
                            Toast.makeText(context,"Preenche os campos",Toast.LENGTH_LONG).show();
                        } else {
                            nota1 = Double.parseDouble(nota1Vazio);
                            nota2 = Double.parseDouble(numberPicker.getValue() + "." + numberPicker2.getValue());
                            viewHolder.nota2.setText(numberPicker.getValue() + "." + numberPicker2.getValue());
                            media = Double.parseDouble(mediaVazio);
                            media = (nota1 + nota2) /2;
                            String mediaFinal = media.toString();
                            viewHolder.media.setText(mediaFinal);
                        }
                    }
                });
                d.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog alertDialog = d.create();
                alertDialog.show();
                return false;
            }
        });

        viewHolder.salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(context,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Salvando...");
                progressDialog.show();

                Preferencias preferencias = new Preferencias(context);
                String identificadorUsuarioLogado = preferencias.getIdentificador();
                firebase = ConfiguracaoFirebase.getFireBase().child("NotasBimestre").child(identificadorUsuarioLogado);
                firebase = firebase.child(turma).child(bimestre).child(aluno.getIdUsuarioAluno());
                NotaBimestre notaBimestre = new NotaBimestre();
                notaBimestre.setNota1(viewHolder.nota1.getText().toString());
                notaBimestre.setNota2(viewHolder.nota2.getText().toString());
                notaBimestre.setMedia(viewHolder.media.getText().toString());
                notaBimestre.setIdUsuario(aluno.getIdUsuarioAluno());
                firebase.setValue(notaBimestre);

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.MyAlertDialogStyle);
                        builder.setMessage("Nota Salvo com Sucesso!");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", null);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                },5000);

            }
        });
    }

    @Override
    public int getItemCount() {
        return alunos.size();
    }

}
