package com.example.expensy_mgm.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "income")
public class Income implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "income_amount")
    private double income_amount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getIncome_amount() {
        return income_amount;
    }

    public void setIncome_amount(double income_amount) {
        this.income_amount = income_amount;
    }

    @Override
    public String toString() {
        return "Income{" +
                "id=" + id +
                ", income_amount=" + income_amount +
                '}';
    }
}
