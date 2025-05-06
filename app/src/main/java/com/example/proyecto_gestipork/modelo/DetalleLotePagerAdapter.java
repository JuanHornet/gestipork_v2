package com.example.proyecto_gestipork.modelo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.proyecto_gestipork.modelo.tabs.AccionesFragment;
import com.example.proyecto_gestipork.modelo.tabs.SalidasFragment;

public class DetalleLotePagerAdapter extends FragmentStateAdapter {

    private final String codLote;
    private final String codExplotacion;

    public DetalleLotePagerAdapter(@NonNull FragmentActivity activity, String codLote, String codExplotacion) {
        super(activity);
        this.codLote = codLote;
        this.codExplotacion = codExplotacion;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle args = new Bundle();
        args.putString("cod_lote", codLote);
        args.putString("cod_explotacion", codExplotacion);

        Fragment fragment;
        if (position == 0) {
            fragment = new AccionesFragment();
        } else {
            fragment = new SalidasFragment();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
