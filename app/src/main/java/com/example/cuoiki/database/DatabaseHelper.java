package com.example.cuoiki.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.cuoiki.model.Account;
import com.example.cuoiki.model.Budget;
import com.example.cuoiki.model.Category;
import com.example.cuoiki.model.Debt;
import com.example.cuoiki.model.Goal;
import com.example.cuoiki.model.Transaction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    // =====================================================
    // DATABASE
    // =====================================================

    private static final String DATABASE_NAME = "BitMoney.db";
    private static final int DATABASE_VERSION = 8;

    // =====================================================
    // TABLES
    // =====================================================

    public static final String TABLE_ACCOUNT = "Account";
    public static final String TABLE_CATEGORY = "Category";
    public static final String TABLE_TRANSACTION = "Transactions";
    public static final String TABLE_GOAL = "Goal";
    public static final String TABLE_DEBT = "Debt";
    public static final String TABLE_BUDGET = "Budget";

    // =====================================================
    // CONSTANTS
    // =====================================================

    public static final String TYPE_INCOME = "Thu nhập";
    public static final String TYPE_EXPENSE = "Chi tiêu";

    // Hỗ trợ dữ liệu cũ đã lưu trước khi chuyển sang tiếng Việt.
    private static final String LEGACY_TYPE_INCOME = "Income";
    private static final String LEGACY_TYPE_EXPENSE = "Expense";

    public static final String DEBT_BORROW = "Mượn";
    public static final String DEBT_LEND = "Cho vay";

    public static final String STATUS_PENDING = "Chưa thanh toán";
    public static final String STATUS_PAID = "Đã thanh toán";
    // =====================================================
    // ACCOUNT COLUMNS
    // =====================================================

    public static final String COL_ACCOUNT_ID = "id";
    public static final String COL_ACCOUNT_USERNAME = "username";
    public static final String COL_ACCOUNT_PASSWORD = "password";
    public static final String COL_ACCOUNT_FULLNAME = "fullName";
    public static final String COL_ACCOUNT_EMAIL = "email";

    // =====================================================
    // CATEGORY COLUMNS
    // =====================================================

    public static final String COL_CATEGORY_ID = "id";
    public static final String COL_CATEGORY_NAME = "name";
    public static final String COL_CATEGORY_TYPE = "type";

    // =====================================================
    // TRANSACTION COLUMNS
    // =====================================================

    public static final String COL_TRANSACTION_ID = "id";
    public static final String COL_TRANSACTION_TITLE = "title";
    public static final String COL_TRANSACTION_AMOUNT = "amount";
    public static final String COL_TRANSACTION_DATE = "date";
    public static final String COL_TRANSACTION_NOTE = "note";
    public static final String COL_TRANSACTION_TYPE = "type";
    public static final String COL_TRANSACTION_CATEGORY_ID = "categoryId";
    public static final String COL_TRANSACTION_ACCOUNT_ID = "accountId";
    public static final String COL_TRANSACTION_CREATED_AT = "createdAt";

    // =====================================================
    // GOAL COLUMNS
    // =====================================================

    public static final String COL_GOAL_ID = "id";
    public static final String COL_GOAL_TITLE = "title";
    public static final String COL_GOAL_TARGET_AMOUNT = "targetAmount";
    public static final String COL_GOAL_CURRENT_AMOUNT = "currentAmount";
    public static final String COL_GOAL_DEADLINE = "deadline";
    public static final String COL_GOAL_ACCOUNT_ID = "accountId";

    // =====================================================
    // DEBT COLUMNS
    // =====================================================

    public static final String COL_DEBT_ID = "id";
    public static final String COL_DEBT_PERSON = "person";
    public static final String COL_DEBT_AMOUNT = "amount";
    public static final String COL_DEBT_DEADLINE = "deadline";
    public static final String COL_DEBT_STATUS = "status";
    public static final String COL_DEBT_TYPE = "type";
    public static final String COL_DEBT_ACCOUNT_ID = "accountId";

    // =====================================================
    // BUDGET COLUMNS
    // =====================================================

    public static final String COL_BUDGET_ID = "id";
    public static final String COL_BUDGET_MONTH = "month";
    public static final String COL_BUDGET_YEAR = "year";
    public static final String COL_BUDGET_AMOUNT = "amount";
    public static final String COL_BUDGET_CATEGORY_ID = "categoryId";
    public static final String COL_BUDGET_ACCOUNT_ID = "accountId";
    public static final String COL_BUDGET_WARNING_PERCENT = "warningPercent";

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // =====================================================
    // CREATE TABLES
    // =====================================================

    private void createAccountTable(SQLiteDatabase db) {

        String sql =
                "CREATE TABLE IF NOT EXISTS " + TABLE_ACCOUNT + " (" +
                        COL_ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_ACCOUNT_USERNAME + " TEXT UNIQUE NOT NULL, " +
                        COL_ACCOUNT_PASSWORD + " TEXT NOT NULL, " +
                        COL_ACCOUNT_FULLNAME + " TEXT NOT NULL, " +
                        COL_ACCOUNT_EMAIL + " TEXT UNIQUE NOT NULL" +
                        ");";

        db.execSQL(sql);
    }

    private void createCategoryTable(SQLiteDatabase db) {

        String sql =
                "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORY + " (" +
                        COL_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_CATEGORY_NAME + " TEXT NOT NULL, " +
                        COL_CATEGORY_TYPE + " TEXT NOT NULL, " +
                        "UNIQUE(" +
                        COL_CATEGORY_NAME + ", " +
                        COL_CATEGORY_TYPE +
                        ")" +
                        ");";

        db.execSQL(sql);
    }

    private void createTransactionTable(SQLiteDatabase db) {

        String sql =
                "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSACTION + " (" +
                        COL_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_TRANSACTION_TITLE + " TEXT NOT NULL, " +
                        COL_TRANSACTION_AMOUNT + " REAL NOT NULL, " +
                        COL_TRANSACTION_DATE + " TEXT NOT NULL, " +
                        COL_TRANSACTION_NOTE + " TEXT, " +
                        COL_TRANSACTION_TYPE + " TEXT NOT NULL, " +
                        COL_TRANSACTION_CATEGORY_ID + " INTEGER NOT NULL, " +
                        COL_TRANSACTION_ACCOUNT_ID + " INTEGER NOT NULL, " +
                        COL_TRANSACTION_CREATED_AT + " TEXT NOT NULL" +
                        ");";

        db.execSQL(sql);
    }

    private void createGoalTable(SQLiteDatabase db) {

        String sql =
                "CREATE TABLE IF NOT EXISTS " + TABLE_GOAL + " (" +
                        COL_GOAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_GOAL_TITLE + " TEXT NOT NULL, " +
                        COL_GOAL_TARGET_AMOUNT + " REAL NOT NULL, " +
                        COL_GOAL_CURRENT_AMOUNT + " REAL NOT NULL, " +
                        COL_GOAL_DEADLINE + " TEXT NOT NULL, " +
                        COL_GOAL_ACCOUNT_ID + " INTEGER NOT NULL" +
                        ");";

        db.execSQL(sql);
    }

    private void createBudgetTable(SQLiteDatabase db) {

        String sql =
                "CREATE TABLE IF NOT EXISTS " + TABLE_BUDGET + " (" +
                        COL_BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_BUDGET_MONTH + " INTEGER NOT NULL, " +
                        COL_BUDGET_YEAR + " INTEGER NOT NULL, " +
                        COL_BUDGET_AMOUNT + " REAL NOT NULL, " +
                        COL_BUDGET_CATEGORY_ID + " INTEGER NOT NULL, " +
                        COL_BUDGET_ACCOUNT_ID + " INTEGER NOT NULL, " +
                        COL_BUDGET_WARNING_PERCENT + " INTEGER NOT NULL, " +
                        "UNIQUE(" +
                        COL_BUDGET_MONTH + ", " +
                        COL_BUDGET_YEAR + ", " +
                        COL_BUDGET_CATEGORY_ID + ", " +
                        COL_BUDGET_ACCOUNT_ID +
                        ")" +
                        ");";

        db.execSQL(sql);
    }
    private void createDebtTable(SQLiteDatabase db) {

        String sql =
                "CREATE TABLE IF NOT EXISTS " + TABLE_DEBT + " (" +
                        COL_DEBT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_DEBT_PERSON + " TEXT NOT NULL, " +
                        COL_DEBT_AMOUNT + " REAL NOT NULL, " +
                        COL_DEBT_DEADLINE + " TEXT NOT NULL, " +
                        COL_DEBT_STATUS + " TEXT NOT NULL, " +
                        COL_DEBT_TYPE + " TEXT NOT NULL, " +
                        COL_DEBT_ACCOUNT_ID + " INTEGER NOT NULL" +
                        ");";

        db.execSQL(sql);
    }

    private void createIndexes(SQLiteDatabase db) {

        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_transaction_account_type_date " +
                        "ON " + TABLE_TRANSACTION + " (" +
                        COL_TRANSACTION_ACCOUNT_ID + ", " +
                        COL_TRANSACTION_TYPE + ", " +
                        COL_TRANSACTION_DATE +
                        ")"
        );

        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_transaction_account_category_date " +
                        "ON " + TABLE_TRANSACTION + " (" +
                        COL_TRANSACTION_ACCOUNT_ID + ", " +
                        COL_TRANSACTION_CATEGORY_ID + ", " +
                        COL_TRANSACTION_DATE +
                        ")"
        );

        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_category_type " +
                        "ON " + TABLE_CATEGORY + " (" +
                        COL_CATEGORY_TYPE +
                        ")"
        );

        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_budget_account_period " +
                        "ON " + TABLE_BUDGET + " (" +
                        COL_BUDGET_ACCOUNT_ID + ", " +
                        COL_BUDGET_YEAR + ", " +
                        COL_BUDGET_MONTH +
                        ")"
        );

        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_goal_account_deadline " +
                        "ON " + TABLE_GOAL + " (" +
                        COL_GOAL_ACCOUNT_ID + ", " +
                        COL_GOAL_DEADLINE +
                        ")"
        );

        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_debt_account_deadline " +
                        "ON " + TABLE_DEBT + " (" +
                        COL_DEBT_ACCOUNT_ID + ", " +
                        COL_DEBT_DEADLINE +
                        ")"
        );
    }

    // =====================================================
    // DATABASE LIFECYCLE
    // =====================================================

    @Override
    public void onCreate(SQLiteDatabase db) {
        createAccountTable(db);
        createCategoryTable(db);
        createTransactionTable(db);
        createGoalTable(db);
        createBudgetTable(db);
        createDebtTable(db);
        createIndexes(db);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {

        if (oldVersion < 2) {
            createCategoryTable(db);
        }

        if (oldVersion < 3) {
            createTransactionTable(db);
        }

        if (oldVersion < 5) {
            createGoalTable(db);
        }

        if (oldVersion < 6) {
            createBudgetTable(db);
        }
        if (oldVersion < 7) {
            createDebtTable(db);
        }

        if (oldVersion < 8) {
            createIndexes(db);
        }
    }

    // =====================================================
    // ACCOUNT
    // =====================================================

    public boolean insertAccount(Account account) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_ACCOUNT_USERNAME, account.getUsername());
        values.put(COL_ACCOUNT_PASSWORD, account.getPassword());
        values.put(COL_ACCOUNT_FULLNAME, account.getFullName());
        values.put(COL_ACCOUNT_EMAIL, account.getEmail());

        long result = db.insert(
                TABLE_ACCOUNT,
                null,
                values
        );

        return result != -1;
    }

    public boolean isUsernameExists(String username) {

        SQLiteDatabase db = getReadableDatabase();

        String query =
                "SELECT " + COL_ACCOUNT_ID +
                        " FROM " + TABLE_ACCOUNT +
                        " WHERE " + COL_ACCOUNT_USERNAME + " = ?";

        Cursor cursor = db.rawQuery(
                query,
                new String[]{username}
        );

        boolean exists = cursor.moveToFirst();
        cursor.close();

        return exists;
    }

    public boolean isEmailExists(String email) {

        SQLiteDatabase db = getReadableDatabase();

        String query =
                "SELECT " + COL_ACCOUNT_ID +
                        " FROM " + TABLE_ACCOUNT +
                        " WHERE " + COL_ACCOUNT_EMAIL + " = ?";

        Cursor cursor = db.rawQuery(
                query,
                new String[]{email}
        );

        boolean exists = cursor.moveToFirst();
        cursor.close();

        return exists;
    }

    public boolean checkLogin(
            String username,
            String password
    ) {

        SQLiteDatabase db = getReadableDatabase();

        String query =
                "SELECT " + COL_ACCOUNT_ID +
                        " FROM " + TABLE_ACCOUNT +
                        " WHERE " + COL_ACCOUNT_USERNAME + " = ?" +
                        " AND " + COL_ACCOUNT_PASSWORD + " = ?";

        Cursor cursor = db.rawQuery(
                query,
                new String[]{
                        username,
                        password
                }
        );

        boolean success = cursor.moveToFirst();
        cursor.close();

        return success;
    }

    public Account getAccountByUsername(String username) {

        if (username == null) {
            return null;
        }

        SQLiteDatabase db = getReadableDatabase();

        String query =
                "SELECT * FROM " + TABLE_ACCOUNT +
                        " WHERE " + COL_ACCOUNT_USERNAME + " = ?";

        Cursor cursor = db.rawQuery(
                query,
                new String[]{username}
        );

        Account account = null;

        if (cursor.moveToFirst()) {
            account = new Account(
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(COL_ACCOUNT_ID)
                    ),
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(COL_ACCOUNT_USERNAME)
                    ),
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(COL_ACCOUNT_PASSWORD)
                    ),
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(COL_ACCOUNT_FULLNAME)
                    ),
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(COL_ACCOUNT_EMAIL)
                    )
            );
        }

        cursor.close();

        return account;
    }

    // =====================================================
    // CATEGORY
    // =====================================================

    public boolean insertCategory(Category category) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_CATEGORY_NAME, category.getName());
        values.put(COL_CATEGORY_TYPE, category.getType());

        long result = db.insert(
                TABLE_CATEGORY,
                null,
                values
        );

        return result != -1;
    }

    public boolean isCategoryExists(
            String name,
            String type
    ) {

        SQLiteDatabase db = getReadableDatabase();

        String query =
                "SELECT " + COL_CATEGORY_ID +
                        " FROM " + TABLE_CATEGORY +
                        " WHERE " + COL_CATEGORY_NAME + " = ?" +
                        " AND " + COL_CATEGORY_TYPE + " = ?";

        Cursor cursor = db.rawQuery(
                query,
                new String[]{
                        name,
                        type
                }
        );

        boolean exists = cursor.moveToFirst();
        cursor.close();

        return exists;
    }

    public ArrayList<Category> getAllCategories() {

        ArrayList<Category> categoryList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        String query =
                "SELECT * FROM " + TABLE_CATEGORY +
                        " ORDER BY " +
                        COL_CATEGORY_TYPE + " ASC, " +
                        COL_CATEGORY_NAME + " ASC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(COL_CATEGORY_ID)
                        ),
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(COL_CATEGORY_NAME)
                        ),
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(COL_CATEGORY_TYPE)
                        )
                );

                categoryList.add(category);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return categoryList;
    }

    public ArrayList<Category> getCategoriesByType(String type) {

        ArrayList<Category> categoryList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        String query =
                "SELECT * FROM " + TABLE_CATEGORY +
                        " WHERE " + COL_CATEGORY_TYPE + " = ?" +
                        " ORDER BY " + COL_CATEGORY_NAME + " ASC";

        Cursor cursor = db.rawQuery(
                query,
                new String[]{type}
        );

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(COL_CATEGORY_ID)
                        ),
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(COL_CATEGORY_NAME)
                        ),
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(COL_CATEGORY_TYPE)
                        )
                );

                categoryList.add(category);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return categoryList;
    }

    public String getCategoryNameById(int categoryId) {

        SQLiteDatabase db = getReadableDatabase();

        String query =
                "SELECT " + COL_CATEGORY_NAME +
                        " FROM " + TABLE_CATEGORY +
                        " WHERE " + COL_CATEGORY_ID + " = ?";

        Cursor cursor = db.rawQuery(
                query,
                new String[]{
                        String.valueOf(categoryId)
                }
        );

        String name = "";

        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }

        cursor.close();

        return name;
    }

    public boolean updateCategory(Category category) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_CATEGORY_NAME, category.getName());
        values.put(COL_CATEGORY_TYPE, category.getType());

        int result = db.update(
                TABLE_CATEGORY,
                values,
                COL_CATEGORY_ID + " = ?",
                new String[]{
                        String.valueOf(category.getId())
                }
        );

        return result > 0;
    }

    public boolean deleteCategory(int categoryId) {

        SQLiteDatabase db = getWritableDatabase();

        int result = db.delete(
                TABLE_CATEGORY,
                COL_CATEGORY_ID + " = ?",
                new String[]{
                        String.valueOf(categoryId)
                }
        );

        return result > 0;
    }

    // =====================================================
    // TRANSACTION
    // =====================================================

    public boolean insertTransaction(Transaction transaction) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(
                COL_TRANSACTION_TITLE,
                transaction.getTitle()
        );

        values.put(
                COL_TRANSACTION_AMOUNT,
                transaction.getAmount()
        );

        values.put(
                COL_TRANSACTION_DATE,
                transaction.getDate()
        );

        values.put(
                COL_TRANSACTION_NOTE,
                transaction.getNote()
        );

        values.put(
                COL_TRANSACTION_TYPE,
                transaction.getType()
        );

        values.put(
                COL_TRANSACTION_CATEGORY_ID,
                transaction.getCategoryId()
        );

        values.put(
                COL_TRANSACTION_ACCOUNT_ID,
                transaction.getAccountId()
        );

        values.put(
                COL_TRANSACTION_CREATED_AT,
                transaction.getCreatedAt()
        );

        long result = db.insert(
                TABLE_TRANSACTION,
                null,
                values
        );

        return result != -1;
    }
    public ArrayList<Transaction> getTransactionsByAccountId(
            int accountId
    ) {

        ArrayList<Transaction> transactionList =
                new ArrayList<>();

        if (accountId <= 0) {
            return transactionList;
        }

        SQLiteDatabase db =
                getReadableDatabase();

        String query =
                "SELECT * FROM "
                        + TABLE_TRANSACTION
                        + " WHERE "
                        + COL_TRANSACTION_ACCOUNT_ID
                        + " = ?"
                        + " ORDER BY "
                        + COL_TRANSACTION_DATE
                        + " DESC, "
                        + COL_TRANSACTION_CREATED_AT
                        + " DESC";

        try (Cursor cursor = db.rawQuery(
                query,
                new String[]{
                        String.valueOf(accountId)
                }
        )) {

            int idIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_TRANSACTION_ID
                    );

            int titleIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_TRANSACTION_TITLE
                    );

            int amountIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_TRANSACTION_AMOUNT
                    );

            int dateIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_TRANSACTION_DATE
                    );

            int noteIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_TRANSACTION_NOTE
                    );

            int typeIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_TRANSACTION_TYPE
                    );

            int categoryIdIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_TRANSACTION_CATEGORY_ID
                    );

            int accountIdIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_TRANSACTION_ACCOUNT_ID
                    );

            int createdAtIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_TRANSACTION_CREATED_AT
                    );

            while (cursor.moveToNext()) {

                Transaction transaction =
                        new Transaction(
                                cursor.getInt(idIndex),
                                cursor.getString(titleIndex),
                                cursor.getDouble(amountIndex),
                                cursor.getString(dateIndex),
                                cursor.getString(noteIndex),
                                cursor.getString(typeIndex),
                                cursor.getInt(categoryIdIndex),
                                cursor.getInt(accountIdIndex),
                                cursor.getString(createdAtIndex)
                        );

                transactionList.add(transaction);
            }
        }

        return transactionList;
    }


    public ArrayList<Transaction> getRecentTransactions(
            int accountId,
            int limit
    ) {

        ArrayList<Transaction> transactionList =
                new ArrayList<>();

        if (accountId <= 0 || limit <= 0) {
            return transactionList;
        }

        SQLiteDatabase db =
                getReadableDatabase();

        String query =
                "SELECT * FROM "
                        + TABLE_TRANSACTION
                        + " WHERE "
                        + COL_TRANSACTION_ACCOUNT_ID
                        + " = ?"
                        + " ORDER BY "
                        + COL_TRANSACTION_DATE
                        + " DESC, "
                        + COL_TRANSACTION_CREATED_AT
                        + " DESC"
                        + " LIMIT ?";

        try (Cursor cursor = db.rawQuery(
                query,
                new String[]{
                        String.valueOf(accountId),
                        String.valueOf(limit)
                }
        )) {

            int idIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_TRANSACTION_ID
                    );

            int titleIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_TRANSACTION_TITLE
                    );

            int amountIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_TRANSACTION_AMOUNT
                    );

            int dateIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_TRANSACTION_DATE
                    );

            int noteIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_TRANSACTION_NOTE
                    );

            int typeIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_TRANSACTION_TYPE
                    );

            int categoryIdIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_TRANSACTION_CATEGORY_ID
                    );

            int accountIdIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_TRANSACTION_ACCOUNT_ID
                    );

            int createdAtIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_TRANSACTION_CREATED_AT
                    );

            while (cursor.moveToNext()) {

                Transaction transaction =
                        new Transaction(
                                cursor.getInt(idIndex),
                                cursor.getString(titleIndex),
                                cursor.getDouble(amountIndex),
                                cursor.getString(dateIndex),
                                cursor.getString(noteIndex),
                                cursor.getString(typeIndex),
                                cursor.getInt(categoryIdIndex),
                                cursor.getInt(accountIdIndex),
                                cursor.getString(createdAtIndex)
                        );

                transactionList.add(transaction);
            }
        }

        return transactionList;
    }

    public double getTotalIncome(int accountId) {
        return getTotalTransactionByType(
                accountId,
                TYPE_INCOME
        );
    }

    public double getTotalExpense(int accountId) {
        return getTotalTransactionByType(
                accountId,
                TYPE_EXPENSE
        );
    }

    private double getTotalTransactionByType(
            int accountId,
            String type
    ) {

        SQLiteDatabase db = getReadableDatabase();

        String query =
                "SELECT COALESCE(SUM(" +
                        COL_TRANSACTION_AMOUNT +
                        "), 0) FROM " + TABLE_TRANSACTION +
                        " WHERE " +
                        COL_TRANSACTION_ACCOUNT_ID + " = ?" +
                        " AND " +
                        COL_TRANSACTION_TYPE + " IN (?, ?)";

        try (Cursor cursor = db.rawQuery(
                query,
                new String[]{
                        String.valueOf(accountId),
                        type,
                        getLegacyTransactionType(type)
                }
        )) {

            return cursor.moveToFirst()
                    ? cursor.getDouble(0)
                    : 0;
        }
    }

    public double getBalance(int accountId) {
        return getTotalIncome(accountId)
                - getTotalExpense(accountId);
    }

    public double getExpenseByCategory(
            int accountId,
            int categoryId,
            int month,
            int year
    ) {

        SQLiteDatabase db = getReadableDatabase();

        String query =
                "SELECT COALESCE(SUM(" +
                        COL_TRANSACTION_AMOUNT +
                        "), 0) FROM " + TABLE_TRANSACTION +
                        " WHERE " +
                        COL_TRANSACTION_ACCOUNT_ID + " = ?" +
                        " AND " +
                        COL_TRANSACTION_CATEGORY_ID + " = ?" +
                        " AND " +
                        COL_TRANSACTION_TYPE + " IN (?, ?)" +
                        " AND substr(" +
                        COL_TRANSACTION_DATE +
                        ", 1, 7) = ?";

        try (Cursor cursor = db.rawQuery(
                query,
                new String[]{
                        String.valueOf(accountId),
                        String.valueOf(categoryId),
                        TYPE_EXPENSE,
                        LEGACY_TYPE_EXPENSE,
                        formatYearMonth(year, month)
                }
        )) {

            return cursor.moveToFirst()
                    ? cursor.getDouble(0)
                    : 0;
        }
    }

    public boolean updateTransaction(Transaction transaction) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(
                COL_TRANSACTION_TITLE,
                transaction.getTitle()
        );

        values.put(
                COL_TRANSACTION_AMOUNT,
                transaction.getAmount()
        );

        values.put(
                COL_TRANSACTION_DATE,
                transaction.getDate()
        );

        values.put(
                COL_TRANSACTION_NOTE,
                transaction.getNote()
        );

        values.put(
                COL_TRANSACTION_TYPE,
                transaction.getType()
        );

        values.put(
                COL_TRANSACTION_CATEGORY_ID,
                transaction.getCategoryId()
        );

        values.put(
                COL_TRANSACTION_ACCOUNT_ID,
                transaction.getAccountId()
        );

        values.put(
                COL_TRANSACTION_CREATED_AT,
                transaction.getCreatedAt()
        );

        int result = db.update(
                TABLE_TRANSACTION,
                values,
                COL_TRANSACTION_ID + " = ?",
                new String[]{
                        String.valueOf(transaction.getId())
                }
        );

        return result > 0;
    }

    public boolean deleteTransaction(int transactionId) {

        SQLiteDatabase db = getWritableDatabase();

        int result = db.delete(
                TABLE_TRANSACTION,
                COL_TRANSACTION_ID + " = ?",
                new String[]{
                        String.valueOf(transactionId)
                }
        );

        return result > 0;
    }

    // =====================================================
    // GOAL
    // =====================================================

    public boolean insertGoal(Goal goal) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(
                COL_GOAL_TITLE,
                goal.getTitle()
        );

        values.put(
                COL_GOAL_TARGET_AMOUNT,
                goal.getTargetAmount()
        );

        values.put(
                COL_GOAL_CURRENT_AMOUNT,
                goal.getCurrentAmount()
        );

        values.put(
                COL_GOAL_DEADLINE,
                goal.getDeadline()
        );

        values.put(
                COL_GOAL_ACCOUNT_ID,
                goal.getAccountId()
        );

        long result = db.insert(
                TABLE_GOAL,
                null,
                values
        );

        return result != -1;
    }

    public ArrayList<Goal> getGoalsByAccountId(
            int accountId
    ) {

        ArrayList<Goal> goalList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        String query =
                "SELECT * FROM " + TABLE_GOAL +
                        " WHERE " +
                        COL_GOAL_ACCOUNT_ID + " = ?" +
                        " ORDER BY " +
                        COL_GOAL_DEADLINE + " ASC";

        Cursor cursor = db.rawQuery(
                query,
                new String[]{
                        String.valueOf(accountId)
                }
        );

        if (cursor.moveToFirst()) {
            do {
                Goal goal = new Goal(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(COL_GOAL_ID)
                        ),
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(COL_GOAL_TITLE)
                        ),
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        COL_GOAL_TARGET_AMOUNT
                                )
                        ),
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        COL_GOAL_CURRENT_AMOUNT
                                )
                        ),
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        COL_GOAL_DEADLINE
                                )
                        ),
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        COL_GOAL_ACCOUNT_ID
                                )
                        )
                );

                goalList.add(goal);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return goalList;
    }

    public boolean updateGoal(Goal goal) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(
                COL_GOAL_TITLE,
                goal.getTitle()
        );

        values.put(
                COL_GOAL_TARGET_AMOUNT,
                goal.getTargetAmount()
        );

        values.put(
                COL_GOAL_CURRENT_AMOUNT,
                goal.getCurrentAmount()
        );

        values.put(
                COL_GOAL_DEADLINE,
                goal.getDeadline()
        );

        int result = db.update(
                TABLE_GOAL,
                values,
                COL_GOAL_ID + " = ?",
                new String[]{
                        String.valueOf(goal.getId())
                }
        );

        return result > 0;
    }

    public boolean deleteGoal(int goalId) {

        SQLiteDatabase db = getWritableDatabase();

        int result = db.delete(
                TABLE_GOAL,
                COL_GOAL_ID + " = ?",
                new String[]{
                        String.valueOf(goalId)
                }
        );

        return result > 0;
    }
    public boolean addGoalAmount(
            int goalId,
            double amount
    ) {

        if (amount <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        String sql =
                "UPDATE " + TABLE_GOAL +
                        " SET " + COL_GOAL_CURRENT_AMOUNT +
                        " = " + COL_GOAL_CURRENT_AMOUNT + " + ?" +
                        " WHERE " + COL_GOAL_ID + " = ?";

        db.execSQL(
                sql,
                new Object[]{
                        amount,
                        goalId
                }
        );

        return true;
    }

    // =====================================================
    // BUDGET
    // =====================================================

    public boolean insertBudget(Budget budget) {

        if (
                budget == null
                        || budget.getAmount() <= 0
                        || budget.getMonth() < 1
                        || budget.getMonth() > 12
                        || budget.getWarningPercent() < 1
                        || budget.getWarningPercent() > 100
        ) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values =
                createBudgetContentValues(budget);

        try {

            long result = db.insertOrThrow(
                    TABLE_BUDGET,
                    null,
                    values
            );

            return result != -1;

        } catch (
                android.database.sqlite.SQLiteConstraintException exception
        ) {
            return false;
        }
    }

    private ContentValues createBudgetContentValues(
            Budget budget
    ) {

        ContentValues values =
                new ContentValues();

        values.put(
                COL_BUDGET_MONTH,
                budget.getMonth()
        );

        values.put(
                COL_BUDGET_YEAR,
                budget.getYear()
        );

        values.put(
                COL_BUDGET_AMOUNT,
                budget.getAmount()
        );

        values.put(
                COL_BUDGET_CATEGORY_ID,
                budget.getCategoryId()
        );

        values.put(
                COL_BUDGET_ACCOUNT_ID,
                budget.getAccountId()
        );

        values.put(
                COL_BUDGET_WARNING_PERCENT,
                budget.getWarningPercent()
        );

        return values;
    }

    public ArrayList<Budget> getBudgetsByAccountId(
            int accountId
    ) {

        ArrayList<Budget> budgetList =
                new ArrayList<>();

        SQLiteDatabase db =
                getReadableDatabase();

        /*
         * Một query duy nhất lấy:
         * - dữ liệu Budget
         * - tên Category
         * - tổng chi theo đúng category, month, year
         */
        String query =
                "SELECT " +
                        "b." + COL_BUDGET_ID + ", " +
                        "b." + COL_BUDGET_MONTH + ", " +
                        "b." + COL_BUDGET_YEAR + ", " +
                        "b." + COL_BUDGET_AMOUNT + ", " +
                        "b." + COL_BUDGET_CATEGORY_ID + ", " +
                        "b." + COL_BUDGET_ACCOUNT_ID + ", " +
                        "b." + COL_BUDGET_WARNING_PERCENT + ", " +
                        "c." + COL_CATEGORY_NAME +
                        " AS categoryName, " +
                        "COALESCE(SUM(t." +
                        COL_TRANSACTION_AMOUNT +
                        "), 0) AS spentAmount " +

                        "FROM " + TABLE_BUDGET + " b " +

                        "LEFT JOIN " + TABLE_CATEGORY + " c " +
                        "ON c." + COL_CATEGORY_ID +
                        " = b." + COL_BUDGET_CATEGORY_ID + " " +

                        "LEFT JOIN " + TABLE_TRANSACTION + " t " +
                        "ON t." + COL_TRANSACTION_ACCOUNT_ID +
                        " = b." + COL_BUDGET_ACCOUNT_ID +
                        " AND t." + COL_TRANSACTION_CATEGORY_ID +
                        " = b." + COL_BUDGET_CATEGORY_ID +
                        " AND t." + COL_TRANSACTION_TYPE +
                        " IN (?, ?) " +
                        " AND substr(t." +
                        COL_TRANSACTION_DATE +
                        ", 1, 7) = printf('%04d-%02d', " +
                        "b." + COL_BUDGET_YEAR + ", " +
                        "b." + COL_BUDGET_MONTH +
                        ") " +

                        "WHERE b." +
                        COL_BUDGET_ACCOUNT_ID +
                        " = ? " +

                        "GROUP BY " +
                        "b." + COL_BUDGET_ID + ", " +
                        "b." + COL_BUDGET_MONTH + ", " +
                        "b." + COL_BUDGET_YEAR + ", " +
                        "b." + COL_BUDGET_AMOUNT + ", " +
                        "b." + COL_BUDGET_CATEGORY_ID + ", " +
                        "b." + COL_BUDGET_ACCOUNT_ID + ", " +
                        "b." + COL_BUDGET_WARNING_PERCENT + ", " +
                        "c." + COL_CATEGORY_NAME + " " +

                        "ORDER BY " +
                        "b." + COL_BUDGET_YEAR + " DESC, " +
                        "b." + COL_BUDGET_MONTH + " DESC, " +
                        "c." + COL_CATEGORY_NAME + " ASC";

        try (Cursor cursor = db.rawQuery(
                query,
                new String[]{
                        TYPE_EXPENSE,
                        LEGACY_TYPE_EXPENSE,
                        String.valueOf(accountId)
                }
        )) {

            int idIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_BUDGET_ID
                    );

            int monthIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_BUDGET_MONTH
                    );

            int yearIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_BUDGET_YEAR
                    );

            int amountIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_BUDGET_AMOUNT
                    );

            int categoryIdIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_BUDGET_CATEGORY_ID
                    );

            int accountIdIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_BUDGET_ACCOUNT_ID
                    );

            int warningIndex =
                    cursor.getColumnIndexOrThrow(
                            COL_BUDGET_WARNING_PERCENT
                    );

            int categoryNameIndex =
                    cursor.getColumnIndexOrThrow(
                            "categoryName"
                    );

            int spentAmountIndex =
                    cursor.getColumnIndexOrThrow(
                            "spentAmount"
                    );

            while (cursor.moveToNext()) {

                Budget budget =
                        new Budget(
                                cursor.getInt(idIndex),
                                cursor.getInt(monthIndex),
                                cursor.getInt(yearIndex),
                                cursor.getDouble(amountIndex),
                                cursor.getInt(categoryIdIndex),
                                cursor.getInt(accountIdIndex),
                                cursor.getInt(warningIndex)
                        );

                budget.setCategoryName(
                        cursor.isNull(categoryNameIndex)
                                ? "Danh mục không xác định"
                                : cursor.getString(categoryNameIndex)
                );

                budget.setSpentAmount(
                        cursor.getDouble(spentAmountIndex)
                );

                budgetList.add(budget);
            }
        }

        return budgetList;
    }

    public boolean updateBudget(Budget budget) {

        if (
                budget == null
                        || budget.getId() <= 0
                        || budget.getAmount() <= 0
                        || budget.getMonth() < 1
                        || budget.getMonth() > 12
                        || budget.getWarningPercent() < 1
                        || budget.getWarningPercent() > 100
        ) {
            return false;
        }

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                createBudgetContentValues(budget);

        try {

            int result = db.update(
                    TABLE_BUDGET,
                    values,
                    COL_BUDGET_ID + " = ?",
                    new String[]{
                            String.valueOf(
                                    budget.getId()
                            )
                    }
            );

            return result > 0;

        } catch (
                android.database.sqlite.SQLiteConstraintException exception
        ) {
            return false;
        }
    }

    public boolean deleteBudget(
            int budgetId
    ) {

        if (budgetId <= 0) {
            return false;
        }

        SQLiteDatabase db =
                getWritableDatabase();

        int result = db.delete(
                TABLE_BUDGET,
                COL_BUDGET_ID + " = ?",
                new String[]{
                        String.valueOf(budgetId)
                }
        );

        return result > 0;
    }
    // =====================================================
    // DEBT
    // =====================================================

    public boolean insertDebt(Debt debt) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_DEBT_PERSON, debt.getPerson());
        values.put(COL_DEBT_AMOUNT, debt.getAmount());
        values.put(COL_DEBT_DEADLINE, debt.getDeadline());
        values.put(COL_DEBT_STATUS, debt.getStatus());
        values.put(COL_DEBT_TYPE, debt.getType());
        values.put(COL_DEBT_ACCOUNT_ID, debt.getAccountId());

        long result = db.insert(
                TABLE_DEBT,
                null,
                values
        );

        return result != -1;
    }
    public ArrayList<Debt> getDebtsByAccountId(int accountId) {

        ArrayList<Debt> debtList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_DEBT +
                        " WHERE " + COL_DEBT_ACCOUNT_ID + "=?" +
                        " ORDER BY " + COL_DEBT_DEADLINE + " ASC",
                new String[]{
                        String.valueOf(accountId)
                }
        );

        if (cursor.moveToFirst()) {

            do {

                debtList.add(
                        new Debt(
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_DEBT_ID)),
                                cursor.getString(cursor.getColumnIndexOrThrow(COL_DEBT_PERSON)),
                                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_DEBT_AMOUNT)),
                                cursor.getString(cursor.getColumnIndexOrThrow(COL_DEBT_DEADLINE)),
                                cursor.getString(cursor.getColumnIndexOrThrow(COL_DEBT_STATUS)),
                                cursor.getString(cursor.getColumnIndexOrThrow(COL_DEBT_TYPE)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_DEBT_ACCOUNT_ID))
                        )
                );

            } while (cursor.moveToNext());

        }

        cursor.close();

        return debtList;
    }
    public boolean updateDebt(Debt debt) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_DEBT_PERSON, debt.getPerson());
        values.put(COL_DEBT_AMOUNT, debt.getAmount());
        values.put(COL_DEBT_DEADLINE, debt.getDeadline());
        values.put(COL_DEBT_STATUS, debt.getStatus());
        values.put(COL_DEBT_TYPE, debt.getType());

        int result = db.update(
                TABLE_DEBT,
                values,
                COL_DEBT_ID + "=?",
                new String[]{
                        String.valueOf(debt.getId())
                }
        );

        return result > 0;
    }
    public boolean deleteDebt(int id) {

        SQLiteDatabase db = getWritableDatabase();

        int result = db.delete(
                TABLE_DEBT,
                COL_DEBT_ID + "=?",
                new String[]{
                        String.valueOf(id)
                }
        );

        return result > 0;
    }


    public double getTotalByTypeAndMonth(
            int accountId,
            String type,
            int month,
            int year
    ) {

        SQLiteDatabase db = getReadableDatabase();

        String query =
                "SELECT COALESCE(SUM(" +
                        COL_TRANSACTION_AMOUNT +
                        "), 0) FROM " + TABLE_TRANSACTION +
                        " WHERE " +
                        COL_TRANSACTION_ACCOUNT_ID + " = ?" +
                        " AND " +
                        COL_TRANSACTION_TYPE + " IN (?, ?)" +
                        " AND substr(" +
                        COL_TRANSACTION_DATE +
                        ", 1, 7) = ?";

        try (Cursor cursor = db.rawQuery(
                query,
                new String[]{
                        String.valueOf(accountId),
                        type,
                        getLegacyTransactionType(type),
                        formatYearMonth(year, month)
                }
        )) {

            return cursor.moveToFirst()
                    ? cursor.getDouble(0)
                    : 0;
        }
    }

    public double getIncomeByMonth(
            int accountId,
            int month,
            int year
    ) {
        return getTotalByTypeAndMonth(
                accountId,
                TYPE_INCOME,
                month,
                year
        );
    }

    public double getExpenseByMonth(
            int accountId,
            int month,
            int year
    ) {
        return getTotalByTypeAndMonth(
                accountId,
                TYPE_EXPENSE,
                month,
                year
        );
    }

    public LinkedHashMap<String, Double> getExpenseSummaryByCategory(
            int accountId,
            int month,
            int year
    ) {

        LinkedHashMap<String, Double> result =
                new LinkedHashMap<>();

        SQLiteDatabase db = getReadableDatabase();

        String query =
                "SELECT c." + COL_CATEGORY_NAME + ", " +
                        "COALESCE(SUM(t." +
                        COL_TRANSACTION_AMOUNT +
                        "), 0) AS totalAmount " +
                        "FROM " + TABLE_TRANSACTION + " t " +
                        "INNER JOIN " + TABLE_CATEGORY + " c " +
                        "ON c." + COL_CATEGORY_ID +
                        " = t." + COL_TRANSACTION_CATEGORY_ID + " " +
                        "WHERE t." +
                        COL_TRANSACTION_ACCOUNT_ID + " = ?" +
                        " AND t." +
                        COL_TRANSACTION_TYPE + " IN (?, ?)" +
                        " AND substr(t." +
                        COL_TRANSACTION_DATE +
                        ", 1, 7) = ? " +
                        "GROUP BY c." +
                        COL_CATEGORY_ID + ", c." +
                        COL_CATEGORY_NAME + " " +
                        "HAVING totalAmount > 0 " +
                        "ORDER BY totalAmount DESC";

        try (Cursor cursor = db.rawQuery(
                query,
                new String[]{
                        String.valueOf(accountId),
                        TYPE_EXPENSE,
                        LEGACY_TYPE_EXPENSE,
                        formatYearMonth(year, month)
                }
        )) {

            int nameIndex = cursor.getColumnIndexOrThrow(
                    COL_CATEGORY_NAME
            );

            int totalIndex = cursor.getColumnIndexOrThrow(
                    "totalAmount"
            );

            while (cursor.moveToNext()) {
                result.put(
                        cursor.getString(nameIndex),
                        cursor.getDouble(totalIndex)
                );
            }
        }

        return result;
    }

    // Giữ lại để các màn hình cũ chưa cần sửa ngay.
    public double getExpenseByCategoryAndMonth(
            int accountId,
            int categoryId,
            int month,
            int year
    ) {
        return getExpenseByCategory(
                accountId,
                categoryId,
                month,
                year
        );
    }

    private static String getLegacyTransactionType(
            String type
    ) {

        return TYPE_INCOME.equals(type)
                ? LEGACY_TYPE_INCOME
                : LEGACY_TYPE_EXPENSE;
    }

    private static String formatYearMonth(
            int year,
            int month
    ) {

        return String.format(
                Locale.US,
                "%04d-%02d",
                year,
                month
        );
    }
    public Budget getBudgetByTransaction(
            int accountId,
            int categoryId,
            String date
    ) {

        SQLiteDatabase db = getReadableDatabase();

        String month = date.substring(5, 7);
        String year = date.substring(0, 4);

        Cursor cursor = db.rawQuery(
                "SELECT * FROM Budget " +
                        "WHERE accountId=? " +
                        "AND categoryId=? " +
                        "AND month=? " +
                        "AND year=?",
                new String[]{
                        String.valueOf(accountId),
                        String.valueOf(categoryId),
                        month,
                        year
                }
        );

        Budget budget = null;

        if (cursor.moveToFirst()) {

            budget = new Budget(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("month")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("year")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("categoryId")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("accountId")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("warningPercent"))
            );

            budget.setCategoryName(
                    getCategoryNameById(
                            budget.getCategoryId()
                    )
            );

            budget.setSpentAmount(
                    getExpenseByCategoryAndMonth(
                            accountId,
                            categoryId,
                            budget.getMonth(),
                            budget.getYear()
                    )
            );
        }

        cursor.close();

        return budget;
    }

}