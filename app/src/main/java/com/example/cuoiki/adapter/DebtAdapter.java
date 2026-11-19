package com.example.cuoiki.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cuoiki.R;
import com.example.cuoiki.model.Debt;

import java.util.ArrayList;

public class DebtAdapter extends RecyclerView.Adapter<DebtAdapter.DebtViewHolder> {
    public interface OnDebtActionListener {

        void onEdit(Debt debt);

        void onDelete(Debt debt);
    }

    private final ArrayList<Debt> debtList;
    private final OnDebtActionListener listener;

    public DebtAdapter(
            ArrayList<Debt> debtList,
            OnDebtActionListener listener
    ) {
        this.debtList = debtList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DebtViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_debt, parent, false);

        return new DebtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull DebtViewHolder holder,
            int position
    ) {

        Debt debt = debtList.get(position);

        holder.txtPerson.setText(debt.getPerson());

        holder.txtAmount.setText(
                String.format("%,.0f VNĐ", debt.getAmount())
        );

        holder.txtDeadline.setText(
                "Hạn: " + debt.getDeadline()
        );

        holder.txtType.setText(
                "Loại: " + debt.getType()
        );

        holder.txtStatus.setText(
                "Trạng thái: " + debt.getStatus()
        );
        holder.btnEditDebt.setOnClickListener(v ->
                listener.onEdit(debt)
        );

        holder.btnDeleteDebt.setOnClickListener(v ->
                listener.onDelete(debt)
        );
    }

    @Override
    public int getItemCount() {
        return debtList.size();
    }

    static class DebtViewHolder extends RecyclerView.ViewHolder {

        TextView txtPerson;
        TextView txtAmount;
        TextView txtDeadline;
        TextView txtType;
        TextView txtStatus;

        ImageButton btnEditDebt;
        ImageButton btnDeleteDebt;

        public DebtViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPerson = itemView.findViewById(R.id.txtPerson);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtDeadline = itemView.findViewById(R.id.txtDeadline);
            txtType = itemView.findViewById(R.id.txtType);
            txtStatus = itemView.findViewById(R.id.txtStatus);

            btnEditDebt = itemView.findViewById(R.id.btnEditDebt);
            btnDeleteDebt = itemView.findViewById(R.id.btnDeleteDebt);
        }
    }
}