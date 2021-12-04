package com.example.expensy_mgm.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.expensy_mgm.dao.CategoryDao;
import com.example.expensy_mgm.dao.ExpenseDao;
import com.example.expensy_mgm.dao.IncomeDao;
import com.example.expensy_mgm.entities.Category;
import com.example.expensy_mgm.entities.Expense;
import com.example.expensy_mgm.entities.Income;

@Database(entities = {Expense.class, Income.class, Category.class}, version = 1, exportSchema = false)
public abstract class ExpensyDatabase extends RoomDatabase {
    public abstract ExpenseDao expenseDao();
    public abstract IncomeDao incomeDao();
    public abstract CategoryDao categoryDao();
    private static ExpensyDatabase expensyDatabase;

    public static synchronized ExpensyDatabase getExpensyDatabase(final Context context) {
        if (expensyDatabase == null) {
            expensyDatabase = Room.databaseBuilder(context.getApplicationContext(), ExpensyDatabase.class, "expensy_db")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return expensyDatabase;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateCategoryTask(expensyDatabase).execute();
        }
    };

    private static class PopulateCategoryTask extends AsyncTask<Void, Void, Void> {
        private final CategoryDao categoryDao;

        PopulateCategoryTask(ExpensyDatabase database){
            categoryDao = database.categoryDao();
        }

        @Override
        protected Void doInBackground(final Void... voids) {
            categoryDao.insertCategory(new Category("Food"));
            categoryDao.insertCategory(new Category("Extras"));
            return null;
        }
    }
}
