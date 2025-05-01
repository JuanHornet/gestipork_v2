package com.example.proyecto_gestipork.modelo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestipork.R;


    public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ResumenViewHolder> {

        private final Context context;

        public DashboardAdapter(Context context) {
            this.context = context;
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
            // ✅ Botón que navega a LotesActivity
            holder.btnVerLotes.setOnClickListener(v -> {
                Intent intent = new Intent(context, LotesActivity.class);
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return 1; // Solo quieres mostrar 1 layout que incluye las dos CardView
        }

        static class ResumenViewHolder extends RecyclerView.ViewHolder {

            Button btnVerLotes;

            public ResumenViewHolder(@NonNull View itemView) {
                super(itemView);
                btnVerLotes = itemView.findViewById(R.id.btnVerLotes);
            }
        }
    }


