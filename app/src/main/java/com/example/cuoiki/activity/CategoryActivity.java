package com.example.cuoiki.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cuoiki.adapter.CategoryAdapter;
import com.example.cuoiki.database.DatabaseHelper;
import com.example.cuoiki.databinding.ActivityCategoryBinding;
import com.example.cuoiki.databinding.DialogAddCategoryBinding;
import com.example.cuoiki.model.Category;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    private ActivityCategoryBinding binding;
    private DatabaseHelper databaseHelper;
    private ArrayList<Category> categoryList;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        loadCategory();

        binding.toolbar.setNavigationIcon(
                androidx.appcompat.R.drawable.abc_ic_ab_back_material
        );

        binding.toolbar.setNavigationOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed()
        );

        binding.fabAddCategory.setOnClickListener(v ->
                showAddCategoryDialog()
        );
    }
    private void showEditCategoryDialog(Category category) {

        DialogAddCategoryBinding dialogBinding =
                DialogAddCategoryBinding.inflate(getLayoutInflater());

        dialogBinding.editCategoryName.setText(category.getName());

        if (DatabaseHelper.TYPE_INCOME.equals(category.getType())) {
            dialogBinding.radioIncome.setChecked(true);
        } else {
            dialogBinding.radioExpense.setChecked(true);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Sửa danh mục")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", (d, which) -> d.dismiss())
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {

                    String name =
                            dialogBinding.editCategoryName.getText() == null
                                    ? ""
                                    : dialogBinding.editCategoryName
                                    .getText()
                                    .toString()
                                    .trim();

                    String type;

                    if (dialogBinding.radioIncome.isChecked()) {
                        type = DatabaseHelper.TYPE_INCOME;
                    } else {
                        type = DatabaseHelper.TYPE_EXPENSE;
                    }

                    if (name.isEmpty()) {
                        dialogBinding.layoutCategoryName.setError(
                                "Vui lòng nhập tên danh mục"
                        );
                        return;
                    }

                    dialogBinding.layoutCategoryName.setError(null);

                    boolean unchanged =
                            name.equalsIgnoreCase(category.getName())
                                    && type.equals(category.getType());

                    if (!unchanged
                            && databaseHelper.isCategoryExists(name, type)) {

                        Toast.makeText(
                                this,
                                "Danh mục đã tồn tại",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    category.setName(name);
                    category.setType(type);

                    boolean success =
                            databaseHelper.updateCategory(category);

                    if (success) {
                        Toast.makeText(
                                this,
                                "Cập nhật thành công",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadCategory();
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
    private void loadCategory() {

        categoryList = databaseHelper.getAllCategories();

        categoryAdapter = new CategoryAdapter(
                categoryList,
                new CategoryAdapter.OnCategoryActionListener() {

                    @Override
                    public void onEdit(Category category) {

                        showEditCategoryDialog(category);

                    }

                    @Override
                    public void onDelete(Category category) {
                        showDeleteCategoryDialog(category);
                    }
                }
        );

        binding.rvCategory.setLayoutManager(
                new LinearLayoutManager(this)
        );

        binding.rvCategory.setAdapter(categoryAdapter);
    }

    private void showAddCategoryDialog() {

        DialogAddCategoryBinding dialogBinding =
                DialogAddCategoryBinding.inflate(getLayoutInflater());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Thêm danh mục")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", (d, which) -> d.dismiss())
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {

                    String name =
                            dialogBinding.editCategoryName.getText() == null
                                    ? ""
                                    : dialogBinding.editCategoryName
                                    .getText()
                                    .toString()
                                    .trim();

                    String type;

                    if (dialogBinding.radioIncome.isChecked()) {
                        type = DatabaseHelper.TYPE_INCOME;
                    } else {
                        type = DatabaseHelper.TYPE_EXPENSE;
                    }

                    if (name.isEmpty()) {
                        dialogBinding.layoutCategoryName.setError(
                                "Vui lòng nhập tên danh mục"
                        );
                        return;
                    }

                    dialogBinding.layoutCategoryName.setError(null);

                    if (databaseHelper.isCategoryExists(name, type)) {
                        Toast.makeText(
                                this,
                                "Danh mục đã tồn tại",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    Category category = new Category(
                            name,
                            type
                    );

                    boolean success =
                            databaseHelper.insertCategory(category);

                    if (success) {
                        Toast.makeText(
                                this,
                                "Thêm thành công",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadCategory();
                        dialog.dismiss();

                    } else {
                        Toast.makeText(
                                this,
                                "Thêm thất bại",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void showDeleteCategoryDialog(Category category) {

        new AlertDialog.Builder(this)
                .setTitle("Xóa danh mục")
                .setMessage(
                        "Bạn có chắc muốn xóa \"" +
                                category.getName() +
                                "\"?"
                )
                .setPositiveButton("Xóa", (dialog, which) -> {

                    boolean success =
                            databaseHelper.deleteCategory(category.getId());

                    if (success) {
                        Toast.makeText(
                                this,
                                "Đã xóa",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadCategory();

                    } else {
                        Toast.makeText(
                                this,
                                "Xóa thất bại",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        binding = null;
        super.onDestroy();
    }
}