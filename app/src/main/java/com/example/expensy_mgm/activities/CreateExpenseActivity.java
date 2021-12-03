package com.example.expensy_mgm.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensy_mgm.R;
import com.example.expensy_mgm.dao.CategoryDao;
import com.example.expensy_mgm.database.ExpensyDatabase;
import com.example.expensy_mgm.entities.Category;
import com.example.expensy_mgm.entities.Expense;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CreateExpenseActivity extends AppCompatActivity {

    private EditText inputExpenseAmount, inputExpenseDescription;
    private TextView textDateTime, textIncome;
    private AutoCompleteTextView inputExpenseCategory;

    private Expense alreadyAvailableExpense;
    private ArrayAdapter<String> adapter;
    private ImageView imageDelete;

    private AlertDialog dialogDeleteExpense;

    private CategoryDao categoryDao;
    private List<Category> categoryList;
    private ArrayList<String> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_expense);

        // handles the back arrow icon
        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(view -> onBackPressed());

        // access the id's from its layout
        inputExpenseAmount = findViewById(R.id.inputExpenseAmount);
        inputExpenseCategory = findViewById(R.id.inputExpenseCategory);
        inputExpenseDescription = findViewById(R.id.inputExpenseDescription);
        textDateTime = findViewById(R.id.textDateTime);
        textIncome = findViewById(R.id.textIncome);
        //
        imageDelete = findViewById(R.id.imageDelete);
        imageDelete.setOnClickListener(view -> showDeleteExpenseDialog());

        // set the textDateTime
        textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                .format(new Date())
        );

        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(view -> {
            saveExpense();
        });

        inputExpenseCategory.setOnTouchListener((view, motionEvent) -> true) ;

        if (getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            alreadyAvailableExpense = (Expense) getIntent().getSerializableExtra("expense");
            setViewOrUpdateExpense();
        }


        Integer size = 0;
        try {
            size = getCountCategories();
            for (int i = 0; i < size; i++) {
                categories.add(getCategories().get(i).getCategory_name());
                //Log.i("value", categories.get(i));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        CATEGORIES = categories.toArray(new String[categories.size()]);

        for (String i : CATEGORIES) {
            Log.i("value", i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, CATEGORIES);
        inputExpenseCategory.setAdapter(adapter);

    }

    // this function sets handles the setting of viewing and updating the expense
    private void setViewOrUpdateExpense() {
        inputExpenseAmount.setText(Double.toString(alreadyAvailableExpense.getAmount()));
        inputExpenseCategory.setText(alreadyAvailableExpense.getCategory());
        inputExpenseDescription.setText(alreadyAvailableExpense.getDescription());
        textDateTime.setText(alreadyAvailableExpense.getDateTime());

        // reinitialize the dropdown list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, CATEGORIES);
        inputExpenseCategory.setAdapter(adapter);

        // show the delete icon when only viewing the expense
        imageDelete.setVisibility(View.VISIBLE);

    }

    // function that parses the string to double
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

    private Integer getCountCategories() throws ExecutionException, InterruptedException {
        class GetCountCategoriesTask extends AsyncTask<Void, Void, Integer> {

            @Override
            protected Integer doInBackground(Void... voids) {
                return ExpensyDatabase.getExpensyDatabase(CreateExpenseActivity.this)
                        .categoryDao().getCount();
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
            }
        }
        return new GetCountCategoriesTask().execute().get();
    }

    private List<Category> getCategories() throws ExecutionException, InterruptedException {
        class GetCategoriesTask extends AsyncTask<Void, Void, List<Category>> {
            @Override
            protected List<Category> doInBackground(Void... voids) {
                return ExpensyDatabase.getExpensyDatabase(CreateExpenseActivity.this)
                        .categoryDao().getAllCategory();
            }

            @Override
            protected void onPostExecute(List<Category> aVoid) {
                super.onPostExecute(aVoid);
            }
        }
        return new GetCategoriesTask().execute().get();
    }

    private void saveExpense() {
        // input validation
        if (inputExpenseAmount.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please input the amount of your expense!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (inputExpenseCategory.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please input the category of your expense!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (inputExpenseDescription.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please input the description of your expense!", Toast.LENGTH_SHORT).show();
            return;
        }


        // set the details of the expense
        final Expense expense = new Expense();

        expense.setAmount(parseDouble(inputExpenseAmount.getText().toString()));
        expense.setCategory(inputExpenseCategory.getText().toString());
        expense.setDescription(inputExpenseDescription.getText().toString());
        expense.setDateTime(textDateTime.getText().toString());

        // check if an expense instance exist
        if (alreadyAvailableExpense != null)
            expense.setId(alreadyAvailableExpense.getId());

        @SuppressLint("StaticFieldLeak")
        class SaveExpenseTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                ExpensyDatabase.getExpensyDatabase(getApplicationContext()).expenseDao().insertExpense(expense);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }

        new SaveExpenseTask().execute();

    }

    // function that handles the alert dialog when delete icon is clicked
    private void showDeleteExpenseDialog() {
        if (dialogDeleteExpense == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateExpenseActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_expense,
                    (ViewGroup) findViewById(R.id.layoutDeleteContainer)
            );
            builder.setView(view);
            dialogDeleteExpense = builder.create();
            if (dialogDeleteExpense.getWindow() != null) {
                dialogDeleteExpense.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteExpense).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    class DeleteExpenseTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... voids) {
                            ExpensyDatabase.getExpensyDatabase(getApplicationContext()).expenseDao()
                                    .deleteExpense(alreadyAvailableExpense);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Intent intent = new Intent();
                            Toast.makeText(CreateExpenseActivity.this, "Expense Deleted!", Toast.LENGTH_SHORT).show();
                            intent.putExtra("isExpenseDeleted", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                    new DeleteExpenseTask().execute();
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogDeleteExpense.dismiss();
                }
            });
        }
        dialogDeleteExpense.show();
    }

   public String[] CATEGORIES = categories.toArray(new String[categories.size()]);

}