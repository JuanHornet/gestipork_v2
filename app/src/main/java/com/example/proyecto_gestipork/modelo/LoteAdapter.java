package com.example.proyecto_gestipork.modelo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestipork.modelo.DetalleLoteActivity;
import com.example.proyecto_gestipork.R;

import java.util.List;

public class LoteAdapter extends RecyclerView.Adapter<LoteAdapter.LoteViewHolder> {

    private List<Lotes> listaLotes;
    private Context context;

    public LoteAdapter(Context context, List<Lotes> listaLotes) {
        this.context = context;
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
        holder.txtCodItaca.setText("AA123456 " + lote.getCod_itaca());
        holder.txtRaza.setText(lote.getRaza());
        Integer disponibles = lote.getnDisponibles();
        holder.txtDisponibles.setText(disponibles != null ? String.valueOf(disponibles) : "0");


        // Aplicar color desde el campo 'color' que viene de la tabla itaca
        try {
            int color = Color.parseColor(lote.getColor()); // ejemplo: "#FF5722"
            holder.viewColor.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        } catch (Exception e) {
            holder.viewColor.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN); // color por defecto
        }

        // Clic para ir a DetalleLoteActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, com.example.proyecto_gestipork.modelo.DetalleLoteActivity.class);

            intent.putExtra("lote_id", lote.getId());
            context.startActivity(intent);
        });
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
