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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestipork.R;

import java.util.List;

public class LoteAdapter extends RecyclerView.Adapter<LoteAdapter.LoteViewHolder> {

    private List<Lotes> listaLotes;
    private Context context;
    private OnLoteClickListener listener;
    private int selectedPosition = -1;   // ✅ NUEVO: posición seleccionada

    public LoteAdapter(Context context, List<Lotes> listaLotes) {
        this.context = context;
        this.listaLotes = listaLotes;
    }

    public void setOnLoteClickListener(OnLoteClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public LoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lote, parent, false);
        return new LoteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LoteViewHolder holder, int position) {
        final Lotes lote = listaLotes.get(position);          // ✅ lote final
        final int adapterPosition = position;                // ✅ posición final

        holder.txtCodLote.setText(lote.getCod_lote());
        holder.txtCodItaca.setText("AA123456 " + lote.getCod_itaca());
        holder.txtRaza.setText(lote.getRaza());
        holder.txtDisponibles.setText(String.valueOf(lote.getnDisponibles()));

        try {
            int color = Color.parseColor(lote.getColor());
            holder.viewColor.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        } catch (Exception e) {
            holder.viewColor.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        }

        // ✅ Resaltar si es seleccionado
        if (selectedPosition == adapterPosition) {
            holder.itemView.setBackgroundResource(R.drawable.selected_card_border);  // crea este drawable
        } else {
            holder.itemView.setBackgroundResource(0); // sin fondo especial
        }

        // ✅ Click normal → solo abre Detalle
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleLoteActivity.class);
            intent.putExtra("cod_lote", lote.getCod_lote());
            intent.putExtra("cod_explotacion", lote.getCod_explotacion());
            ((AppCompatActivity) context).startActivityForResult(intent, 1001);
        });

        // ✅ Long click → selecciona para acciones (Contar, Pesar, Bajas)
        holder.itemView.setOnLongClickListener(v -> {
            if (selectedPosition == adapterPosition) {
                // ✅ Si ya estaba seleccionado → des-seleccionar
                selectedPosition = -1;
            } else {
                // ✅ Nuevo lote seleccionado
                selectedPosition = adapterPosition;
                if (listener != null) {
                    listener.onContarClick(lote);
                }
            }
            notifyDataSetChanged();  // refresca para marcar/desmarcar
            return true;
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

    // ✅ Interfaz para devolver lote seleccionado
    public interface OnLoteClickListener {
        void onContarClick(Lotes lote);
    }
}
