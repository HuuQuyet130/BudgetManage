package com.example.cuoiki.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cuoiki.R;
import com.example.cuoiki.model.Budget;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class BudgetAdapter
        extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    public interface OnBudgetActionListener {

        void onEdit(Budget budget);

        void onDelete(Budget budget);
    }

    private final ArrayList<Budget> budgetList;
    private final OnBudgetActionListener listener;

    public BudgetAdapter(
            ArrayList<Budget> budgetList,
            OnBudgetActionListener listener
    ) {
        this.budgetList = budgetList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_budget,
                        parent,
                        false
                );

        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull BudgetViewHolder holder,
            int position
    ) {

        Budget budget = budgetList.get(position);

        bindGeneralInformation(
                holder,
                budget
        );

        bindProgressAndStatus(
                holder,
                budget
        );

        bindTimeInformation(
                holder,
                budget
        );

        bindActions(
                holder,
                budget
        );
    }

    private void bindGeneralInformation(
            BudgetViewHolder holder,
            Budget budget
    ) {

        String categoryName =
                budget.getCategoryName();

        if (
                categoryName == null
                        || categoryName.trim().isEmpty()
        ) {
            categoryName =
                    "Danh mục không xác định";
        }

        holder.txtBudgetCategory.setText(
                categoryName
        );

        holder.txtBudgetMonth.setText(
                String.format(
                        Locale.getDefault(),
                        "%02d/%d",
                        budget.getMonth(),
                        budget.getYear()
                )
        );

        holder.txtBudgetAmount.setText(
                String.format(
                        Locale.getDefault(),
                        "Đã chi: %,.0f / %,.0f VNĐ",
                        budget.getSpentAmount(),
                        budget.getAmount()
                )
        );
    }

    private void bindProgressAndStatus(
            BudgetViewHolder holder,
            Budget budget
    ) {

        int actualPercent =
                Math.max(
                        budget.getUsagePercent(),
                        0
                );

        int displayedProgress =
                Math.min(
                        actualPercent,
                        100
                );

        holder.progressBudget.setMax(100);

        holder.progressBudget.setProgress(
                displayedProgress
        );

        Context context =
                holder.itemView.getContext();

        int progressColor;
        int statusTextColor;
        int cardBackgroundColor;
        int cardStrokeColor;

        String statusText;

        if (actualPercent >= 100) {

            progressColor =
                    ContextCompat.getColor(
                            context,
                            R.color.app_red
                    );

            statusTextColor =
                    progressColor;

            cardBackgroundColor =
                    Color.parseColor("#FFF5F5");

            cardStrokeColor =
                    Color.parseColor("#FECACA");

            double exceededAmount =
                    Math.max(
                            budget.getSpentAmount()
                                    - budget.getAmount(),
                            0
                    );

            statusText =
                    String.format(
                            Locale.getDefault(),
                            "✕ %d%% - Vượt %,.0f VNĐ",
                            actualPercent,
                            exceededAmount
                    );

        } else if (
                actualPercent
                        >= budget.getWarningPercent()
        ) {

            progressColor =
                    ContextCompat.getColor(
                            context,
                            R.color.app_orange
                    );

            statusTextColor =
                    progressColor;

            cardBackgroundColor =
                    Color.parseColor("#FFF8ED");

            cardStrokeColor =
                    Color.parseColor("#FED7AA");

            double remainingAmount =
                    Math.max(
                            budget.getRemainingAmount(),
                            0
                    );

            statusText =
                    String.format(
                            Locale.getDefault(),
                            "⚠ %d%% - Sắp đạt giới hạn, còn %,.0f VNĐ",
                            actualPercent,
                            remainingAmount
                    );

        } else {

            progressColor =
                    ContextCompat.getColor(
                            context,
                            R.color.app_green
                    );

            statusTextColor =
                    progressColor;

            cardBackgroundColor =
                    Color.parseColor("#F3FCF6");

            cardStrokeColor =
                    Color.parseColor("#BBF7D0");

            double remainingAmount =
                    Math.max(
                            budget.getRemainingAmount(),
                            0
                    );

            statusText =
                    String.format(
                            Locale.getDefault(),
                            "✓ %d%% - Còn %,.0f VNĐ",
                            actualPercent,
                            remainingAmount
                    );
        }

        holder.progressBudget.setProgressTintList(
                ColorStateList.valueOf(
                        progressColor
                )
        );

        holder.txtBudgetPercent.setText(
                statusText
        );

        holder.txtBudgetPercent.setTextColor(
                statusTextColor
        );

        holder.cardBudget.setCardBackgroundColor(
                cardBackgroundColor
        );

        holder.cardBudget.setStrokeColor(
                cardStrokeColor
        );
    }

    private void bindTimeInformation(
            BudgetViewHolder holder,
            Budget budget
    ) {

        Calendar currentDate =
                Calendar.getInstance();

        int currentMonth =
                currentDate.get(
                        Calendar.MONTH
                ) + 1;

        int currentYear =
                currentDate.get(
                        Calendar.YEAR
                );

        if (
                budget.getYear() < currentYear
                        || (
                        budget.getYear() == currentYear
                                && budget.getMonth() < currentMonth
                )
        ) {

            holder.txtBudgetDays.setText(
                    "Ngân sách này đã kết thúc"
            );

            holder.txtBudgetDaily.setText(
                    "Không còn hạn mức theo ngày"
            );

            holder.txtBudgetDaily.setVisibility(
                    View.VISIBLE
            );

            return;
        }

        if (
                budget.getYear() > currentYear
                        || budget.getMonth() > currentMonth
        ) {

            holder.txtBudgetDays.setText(
                    "Ngân sách chưa bắt đầu"
            );

            holder.txtBudgetDaily.setText(
                    "Bắt đầu từ tháng "
                            + String.format(
                            Locale.getDefault(),
                            "%02d/%d",
                            budget.getMonth(),
                            budget.getYear()
                    )
            );

            holder.txtBudgetDaily.setVisibility(
                    View.VISIBLE
            );

            return;
        }

        Calendar lastDayOfMonth =
                Calendar.getInstance();

        lastDayOfMonth.set(
                budget.getYear(),
                budget.getMonth() - 1,
                1
        );

        int totalDaysInMonth =
                lastDayOfMonth.getActualMaximum(
                        Calendar.DAY_OF_MONTH
                );

        int today =
                currentDate.get(
                        Calendar.DAY_OF_MONTH
                );

        int remainingDays =
                totalDaysInMonth - today + 1;

        remainingDays =
                Math.max(
                        remainingDays,
                        1
                );

        holder.txtBudgetDays.setText(
                "Còn "
                        + remainingDays
                        + " ngày trong tháng"
        );

        if (budget.getRemainingAmount() <= 0) {

            holder.txtBudgetDaily.setText(
                    "Bạn không còn hạn mức để chi"
            );

            holder.txtBudgetDaily.setTextColor(
                    ContextCompat.getColor(
                            holder.itemView.getContext(),
                            R.color.app_red
                    )
            );

            return;
        }

        double dailyAmount =
                budget.getRemainingAmount()
                        / remainingDays;

        holder.txtBudgetDaily.setText(
                String.format(
                        Locale.getDefault(),
                        "Có thể chi khoảng %,.0f VNĐ/ngày",
                        dailyAmount
                )
        );

        if (remainingDays <= 3) {

            holder.txtBudgetDaily.setTextColor(
                    ContextCompat.getColor(
                            holder.itemView.getContext(),
                            R.color.app_orange
                    )
            );

        } else {

            holder.txtBudgetDaily.setTextColor(
                    ContextCompat.getColor(
                            holder.itemView.getContext(),
                            R.color.app_blue_dark
                    )
            );
        }
    }

    private void bindActions(
            BudgetViewHolder holder,
            Budget budget
    ) {

        holder.btnEditBudget.setOnClickListener(v -> {

            if (listener != null) {
                listener.onEdit(budget);
            }
        });

        holder.btnDeleteBudget.setOnClickListener(v -> {

            if (listener != null) {
                listener.onDelete(budget);
            }
        });
    }

    @Override
    public int getItemCount() {

        return budgetList == null
                ? 0
                : budgetList.size();
    }

    public static class BudgetViewHolder
            extends RecyclerView.ViewHolder {

        MaterialCardView cardBudget;

        TextView txtBudgetCategory;
        TextView txtBudgetMonth;
        TextView txtBudgetAmount;
        TextView txtBudgetPercent;
        TextView txtBudgetDays;
        TextView txtBudgetDaily;

        ProgressBar progressBudget;

        ImageButton btnEditBudget;
        ImageButton btnDeleteBudget;

        public BudgetViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            cardBudget =
                    itemView.findViewById(
                            R.id.cardBudget
                    );

            txtBudgetCategory =
                    itemView.findViewById(
                            R.id.txtBudgetCategory
                    );

            txtBudgetMonth =
                    itemView.findViewById(
                            R.id.txtBudgetMonth
                    );

            txtBudgetAmount =
                    itemView.findViewById(
                            R.id.txtBudgetAmount
                    );

            txtBudgetPercent =
                    itemView.findViewById(
                            R.id.txtBudgetPercent
                    );

            txtBudgetDays =
                    itemView.findViewById(
                            R.id.txtBudgetDays
                    );

            txtBudgetDaily =
                    itemView.findViewById(
                            R.id.txtBudgetDaily
                    );

            progressBudget =
                    itemView.findViewById(
                            R.id.progressBudget
                    );

            btnEditBudget =
                    itemView.findViewById(
                            R.id.btnEditBudget
                    );

            btnDeleteBudget =
                    itemView.findViewById(
                            R.id.btnDeleteBudget
                    );
        }
    }
}