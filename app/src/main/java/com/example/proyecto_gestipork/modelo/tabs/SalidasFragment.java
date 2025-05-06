package com.example.proyecto_gestipork.modelo.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestipork.R;

import java.util.ArrayList;
import java.util.List;

public class SalidasFragment extends Fragment {

    private RecyclerView recyclerView;
    private SalidaAdapter adapter;
    private List<SalidaTab> listaSalidas;

    public SalidasFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_salidas, container, false);

        recyclerView = vista.findViewById(R.id.recycler_salidas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listaSalidas = new ArrayList<>();
        listaSalidas.add(new SalidaTab("Sacrificio", "04/05/2025", 12));
        listaSalidas.add(new SalidaTab("Fallecimiento", "05/05/2025", 4));

        adapter = new SalidaAdapter(listaSalidas);
        recyclerView.setAdapter(adapter);

        return vista;
    }
}
