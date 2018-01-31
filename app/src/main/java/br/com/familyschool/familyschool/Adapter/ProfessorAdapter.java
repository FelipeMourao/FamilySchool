package br.com.familyschool.familyschool.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import br.com.familyschool.familyschool.fragments.AtividadeFragment;
import br.com.familyschool.familyschool.fragments.ContatoFragment;
import br.com.familyschool.familyschool.fragments.FrequenciaFragment;

public class ProfessorAdapter extends FragmentStatePagerAdapter {

    private String[] titulosAbas = {"TAREFAS", "FREQUENCIA"};

    public ProfessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position){
            case 0:
                fragment = new AtividadeFragment();
                break;
            case 1:
                fragment = new FrequenciaFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return titulosAbas.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titulosAbas[position];
    }
}
