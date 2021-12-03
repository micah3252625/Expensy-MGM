package com.example.expensy_mgm.listeners;

import com.example.expensy_mgm.entities.Expense;

public interface ExpenseListener {
    void onExpenseClicked(Expense expense, int position);
}
