package com.example.app.controller;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.model.Barbeiro;
import com.example.app.view.AgendamentoActivity;

import java.util.ArrayList;
import java.util.List;

public class BarbeiroAdapter extends RecyclerView.Adapter<BarbeiroAdapter.BarbeiroViewHolder> {
    List<Barbeiro> listaBarbeiros;

    public BarbeiroAdapter(List<Barbeiro> listaBarbeiros) {
        this.listaBarbeiros = listaBarbeiros != null ? listaBarbeiros : new ArrayList<>();
    }

    public void updateData(List<Barbeiro> novaLista) {
        this.listaBarbeiros = novaLista != null ? novaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BarbeiroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_barbeiro, parent, false);
        return new BarbeiroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarbeiroViewHolder holder, int position) {
        Barbeiro barbeiro = listaBarbeiros.get(position);

        if (holder.txtNomeBarbeiro != null) {
            holder.txtNomeBarbeiro.setText(barbeiro.getNome() != null ? barbeiro.getNome() : "");
        }

        if (holder.txtEmailBarbeiro != null) {
            holder.txtEmailBarbeiro.setText("Email: " + (barbeiro.getEmail() != null ? barbeiro.getEmail() : ""));
        }

        if (holder.txtEnderecoBarbeiro != null) {
            holder.txtEnderecoBarbeiro.setText("Endereço: " + (barbeiro.getEndereco() != null ? barbeiro.getEndereco() : ""));
        }

        List<String> servicos = barbeiro.getServicos();
        if (holder.txtServicos != null) {
            holder.txtServicos.setText(servicos != null && !servicos.isEmpty() ?
                    "Serviços: " + String.join(", ", servicos) : "Serviços: Nenhum");
        }

        List<String> diasDisponiveis = barbeiro.getDiasDisponiveis();
        if (holder.txtDiasDisponiveis != null) {
            holder.txtDiasDisponiveis.setText(diasDisponiveis != null && !diasDisponiveis.isEmpty() ?
                    "Dias Disponíveis: " + String.join(", ", diasDisponiveis) : "Dias Disponíveis: Nenhum");
        }
    }

    @Override
    public int getItemCount() {
        return listaBarbeiros.size();
    }

    public class BarbeiroViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeBarbeiro, txtEmailBarbeiro, txtEnderecoBarbeiro, txtServicos, txtDiasDisponiveis;

        public BarbeiroViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeBarbeiro = itemView.findViewById(R.id.txtNomeBarbeiro);
            txtEmailBarbeiro = itemView.findViewById(R.id.txtEmailBarbeiro);
            txtEnderecoBarbeiro = itemView.findViewById(R.id.txtEnderecoBarbeiro);
            txtServicos = itemView.findViewById(R.id.txtServicos);
            txtDiasDisponiveis = itemView.findViewById(R.id.txtDiasDisponiveis);


            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Barbeiro barbeiroSelecionado = listaBarbeiros.get(position);
                    Intent intent = new Intent(itemView.getContext(), AgendamentoActivity.class);
                    intent.putExtra("barbeiro", barbeiroSelecionado); 
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}