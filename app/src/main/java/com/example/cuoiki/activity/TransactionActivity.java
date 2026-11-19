package com.example.cuoiki.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.view.View;
import android.view.View;
import android.widget.AdapterView;

import java.util.Collections;
import java.util.LinkedHashSet;
import android.widget.AdapterView;
import com.example.cuoiki.adapter.TransactionAdapter;
import com.example.cuoiki.database.DatabaseHelper;
import com.example.cuoiki.databinding.ActivityTransactionBinding;
import com.example.cuoiki.databinding.DialogAddTransactionBinding;
import com.example.cuoiki.model.Account;
import com.example.cuoiki.model.Budget;
import com.example.cuoiki.model.Category;
import com.example.cuoiki.model.Transaction;
import com.example.cuoiki.utils.SessionManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TransactionActivity extends AppCompatActivity {

    private ActivityTransactionBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private ArrayList<Transaction> transactionList;
    private TransactionAdapter transactionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        binding.toolbar.setNavigationOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed()
        );

        binding.fabAddTransaction.setOnClickListener(v ->
                showAddTransactionDialog()
        );

        loadTransaction();
        setupTypeFilter();
        setupMonthFilter();
        setupSearch();
        binding.searchView.setOnQueryTextListener(
                new androidx.appcompat.widget.SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        transactionAdapter.getFilter().filter(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        transactionAdapter.getFilter().filter(newText);
                        return false;
                    }
                }
        );
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{
                        "Tất cả",
                        "Thu nhập",
                        "Chi tiêu"
                }
        );

        binding.spinnerTypeFilter.setAdapter(filterAdapter);
        binding.spinnerTypeFilter.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        transactionAdapter.setTypeFilter(
                                parent.getItemAtPosition(position).toString()
                        );
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

    }

    private void loadTransaction() {

        String username = sessionManager.getUsername();

        if (username == null) {
            return;
        }

        Account account =
                databaseHelper.getAccountByUsername(username);

        if (account == null) {
            return;
        }

        transactionList =
                databaseHelper.getTransactionsByAccountId(
                        account.getId()
                );

        transactionAdapter =
                new TransactionAdapter(
                        transactionList,
                        new TransactionAdapter.OnTransactionActionListener() {

                            @Override
                            public void onEdit(Transaction transaction) {
                                showEditTransactionDialog(transaction);
                            }
                            @Override
                            public void onDelete(Transaction transaction) {
                                showDeleteTransactionDialog(transaction);
                            }
                        }
                );

        binding.rvTransaction.setLayoutManager(
                new LinearLayoutManager(this)
        );

        binding.rvTransaction.setAdapter(transactionAdapter);
    }

    private void loadCategorySpinner(
            DialogAddTransactionBinding dialogBinding,
            String type
    ) {

        ArrayList<Category> categoryList =
                databaseHelper.getCategoriesByType(type);

        ArrayAdapter<Category> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categoryList
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        dialogBinding.spinnerCategory.setAdapter(adapter);
    }
private void selectCategoryInSpinner(
        DialogAddTransactionBinding dialogBinding,
        int categoryId
) {

    for (int i = 0;
         i < dialogBinding.spinnerCategory.getCount();
         i++) {

        Category category =
                (Category) dialogBinding.spinnerCategory.getItemAtPosition(i);

        if (category.getId() == categoryId) {
            dialogBinding.spinnerCategory.setSelection(i);
            break;
        }
    }
}

    private void showAddTransactionDialog() {

        DialogAddTransactionBinding dialogBinding =
                DialogAddTransactionBinding.inflate(getLayoutInflater());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Thêm giao dịch")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", (d, which) -> d.dismiss())
                .create();

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(
                    android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            );
        }

        setCurrentDate(dialogBinding);
        setupDatePicker(dialogBinding);

        loadCategorySpinner(
                dialogBinding,
                DatabaseHelper.TYPE_EXPENSE
        );

        dialogBinding.radioIncome.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    if (isChecked) {
                        loadCategorySpinner(
                                dialogBinding,
                                DatabaseHelper.TYPE_INCOME
                        );
                    }
                }
        );

        dialogBinding.radioExpense.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    if (isChecked) {
                        loadCategorySpinner(
                                dialogBinding,
                                DatabaseHelper.TYPE_EXPENSE
                        );
                    }
                }
        );

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v ->
                        saveTransaction(dialog, dialogBinding)
                );
    }

    private void setCurrentDate(
            DialogAddTransactionBinding dialogBinding
    ) {

        Calendar calendar = Calendar.getInstance();

        String currentDate = String.format(
                Locale.getDefault(),
                "%04d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialogBinding.editTransactionDate.setText(currentDate);
    }

    private void setupDatePicker(
            DialogAddTransactionBinding dialogBinding
    ) {

        dialogBinding.editTransactionDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(
                            TransactionActivity.this,
                            (view,
                             selectedYear,
                             selectedMonth,
                             selectedDay) -> {

                                String selectedDate = String.format(
                                        Locale.getDefault(),
                                        "%04d-%02d-%02d",
                                        selectedYear,
                                        selectedMonth + 1,
                                        selectedDay
                                );

                                dialogBinding.editTransactionDate.setText(
                                        selectedDate
                                );
                            },
                            year,
                            month,
                            day
                    );

            datePickerDialog.show();
        });
    }

    private void saveTransaction(
            AlertDialog dialog,
            DialogAddTransactionBinding dialogBinding
    ) {

        String title =
                dialogBinding.editTransactionTitle.getText() == null
                        ? ""
                        : dialogBinding.editTransactionTitle
                        .getText()
                        .toString()
                        .trim();

        String amountText =
                dialogBinding.editTransactionAmount.getText() == null
                        ? ""
                        : dialogBinding.editTransactionAmount
                        .getText()
                        .toString()
                        .trim();

        String date =
                dialogBinding.editTransactionDate.getText() == null
                        ? ""
                        : dialogBinding.editTransactionDate
                        .getText()
                        .toString()
                        .trim();

        String note =
                dialogBinding.editTransactionNote.getText() == null
                        ? ""
                        : dialogBinding.editTransactionNote
                        .getText()
                        .toString()
                        .trim();

        dialogBinding.layoutTransactionTitle.setError(null);
        dialogBinding.layoutTransactionAmount.setError(null);
        dialogBinding.layoutTransactionDate.setError(null);

        if (title.isEmpty()) {
            dialogBinding.layoutTransactionTitle.setError(
                    "Vui lòng nhập tên giao dịch"
            );
            return;
        }

        if (amountText.isEmpty()) {
            dialogBinding.layoutTransactionAmount.setError(
                    "Vui lòng nhập số tiền"
            );
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            dialogBinding.layoutTransactionAmount.setError(
                    "Số tiền không hợp lệ"
            );
            return;
        }

        if (amount <= 0) {
            dialogBinding.layoutTransactionAmount.setError(
                    "Số tiền phải lớn hơn 0"
            );
            return;
        }

        if (date.isEmpty()) {
            dialogBinding.layoutTransactionDate.setError(
                    "Vui lòng chọn ngày giao dịch"
            );
            return;
        }

        Category selectedCategory =
                (Category) dialogBinding.spinnerCategory
                        .getSelectedItem();

        if (selectedCategory == null) {
            Toast.makeText(
                    this,
                    "Chưa có danh mục phù hợp",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String type;

        if (dialogBinding.radioIncome.isChecked()) {
            type = DatabaseHelper.TYPE_INCOME;
        } else {
            type = DatabaseHelper.TYPE_EXPENSE;
        }

        String username = sessionManager.getUsername();

        if (username == null) {
            Toast.makeText(
                    this,
                    "Phiên đăng nhập không hợp lệ",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Account account =
                databaseHelper.getAccountByUsername(username);

        if (account == null) {
            Toast.makeText(
                    this,
                    "Không tìm thấy tài khoản",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Transaction transaction = new Transaction(
                title,
                amount,
                date,
                note,
                type,
                selectedCategory.getId(),
                account.getId(),
                String.valueOf(System.currentTimeMillis())
        );

        boolean success =
                databaseHelper.insertTransaction(transaction);

        if (success) {

            Toast.makeText(
                    this,
                    "Thêm giao dịch thành công",
                    Toast.LENGTH_SHORT
            ).show();

            loadTransaction();
            dialog.dismiss();

            checkBudgetWarning(transaction);

        } else {

            Toast.makeText(
                    this,
                    "Thêm giao dịch thất bại",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
private void showEditTransactionDialog(
        Transaction transaction
) {

    DialogAddTransactionBinding dialogBinding =
            DialogAddTransactionBinding.inflate(getLayoutInflater());

    dialogBinding.editTransactionTitle.setText(
            transaction.getTitle()
    );

    dialogBinding.editTransactionAmount.setText(
            String.valueOf(transaction.getAmount())
    );

    dialogBinding.editTransactionDate.setText(
            transaction.getDate()
    );

    dialogBinding.editTransactionNote.setText(
            transaction.getNote()
    );

    if (DatabaseHelper.TYPE_INCOME.equals(
            transaction.getType()
    )) {
        dialogBinding.radioIncome.setChecked(true);
    } else {
        dialogBinding.radioExpense.setChecked(true);
    }

    loadCategorySpinner(
            dialogBinding,
            transaction.getType()
    );

    selectCategoryInSpinner(
            dialogBinding,
            transaction.getCategoryId()
    );

    setupDatePicker(dialogBinding);

    AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Sửa giao dịch")
            .setView(dialogBinding.getRoot())
            .setPositiveButton("Lưu", null)
            .setNegativeButton("Hủy", null)
            .create();

    dialog.show();

    dialogBinding.radioIncome.setOnCheckedChangeListener(
            (buttonView, isChecked) -> {
                if (isChecked) {
                    loadCategorySpinner(
                            dialogBinding,
                            DatabaseHelper.TYPE_INCOME
                    );
                }
            }
    );

    dialogBinding.radioExpense.setOnCheckedChangeListener(
            (buttonView, isChecked) -> {
                if (isChecked) {
                    loadCategorySpinner(
                            dialogBinding,
                            DatabaseHelper.TYPE_EXPENSE
                    );
                }
            }
    );

    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener(v -> {

                String title =
                        dialogBinding.editTransactionTitle.getText() == null
                                ? ""
                                : dialogBinding.editTransactionTitle
                                .getText()
                                .toString()
                                .trim();

                String amountText =
                        dialogBinding.editTransactionAmount.getText() == null
                                ? ""
                                : dialogBinding.editTransactionAmount
                                .getText()
                                .toString()
                                .trim();

                String date =
                        dialogBinding.editTransactionDate.getText() == null
                                ? ""
                                : dialogBinding.editTransactionDate
                                .getText()
                                .toString()
                                .trim();

                String note =
                        dialogBinding.editTransactionNote.getText() == null
                                ? ""
                                : dialogBinding.editTransactionNote
                                .getText()
                                .toString()
                                .trim();

                if (title.isEmpty()) {
                    dialogBinding.layoutTransactionTitle.setError(
                            "Vui lòng nhập tên giao dịch"
                    );
                    return;
                }

                if (amountText.isEmpty()) {
                    dialogBinding.layoutTransactionAmount.setError(
                            "Vui lòng nhập số tiền"
                    );
                    return;
                }

                double amount;

                try {
                    amount = Double.parseDouble(amountText);
                } catch (NumberFormatException e) {
                    dialogBinding.layoutTransactionAmount.setError(
                            "Số tiền không hợp lệ"
                    );
                    return;
                }

                if (amount <= 0) {
                    dialogBinding.layoutTransactionAmount.setError(
                            "Số tiền phải lớn hơn 0"
                    );
                    return;
                }

                Category selectedCategory =
                        (Category) dialogBinding.spinnerCategory
                                .getSelectedItem();

                if (selectedCategory == null) {
                    Toast.makeText(
                            this,
                            "Chưa có danh mục phù hợp",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                String type =
                        dialogBinding.radioIncome.isChecked()
                                ? DatabaseHelper.TYPE_INCOME
                                : DatabaseHelper.TYPE_EXPENSE;

                transaction.setTitle(title);
                transaction.setAmount(amount);
                transaction.setDate(date);
                transaction.setNote(note);
                transaction.setType(type);
                transaction.setCategoryId(
                        selectedCategory.getId()
                );

                boolean success =
                        databaseHelper.updateTransaction(
                                transaction
                        );

                if (success) {
                    Toast.makeText(
                            this,
                            "Cập nhật giao dịch thành công",
                            Toast.LENGTH_SHORT
                    ).show();

                    loadTransaction();
                    dialog.dismiss();

                } else {
                    Toast.makeText(
                            this,
                            "Cập nhật giao dịch thất bại",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
}
    private void showDeleteTransactionDialog(
            Transaction transaction
    ) {

        new AlertDialog.Builder(this)
                .setTitle("Xóa giao dịch")
                .setMessage(
                        "Bạn có chắc muốn xóa \"" +
                                transaction.getTitle() +
                                "\"?"
                )
                .setPositiveButton("Xóa", (dialog, which) -> {

                    boolean success =
                            databaseHelper.deleteTransaction(
                                    transaction.getId()
                            );

                    if (success) {
                        Toast.makeText(
                                this,
                                "Đã xóa giao dịch",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadTransaction();

                    } else {
                        Toast.makeText(
                                this,
                                "Xóa giao dịch thất bại",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    private void checkBudgetWarning(
            Transaction transaction
    ) {

        if (!DatabaseHelper.TYPE_EXPENSE.equals(
                transaction.getType()
        )) {
            return;
        }

        Budget budget =
                databaseHelper.getBudgetByTransaction(
                        transaction.getAccountId(),
                        transaction.getCategoryId(),
                        transaction.getDate()
                );

        if (budget == null) {
            return;
        }

        int percent = budget.getUsagePercent();

        if (percent >= 100) {

            showBudgetExceededDialog(budget);

        } else if (
                percent >= budget.getWarningPercent()
        ) {

            showBudgetWarningDialog(budget);
        }
    }
    private void showBudgetWarningDialog(
            Budget budget
    ) {

        String message =
                "Danh mục: "
                        + budget.getCategoryName()

                        + "\n\nĐã chi: "
                        + String.format("%,.0f", budget.getSpentAmount())
                        + " VNĐ"

                        + "\nNgân sách: "
                        + String.format("%,.0f", budget.getAmount())
                        + " VNĐ"

                        + "\nĐã sử dụng: "
                        + budget.getUsagePercent()
                        + "%"

                        + "\nCòn lại: "
                        + String.format(
                        "%,.0f",
                        budget.getRemainingAmount()
                )
                        + " VNĐ";

        new MaterialAlertDialogBuilder(this)
                .setTitle("⚠ CẢNH BÁO NGÂN SÁCH")
                .setMessage(message)
                .setPositiveButton("Đã hiểu", null)
                .show();
    }

    private void showBudgetExceededDialog(
            Budget budget
    ) {

        double exceeded =
                budget.getSpentAmount()
                        - budget.getAmount();

        String message =
                "Danh mục: "
                        + budget.getCategoryName()

                        + "\n\nĐã chi: "
                        + String.format("%,.0f", budget.getSpentAmount())
                        + " VNĐ"

                        + "\nNgân sách: "
                        + String.format("%,.0f", budget.getAmount())
                        + " VNĐ"

                        + "\nVượt: "
                        + String.format("%,.0f", exceeded)
                        + " VNĐ";

        new MaterialAlertDialogBuilder(this)
                .setTitle("🚨 ĐÃ VƯỢT NGÂN SÁCH")
                .setMessage(message)
                .setPositiveButton("Đã hiểu", null)
                .show();
    }
    private void setupSearch() {

        binding.searchView.setOnQueryTextListener(
                new androidx.appcompat.widget.SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        if (transactionAdapter != null) {
                            transactionAdapter
                                    .getFilter()
                                    .filter(query);
                        }

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {

                        if (transactionAdapter != null) {
                            transactionAdapter
                                    .getFilter()
                                    .filter(newText);
                        }

                        return false;
                    }
                }
        );
    }
    private void setupTypeFilter() {

        ArrayList<String> typeList = new ArrayList<>();

        typeList.add("Tất cả");
        typeList.add("Thu nhập");
        typeList.add("Chi tiêu");

        ArrayAdapter<String> typeAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        typeList
                );

        typeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        binding.spinnerTypeFilter.setAdapter(typeAdapter);

        binding.spinnerTypeFilter.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        if (transactionAdapter == null) {
                            return;
                        }

                        String selectedType =
                                parent.getItemAtPosition(position)
                                        .toString();

                        transactionAdapter.setTypeFilter(
                                selectedType
                        );
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {

                    }
                }
        );
    }
    private void setupMonthFilter() {

        ArrayList<String> monthList =
                new ArrayList<>();

        monthList.add("Tất cả tháng");

        LinkedHashSet<String> monthSet =
                new LinkedHashSet<>();

        if (transactionList != null) {

            for (Transaction transaction : transactionList) {

                String date = transaction.getDate();

                if (date != null && date.length() >= 7) {

                    try {

                        String year =
                                date.substring(0, 4);

                        String month =
                                date.substring(5, 7);

                        monthSet.add(
                                month + "/" + year
                        );

                    } catch (Exception ignored) {

                    }
                }
            }
        }

        ArrayList<String> sortedMonths =
                new ArrayList<>(monthSet);

        Collections.sort(
                sortedMonths,
                (month1, month2) -> {

                    String[] parts1 =
                            month1.split("/");

                    String[] parts2 =
                            month2.split("/");

                    if (parts1.length < 2 || parts2.length < 2) {
                        return 0;
                    }

                    String key1 =
                            parts1[1] + parts1[0];

                    String key2 =
                            parts2[1] + parts2[0];

                    return key2.compareTo(key1);
                }
        );

        monthList.addAll(sortedMonths);

        ArrayAdapter<String> monthAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        monthList
                );

        monthAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        binding.spinnerMonthFilter.setAdapter(monthAdapter);

        binding.spinnerMonthFilter.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        if (transactionAdapter == null) {
                            return;
                        }

                        String selectedMonth =
                                parent.getItemAtPosition(position)
                                        .toString();

                        transactionAdapter.setMonthFilter(
                                selectedMonth
                        );
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {

                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        binding = null;
        super.onDestroy();
    }
}