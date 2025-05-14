package com.example.app.controller;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app.R;
import com.example.app.model.Agendamento;
import java.util.List;

public class BarberClientsAdapter extends RecyclerView.Adapter<BarberClientsAdapter.ViewHolder> {

    private static final String TAG = "BarberClientsAdapter";
    private final List<Agendamento> clients;
    private final OnClientActionListener listener;

    public interface OnClientActionListener {
        void onConfirm(Agendamento agendamento);
        void onCancel(Agendamento agendamento);
    }

    public BarberClientsAdapter(List<Agendamento> clients, OnClientActionListener listener) {
        this.clients = clients;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_barber_client, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Agendamento agendamento = clients.get(position);
        Log.d(TAG, "Exibindo agendamento #" + position + ": " + agendamento.toString());

        holder.tvClientName.setText(agendamento.getClienteNome());
        holder.tvService.setText(agendamento.getServico());
        holder.tvDateTime.setText(formatDateTime(agendamento.getDia(), agendamento.getHorario()));

        setupActionButtons(holder, agendamento);
        setupStatusUI(holder, agendamento);
    }

    private String formatDateTime(String date, String time) {
        return String.format("%s • %s", date, time);
    }

    private void setupActionButtons(ViewHolder holder, Agendamento agendamento) {
        holder.btnConfirm.setOnClickListener(v -> {
            if (listener != null) {
                Log.d(TAG, "Confirmando agendamento: " + agendamento.getId());
                listener.onConfirm(agendamento);
            }
        });

        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) {
                Log.d(TAG, "Cancelando agendamento: " + agendamento.getId());
                listener.onCancel(agendamento);
            }
        });
    }

    protected void setupStatusUI(ViewHolder holder, Agendamento agendamento) {
        String status = agendamento.getStatus();

        if ("pendente".equalsIgnoreCase(status)) {
            holder.btnConfirm.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.tvStatus.setVisibility(View.GONE);
        } else {
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);

            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(capitalize(status));

            switch (status.toLowerCase()) {
                case "confirmado":
                    holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
                    break;
                case "cancelado":
                    holder.tvStatus.setTextColor(Color.parseColor("#F44336"));
                    break;
                default:
                    holder.tvStatus.setTextColor(Color.GRAY);
                    break;
            }
        }
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    @Override
    public int getItemCount() {
        return clients.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvClientName;
        final TextView tvService;
        final TextView tvDateTime;
        final TextView tvStatus;
        final Button btnConfirm;
        final Button btnCancel;

        public ViewHolder(View itemView) {
            super(itemView);
            tvClientName = itemView.findViewById(R.id.tv_client_name);
            tvService = itemView.findViewById(R.id.tv_service);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnConfirm = itemView.findViewById(R.id.btn_confirm);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
        }
    }
}
