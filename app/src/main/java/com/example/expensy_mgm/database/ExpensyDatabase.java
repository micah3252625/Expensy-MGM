package com.example.expensy_mgm.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.expensy_mgm.dao.ExpenseDao;
import com.example.expensy_mgm.entities.Expense;

@Database(entities = Expense.class, version = 1, exportSchema = false)
public abstract class ExpensyDatabase extends RoomDatabase {
    private static ExpensyDatabase expensyDatabase;

    public static synchronized ExpensyDatabase getExpensyDatabase(Context context) {
        if (expensyDatabase == null) {
            expensyDatabase = Room.databaseBuilder(context, ExpensyDatabase.class, "expeny_db").build();
        }
        return expensyDatabase;
    }

    public abstract ExpenseDao expenseDao();
}
