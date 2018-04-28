package br.com.familyschool.familyschool.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import br.com.familyschool.familyschool.Adapter.ProfessorAdapter;
import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;
import br.com.familyschool.familyschool.helper.Base64Custom;
import br.com.familyschool.familyschool.helper.SlidingTabLayout;
import br.com.familyschool.familyschool.model.Turma;
import br.com.familyschool.familyschool.model.Usuario;

public class ProfessorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private DatabaseReference firebase;
    private String identificadorUsuario,identificadorUsuarioLogado;
    private View header;
    private NavigationView navigationView;
    private FirebaseAuth autenticacao;
    private String codigoVazio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Family School");
        setSupportActionBar(toolbar);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
        viewPager = (ViewPager) findViewById(R.id.vp_pagina);

        ProfessorAdapter professorAdapter = new ProfessorAdapter(getSupportFragmentManager());
        viewPager.setAdapter(professorAdapter);

        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this,R.color.primary_darker));

        slidingTabLayout.setViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExibeDialog();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null){
            identificadorUsuario = bundle.getString("email");
        }

        identificadorUsuarioLogado = Base64Custom.codificarBase64(identificadorUsuario);
        firebase = ConfiguracaoFirebase.getFireBase().child("Usuario").child(identificadorUsuarioLogado);

        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                TextView tEmail = (TextView) header.findViewById(R.id.text_email);
                TextView tNome = (TextView) header.findViewById(R.id.text_nome);
                tEmail.setText(usuario.getEmail());
                tNome.setText(usuario.getNome());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_professor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_settings:
                startActivity(new Intent(this, SobreActivity.class));
                break;
            case R.id.adicionar_tarefa:
                Intent TarefaIntent = new Intent(ProfessorActivity.this, TarefaActivity.class);
                startActivity(TarefaIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch(item.getItemId()) {
            case R.id.nav_item_family_nota:
                Intent intent = new Intent(ProfessorActivity.this, BimestreActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_item_family_frequencia:
                viewPager.setCurrentItem(1);
                break;
           // case R.id.nav_item_family_chat:
           //     replaceFragment(new AtividadeFragment());
           //     break;
          //  case R.id.nav_item_family_contatos:
          //      replaceFragment(new AtividadeFragment());
          //      break;
            case R.id.nav_item_family_tarefas:
                viewPager.setCurrentItem(0);
                break;
            case R.id.ic_sair:
                autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                autenticacao.signOut();
                voltar();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void voltar(){
        Intent intent = new Intent(ProfessorActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void ExibeDialog(){
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.layout_dialog_professor);

        dialog.setCancelable(false);

        //instancia os objetos que estão no layout
        final Button confirmar = (Button) dialog.findViewById(R.id.btn_Confirmar);
        final Button cancelar = (Button) dialog.findViewById(R.id.btn_Cancelar);
        final EditText editTurma = (EditText) dialog.findViewById(R.id.etTurma);

        confirmar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String nometurma = editTurma.getText().toString();
                if (nometurma.isEmpty()) {
                    Toast.makeText(ProfessorActivity.this, "Preencha o campos", Toast.LENGTH_LONG).show();
                } else  {
                    //Gerar codigo da turma
                    codigoVazio = gerarSenhaAleatoria();

                    String codigoCodificadoBase64 = Base64Custom.codificarBase64(codigoVazio);

                    Turma turma = new Turma();
                    turma.setIdUsuario(identificadorUsuarioLogado);
                    turma.setNomeTurma(nometurma);
                    turma.setCodigoTurma(codigoCodificadoBase64);

                    firebase = ConfiguracaoFirebase.getFireBase();
                    firebase = firebase.child("Classe").child(identificadorUsuarioLogado).child(nometurma);
                    firebase.setValue(turma);

                    FirebaseMessaging.getInstance().subscribeToTopic("NotificacaoPresenca");
                }
                dialog.dismiss();

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfessorActivity.this,R.style.MyAlertDialogStyle);
                builder.setTitle("Geração de Código");
                builder.setMessage("Turma Criada! O Código é " + codigoVazio);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //finaliza o dialog
                dialog.dismiss();
            }
        });
        //exibe na tela o dialog
        dialog.show();
    }

    private static String gerarSenhaAleatoria() {
        int qtdeMaximaCaracteres = 8;
        String[] caracteres = { "a", "1", "b", "2", "4", "5", "6", "7", "8",
                "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
                "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w",
                "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I",
                "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
                "V", "W", "X", "Y", "Z" };

        StringBuilder senha = new StringBuilder();

        for (int i = 0; i < qtdeMaximaCaracteres; i++) {
            int posicao = (int) (Math.random() * caracteres.length);
            senha.append(caracteres[posicao]);
        }
        return senha.toString();
    }
}
