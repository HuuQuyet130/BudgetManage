package com.example.cuoiki.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.cuoiki.database.DatabaseHelper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cuoiki.R;
import com.example.cuoiki.model.Transaction;

import java.util.ArrayList;

public class TransactionAdapter
        extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>
        implements Filterable {

    private final ArrayList<Transaction> transactionList;
    private final ArrayList<Transaction> transactionListFull;
    private final OnTransactionActionListener listener;
    private String currentKeyword = "";
    private String currentType = "Tất cả";
    private String currentMonth = "Tất cả tháng";

    public interface OnTransactionActionListener {

        void onEdit(Transaction transaction);

        void onDelete(Transaction transaction);
    }

    public TransactionAdapter(
            ArrayList<Transaction> transactionList,
            OnTransactionActionListener listener
    ) {
        this.transactionList = transactionList;
        this.listener = listener;

        this.transactionListFull = new ArrayList<>();
        this.transactionListFull.addAll(transactionList);
    }
    public void setTypeFilter(String type) {

        currentType = type;

        getFilter().filter(currentKeyword);
    }
    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_transaction,
                        parent,
                        false
                );

        return new TransactionViewHolder(view);
    }
    public void setMonthFilter(String month) {
        currentMonth = month;
        getFilter().filter(currentKeyword);
    }
    @Override
    public void onBindViewHolder(
            @NonNull TransactionViewHolder holder,
            int position
    ) {

        Transaction transaction = transactionList.get(position);

        holder.txtTitle.setText(transaction.getTitle());

        holder.txtType.setText(
                DatabaseHelper.TYPE_INCOME.equals(transaction.getType())
                        ? "Thu nhập"
                        : "Chi tiêu"
        );

        holder.txtAmount.setText(
                String.format("%,.0f VNĐ", transaction.getAmount())
        );
        if (DatabaseHelper.TYPE_INCOME.equals(transaction.getType())) {
            holder.txtAmount.setTextColor(
                    holder.itemView
                            .getContext()
                            .getColor(android.R.color.holo_green_dark)
            );
        } else {
            holder.txtAmount.setTextColor(
                    holder.itemView
                            .getContext()
                            .getColor(android.R.color.holo_red_dark)
            );
        }
        holder.txtDate.setText(transaction.getDate());

        holder.btnEditTransaction.setOnClickListener(v ->
                listener.onEdit(transaction)
        );

        holder.btnDeleteTransaction.setOnClickListener(v ->
                listener.onDelete(transaction)
        );
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }
    public static class TransactionViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtTitle;
        TextView txtType;
        TextView txtAmount;
        TextView txtDate;

        ImageButton btnEditTransaction;
        ImageButton btnDeleteTransaction;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle =
                    itemView.findViewById(R.id.txtTitle);

            txtType =
                    itemView.findViewById(R.id.txtType);

            txtAmount =
                    itemView.findViewById(R.id.txtAmount);

            txtDate =
                    itemView.findViewById(R.id.txtDate);

            btnEditTransaction =
                    itemView.findViewById(R.id.btnEditTransaction);

            btnDeleteTransaction =
                    itemView.findViewById(R.id.btnDeleteTransaction);
        }

    }
    @Override
    public Filter getFilter() {

        return transactionFilter;
    }

    private final Filter transactionFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<Transaction> filteredList = new ArrayList<>();

            currentKeyword = constraint == null
                    ? ""
                    : constraint.toString().toLowerCase().trim();

            for (Transaction transaction : transactionListFull) {

                String title = transaction.getTitle() == null
                        ? ""
                        : transaction.getTitle().toLowerCase();

                String note = transaction.getNote() == null
                        ? ""
                        : transaction.getNote().toLowerCase();

                String date = transaction.getDate() == null
                        ? ""
                        : transaction.getDate();

                boolean matchKeyword =
                        title.contains(currentKeyword)
                                || note.contains(currentKeyword)
                                || date.contains(currentKeyword);

                boolean matchType =
                        currentType.equals("Tất cả")
                                || (
                                currentType.equals("Thu nhập")
                                        && DatabaseHelper.TYPE_INCOME.equals(
                                        transaction.getType()
                                )
                        )
                                || (
                                currentType.equals("Chi tiêu")
                                        && DatabaseHelper.TYPE_EXPENSE.equals(
                                        transaction.getType()
                                )
                        );

                boolean matchMonth = currentMonth.equals("Tất cả tháng");

                // Date đang có định dạng yyyy-MM-dd
                if (!matchMonth && date.length() >= 7) {

                    String year = date.substring(0, 4);
                    String month = date.substring(5, 7);

                    String transactionMonth =
                            month + "/" + year;

                    matchMonth =
                            currentMonth.equals(transactionMonth);
                }

                if (matchKeyword && matchType && matchMonth) {
                    filteredList.add(transaction);
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();

            return results;
        }

        @Override
        protected void publishResults(
                CharSequence constraint,
                FilterResults results
        ) {

            transactionList.clear();

            if (results.values != null) {
                transactionList.addAll(
                        (ArrayList<Transaction>) results.values
                );
            }

            notifyDataSetChanged();
        }
    };
}