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
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.ArrayList;
import br.com.familyschool.familyschool.Adapter.TabAdapter;
import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.config.ConfiguracaoFirebase;
import br.com.familyschool.familyschool.helper.Base64Custom;
import br.com.familyschool.familyschool.helper.Preferencias;
import br.com.familyschool.familyschool.helper.SlidingTabLayout;
import br.com.familyschool.familyschool.model.Contato;
import br.com.familyschool.familyschool.model.Frequencia;
import br.com.familyschool.familyschool.model.Turma;
import br.com.familyschool.familyschool.model.Usuario;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference firebase;
    private String identificadorUsuario,identificadorUsuarioLogado,identificadorContato,codigoVerificado,codigoTurma,nomeTurma;
    private View header;
    private NavigationView navigationView;
    private FirebaseAuth autenticacao;
    private ArrayList<String> codigoTurmas;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Family School");
        setSupportActionBar(toolbar);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
        viewPager = (ViewPager) findViewById(R.id.vp_pagina);

        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);

        FirebaseMessaging.getInstance().subscribeToTopic("Notificacao");
        FirebaseMessaging.getInstance().subscribeToTopic("NotificacaoPresenca");

        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this,R.color.primary_darker));

        slidingTabLayout.setViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SobreActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch(item.getItemId()) {
           // case R.id.nav_item_family_consulta:
           //     replaceFragment(new AtividadeFragment());
           //     break;
            case R.id.nav_item_family_nota:
                Intent intent = new Intent(MainActivity.this, ListaProfessorActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_item_family_frequencia:
                Intent intent2 = new Intent(MainActivity.this, ProfessoresFrequenciaActivity.class);
                startActivity(intent2);
                break;
           // case R.id.nav_item_family_chat:
           //     replaceFragment(new AtividadeFragment());
           //     break;
           // case R.id.nav_item_family_contatos:
           //     replaceFragment(new ContatoFragment());
            //    break;
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
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void ExibeDialog(){
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.layout_dialog);

        //instancia os objetos que estão no layout
        final Button confirmar = (Button) dialog.findViewById(R.id.btn_Confirmar);
        final Button cancelar = (Button) dialog.findViewById(R.id.btn_Cancelar);
        final EditText editEmail = (EditText) dialog.findViewById(R.id.etEmail);
        final EditText editCodigo = (EditText) dialog.findViewById(R.id.etCodigo);
        SimpleMaskFormatter smf = new SimpleMaskFormatter("AAAAAAAA");
        MaskTextWatcher mtw = new MaskTextWatcher(editCodigo,smf);;
        editCodigo.addTextChangedListener(mtw);

        confirmar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String emailContato = editEmail.getText().toString();
                final String codigoContato = editCodigo.getText().toString();
                if (emailContato.isEmpty() || codigoContato.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Preencha o campos", Toast.LENGTH_LONG).show();
                } else {

                    //Verificar a existência do contato do Professor

                    identificadorContato = Base64Custom.codificarBase64(emailContato);
                    codigoVerificado = Base64Custom.codificarBase64(codigoContato);
                    codigoTurmas = new ArrayList<>();
                    codigoTurma = "";

                    //recuperar classe
                    firebase = ConfiguracaoFirebase.getFireBase();
                    firebase = firebase.child("Classe").child(identificadorContato);
                    firebase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            codigoTurmas.clear();

                            for (DataSnapshot dados: dataSnapshot.getChildren()) {
                                Turma turma = dados.getValue(Turma.class);
                                if (codigoVerificado.equals(turma.getCodigoTurma())){
                                    codigoTurma = codigoVerificado;
                                    nomeTurma = turma.getNomeTurma();
                                }
                            }
                            if (codigoTurma.isEmpty()) {
                                  AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
                                  builder2.setTitle("Adicionar Professor");
                                  builder2.setMessage("Turma ou Professor Não existe!");
                                  builder2.setCancelable(false);
                                  builder2.setPositiveButton("OK", null);
                                  AlertDialog alertDialog = builder2.create();
                                  alertDialog.show();
                            } else {
                                adicionarContato(nomeTurma);
                            }
                            dialog.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }}
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

    private void adicionarContato(final String nomeTurma){

            //adicionar na frequencia
            firebase = ConfiguracaoFirebase.getFireBase().child("Usuario").child(identificadorUsuarioLogado);
            firebase.addValueEventListener(new ValueEventListener() {
                @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {

                            //Recuperar o dados do aluno
                            Usuario usuario = dataSnapshot.getValue(Usuario.class);

                            firebase = ConfiguracaoFirebase.getFireBase();
                            firebase = firebase.child("Frequencia").child(identificadorContato).child(nomeTurma).child(identificadorUsuarioLogado);
                            Frequencia frequencia = new Frequencia();
                            frequencia.setIdUsuarioAluno(identificadorUsuarioLogado);
                            frequencia.setNomeUsuario(usuario.getNome());
                            firebase.setValue(frequencia);
                        }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //Recuperar os dados do Professor
            firebase = ConfiguracaoFirebase.getFireBase().child("Usuario").child(identificadorContato);
            firebase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {

                        //Recuperar o dados do contato
                        Usuario usuario = dataSnapshot.getValue(Usuario.class);

                        //recuperar identificador usuario logado (base64)
                        Preferencias preferencias = new Preferencias(MainActivity.this);
                        String identificadorUsuarioBase64 = preferencias.getIdentificador();

                        firebase = ConfiguracaoFirebase.getFireBase();
                        firebase = firebase.child("Contatos")
                                .child(identificadorUsuarioBase64)
                                .child(identificadorContato);

                        Contato contato = new Contato();
                        contato.setIdentificadorUsuario(identificadorContato);
                        contato.setEmail(usuario.getEmail());
                        contato.setNome(usuario.getNome());

                        firebase.setValue(contato);

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.MyAlertDialogStyle);
                        builder.setTitle("Adicionar Professor");
                        builder.setMessage("Turma e Professor adicionado com Sucesso!");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", null);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }
}