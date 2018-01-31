package br.com.familyschool.familyschool.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import br.com.familyschool.familyschool.fragments.AtividadeFragment;
import br.com.familyschool.familyschool.helper.Base64Custom;
import br.com.familyschool.familyschool.helper.Preferencias;
import br.com.familyschool.familyschool.helper.SlidingTabLayout;
import br.com.familyschool.familyschool.model.Contato;
import br.com.familyschool.familyschool.model.Frequencia;
import br.com.familyschool.familyschool.model.Turma;
import br.com.familyschool.familyschool.model.Usuario;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ResponsavelActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @InjectView(R.id.stl_tabs)
    SlidingTabLayout slidingTabLayout;
    @InjectView(R.id.vp_pagina)
    ViewPager viewPager;
    private DatabaseReference firebase;
    private String identificadorUsuario,identificadorUsuarioLogado,identificadorContato,codigoVerificado,codigoTurma,nomeTurma;
    private View header;
    private NavigationView navigationView;
    private FirebaseAuth autenticacao;
    private ArrayList<String> codigoTurmas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responsavel);
        ButterKnife.inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Family School");
        setSupportActionBar(toolbar);

        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);

        FirebaseMessaging.getInstance().subscribeToTopic("Notificacao");
        FirebaseMessaging.getInstance().subscribeToTopic("NotificacaoPresenca");

        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this,R.color.primary_darker));

        slidingTabLayout.setViewPager(viewPager);

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
            return true;
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
                Intent intent = new Intent(ResponsavelActivity.this, ListaProfessorActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_item_family_frequencia:
                Intent intent2 = new Intent(ResponsavelActivity.this, ProfessoresFrequenciaActivity.class);
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

    protected void replaceFragment(Fragment frag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.contaneir, frag, "TAG").commit();
    }

    private void voltar(){
        Intent intent = new Intent(ResponsavelActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}