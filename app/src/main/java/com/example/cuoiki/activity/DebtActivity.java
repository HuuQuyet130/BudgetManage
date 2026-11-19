package com.example.cuoiki.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cuoiki.adapter.DebtAdapter;
import com.example.cuoiki.database.DatabaseHelper;
import com.example.cuoiki.model.Account;
import com.example.cuoiki.model.Debt;
import com.example.cuoiki.utils.SessionManager;
import androidx.appcompat.app.AlertDialog;
import android.app.DatePickerDialog;
import android.widget.Toast;

import com.example.cuoiki.databinding.DialogAddDebtBinding;

import java.util.Calendar;
import java.util.Locale;

import java.util.ArrayList;

import com.example.cuoiki.databinding.ActivityDebtBinding;

public class DebtActivity extends AppCompatActivity {

    private ActivityDebtBinding binding;
    private DatabaseHelper databaseHelper;

    private SessionManager sessionManager;

    private ArrayList<Debt> debtList;

    private DebtAdapter debtAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDebtBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        databaseHelper = new DatabaseHelper(this);

        sessionManager = new SessionManager(this);

        loadDebt();

        binding.toolbar.setNavigationIcon(
                androidx.appcompat.R.drawable.abc_ic_ab_back_material
        );

        binding.toolbar.setNavigationOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed()
        );
        binding.fabAddDebt.setOnClickListener(v ->
                showAddDebtDialog()
        );
    }
    private void loadDebt() {

        String username =
                sessionManager.getUsername();

        if (username == null) {
            return;
        }

        Account account =
                databaseHelper.getAccountByUsername(username);

        if (account == null) {
            return;
        }

        debtList =
                databaseHelper.getDebtsByAccountId(
                        account.getId()
                );

        debtAdapter =
                new DebtAdapter(
                        debtList,
                        new DebtAdapter.OnDebtActionListener() {

                            @Override
                            public void onEdit(Debt debt) {

                                showEditDebtDialog(debt);

                            }

                            @Override
                            public void onDelete(Debt debt) {
                                showDeleteDebtDialog(debt);
                            }
                        }
                );

        binding.rvDebt.setLayoutManager(
                new LinearLayoutManager(this)
        );

        binding.rvDebt.setAdapter(debtAdapter);
    }
    private void showAddDebtDialog() {

        DialogAddDebtBinding dialogBinding =
                DialogAddDebtBinding.inflate(getLayoutInflater());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Thêm khoản nợ")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", (d, which) -> d.dismiss())
                .create();

        dialog.show();

        dialogBinding.editDeadline.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            DatePickerDialog picker =
                    new DatePickerDialog(
                            this,
                            (view, year, month, day) -> {

                                String date = String.format(
                                        Locale.getDefault(),
                                        "%04d-%02d-%02d",
                                        year,
                                        month + 1,
                                        day
                                );

                                dialogBinding.editDeadline.setText(date);

                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                    );

            picker.show();

        });

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {

                    String person =
                            dialogBinding.editPerson.getText() == null
                                    ? ""
                                    : dialogBinding.editPerson
                                    .getText()
                                    .toString()
                                    .trim();

                    String amountText =
                            dialogBinding.editAmount.getText() == null
                                    ? ""
                                    : dialogBinding.editAmount
                                    .getText()
                                    .toString()
                                    .trim();

                    String deadline =
                            dialogBinding.editDeadline.getText() == null
                                    ? ""
                                    : dialogBinding.editDeadline
                                    .getText()
                                    .toString()
                                    .trim();

                    dialogBinding.layoutPerson.setError(null);
                    dialogBinding.layoutAmount.setError(null);
                    dialogBinding.layoutDeadline.setError(null);

                    if (person.isEmpty()) {
                        dialogBinding.layoutPerson.setError(
                                "Vui lòng nhập tên"
                        );
                        return;
                    }

                    if (amountText.isEmpty()) {
                        dialogBinding.layoutAmount.setError(
                                "Vui lòng nhập số tiền"
                        );
                        return;
                    }

                    if (deadline.isEmpty()) {
                        dialogBinding.layoutDeadline.setError(
                                "Vui lòng chọn hạn"
                        );
                        return;
                    }

                    double amount;

                    try {

                        amount = Double.parseDouble(amountText);

                    } catch (NumberFormatException e) {

                        dialogBinding.layoutAmount.setError(
                                "Số tiền không hợp lệ"
                        );

                        return;
                    }

                    if (amount <= 0) {

                        dialogBinding.layoutAmount.setError(
                                "Số tiền phải lớn hơn 0"
                        );

                        return;
                    }

                    String type =
                            dialogBinding.radioBorrow.isChecked()
                                    ? DatabaseHelper.DEBT_BORROW
                                    : DatabaseHelper.DEBT_LEND;

                    String status =
                            dialogBinding.radioPending.isChecked()
                                    ? DatabaseHelper.STATUS_PENDING
                                    : DatabaseHelper.STATUS_PAID;

                    String username =
                            sessionManager.getUsername();

                    Account account =
                            databaseHelper.getAccountByUsername(
                                    username
                            );

                    if (account == null) {

                        Toast.makeText(
                                this,
                                "Không tìm thấy tài khoản",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    Debt debt = new Debt(
                            person,
                            amount,
                            deadline,
                            status,
                            type,
                            account.getId()
                    );

                    boolean success =
                            databaseHelper.insertDebt(debt);

                    if (success) {

                        Toast.makeText(
                                this,
                                "Thêm khoản nợ thành công",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadDebt();

                        dialog.dismiss();

                    } else {

                        Toast.makeText(
                                this,
                                "Thêm khoản nợ thất bại",
                                Toast.LENGTH_SHORT
                        ).show();

                    }

                });

    }
    private void showDeleteDebtDialog(Debt debt) {

        new AlertDialog.Builder(this)
                .setTitle("Xóa khoản nợ")
                .setMessage(
                        "Bạn có chắc muốn xóa khoản nợ của \"" +
                                debt.getPerson() +
                                "\"?"
                )
                .setPositiveButton("Xóa", (dialog, which) -> {

                    boolean success =
                            databaseHelper.deleteDebt(debt.getId());

                    if (success) {
                        Toast.makeText(
                                DebtActivity.this,
                                "Đã xóa khoản nợ",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadDebt();

                    } else {
                        Toast.makeText(
                                DebtActivity.this,
                                "Xóa khoản nợ thất bại",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    private void showEditDebtDialog(Debt debt) {

        DialogAddDebtBinding dialogBinding =
                DialogAddDebtBinding.inflate(getLayoutInflater());

        dialogBinding.editPerson.setText(
                debt.getPerson()
        );

        dialogBinding.editAmount.setText(
                String.valueOf(debt.getAmount())
        );

        dialogBinding.editDeadline.setText(
                debt.getDeadline()
        );

        if (debt.getType().equals(DatabaseHelper.DEBT_BORROW)) {
            dialogBinding.radioBorrow.setChecked(true);
        } else {
            dialogBinding.radioLend.setChecked(true);
        }

        if (debt.getStatus().equals(DatabaseHelper.STATUS_PENDING)) {
            dialogBinding.radioPending.setChecked(true);
        } else {
            dialogBinding.radioPaid.setChecked(true);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Sửa khoản nợ")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Cập nhật", null)
                .setNegativeButton("Hủy", (d, w) -> d.dismiss())
                .create();

        dialog.show();

        dialogBinding.editDeadline.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            DatePickerDialog picker =
                    new DatePickerDialog(
                            this,
                            (view, year, month, day) -> {

                                String date = String.format(
                                        Locale.getDefault(),
                                        "%04d-%02d-%02d",
                                        year,
                                        month + 1,
                                        day
                                );

                                dialogBinding.editDeadline.setText(date);

                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                    );

            picker.show();

        });

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {

                    String person =
                            dialogBinding.editPerson.getText()
                                    .toString()
                                    .trim();

                    String amountText =
                            dialogBinding.editAmount.getText()
                                    .toString()
                                    .trim();

                    String deadline =
                            dialogBinding.editDeadline.getText()
                                    .toString()
                                    .trim();

                    if (person.isEmpty()
                            || amountText.isEmpty()
                            || deadline.isEmpty()) {

                        Toast.makeText(
                                this,
                                "Vui lòng nhập đầy đủ",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    double amount;

                    try {

                        amount = Double.parseDouble(amountText);

                    } catch (Exception e) {

                        Toast.makeText(
                                this,
                                "Số tiền không hợp lệ",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    debt.setPerson(person);
                    debt.setAmount(amount);
                    debt.setDeadline(deadline);

                    debt.setType(
                            dialogBinding.radioBorrow.isChecked()
                                    ? DatabaseHelper.DEBT_BORROW
                                    : DatabaseHelper.DEBT_LEND
                    );

                    debt.setStatus(
                            dialogBinding.radioPending.isChecked()
                                    ? DatabaseHelper.STATUS_PENDING
                                    : DatabaseHelper.STATUS_PAID
                    );

                    if (databaseHelper.updateDebt(debt)) {

                        Toast.makeText(
                                this,
                                "Cập nhật thành công",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadDebt();

                        dialog.dismiss();

                    } else {

                        Toast.makeText(
                                this,
                                "Cập nhật thất bại",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                });

    }

    @Override
    protected void onDestroy() {

        databaseHelper.close();

        binding = null;

        super.onDestroy();
    }
}