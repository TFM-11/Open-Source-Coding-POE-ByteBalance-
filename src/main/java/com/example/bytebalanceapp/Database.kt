package com.example.bytebalanceapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Database(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ByteBalance.db"
        private const val DATABASE_VERSION = 4 // Updated version to include 'amount' in Expenses

        // Users table
        private const val TABLE_USERS = "Users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"

        // Expenses table
        private const val TABLE_EXPENSES = "Expenses"
        private const val COLUMN_EXPENSE_ID = "expense_id"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_IMAGE_URI = "image_uri"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_START_TIME = "startTime"
        private const val COLUMN_END_TIME = "endTime"
        private const val COLUMN_AMOUNT = "amount"

        // Categories table
        private const val TABLE_CATEGORIES = "Categories"
        private const val COLUMN_CATEGORY_ID = "category_id"
        private const val COLUMN_CATEGORY_NAME = "category_name"

        // Budget Goals table
        private const val TABLE_BUDGET_GOALS = "BudgetGoals"
        private const val COLUMN_BUDGET_ID = "id"
        private const val COLUMN_MONTH = "month"
        private const val COLUMN_MIN_GOAL = "min_goal"
        private const val COLUMN_MAX_GOAL = "max_goal"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT UNIQUE,
                $COLUMN_PASSWORD TEXT
            )
        """.trimIndent()

        val createExpensesTable = """
            CREATE TABLE $TABLE_EXPENSES (
                $COLUMN_EXPENSE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_CATEGORY TEXT,
                $COLUMN_IMAGE_URI TEXT,
                $COLUMN_DATE TEXT,
                $COLUMN_START_TIME TEXT,
                $COLUMN_END_TIME TEXT,
                $COLUMN_AMOUNT REAL
            )
        """.trimIndent()

        val createCategoriesTable = """
            CREATE TABLE $TABLE_CATEGORIES (
                $COLUMN_CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CATEGORY_NAME TEXT UNIQUE
            )
        """.trimIndent()

        val createBudgetGoalsTable = """
            CREATE TABLE $TABLE_BUDGET_GOALS (
                $COLUMN_BUDGET_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_MONTH TEXT,
                $COLUMN_MIN_GOAL REAL,
                $COLUMN_MAX_GOAL REAL
            )
        """.trimIndent()

        db.execSQL(createUserTable)
        db.execSQL(createExpensesTable)
        db.execSQL(createCategoriesTable)
        db.execSQL(createBudgetGoalsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BUDGET_GOALS")
        onCreate(db)
    }

    // --- User-related methods ---
    fun registerUser(username: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    // --- Expense-related methods ---

    fun insertExpense(
        description: String,
        category: String,
        imageUri: String?,
        date: String,
        startTime: String,
        endTime: String,
        amount: Double
    ): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_IMAGE_URI, imageUri)
            put(COLUMN_DATE, date)
            put(COLUMN_START_TIME, startTime)
            put(COLUMN_END_TIME, endTime)
            put(COLUMN_AMOUNT, amount)
        }
        val result = db.insert(TABLE_EXPENSES, null, values)
        db.close()
        return result != -1L
    }

    fun getAllExpenses(): List<Expense> {
        val expenses = mutableListOf<Expense>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_EXPENSES", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_ID))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                val photoPath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                val startTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME))
                val endTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME))
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))

                expenses.add(
                    Expense(
                        id, date, startTime, endTime, description, category, photoPath, amount
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return expenses
    }

    fun getExpensesByDateRange(startDate: String, endDate: String): List<Expense> {
        val expenses = mutableListOf<Expense>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_EXPENSES WHERE $COLUMN_DATE BETWEEN ? AND ?",
            arrayOf(startDate, endDate)
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_ID))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                val photoPath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                val startTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME))
                val endTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME))
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))

                expenses.add(
                    Expense(
                        id, date, startTime, endTime, description, category, photoPath, amount
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return expenses
    }

    // --- Category-related methods ---
    fun insertCategory(name: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CATEGORY_NAME, name)
        }
        val result = db.insert(TABLE_CATEGORIES, null, values)
        db.close()
        return result != -1L
    }

    // --- Budget Goal-related methods ---
    fun insertBudgetGoal(month: String, minGoal: Double, maxGoal: Double): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MONTH, month)
            put(COLUMN_MIN_GOAL, minGoal)
            put(COLUMN_MAX_GOAL, maxGoal)
        }
        val result = db.insert(TABLE_BUDGET_GOALS, null, values)
        db.close()
        return result != -1L
    }

    // --- Summary queries ---
    fun getCategoryTotals(): List<ItemSummaryData> {
        val summaryList = mutableListOf<ItemSummaryData>()
        val db = this.readableDatabase

        val query = """
            SELECT $COLUMN_CATEGORY, SUM($COLUMN_AMOUNT) AS totalAmount 
            FROM $TABLE_EXPENSES 
            GROUP BY $COLUMN_CATEGORY
        """.trimIndent()

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                val totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("totalAmount"))
                summaryList.add(ItemSummaryData(category, totalAmount))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return summaryList
    }

    fun getCategoryTotals(startDate: String, endDate: String): List<ItemSummaryData> {
        val summaryList = mutableListOf<ItemSummaryData>()
        val db = this.readableDatabase

        val query = """
            SELECT $COLUMN_CATEGORY, SUM($COLUMN_AMOUNT) AS totalAmount 
            FROM $TABLE_EXPENSES 
            WHERE $COLUMN_DATE BETWEEN ? AND ?
            GROUP BY $COLUMN_CATEGORY
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(startDate, endDate))

        if (cursor.moveToFirst()) {
            do {
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                val totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("totalAmount"))
                summaryList.add(ItemSummaryData(category, totalAmount))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return summaryList
    }
    fun getBudgetGoalsForMonth(month: String): List<BudgetGoal> {
        val budgetGoals = mutableListOf<BudgetGoal>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_BUDGET_GOALS WHERE $COLUMN_MONTH = ?", arrayOf(month))

        if (cursor.moveToFirst()) {
            do {
                val monthName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONTH))
                val minGoal = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_MIN_GOAL))
                val maxGoal = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_MAX_GOAL))
                budgetGoals.add(BudgetGoal(monthName, minGoal, maxGoal))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return budgetGoals
    }

    fun deleteExpense(expenseId: Int): Boolean {
        val db = this.writableDatabase
        val deletedRows = db.delete(TABLE_EXPENSES, "$COLUMN_EXPENSE_ID=?", arrayOf(expenseId.toString()))
        db.close()
        return deletedRows > 0
    }





}
