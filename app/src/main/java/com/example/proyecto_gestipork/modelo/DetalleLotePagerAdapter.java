package com.example.proyecto_gestipork.modelo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.proyecto_gestipork.modelo.tabs.AccionesFragment;
import com.example.proyecto_gestipork.modelo.tabs.SalidasFragment;

public class DetalleLotePagerAdapter extends FragmentStateAdapter {

    public DetalleLotePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return (position == 0) ? new AccionesFragment() : new SalidasFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
