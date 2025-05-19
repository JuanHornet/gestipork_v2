package com.example.proyecto_gestipork.modelo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestipork.R;
import com.example.proyecto_gestipork.base.ColorUtils;
import com.example.proyecto_gestipork.data.DBHelper;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class LoteAdapter extends RecyclerView.Adapter<LoteAdapter.LoteViewHolder> {

    private List<Lotes> listaLotes;
    private Context context;
    private OnLoteClickListener listener;
    private int selectedPosition = -1;

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
    public void onBindViewHolder(@NonNull LoteViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Lotes lote = listaLotes.get(position);

        holder.txtCodLote.setText(lote.getCod_lote());
        holder.txtRaza.setText(lote.getRaza());
        holder.txtDisponibles.setText(String.valueOf(lote.getnDisponibles()));

        // obtener DCER desde tabla itaca
        DBHelper dbHelper = new DBHelper(context);
        String dcer = "";
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT DCER FROM itaca WHERE cod_lote = ? AND cod_explotacion = ?",
                new String[]{lote.getCod_lote(), lote.getCod_explotacion()}
        );
        if (cursor.moveToFirst()) {
            dcer = cursor.getString(0);
        }
        cursor.close();

        holder.txtCodItaca.setText(dcer);   // ✅ Mostrar DCER directamente

        // ✅ Usa ColorUtils para el círculo
        int color = ColorUtils.mapColorNameToHex(context, lote.getColor());
        holder.viewColor.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        MaterialCardView cardView = holder.cardView;
        if (selectedPosition == position) {
            cardView.setStrokeColor(androidx.core.content.ContextCompat.getColor(context, R.color.verde));
            cardView.setStrokeWidth(12);
        } else {
            cardView.setStrokeColor(androidx.core.content.ContextCompat.getColor(context, R.color.gray));
            cardView.setStrokeWidth(0);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleLoteActivity.class);
            intent.putExtra("cod_lote", lote.getCod_lote());
            intent.putExtra("cod_explotacion", lote.getCod_explotacion());
            ((AppCompatActivity) context).startActivityForResult(intent, 1001);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (selectedPosition == position) {
                selectedPosition = -1;
            } else {
                selectedPosition = position;
                if (listener != null) {
                    listener.onContarClick(lote);
                }
            }
            notifyDataSetChanged();
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
        MaterialCardView cardView;

        public LoteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCodLote = itemView.findViewById(R.id.txt_cod_lote);
            txtCodItaca = itemView.findViewById(R.id.txt_cod_itaca);
            txtRaza = itemView.findViewById(R.id.txt_raza);
            txtDisponibles = itemView.findViewById(R.id.txt_n_disponibles);
            viewColor = itemView.findViewById(R.id.view_color);
            cardView = itemView.findViewById(R.id.card_view);

        }
    }

    public interface OnLoteClickListener {
        void onContarClick(Lotes lote);
    }
}
