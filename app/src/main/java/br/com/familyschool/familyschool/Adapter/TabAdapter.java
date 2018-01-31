package br.com.familyschool.familyschool.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import br.com.familyschool.familyschool.fragments.AtividadeAlunoFragment;
import br.com.familyschool.familyschool.fragments.ContatoFragment;

public class TabAdapter extends FragmentStatePagerAdapter {

    private String[] titulosAbas = {"TAREFAS", "CONTATOS"};

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position){
            case 0:
                fragment = new AtividadeAlunoFragment();
                break;
            case 1:
                fragment = new ContatoFragment();
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
