package com.example.gestipork_v2.modelo;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestipork_v2.R;
import com.example.gestipork_v2.data.DBHelper;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ResumenViewHolder> {

    private final Context context;
    private final String codExplotacion;

    public DashboardAdapter(Context context, String codExplotacion) {
        this.context = context;
        this.codExplotacion = codExplotacion;
    }

    @NonNull
    @Override
    public ResumenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dashboard_activity, parent, false);
        return new ResumenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResumenViewHolder holder, int position) {
        DBHelper db = new DBHelper(context);

        int totalIb100 = db.obtenerAnimalesPorRaza(codExplotacion, "Ibérico 100%");
        int totalCruz50 = db.obtenerAnimalesPorRaza(codExplotacion, "Cruzado 50%");
        int totalAnimales = totalIb100 + totalCruz50;

        holder.txtIb100.setText(String.valueOf(totalIb100));
        holder.txtCruz50.setText(String.valueOf(totalCruz50));
        holder.txtTotal.setText(String.valueOf(totalAnimales));

        int aforo = db.obtenerAforo(codExplotacion);

        actualizarTextoAforo(holder, totalAnimales, aforo);

        // Editar aforo
        holder.iconEditAforo.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Editar aforo máximo");

            final EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setHint("Introduce nuevo aforo");
            builder.setView(input);

            builder.setPositiveButton("Guardar", (dialog, which) -> {
                String valor = input.getText().toString().trim();
                if (!valor.isEmpty()) {
                    try {
                        int nuevoAforo = Integer.parseInt(valor);
                        db.guardarAforo(codExplotacion, nuevoAforo);
                        actualizarTextoAforo(holder, totalAnimales, nuevoAforo);
                        Toast.makeText(context, "Aforo actualizado", Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "Número inválido", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            builder.setNegativeButton("Cancelar", null);
            builder.show();
        });

        holder.btnVerLotes.setOnClickListener(v -> {
            if (context instanceof DashboardActivity) {
                ((DashboardActivity) context).irALotes();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    private void actualizarTextoAforo(@NonNull ResumenViewHolder holder, int totalAnimales, int aforo) {
        if (totalAnimales > aforo) {
            holder.textAforoInfo.setText("⚠️ Has superado el aforo máximo (" + totalAnimales + "/" + aforo + ")");
            holder.textAforoInfo.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        } else {
            holder.textAforoInfo.setText("Aforo máximo: " + aforo + " animales");
            holder.textAforoInfo.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }
    }

    static class ResumenViewHolder extends RecyclerView.ViewHolder {
        Button btnVerLotes;
        TextView textAforoInfo, txtIb100, txtCruz50, txtTotal;
        ImageView iconEditAforo;

        public ResumenViewHolder(@NonNull View itemView) {
            super(itemView);
            btnVerLotes = itemView.findViewById(R.id.btnVerLotes);
            textAforoInfo = itemView.findViewById(R.id.text_aforo_info);
            iconEditAforo = itemView.findViewById(R.id.icon_edit_aforo);
            txtIb100 = itemView.findViewById(R.id.lotes_ib100);
            txtCruz50 = itemView.findViewById(R.id.lotes_cruz50);
            txtTotal = itemView.findViewById(R.id.lotes_total);
        }
    }
}
