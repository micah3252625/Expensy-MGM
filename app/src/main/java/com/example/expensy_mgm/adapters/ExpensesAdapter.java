package com.example.expensy_mgm.adapters;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensy_mgm.R;
import com.example.expensy_mgm.entities.Expense;
import com.example.expensy_mgm.listeners.ExpenseListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ExpenseViewHolder>{

    private List<Expense> expenses;
    private ExpenseListener expenseListener;
    private Timer timer;
    private List<Expense> expensesSource;

    public ExpensesAdapter(List<Expense> expenses, ExpenseListener expenseListener) {
        this.expenses = expenses;
        this.expenseListener = expenseListener;
        expensesSource = expenses;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExpenseViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_expense,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        holder.setExpense(expenses.get(position));
        holder.layoutExpense.setOnClickListener(view -> {
            expenseListener.onExpenseClicked(expenses.get(position), position);
        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutExpense;
        TextView textAmount, textCategory, textDateTime;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            textAmount = itemView.findViewById(R.id.textAmount);
            textCategory = itemView.findViewById(R.id.textCategory);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            layoutExpense = itemView.findViewById(R.id.layoutExpense);
        }

        void setExpense(Expense expense) {
            String strAmount = Double.toString(expense.getAmount());
            textAmount.setText(strAmount);
            if (strAmount.trim().isEmpty()) {
                textCategory.setVisibility(View.GONE);
            }
            else {
                textCategory.setText(expense.getCategory());
            }
            textDateTime.setText(expense.getDateTime());
        }


    }

    public double totalExpenseAmount() {
        double amount = 0.0;
        for (Expense expense : expenses) {
            amount += expense.getAmount();
        }
        return amount;
    }

    public void searchExpense(final String searchKeyword) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()) {
                    expenses = expensesSource;
                }
                else {
                    ArrayList<Expense> temp = new ArrayList<>();
                    for (Expense expense : expensesSource) {
                        if (expense.getCategory().toLowerCase().contains(searchKeyword.toLowerCase())
                            || expense.getDescription().toLowerCase().contains(searchKeyword.toLowerCase())
                            || expense.getDateTime().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            temp.add(expense);
                        }
                    }
                    expenses = temp;
                }
                new Handler(Looper.getMainLooper()).post(() -> notifyDataSetChanged());
            }
        }, 500);
    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

}
