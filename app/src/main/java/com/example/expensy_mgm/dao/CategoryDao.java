package com.example.expensy_mgm.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.expensy_mgm.entities.Category;
import com.example.expensy_mgm.entities.Expense;

import java.util.List;

@Dao
public interface CategoryDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategory(Category category);

    @Delete
    void deleteCategory(Category category);

    @Query("SELECT * FROM category ORDER BY id DESC")
    List<Category> getAllCategory();

    @Query("SELECT COUNT(*) FROM category")
    int getCount();
}
