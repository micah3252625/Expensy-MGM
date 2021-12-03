package com.example.expensy_mgm.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.expensy_mgm.entities.Income;

@Dao
public interface IncomeDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertIncome(Income income);

    @Query("INSERT INTO income(id, income_amount) SELECT MAX(id) + 1, income_amount + (:expense_amount) FROM income")
    void addIncome(double expense_amount);

    @Query("INSERT INTO income(id, income_amount) SELECT  MAX(id) + 1, income_amount - (:expense_amount) FROM income")
    void subtractIncome(double expense_amount);

    @Query("SELECT income_amount FROM income WHERE id = (SELECT MAX(id) FROM income) limit 1")
    double getCurrentIncome();
}
