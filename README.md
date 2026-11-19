# BudgetManage

Android personal finance app built with Java, XML layouts, View Binding, SQLite, and MPAndroidChart.

## Features

- Authentication & session management (`LoginActivity`, `RegisterActivity`, `SessionManager`)
- Dashboard with balance & recent transactions (`HomeActivity`)
- Income/expense tracking with search, type & month filters (`TransactionActivity`)
- Category budgets with spending threshold alerts (`BudgetActivity`)
- Custom income/expense categories (`CategoryActivity`)
- Savings goals & progress updates (`GoalActivity`)
- Borrow/lend debt tracking (`DebtActivity`)
- Monthly statistics with interactive Donut chart (`StatisticsActivity`)

## Tech Stack

- Java, Android XML, View Binding, RecyclerView
- SQLite (`DatabaseHelper`), SharedPreferences (`SessionManager`)
- MPAndroidChart

## Structure (`app/src/main`)

- `java/com/example/cuoiki/` — `activity`, `adapter`, `database`, `model`, `utils`
- `res/` — `layout`, `drawable`, `values`, `xml`
