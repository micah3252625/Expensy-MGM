package com.example.expensy_mgm.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.expensy_mgm.entities.Expense;
import com.example.expensy_mgm.entities.Income;

import java.util.List;

@Dao
public interface IncomeDao {

    @Query("SELECT * FROM income ORDER BY id DESC")
    List<Income> getAllIncome();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertIncome(Expense expense);

    @Delete
    void deleteIncome(Income income);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateIncome(Income income);
}
