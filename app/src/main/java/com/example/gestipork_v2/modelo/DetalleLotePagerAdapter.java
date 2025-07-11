package com.example.gestipork_v2.modelo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.gestipork_v2.modelo.tabs.AccionesFragment;
import com.example.gestipork_v2.modelo.tabs.SalidasFragment;

public class DetalleLotePagerAdapter extends FragmentStateAdapter {

    private final String idLote;
    private final String idExplotacion;

    public DetalleLotePagerAdapter(@NonNull FragmentActivity activity, String idLote, String idExplotacion) {
        super(activity);
        this.idLote = idLote;
        this.idExplotacion = idExplotacion;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle args = new Bundle();
        args.putString("id_lote", idLote);
        args.putString("id_explotacion", idExplotacion);

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
