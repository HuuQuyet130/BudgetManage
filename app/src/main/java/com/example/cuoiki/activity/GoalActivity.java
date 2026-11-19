package com.example.cuoiki.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cuoiki.adapter.GoalAdapter;
import com.example.cuoiki.database.DatabaseHelper;
import com.example.cuoiki.databinding.ActivityGoalBinding;
import com.example.cuoiki.databinding.DialogAddGoalBinding;
import com.example.cuoiki.model.Account;
import com.example.cuoiki.model.Goal;
import com.example.cuoiki.utils.SessionManager;
import com.example.cuoiki.databinding.DialogAddGoalAmountBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class GoalActivity extends AppCompatActivity {

    private ActivityGoalBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private ArrayList<Goal> goalList;
    private GoalAdapter goalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGoalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        binding.toolbar.setNavigationIcon(
                androidx.appcompat.R.drawable.abc_ic_ab_back_material
        );

        binding.toolbar.setNavigationOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed()
        );

        binding.fabAddGoal.setOnClickListener(v ->
                showAddGoalDialog()
        );

        loadGoal();
    }

    private void loadGoal() {

        String username = sessionManager.getUsername();

        if (username == null) {
            return;
        }

        Account account =
                databaseHelper.getAccountByUsername(username);

        if (account == null) {
            return;
        }

        goalList =
                databaseHelper.getGoalsByAccountId(
                        account.getId()
                );

        goalAdapter =
                new GoalAdapter(
                        goalList,
                        new GoalAdapter.OnGoalActionListener() {

                            @Override
                            public void onEdit(Goal goal) {
                                showEditGoalDialog(goal);
                            }

                            @Override
                            public void onAddAmount(Goal goal) {
                                showAddGoalAmountDialog(goal);
                            }

                            @Override
                            public void onDelete(Goal goal) {
                                showDeleteGoalDialog(goal);
                            }
                        }
                );

        binding.rvGoal.setLayoutManager(
                new LinearLayoutManager(this)
        );

        binding.rvGoal.setAdapter(goalAdapter);
    }
    private void showAddGoalDialog() {

        DialogAddGoalBinding dialogBinding =
                DialogAddGoalBinding.inflate(getLayoutInflater());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Thêm mục tiêu")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", (d, which) -> d.dismiss())
                .create();

        dialog.show();

        dialogBinding.editDeadline.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            DatePickerDialog picker =
                    new DatePickerDialog(
                            GoalActivity.this,
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

                    String title =
                            dialogBinding.editGoalTitle.getText() == null
                                    ? ""
                                    : dialogBinding.editGoalTitle
                                    .getText()
                                    .toString()
                                    .trim();

                    String targetText =
                            dialogBinding.editTargetAmount.getText() == null
                                    ? ""
                                    : dialogBinding.editTargetAmount
                                    .getText()
                                    .toString()
                                    .trim();

                    String currentText =
                            dialogBinding.editCurrentAmount.getText() == null
                                    ? ""
                                    : dialogBinding.editCurrentAmount
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

                    dialogBinding.layoutGoalTitle.setError(null);
                    dialogBinding.layoutTargetAmount.setError(null);
                    dialogBinding.layoutCurrentAmount.setError(null);
                    dialogBinding.layoutDeadline.setError(null);

                    if (title.isEmpty()) {
                        dialogBinding.layoutGoalTitle.setError(
                                "Vui lòng nhập tên mục tiêu"
                        );
                        return;
                    }

                    if (targetText.isEmpty()) {
                        dialogBinding.layoutTargetAmount.setError(
                                "Vui lòng nhập số tiền mục tiêu"
                        );
                        return;
                    }

                    if (currentText.isEmpty()) {
                        currentText = "0";
                    }

                    double targetAmount;
                    double currentAmount;

                    try {
                        targetAmount = Double.parseDouble(targetText);
                        currentAmount = Double.parseDouble(currentText);
                    } catch (NumberFormatException e) {
                        Toast.makeText(
                                GoalActivity.this,
                                "Số tiền không hợp lệ",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    if (targetAmount <= 0) {
                        dialogBinding.layoutTargetAmount.setError(
                                "Số tiền mục tiêu phải lớn hơn 0"
                        );
                        return;
                    }

                    if (currentAmount < 0) {
                        dialogBinding.layoutCurrentAmount.setError(
                                "Số tiền đã tiết kiệm không hợp lệ"
                        );
                        return;
                    }

                    if (currentAmount > targetAmount) {
                        dialogBinding.layoutCurrentAmount.setError(
                                "Số tiền đã tiết kiệm không được vượt mục tiêu"
                        );
                        return;
                    }

                    if (deadline.isEmpty()) {
                        dialogBinding.layoutDeadline.setError(
                                "Vui lòng chọn hạn hoàn thành"
                        );
                        return;
                    }

                    String username = sessionManager.getUsername();

                    if (username == null) {
                        Toast.makeText(
                                GoalActivity.this,
                                "Phiên đăng nhập không hợp lệ",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    Account account =
                            databaseHelper.getAccountByUsername(username);

                    if (account == null) {
                        Toast.makeText(
                                GoalActivity.this,
                                "Không tìm thấy tài khoản",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    Goal goal = new Goal(
                            title,
                            targetAmount,
                            currentAmount,
                            deadline,
                            account.getId()
                    );

                    boolean success =
                            databaseHelper.insertGoal(goal);

                    if (success) {
                        Toast.makeText(
                                GoalActivity.this,
                                "Thêm mục tiêu thành công",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadGoal();
                        dialog.dismiss();

                    } else {
                        Toast.makeText(
                                GoalActivity.this,
                                "Thêm mục tiêu thất bại",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });

    }
    private void showDeleteGoalDialog(Goal goal) {

        new AlertDialog.Builder(this)
                .setTitle("Xóa mục tiêu")
                .setMessage(
                        "Bạn có chắc muốn xóa \"" +
                                goal.getTitle() +
                                "\" ?"
                )
                .setPositiveButton("Xóa", (dialog, which) -> {

                    boolean success =
                            databaseHelper.deleteGoal(
                                    goal.getId()
                            );

                    if (success) {

                        Toast.makeText(
                                GoalActivity.this,
                                "Đã xóa",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadGoal();

                    } else {

                        Toast.makeText(
                                GoalActivity.this,
                                "Xóa thất bại",
                                Toast.LENGTH_SHORT
                        ).show();

                    }

                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    private void showEditGoalDialog(Goal goal) {

        DialogAddGoalBinding dialogBinding =
                DialogAddGoalBinding.inflate(getLayoutInflater());

        dialogBinding.editGoalTitle.setText(
                goal.getTitle()
        );

        dialogBinding.editTargetAmount.setText(
                String.valueOf(goal.getTargetAmount())
        );

        dialogBinding.editCurrentAmount.setText(
                String.valueOf(goal.getCurrentAmount())
        );

        dialogBinding.editDeadline.setText(
                goal.getDeadline()
        );

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Sửa mục tiêu")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", null)
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

                    String title =
                            dialogBinding.editGoalTitle.getText()
                                    .toString()
                                    .trim();

                    String targetText =
                            dialogBinding.editTargetAmount.getText()
                                    .toString()
                                    .trim();

                    String currentText =
                            dialogBinding.editCurrentAmount.getText()
                                    .toString()
                                    .trim();

                    String deadline =
                            dialogBinding.editDeadline.getText()
                                    .toString()
                                    .trim();

                    if (title.isEmpty()) {
                        dialogBinding.layoutGoalTitle.setError(
                                "Vui lòng nhập tên mục tiêu"
                        );
                        return;
                    }

                    if (targetText.isEmpty()) {
                        dialogBinding.layoutTargetAmount.setError(
                                "Vui lòng nhập số tiền mục tiêu"
                        );
                        return;
                    }

                    if (currentText.isEmpty()) {
                        currentText = "0";
                    }

                    double targetAmount;
                    double currentAmount;

                    try {

                        targetAmount =
                                Double.parseDouble(targetText);

                        currentAmount =
                                Double.parseDouble(currentText);

                    } catch (NumberFormatException e) {

                        Toast.makeText(
                                this,
                                "Số tiền không hợp lệ",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    goal.setTitle(title);
                    goal.setTargetAmount(targetAmount);
                    goal.setCurrentAmount(currentAmount);
                    goal.setDeadline(deadline);

                    boolean success =
                            databaseHelper.updateGoal(goal);

                    if (success) {

                        Toast.makeText(
                                this,
                                "Cập nhật thành công",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadGoal();

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
    private void showAddGoalAmountDialog(Goal goal) {

        DialogAddGoalAmountBinding dialogBinding =
                DialogAddGoalAmountBinding.inflate(
                        getLayoutInflater()
                );

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Thêm tiền vào mục tiêu")
                        .setMessage(
                                "Mục tiêu: " + goal.getTitle()
                        )
                        .setView(dialogBinding.getRoot())
                        .setPositiveButton("Thêm", null)
                        .setNegativeButton("Hủy", null)
                        .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {

                    String amountText =
                            dialogBinding.editGoalAmount
                                    .getText() == null
                                    ? ""
                                    : dialogBinding.editGoalAmount
                                    .getText()
                                    .toString()
                                    .trim();

                    dialogBinding.layoutGoalAmount.setError(null);

                    if (amountText.isEmpty()) {
                        dialogBinding.layoutGoalAmount.setError(
                                "Vui lòng nhập số tiền"
                        );
                        return;
                    }

                    double amount;

                    try {
                        amount = Double.parseDouble(amountText);

                    } catch (NumberFormatException e) {
                        dialogBinding.layoutGoalAmount.setError(
                                "Số tiền không hợp lệ"
                        );
                        return;
                    }

                    if (amount <= 0) {
                        dialogBinding.layoutGoalAmount.setError(
                                "Số tiền phải lớn hơn 0"
                        );
                        return;
                    }

                    double remainingAmount =
                            goal.getTargetAmount()
                                    - goal.getCurrentAmount();

                    if (remainingAmount <= 0) {
                        Toast.makeText(
                                this,
                                "Mục tiêu này đã hoàn thành",
                                Toast.LENGTH_SHORT
                        ).show();

                        dialog.dismiss();
                        return;
                    }

                    if (amount > remainingAmount) {
                        dialogBinding.layoutGoalAmount.setError(
                                String.format(
                                        Locale.getDefault(),
                                        "Chỉ còn thiếu %,.0f VNĐ",
                                        remainingAmount
                                )
                        );
                        return;
                    }

                    boolean success =
                            databaseHelper.addGoalAmount(
                                    goal.getId(),
                                    amount
                            );

                    if (success) {

                        Toast.makeText(
                                this,
                                "Đã thêm tiền vào mục tiêu",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadGoal();
                        dialog.dismiss();

                    } else {

                        Toast.makeText(
                                this,
                                "Thêm tiền thất bại",
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