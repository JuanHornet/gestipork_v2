package com.example.proyecto_gestipork.modelo;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestipork.R;

import java.util.List;

public class LoteAdapter extends RecyclerView.Adapter<LoteAdapter.LoteViewHolder> {

    private List<Lotes> listaLotes;

    public LoteAdapter(List<Lotes> listaLotes) {
        this.listaLotes = listaLotes;
    }

    @NonNull
    @Override
    public LoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lote, parent, false);
        return new LoteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LoteViewHolder holder, int position) {
        Lotes lote = listaLotes.get(position);
        holder.txtCodLote.setText(lote.getCod_lote());
        holder.txtCodItaca.setText("Código ITACA: " + lote.getCod_itaca());
        holder.txtRaza.setText("Raza: " + lote.getRaza());
        holder.txtDisponibles.setText(lote.getnDisponibles() + " disponibles");

        // Aplicar color desde el campo 'color' que viene de la tabla itaca
        try {
            int color = Color.parseColor(lote.getColor()); // ejemplo: "#FF5722"
            holder.viewColor.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        } catch (Exception e) {
            holder.viewColor.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN); // color por defecto
        }
    }

    @Override
    public int getItemCount() {
        return listaLotes.size();
    }

    static class LoteViewHolder extends RecyclerView.ViewHolder {
        TextView txtCodLote, txtCodItaca, txtRaza, txtDisponibles;
        View viewColor;

        public LoteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCodLote = itemView.findViewById(R.id.txt_cod_lote);
            txtCodItaca = itemView.findViewById(R.id.txt_cod_itaca);
            txtRaza = itemView.findViewById(R.id.txt_raza);
            txtDisponibles = itemView.findViewById(R.id.txt_n_disponibles);
            viewColor = itemView.findViewById(R.id.view_color);
        }
    }
}
