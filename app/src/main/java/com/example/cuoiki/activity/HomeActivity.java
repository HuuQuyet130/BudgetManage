package com.example.cuoiki.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.cuoiki.model.Transaction;

import java.util.ArrayList;
import com.example.cuoiki.database.DatabaseHelper;
import com.example.cuoiki.databinding.ActivityHomeBinding;
import com.example.cuoiki.model.Account;
import com.example.cuoiki.model.Transaction;
import com.example.cuoiki.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    /*
     * Home chỉ hiển thị tối đa 5 giao dịch.
     * Toàn bộ lịch sử nằm trong TransactionActivity.
     */
    private static final int RECENT_TRANSACTION_LIMIT = 5;

    private ActivityHomeBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding =
                ActivityHomeBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(binding.getRoot());

        databaseHelper =
                new DatabaseHelper(this);

        sessionManager =
                new SessionManager(this);

        setupActions();
        loadHomeData();
    }

    private void setupActions() {

        binding.btnLogout.setOnClickListener(v ->
                logout()
        );

        binding.btnCategory.setOnClickListener(v ->
                openActivity(CategoryActivity.class)
        );

        binding.btnBudget.setOnClickListener(v ->
                openActivity(BudgetActivity.class)
        );

        binding.btnDebt.setOnClickListener(v ->
                openActivity(DebtActivity.class)
        );

        binding.btnStatistics.setOnClickListener(v ->
                openActivity(StatisticsActivity.class)
        );

        binding.fabTransaction.setOnClickListener(v ->
                openActivity(TransactionActivity.class)
        );

        binding.txtViewAllTransactions.setOnClickListener(v ->
                openActivity(TransactionActivity.class)
        );
    }

    private void openActivity(
            Class<?> destination
    ) {

        Intent intent =
                new Intent(
                        HomeActivity.this,
                        destination
                );

        startActivity(intent);
    }

    private void loadHomeData() {

        Account account =
                getCurrentAccount();

        if (account == null) {
            return;
        }

        showCurrentUser(account);
        loadBalance(account);
        loadRecentTransactions(account);
    }

    private Account getCurrentAccount() {

        String username =
                sessionManager.getUsername();

        if (
                username == null
                        || username.trim().isEmpty()
        ) {
            logout();
            return null;
        }

        Account account =
                databaseHelper.getAccountByUsername(
                        username
                );

        if (account == null) {
            logout();
            return null;
        }

        return account;
    }

    private void showCurrentUser(
            Account account
    ) {

        String fullName =
                account.getFullName();

        if (
                fullName == null
                        || fullName.trim().isEmpty()
        ) {

            binding.txtWelcome.setText(
                    "Xin chào"
            );

        } else {

            binding.txtWelcome.setText(
                    "Xin chào, " + fullName.trim()
            );
        }
    }

    private void loadBalance(
            Account account
    ) {

        double balance =
                databaseHelper.getBalance(
                        account.getId()
                );

        binding.txtBalance.setText(
                String.format(
                        Locale.getDefault(),
                        "%,.0f VNĐ",
                        balance
                )
        );

        if (balance < 0) {

            binding.txtBalance.setTextColor(
                    Color.parseColor("#DC2626")
            );

            binding.txtBalanceDescription.setText(
                    "Chi tiêu đang lớn hơn thu nhập"
            );

        } else if (balance == 0) {

            binding.txtBalance.setTextColor(
                    Color.parseColor("#174EA6")
            );

            binding.txtBalanceDescription.setText(
                    "Chưa phát sinh số dư"
            );

        } else {

            binding.txtBalance.setTextColor(
                    Color.parseColor("#174EA6")
            );

            binding.txtBalanceDescription.setText(
                    "Tổng tiền vào trừ tổng tiền ra"
            );
        }
    }

    private void loadRecentTransactions(
            Account account
    ) {

        ArrayList<Transaction> recentTransactions =
                databaseHelper.getRecentTransactions(
                        account.getId(),
                        RECENT_TRANSACTION_LIMIT
                );

        binding.layoutRecentTransactions.removeAllViews();

        if (
                recentTransactions == null
                        || recentTransactions.isEmpty()
        ) {

            binding.cardEmptyTransactions.setVisibility(
                    View.VISIBLE
            );

            binding.layoutRecentTransactions.setVisibility(
                    View.GONE
            );

            binding.txtViewAllTransactions.setVisibility(
                    View.GONE
            );

            return;
        }

        binding.cardEmptyTransactions.setVisibility(
                View.GONE
        );

        binding.layoutRecentTransactions.setVisibility(
                View.VISIBLE
        );

        binding.txtViewAllTransactions.setVisibility(
                View.VISIBLE
        );

        for (Transaction transaction : recentTransactions) {

            binding.layoutRecentTransactions.addView(
                    createRecentTransactionView(
                            transaction
                    )
            );
        }
    }

    private View createRecentTransactionView(
            Transaction transaction
    ) {

        MaterialCardView card =
                new MaterialCardView(this);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                dpToPx(10)
        );

        card.setLayoutParams(cardParams);
        card.setRadius(dpToPx(17));
        card.setCardElevation(dpToPx(2));
        card.setCardBackgroundColor(Color.WHITE);
        card.setStrokeWidth(dpToPx(1));

        card.setStrokeColor(
                Color.parseColor("#E1EAF7")
        );

        card.setClickable(true);
        card.setFocusable(true);

        LinearLayout row =
                new LinearLayout(this);

        row.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setGravity(
                Gravity.CENTER_VERTICAL
        );

        row.setPadding(
                dpToPx(16),
                dpToPx(14),
                dpToPx(16),
                dpToPx(14)
        );

        LinearLayout informationLayout =
                new LinearLayout(this);

        informationLayout.setOrientation(
                LinearLayout.VERTICAL
        );

        informationLayout.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        TextView titleView =
                new TextView(this);

        String title =
                transaction.getTitle();

        titleView.setText(
                title == null || title.trim().isEmpty()
                        ? "Giao dịch"
                        : title
        );

        titleView.setTextSize(16f);

        titleView.setTextColor(
                Color.parseColor("#172033")
        );

        titleView.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        titleView.setMaxLines(1);
        titleView.setEllipsize(
                android.text.TextUtils.TruncateAt.END
        );

        TextView detailView =
                new TextView(this);

        String categoryName =
                databaseHelper.getCategoryNameById(
                        transaction.getCategoryId()
                );

        if (
                categoryName == null
                        || categoryName.trim().isEmpty()
        ) {
            categoryName =
                    "Không xác định";
        }

        detailView.setText(
                categoryName
                        + " • "
                        + transaction.getDate()
        );

        detailView.setTextSize(13f);

        detailView.setTextColor(
                Color.parseColor("#64748B")
        );

        detailView.setPadding(
                0,
                dpToPx(5),
                0,
                0
        );

        informationLayout.addView(titleView);
        informationLayout.addView(detailView);

        TextView amountView =
                new TextView(this);

        boolean isIncome =
                DatabaseHelper.TYPE_INCOME.equals(
                        transaction.getType()
                );

        amountView.setText(
                String.format(
                        Locale.getDefault(),
                        "%s%,.0f VNĐ",
                        isIncome ? "+" : "-",
                        transaction.getAmount()
                )
        );

        amountView.setTextSize(15f);

        amountView.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        amountView.setTextColor(
                isIncome
                        ? Color.parseColor("#16854B")
                        : Color.parseColor("#D83C4A")
        );

        row.addView(informationLayout);
        row.addView(amountView);

        card.addView(row);

        card.setOnClickListener(v ->
                openActivity(TransactionActivity.class)
        );

        return card;
    }

    private int dpToPx(
            int dp
    ) {

        return Math.round(
                dp
                        * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    private void logout() {

        sessionManager.logout();

        Intent intent =
                new Intent(
                        HomeActivity.this,
                        LoginActivity.class
                );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (
                databaseHelper != null
                        && sessionManager != null
        ) {
            loadHomeData();
        }
    }

    @Override
    protected void onDestroy() {

        if (databaseHelper != null) {
            databaseHelper.close();
        }

        binding = null;

        super.onDestroy();
    }
}