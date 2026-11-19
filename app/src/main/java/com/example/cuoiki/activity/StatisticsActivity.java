package com.example.cuoiki.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cuoiki.database.DatabaseHelper;
import com.example.cuoiki.databinding.ActivityStatisticsBinding;
import com.example.cuoiki.model.Account;
import com.example.cuoiki.utils.SessionManager;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    private ActivityStatisticsBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding =
                ActivityStatisticsBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(binding.getRoot());

        databaseHelper =
                new DatabaseHelper(this);

        sessionManager =
                new SessionManager(this);

        setupToolbar();
        setupMonthSpinner();
        setupYearSpinner();
        setupPieChart();

        binding.btnViewStatistics.setOnClickListener(v ->
                loadStatistics()
        );

        loadStatistics();
    }

    private void setupToolbar() {

        binding.toolbar.setNavigationOnClickListener(v ->
                getOnBackPressedDispatcher()
                        .onBackPressed()
        );
    }

    private void setupMonthSpinner() {

        ArrayList<Integer> months =
                new ArrayList<>();

        for (int month = 1;
             month <= 12;
             month++) {

            months.add(month);
        }

        ArrayAdapter<Integer> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        months
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        binding.spinnerMonth.setAdapter(adapter);

        int currentMonth =
                Calendar.getInstance()
                        .get(Calendar.MONTH) + 1;

        binding.spinnerMonth.setSelection(
                currentMonth - 1
        );
    }

    private void setupYearSpinner() {

        ArrayList<Integer> years =
                new ArrayList<>();

        int currentYear =
                Calendar.getInstance()
                        .get(Calendar.YEAR);

        for (
                int year = currentYear - 5;
                year <= currentYear + 5;
                year++
        ) {
            years.add(year);
        }

        ArrayAdapter<Integer> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        years
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        binding.spinnerYear.setAdapter(adapter);

        binding.spinnerYear.setSelection(5);
    }

    private void setupPieChart() {

        binding.pieChart
                .getDescription()
                .setEnabled(false);

        binding.pieChart.setUsePercentValues(true);

        // Không hiện tên trực tiếp trên lát bánh
        binding.pieChart.setDrawEntryLabels(false);

        // Biểu đồ dạng donut
        binding.pieChart.setDrawHoleEnabled(true);
        binding.pieChart.setHoleRadius(57f);
        binding.pieChart.setTransparentCircleRadius(62f);

        binding.pieChart.setHoleColor(
                Color.WHITE
        );

        binding.pieChart.setTransparentCircleColor(
                Color.WHITE
        );

        binding.pieChart.setTransparentCircleAlpha(
                110
        );

        // Chữ giữa biểu đồ
        binding.pieChart.setCenterTextSize(14f);

        binding.pieChart.setCenterTextColor(
                Color.parseColor("#172033")
        );

        binding.pieChart.setCenterTextRadiusPercent(
                85f
        );

        // Cho phép xoay và chọn lát bánh
        binding.pieChart.setRotationEnabled(true);

        binding.pieChart.setRotationAngle(
                270f
        );

        binding.pieChart.setHighlightPerTapEnabled(
                true
        );

        binding.pieChart.setExtraOffsets(
                10f,
                10f,
                10f,
                16f
        );

        binding.pieChart.setElevation(
                dpToPx(4f)
        );

        setupPieChartLegend();
    }

    private void setupPieChartLegend() {

        Legend legend =
                binding.pieChart.getLegend();

        legend.setEnabled(true);

        legend.setVerticalAlignment(
                Legend.LegendVerticalAlignment.BOTTOM
        );

        legend.setHorizontalAlignment(
                Legend.LegendHorizontalAlignment.CENTER
        );

        legend.setOrientation(
                Legend.LegendOrientation.HORIZONTAL
        );

        legend.setDrawInside(false);

        legend.setWordWrapEnabled(true);

        legend.setForm(
                Legend.LegendForm.CIRCLE
        );

        legend.setFormSize(10f);

        legend.setTextSize(12f);

        legend.setTextColor(
                Color.parseColor("#172033")
        );

        legend.setXEntrySpace(12f);

        legend.setYEntrySpace(8f);

        legend.setFormToTextSpace(6f);
    }

    private void loadStatistics() {

        Account account =
                getCurrentAccount();

        if (account == null) {
            return;
        }

        Integer month =
                (Integer) binding.spinnerMonth
                        .getSelectedItem();

        Integer year =
                (Integer) binding.spinnerYear
                        .getSelectedItem();

        if (month == null || year == null) {

            Toast.makeText(
                    this,
                    "Vui lòng chọn tháng và năm",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        double income =
                databaseHelper.getIncomeByMonth(
                        account.getId(),
                        month,
                        year
                );

        double expense =
                databaseHelper.getExpenseByMonth(
                        account.getId(),
                        month,
                        year
                );

        double balance =
                income - expense;

        updateSummary(
                income,
                expense,
                balance
        );

        LinkedHashMap<String, Double> expenseSummary =
                databaseHelper
                        .getExpenseSummaryByCategory(
                                account.getId(),
                                month,
                                year
                        );

        updateCategoryStatistics(
                expenseSummary,
                month,
                year
        );
    }

    private Account getCurrentAccount() {

        String username =
                sessionManager.getUsername();

        if (username == null) {

            Toast.makeText(
                    this,
                    "Phiên đăng nhập không hợp lệ",
                    Toast.LENGTH_SHORT
            ).show();

            return null;
        }

        Account account =
                databaseHelper
                        .getAccountByUsername(
                                username
                        );

        if (account == null) {

            Toast.makeText(
                    this,
                    "Không tìm thấy tài khoản",
                    Toast.LENGTH_SHORT
            ).show();
        }

        return account;
    }

    private void updateSummary(
            double income,
            double expense,
            double balance
    ) {

        binding.txtStatisticsIncome.setText(
                String.format(
                        Locale.getDefault(),
                        "Tổng thu: %,.0f VNĐ",
                        income
                )
        );

        binding.txtStatisticsExpense.setText(
                String.format(
                        Locale.getDefault(),
                        "Tổng chi: %,.0f VNĐ",
                        expense
                )
        );

        binding.txtStatisticsBalance.setText(
                String.format(
                        Locale.getDefault(),
                        "Số dư: %,.0f VNĐ",
                        balance
                )
        );
    }

    private void updateCategoryStatistics(
            LinkedHashMap<String, Double> expenseSummary,
            int month,
            int year
    ) {

        binding.layoutCategoryStatistics
                .removeAllViews();

        ArrayList<PieEntry> pieEntries =
                new ArrayList<>();

        double totalExpense = 0;

        for (double amount
                : expenseSummary.values()) {

            if (amount > 0) {
                totalExpense += amount;
            }
        }

        for (
                Map.Entry<String, Double> entry
                : expenseSummary.entrySet()
        ) {

            String categoryName =
                    entry.getKey();

            double amount =
                    entry.getValue();

            if (amount <= 0) {
                continue;
            }

            double percentage =
                    totalExpense > 0
                            ? amount * 100 / totalExpense
                            : 0;

            addCategoryText(
                    categoryName,
                    amount,
                    percentage
            );

            pieEntries.add(
                    new PieEntry(
                            (float) amount,
                            categoryName
                    )
            );
        }

        updatePieChart(
                pieEntries,
                month,
                year
        );
    }

    private void addCategoryText(
            String categoryName,
            double amount,
            double percentage
    ) {

        TextView textView =
                new TextView(this);

        textView.setText(
                String.format(
                        Locale.getDefault(),
                        "%s\n%,.0f VNĐ • %.1f%% tổng chi",
                        categoryName,
                        amount,
                        percentage
                )
        );

        textView.setTextSize(15f);

        textView.setTextColor(
                Color.parseColor("#172033")
        );

        textView.setBackgroundColor(
                Color.WHITE
        );

        textView.setPadding(
                dpToInt(16),
                dpToInt(14),
                dpToInt(16),
                dpToInt(14)
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                0,
                0,
                0,
                dpToInt(8)
        );

        textView.setLayoutParams(params);

        binding.layoutCategoryStatistics
                .addView(textView);
    }

    private void updatePieChart(
            ArrayList<PieEntry> pieEntries,
            int month,
            int year
    ) {

        float totalExpense = 0f;

        for (PieEntry entry : pieEntries) {
            totalExpense += entry.getValue();
        }

        String defaultCenterText =
                String.format(
                        Locale.getDefault(),
                        "Tổng chi\n%,.0f VNĐ\n%02d/%d",
                        totalExpense,
                        month,
                        year
                );

        if (pieEntries.isEmpty()) {

            binding.pieChart.clear();

            binding.pieChart.setCenterText("");

            binding.pieChart.setNoDataText(
                    "Chưa có dữ liệu chi tiêu trong tháng này"
            );

            binding.pieChart.setNoDataTextColor(
                    Color.parseColor("#64748B")
            );

            binding.pieChart.invalidate();

            return;
        }

        PieDataSet dataSet =
                new PieDataSet(
                        pieEntries,
                        ""
                );

        // Khoảng cách giữa các lát bánh
        dataSet.setSliceSpace(3.5f);

        // Khi bấm lát bánh sẽ nhô ra
        dataSet.setSelectionShift(8f);

        dataSet.setHighlightEnabled(true);

        dataSet.setDrawValues(true);

        dataSet.setValueTextColor(
                Color.WHITE
        );

        dataSet.setValueTextSize(12f);

        dataSet.setColors(
                generateChartColors(
                        pieEntries.size()
                )
        );

        PieData pieData =
                new PieData(dataSet);

        pieData.setValueFormatter(
                new PercentFormatter(
                        binding.pieChart
                ) {

                    @Override
                    public String getFormattedValue(
                            float value
                    ) {

                        // Không hiện chữ nếu lát nhỏ hơn 5%
                        if (value < 5f) {
                            return "";
                        }

                        return String.format(
                                Locale.getDefault(),
                                "%.1f%%",
                                value
                        );
                    }
                }
        );

        binding.pieChart.setData(
                pieData
        );

        binding.pieChart.setCenterText(
                defaultCenterText
        );

        setupChartSelectionListener(
                pieEntries,
                defaultCenterText
        );

        binding.pieChart.notifyDataSetChanged();

        binding.pieChart.animateY(
                1200
        );

        binding.pieChart.invalidate();
    }

    private ArrayList<Integer> generateChartColors(
            int colorCount
    ) {

        int[] palette = {
                Color.parseColor("#4F46E5"),
                Color.parseColor("#10B981"),
                Color.parseColor("#F59E0B"),
                Color.parseColor("#EF4444"),
                Color.parseColor("#06B6D4"),
                Color.parseColor("#8B5CF6"),
                Color.parseColor("#EC4899"),
                Color.parseColor("#84CC16"),
                Color.parseColor("#F97316"),
                Color.parseColor("#64748B")
        };

        ArrayList<Integer> colors =
                new ArrayList<>();

        for (
                int index = 0;
                index < colorCount;
                index++
        ) {

            colors.add(
                    palette[
                            index % palette.length
                            ]
            );
        }

        return colors;
    }

    private void setupChartSelectionListener(
            ArrayList<PieEntry> pieEntries,
            String defaultCenterText
    ) {

        float totalExpense = 0f;

        for (PieEntry entry : pieEntries) {
            totalExpense += entry.getValue();
        }

        final float finalTotalExpense =
                totalExpense;

        binding.pieChart
                .setOnChartValueSelectedListener(
                        new OnChartValueSelectedListener() {

                            @Override
                            public void onValueSelected(
                                    Entry entry,
                                    Highlight highlight
                            ) {

                                if (!(entry instanceof PieEntry)) {
                                    return;
                                }

                                PieEntry pieEntry =
                                        (PieEntry) entry;

                                float amount =
                                        pieEntry.getValue();

                                float percentage = 0f;

                                if (finalTotalExpense > 0f) {

                                    percentage =
                                            amount
                                                    * 100f
                                                    / finalTotalExpense;
                                }

                                binding.pieChart.setCenterText(
                                        String.format(
                                                Locale.getDefault(),
                                                "%s\n%,.0f VNĐ\n%.1f%%",
                                                pieEntry.getLabel(),
                                                amount,
                                                percentage
                                        )
                                );

                                binding.pieChart.invalidate();
                            }

                            @Override
                            public void onNothingSelected() {

                                binding.pieChart.setCenterText(
                                        defaultCenterText
                                );

                                binding.pieChart.invalidate();
                            }
                        }
                );
    }

    private float dpToPx(float dp) {

        return dp
                * getResources()
                .getDisplayMetrics()
                .density;
    }

    private int dpToInt(int dp) {

        return Math.round(
                dp
                        * getResources()
                        .getDisplayMetrics()
                        .density
        );
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