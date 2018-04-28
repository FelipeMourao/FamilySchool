package br.com.familyschool.familyschool.activity;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.com.familyschool.familyschool.R;
import br.com.familyschool.familyschool.fragments.ListaBimestresFragment;

public class BimestreActivity extends AppCompatActivity {

    private ListView listaBimetres;
    private String[] itens = {"1º Bimestre","2º Bimestre","3º Bimestre","4º Bimestre","Recuperação"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bimestre);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Family School");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        listaBimetres = (ListView) findViewById(R.id.lv_turmas1);

        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(
                getApplicationContext(),
                R.layout.lista_bimestre,
                itens    );
        listaBimetres.setAdapter(adaptador);

        listaBimetres.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("bimestre", itens[position]);
                ListaBimestresFragment listaBimestresFragment = new ListaBimestresFragment();
                listaBimestresFragment.setArguments(bundle);
                replaceFragment(listaBimestresFragment);
                listaBimetres.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void replaceFragment(Fragment frag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.rl_contaneir_fragment, frag, "TAG").commit();
    }
}
