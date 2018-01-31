package br.com.familyschool.familyschool.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.com.familyschool.familyschool.Adapter.TarefaAlunoAdapter;
import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.activity.TarefaAlunoActivity;
import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;
import br.com.familyschool.familyschool.helper.Base64Custom;
import br.com.familyschool.familyschool.helper.Preferencias;
import br.com.familyschool.familyschool.model.Contato;
import br.com.familyschool.familyschool.model.Tarefa;
import br.com.familyschool.familyschool.model.Usuario;

public class AtividadeAlunoFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Tarefa> tarefas, tarefaNot;
    private ArrayList<String> turmas, turmasEncontrados;
    private DatabaseReference firebase;
    private ArrayList<String> ProfessorContatos, professoresEncontrados;
    private ValueEventListener valueEventListenerTarefas, valueEventListenerProfessor;
    private String emailLogado;
    private ProgressDialog progressDialog;

    public AtividadeAlunoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_atividade_aluno, container, false);

        tarefas = new ArrayList<>();
        ProfessorContatos = new ArrayList<>();
        professoresEncontrados = new ArrayList<>();
        turmas = new ArrayList<>();
        tarefaNot = new ArrayList<>();
        turmasEncontrados = new ArrayList<>();

        SimpleDateFormat formataData = new SimpleDateFormat("dd/MM/yyyy");
        Date data = new Date();
        final String dataFormatada = formataData.format(data);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            emailLogado = user.getEmail();
        }

        final String identificadorEmailCodificado = Base64Custom.codificarBase64(emailLogado);

        listView = (ListView) view.findViewById(R.id.lv_atividades);
        adapter = new TarefaAlunoAdapter(getActivity(),tarefas);
        listView.setAdapter(adapter);

        Preferencias preferencias = new Preferencias(getActivity());
        final String identificadorUsuarioLogado = preferencias.getIdentificador();
        firebase = ConfiguracaoFirebase.getFireBase().child("Contatos").child(identificadorUsuarioLogado);

        valueEventListenerProfessor = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    Contato contato = dados.getValue(Contato.class);
                    ProfessorContatos.add(contato.getIdentificadorUsuario());
                }
                adicionarTarefas(ProfessorContatos);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener(valueEventListenerProfessor);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Tarefa tarefa = tarefas.get(position);
                final int comparacaoData = dataFormatada.compareTo(tarefa.getDataEntrega());

                firebase = ConfiguracaoFirebase.getFireBase().child("Usuario").child(identificadorEmailCodificado);
                firebase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Usuario usuario = dataSnapshot.getValue(Usuario.class);
                        if (usuario.getTipoPessoa().equals("Aluno")){
                            if (tarefa.getUrlConteudo().isEmpty() && comparacaoData <= 0){
                                Intent intent = new Intent(getActivity(),TarefaAlunoActivity.class);
                                intent.putExtra("assunto", tarefa.getAssunto());
                                intent.putExtra("professor", tarefa.getIdProfessor());
                                intent.putExtra("descricao", tarefa.getDescricao());
                                intent.putExtra("nota", tarefa.getNota());
                                intent.putExtra("data", tarefa.getDataEntrega());
                                intent.putExtra("turma",tarefa.getNomeTurma());
                                intent.putExtra("urlConteudo", "");
                                startActivity(intent);
                            } else if (comparacaoData <= 0){
                                Intent intent = new Intent(getActivity(),TarefaAlunoActivity.class);
                                intent.putExtra("assunto", tarefa.getAssunto());
                                intent.putExtra("professor", tarefa.getIdProfessor());
                                intent.putExtra("descricao", tarefa.getDescricao());
                                intent.putExtra("nota", tarefa.getNota());
                                intent.putExtra("data", tarefa.getDataEntrega());
                                intent.putExtra("turma",tarefa.getNomeTurma());
                                intent.putExtra("urlConteudo",tarefa.getUrlConteudo());
                                startActivity(intent);
                            } else if (comparacaoData > 0){
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.MyAlertDialogStyle);
                                builder.setMessage("O Tempo de entrega expirado!");
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", null);
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        } else if (usuario.getTipoPessoa().equals("Responsavel")){
                            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Data da Entrega: " + tarefa.getDataEntrega());
                            builder.setPositiveButton("OK",null);
                            AlertDialog dialog = builder.create();
                            dialog.setTitle("Detalhe");
                            dialog.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        return view;
    }

   private void adicionarTarefas(ArrayList<String> contatos) {
       for (String contato : contatos) {
               firebase = ConfiguracaoFirebase.getFireBase().child("Tarefas").child(contato);

               valueEventListenerTarefas = new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       //Listar as tarefas
                       for (DataSnapshot dados : dataSnapshot.getChildren()) {
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
           }
   }
}
