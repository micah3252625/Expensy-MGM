package com.example.expensy_mgm.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensy_mgm.R;
import com.example.expensy_mgm.adapters.ExpensesAdapter;
import com.example.expensy_mgm.dao.CategoryDao;
import com.example.expensy_mgm.database.ExpensyDatabase;
import com.example.expensy_mgm.entities.Category;
import com.example.expensy_mgm.entities.Expense;
import com.example.expensy_mgm.entities.Income;
import com.example.expensy_mgm.fragments.AnalyticsFragment;
import com.example.expensy_mgm.listeners.ExpenseListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements ExpenseListener {

    // request codes
    public static final int REQUEST_CODE_ADD_EXPENSE = 1;
    public static final int REQUEST_CODE_UPDATE_EXPENSE = 2;
    public static final int REQUEST_CODE_SHOW_EXPENSE = 3;

    private RecyclerView expensesRecyclerView;
    private List<Expense> expenseList;
    private ExpensesAdapter expensesAdapter;
    private ImageView imageAddIncome, imageAddCategory, imageMore;
    private LinearLayout layoutAddIncome;
    private AlertDialog dialogAddIncome, dialogAddCategory;
    private TextView textIncome;
    private LinearLayout layoutIncome, layoutAddCategory;
    private EditText inputIncome;

    private int expenseClickedPosition = -1;
    private int id = 1;

    private CreateExpenseActivity createExpenseActivity;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutIncome = findViewById(R.id.layoutIncome);
        inputIncome = findViewById(R.id.inputIncome);
        textIncome = findViewById(R.id.textIncome);

        // handles the add expense button
        ImageView imageAddExpenseMain = findViewById(R.id.imageAddExpenseMain);
        imageAddExpenseMain.setOnClickListener(view -> {
            if (parseDouble(textIncome.getText().toString().trim()) == 0.0) {
                Toast.makeText(MainActivity.this, "Not enough income", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this, CreateExpenseActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_EXPENSE);
        });

        // handles the redirection of main screen to analytics fragment


        // sets the recycler view
        expensesRecyclerView = findViewById(R.id.expenseRecyclerView);
        expensesRecyclerView.setLayoutManager(
                new LinearLayoutManager(MainActivity.this)
        );
        expenseList = new ArrayList<>();
        expensesAdapter = new ExpensesAdapter(expenseList, this);
        expensesRecyclerView.setAdapter(expensesAdapter);
        getExpenses(REQUEST_CODE_SHOW_EXPENSE, false);

        // handles the search functionality
        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                expensesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (expenseList.size() != 0) {
                    expensesAdapter.searchExpense(editable.toString());

                    layoutIncome.setVisibility(View.GONE);
                    imageAddIncome.setVisibility(View.GONE);
                }
                if (inputSearch.length() == 0) {
                    layoutIncome.setVisibility(View.VISIBLE);
                    imageAddIncome.setVisibility(View.VISIBLE);
                }
                /*if (expensesAdapter.searchExpenseExist(editable.toString()))
                    textPrompt.setVisibility(View.VISIBLE);*/

            }
        });

        // handles the more activity
        imageMore = findViewById(R.id.imageMore);
        imageMore.setOnClickListener(view -> {
            Intent moreActivity = new Intent(getApplicationContext(), MoreActivity.class);
            startActivity(moreActivity);
            finish();
        });


        // handles the add income button
        imageAddIncome = findViewById(R.id.imageAddIncome);
        layoutAddIncome = findViewById(R.id.layoutAddIncome);
        imageAddIncome.setOnClickListener(view -> showAddIncomeDialog());

        // handles the add category button
        imageAddCategory = findViewById(R.id.imageAddCategory);
        layoutAddCategory = findViewById(R.id.layoutAddCategory);
        imageAddCategory.setOnClickListener(view -> showAddCategoryDialog());

        try {
            textIncome.setText(Double.toString(getCurrentIncome()));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // This function prepopulate the category database for the default category

    }

    private void getSubtractedIncome() {
        Double getTotalExpenses = 0.0;
        try {
            getTotalExpenses = getTotalExpenses();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Double finalGetTotalExpenses = getTotalExpenses;
        Log.d("minus", Double.toString(finalGetTotalExpenses));
        class SubtractIncomeTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                ExpensyDatabase.getExpensyDatabase(getApplicationContext()).incomeDao().subtractIncome(finalGetTotalExpenses);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    textIncome.setText(Double.toString(getCurrentIncome()));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        new SubtractIncomeTask().execute();
    }

    double parseDouble(String strAmount) {
        if (strAmount != null && strAmount.length() > 0) {
            try {
                return Float.parseFloat(strAmount);
            } catch (Exception e) {
                return -1;
            }
        }
        else
            return 0;
    }

    private Double getTotalExpenses() throws ExecutionException, InterruptedException {
        class TotalExpensesTask extends AsyncTask<Void, Void, Double> {
            @Override
            protected Double doInBackground(Void... voids) {
                return  ExpensyDatabase.getExpensyDatabase(getApplicationContext()).expenseDao().getAmountExpense();
            }

            @Override
            protected void onPostExecute(Double aDouble) {
                super.onPostExecute(aDouble);
            }
        }
        return new TotalExpensesTask().execute().get();
    }

    private Double getCurrentIncome() throws ExecutionException, InterruptedException {
        class GetCurrentIncomeTask extends AsyncTask<Void, Void, Double> {
            @Override
            protected Double doInBackground(Void... voids) {
                Double income = ExpensyDatabase.getExpensyDatabase(getApplicationContext()).incomeDao()
                        .getCurrentIncome();

                return Math.abs(income);
            }

            @Override
            protected void onPostExecute(Double aDouble) {
                super.onPostExecute(aDouble);
            }
        }
        return new GetCurrentIncomeTask().execute().get();
    }

    @Override
    public void onExpenseClicked(Expense expense, int position) {
        expenseClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateExpenseActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("expense", expense);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_EXPENSE);
    }

    private void populateCategory() {
        class PopulateCategoryTAsk extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(final Void... params) {
                ExpensyDatabase.getExpensyDatabase(getApplicationContext()).categoryDao().insertCategory(new Category("Food"));
                ExpensyDatabase.getExpensyDatabase(getApplicationContext()).categoryDao().insertCategory(new Category("Gas"));
                ExpensyDatabase.getExpensyDatabase(getApplicationContext()).categoryDao().insertCategory(new Category("Netflix"));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }
        new PopulateCategoryTAsk().execute();
    }

    private void getExpenses(final int requestCode, final boolean isExpenseDeleted) {
        class GetExpensesTask extends AsyncTask<Void, Void, List<Expense>> {
            @Override
            protected List<Expense> doInBackground(Void... voids) {
                return ExpensyDatabase
                        .getExpensyDatabase(getApplicationContext())
                        .expenseDao().getAllExpenses();
            }

            @Override
            protected void onPostExecute(List<Expense> expenses) {
                super.onPostExecute(expenses);
                if (requestCode == REQUEST_CODE_SHOW_EXPENSE) { // add all notess from database to expenseList and notify adapter about the new dataset
                    expenseList.addAll(expenses);
                    expensesAdapter.notifyDataSetChanged();
                }
                // adding an only first expense (newly added expense) from the
                // database to expenseList and notify the adapter for the newly
                // inserted item and scrolling recycler view to the top
                else if(requestCode == REQUEST_CODE_ADD_EXPENSE) {
                    expenseList.add(0, expenses.get(0));
                    expensesAdapter.notifyItemInserted(0);
                    expensesRecyclerView.smoothScrollToPosition(0);
                }
                // removing expense from the clicked position and adding the
                // latest updated expense from same position from the database
                // notify the adapter for item changed at the
                else if (requestCode == REQUEST_CODE_UPDATE_EXPENSE) {
                    expenseList.remove(expenseClickedPosition);
                    /*
                        if request code is REQUEST_CODE_UPDATE_EXPENSE, first, remove
                        expense from the list. Then we checked whether the note is deleted
                        or not. if the expense is deleted then notifies the adapter about
                        item removed. if the expense is not deleted then it must be updated
                        that's why we are adding a newly updated note to that same position
                        where we removed and notifies adapter about the item changed.
                    * */
                    if (isExpenseDeleted) {
                        expensesAdapter.notifyItemRemoved(expenseClickedPosition);
                    }
                    else {
                        expenseList.add(expenseClickedPosition, expenses.get(expenseClickedPosition));
                        expensesAdapter.notifyItemChanged(expenseClickedPosition );
                    }
                }
            }
        }
        new GetExpensesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("H", "RequestCode:" + requestCode);
        if (requestCode == REQUEST_CODE_ADD_EXPENSE && resultCode == RESULT_OK) {
            getExpenses(REQUEST_CODE_ADD_EXPENSE, false);
            getSubtractedIncome();
            Toast.makeText(this, "Expense Added", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == REQUEST_CODE_UPDATE_EXPENSE && resultCode == RESULT_OK) {
            if (data != null) {
                getExpenses(REQUEST_CODE_UPDATE_EXPENSE, data.getBooleanExtra("isExpenseDeleted", false));
            }
        }
    }

    private void showAddCategoryDialog() {
        if (dialogAddCategory == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_add_category,
                    (ViewGroup) findViewById(R.id.layoutAddCategoryContainer)
            );
            builder.setView(view);
            dialogAddCategory = builder.create();
            if (dialogAddCategory.getWindow() != null) {
                dialogAddCategory.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText inputCategory = view.findViewById(R.id.inputCategory);
            inputCategory.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String inputCategoryText = inputCategory.getText().toString().trim();

                    // validate
                    if (inputCategoryText.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please add a category", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // set the new inserted category
                    class InsertCategoryTask extends AsyncTask<Void, Void, Void> {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            ExpensyDatabase.getExpensyDatabase(getApplicationContext()).categoryDao()
                                    .insertCategory(new Category(inputCategoryText));
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                        }
                    }

                    new InsertCategoryTask().execute();

                    layoutAddCategory.setVisibility(View.VISIBLE);
                    dialogAddCategory.dismiss();
                    Toast.makeText(MainActivity.this, "Category has been added", Toast.LENGTH_SHORT).show();
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(view1 -> dialogAddCategory.dismiss());
        }
        dialogAddCategory.show();
    }

    private void showAddIncomeDialog() {
        if (dialogAddIncome == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_add_income,
                    (ViewGroup) findViewById(R.id.layoutAddIncomeContainer)
            );
            builder.setView(view);
            dialogAddIncome = builder.create();
            if (dialogAddIncome.getWindow() != null) {
                dialogAddIncome.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText inputIncome = view.findViewById(R.id.inputIncome);
            inputIncome.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String inputIncomeText = inputIncome.getText().toString().trim();
                    double textIncomeAmount = parseDouble(textIncome.getText().toString().trim());

                    if (inputIncomeText.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please add your income", Toast.LENGTH_SHORT).show();
                    }
                    else if (!inputIncomeText.isEmpty() && (textIncomeAmount == 0.0)) {
                        Income income = new Income();
                        income.setIncome_amount(parseDouble(inputIncomeText));

                        class SetIncomeTask extends AsyncTask<Void, Void, Void> {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                ExpensyDatabase.getExpensyDatabase(getApplicationContext()).incomeDao()
                                        .insertIncome(income);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                try {
                                    textIncome.setText(Double.toString(getCurrentIncome()));
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                layoutAddIncome.setVisibility(View.VISIBLE);
                                dialogAddIncome.dismiss();
                                Toast.makeText(MainActivity.this, "Income has been set", Toast.LENGTH_SHORT).show();
                            }
                        }

                        new SetIncomeTask().execute();
                    }
                    else {
                        double inputIncomeAmount = parseDouble(inputIncomeText);

                        class AddIncomeTask extends AsyncTask<Void, Void, Void> {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                ExpensyDatabase.getExpensyDatabase(getApplicationContext()).incomeDao()
                                        .addIncome(inputIncomeAmount);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                try {
                                    textIncome.setText(Double.toString(getCurrentIncome()));
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                layoutAddIncome.setVisibility(View.VISIBLE);
                                dialogAddIncome.dismiss();
                                Toast.makeText(MainActivity.this, "Income added", Toast.LENGTH_SHORT).show();
                            }
                        }
                        new AddIncomeTask().execute();
                    }
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(view1 -> dialogAddIncome.dismiss());
        }
        dialogAddIncome.show();
    }

}