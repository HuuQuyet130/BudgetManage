package com.example.cuoiki.activity;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cuoiki.adapter.BudgetAdapter;
import com.example.cuoiki.database.DatabaseHelper;
import com.example.cuoiki.databinding.ActivityBudgetBinding;
import com.example.cuoiki.databinding.DialogAddBudgetBinding;
import com.example.cuoiki.model.Account;
import com.example.cuoiki.model.Budget;
import com.example.cuoiki.model.Category;
import com.example.cuoiki.utils.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class BudgetActivity extends AppCompatActivity {

    private ActivityBudgetBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private ArrayList<Budget> budgetList;
    private BudgetAdapter budgetAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding =
                ActivityBudgetBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(binding.getRoot());

        databaseHelper =
                new DatabaseHelper(this);

        sessionManager =
                new SessionManager(this);

        setupToolbar();
        setupRecyclerView();
        setupActions();

        loadBudget();
    }

    private void setupToolbar() {



        binding.toolbar.setNavigationOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed()
        );
    }

    private void setupRecyclerView() {

        binding.rvBudget.setLayoutManager(
                new LinearLayoutManager(this)
        );

        binding.rvBudget.setHasFixedSize(true);
    }

    private void setupActions() {

        binding.fabAddBudget.setOnClickListener(v ->
                showAddBudgetDialog()
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
                databaseHelper.getAccountByUsername(username);

        if (account == null) {

            Toast.makeText(
                    this,
                    "Không tìm thấy tài khoản",
                    Toast.LENGTH_SHORT
            ).show();
        }

        return account;
    }

    private void loadBudget() {

        Account account = getCurrentAccount();

        if (account == null) {
            return;
        }

        budgetList =
                databaseHelper.getBudgetsByAccountId(
                        account.getId()
                );

        budgetAdapter =
                new BudgetAdapter(
                        budgetList,
                        new BudgetAdapter.OnBudgetActionListener() {

                            @Override
                            public void onEdit(Budget budget) {
                                showEditBudgetDialog(budget);
                            }

                            @Override
                            public void onDelete(Budget budget) {
                                showDeleteBudgetDialog(budget);
                            }
                        }
                );

        binding.rvBudget.setAdapter(budgetAdapter);
    }

    private void loadCategorySpinner(
            DialogAddBudgetBinding dialogBinding
    ) {

        ArrayList<Category> categoryList =
                databaseHelper.getCategoriesByType(
                        DatabaseHelper.TYPE_EXPENSE
                );

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
            DialogAddBudgetBinding dialogBinding,
            int categoryId
    ) {

        int itemCount =
                dialogBinding.spinnerCategory.getCount();

        for (int index = 0;
             index < itemCount;
             index++) {

            Object item =
                    dialogBinding.spinnerCategory
                            .getItemAtPosition(index);

            if (!(item instanceof Category)) {
                continue;
            }

            Category category = (Category) item;

            if (category.getId() == categoryId) {

                dialogBinding.spinnerCategory
                        .setSelection(index);

                return;
            }
        }
    }

    private void setDefaultBudgetValues(
            DialogAddBudgetBinding dialogBinding
    ) {

        Calendar calendar =
                Calendar.getInstance();

        int currentMonth =
                calendar.get(Calendar.MONTH) + 1;

        int currentYear =
                calendar.get(Calendar.YEAR);

        dialogBinding.editMonth.setText(
                String.valueOf(currentMonth)
        );

        dialogBinding.editYear.setText(
                String.valueOf(currentYear)
        );

        dialogBinding.editWarning.setText("80");
    }

    private void showAddBudgetDialog() {

        DialogAddBudgetBinding dialogBinding =
                DialogAddBudgetBinding.inflate(
                        getLayoutInflater()
                );

        loadCategorySpinner(dialogBinding);
        setDefaultBudgetValues(dialogBinding);

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Thêm ngân sách")
                        .setView(dialogBinding.getRoot())
                        .setPositiveButton("Lưu", null)
                        .setNegativeButton(
                                "Hủy",
                                (currentDialog, which) ->
                                        currentDialog.dismiss()
                        )
                        .create();

        dialog.show();

        dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
        ).setOnClickListener(v ->
                saveBudget(
                        dialog,
                        dialogBinding,
                        null
                )
        );
    }

    private void showEditBudgetDialog(
            Budget budget
    ) {

        DialogAddBudgetBinding dialogBinding =
                DialogAddBudgetBinding.inflate(
                        getLayoutInflater()
                );

        loadCategorySpinner(dialogBinding);

        dialogBinding.editBudgetAmount.setText(
                formatEditableNumber(
                        budget.getAmount()
                )
        );

        dialogBinding.editMonth.setText(
                String.valueOf(
                        budget.getMonth()
                )
        );

        dialogBinding.editYear.setText(
                String.valueOf(
                        budget.getYear()
                )
        );

        dialogBinding.editWarning.setText(
                String.valueOf(
                        budget.getWarningPercent()
                )
        );

        selectCategoryInSpinner(
                dialogBinding,
                budget.getCategoryId()
        );

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Sửa ngân sách")
                        .setView(dialogBinding.getRoot())
                        .setPositiveButton("Lưu", null)
                        .setNegativeButton("Hủy", null)
                        .create();

        dialog.show();

        dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
        ).setOnClickListener(v ->
                saveBudget(
                        dialog,
                        dialogBinding,
                        budget
                )
        );
    }

    private void saveBudget(
            AlertDialog dialog,
            DialogAddBudgetBinding dialogBinding,
            Budget existingBudget
    ) {

        clearErrors(dialogBinding);

        String amountText =
                getText(
                        dialogBinding.editBudgetAmount
                                .getText()
                );

        String monthText =
                getText(
                        dialogBinding.editMonth
                                .getText()
                );

        String yearText =
                getText(
                        dialogBinding.editYear
                                .getText()
                );

        String warningText =
                getText(
                        dialogBinding.editWarning
                                .getText()
                );

        if (amountText.isEmpty()) {

            dialogBinding.layoutBudgetAmount.setError(
                    "Vui lòng nhập ngân sách"
            );

            return;
        }

        if (monthText.isEmpty()) {

            dialogBinding.layoutMonth.setError(
                    "Vui lòng nhập tháng"
            );

            return;
        }

        if (yearText.isEmpty()) {

            dialogBinding.layoutYear.setError(
                    "Vui lòng nhập năm"
            );

            return;
        }

        if (warningText.isEmpty()) {
            warningText = "80";
        }

        double amount;
        int month;
        int year;
        int warningPercent;

        try {

            amount =
                    Double.parseDouble(amountText);

            month =
                    Integer.parseInt(monthText);

            year =
                    Integer.parseInt(yearText);

            warningPercent =
                    Integer.parseInt(warningText);

        } catch (NumberFormatException exception) {

            Toast.makeText(
                    this,
                    "Dữ liệu không hợp lệ",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (amount <= 0) {

            dialogBinding.layoutBudgetAmount.setError(
                    "Ngân sách phải lớn hơn 0"
            );

            return;
        }

        if (month < 1 || month > 12) {

            dialogBinding.layoutMonth.setError(
                    "Tháng phải từ 1 đến 12"
            );

            return;
        }

        if (year < 2000 || year > 2100) {

            dialogBinding.layoutYear.setError(
                    "Năm phải từ 2000 đến 2100"
            );

            return;
        }

        if (
                warningPercent < 1
                        || warningPercent > 100
        ) {

            dialogBinding.layoutWarning.setError(
                    "Mức cảnh báo phải từ 1 đến 100%"
            );

            return;
        }

        Object selectedItem =
                dialogBinding.spinnerCategory
                        .getSelectedItem();

        if (!(selectedItem instanceof Category)) {

            Toast.makeText(
                    this,
                    "Chưa có danh mục chi tiêu",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Category category =
                (Category) selectedItem;

        Account account =
                getCurrentAccount();

        if (account == null) {
            return;
        }

        boolean success;

        try {

            if (existingBudget == null) {

                Budget newBudget =
                        new Budget(
                                month,
                                year,
                                amount,
                                category.getId(),
                                account.getId(),
                                warningPercent
                        );

                success =
                        databaseHelper.insertBudget(
                                newBudget
                        );

            } else {

                existingBudget.setMonth(month);
                existingBudget.setYear(year);
                existingBudget.setAmount(amount);

                existingBudget.setCategoryId(
                        category.getId()
                );

                existingBudget.setAccountId(
                        account.getId()
                );

                existingBudget.setWarningPercent(
                        warningPercent
                );

                success =
                        databaseHelper.updateBudget(
                                existingBudget
                        );
            }

        } catch (SQLiteConstraintException exception) {

            success = false;
        }

        if (success) {

            Toast.makeText(
                    this,
                    existingBudget == null
                            ? "Thêm ngân sách thành công"
                            : "Cập nhật ngân sách thành công",
                    Toast.LENGTH_SHORT
            ).show();

            loadBudget();
            dialog.dismiss();

        } else {

            Toast.makeText(
                    this,
                    "Ngân sách của danh mục trong tháng này đã tồn tại",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void clearErrors(
            DialogAddBudgetBinding dialogBinding
    ) {

        dialogBinding.layoutBudgetAmount.setError(null);
        dialogBinding.layoutMonth.setError(null);
        dialogBinding.layoutYear.setError(null);
        dialogBinding.layoutWarning.setError(null);
    }

    private String getText(
            CharSequence value
    ) {

        return value == null
                ? ""
                : value.toString().trim();
    }

    private String formatEditableNumber(
            double value
    ) {

        if (value == Math.floor(value)) {

            return String.format(
                    Locale.US,
                    "%.0f",
                    value
            );
        }

        return String.valueOf(value);
    }

    private void showDeleteBudgetDialog(
            Budget budget
    ) {

        String categoryName =
                budget.getCategoryName();

        if (
                categoryName == null
                        || categoryName.trim().isEmpty()
        ) {
            categoryName = "danh mục này";
        }

        new AlertDialog.Builder(this)
                .setTitle("Xóa ngân sách")
                .setMessage(
                        "Bạn có chắc muốn xóa ngân sách \""
                                + categoryName
                                + "\" tháng "
                                + String.format(
                                Locale.getDefault(),
                                "%02d/%d",
                                budget.getMonth(),
                                budget.getYear()
                        )
                                + "?"
                )
                .setPositiveButton(
                        "Xóa",
                        (dialog, which) -> {

                            boolean success =
                                    databaseHelper.deleteBudget(
                                            budget.getId()
                                    );

                            if (success) {

                                Toast.makeText(
                                        this,
                                        "Đã xóa ngân sách",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadBudget();

                            } else {

                                Toast.makeText(
                                        this,
                                        "Xóa ngân sách thất bại",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
         * Khi quay lại từ màn hình Giao dịch,
         * ngân sách sẽ tính lại số tiền đã chi.
         */
        if (
                databaseHelper != null
                        && sessionManager != null
        ) {
            loadBudget();
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