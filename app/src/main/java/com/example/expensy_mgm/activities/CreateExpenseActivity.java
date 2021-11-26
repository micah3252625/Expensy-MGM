package com.example.expensy_mgm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensy_mgm.R;
import com.example.expensy_mgm.database.ExpensyDatabase;
import com.example.expensy_mgm.entities.Expense;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateExpenseActivity extends AppCompatActivity {

    private EditText inputExpenseAmount, inputExpenseCategory, inputExpenseDescription;
    private TextView textDateTime;

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

        // set the textDateTime
        textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                .format(new Date())
        );

        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveExpense();
                Toast.makeText(CreateExpenseActivity.this, "Expense added!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // function that parses the string to double
    float parseDouble(String strAmount) {
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
}