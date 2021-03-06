package com.example.expensy_mgm.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.expensy_mgm.entities.Expense;

import java.util.List;

@Dao
public interface ExpenseDao {

    @Query("SELECT * FROM expenses ORDER BY id DESC")
    List<Expense> getAllExpenses();

    @Query("SELECT amount FROM expenses WHERE id = (SELECT MAX(id) FROM expenses) limit 1")
    double getAmountExpense();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertExpense(Expense expense);

    @Delete
    void deleteExpense(Expense expense);
}
