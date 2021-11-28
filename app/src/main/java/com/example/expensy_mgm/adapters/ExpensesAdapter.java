package com.example.expensy_mgm.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensy_mgm.R;
import com.example.expensy_mgm.entities.Expense;

import java.util.List;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ExpenseViewHolder>{

    private List<Expense> expenses;

    public ExpensesAdapter(List<Expense> expenses) {
        this.expenses = expenses;
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

        TextView textAmount, textCategory, textDateTime;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            textAmount = itemView.findViewById(R.id.textAmount);
            textCategory = itemView.findViewById(R.id.textCategory);
            textDateTime = itemView.findViewById(R.id.textDateTime);
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
}
