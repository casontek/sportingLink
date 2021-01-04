package com.mycornership.betlink.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amplifyframework.core.Amplify;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mycornership.betlink.R;
import com.mycornership.betlink.models.DataResponse;
import com.mycornership.betlink.networks.RestApi;
import com.mycornership.betlink.networks.RestService;

import java.util.Date;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionActivity extends AppCompatActivity {
    TextInputEditText edt_card, edt_exp, edt_cvv;
    ProgressBar bar;
    int amount = 400;
    int number = 0;
    Transaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        //sets back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("sportingLink subscription");
        //initialize components
        init();
        //initialize Paystack for payment
        PaystackSdk.initialize(getBaseContext());
        //listen to text input changes
        inputListener();
    }

    private void init(){
        edt_card = findViewById(R.id.card_no);
        edt_exp = findViewById(R.id.card_expire);
        edt_cvv = findViewById(R.id.card_code);

        bar = findViewById(R.id.payment_bar);
        bar.setVisibility(View.GONE);

        MaterialButton btn_sub = findViewById(R.id.btn_subscribe);
        btn_sub.setOnClickListener(l ->{
            String card = edt_card.getText().toString().trim();
            String date = edt_exp.getText().toString().trim();
            String cvv = edt_cvv.getText().toString().trim();
            if(validInput(card, date, cvv))
                payment(card, date, cvv);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void inputListener(){
        //listen to card no text change
        edt_card.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(i1 == 0){
                    if( charSequence.length() % 5 == 0 && number != 0){
                        String xx = charSequence.toString();
                        String prf = xx.substring(0, charSequence.length() - 1);
                        char last = charSequence.charAt(charSequence.length() - 1);
                        String cr = prf + " " + last;
                        edt_card.setText(cr);
                        edt_card.setSelection(charSequence.length() + 1);
                        //reset the number to zero
                        //number = 0;
                    }

                    number += 1;
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //listen to card expiration date text change
        edt_exp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(i == 1 && i1 == 0){
                    //input
                    char sec = charSequence.charAt(1);
                    char first = charSequence.charAt(0);
                    String lx = first + "" + sec + "/";
                    edt_exp.setText(lx);
                    edt_exp.setSelection(3);
                }

                if(i == 2 && i1 == 0){
                    char last = charSequence.charAt(2);
                    char sec = charSequence.charAt(1);
                    char first = charSequence.charAt(0);
                    String lx = first + "" + sec + "/" + last;
                    edt_exp.setText(lx);
                    edt_exp.setSelection(4);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private boolean validInput(String card, String date, String cvv) {
        if(TextUtils.isEmpty(card)) {
            edt_card.setError("enter card number");

            return false;
        }

        if(TextUtils.isEmpty(date)){
            edt_exp.setError("enter expiration date");
            return false;
        }

        if(date.length() < 5){
            edt_exp.setError("incorrect expiration date");

            return false;
        }

        if(TextUtils.isEmpty(cvv)){
            edt_cvv.setError("enter security code");

            return false;
        }

        return true;
    }

    private void payment(String cardno, String date, String cvv) {

        String exp_mth = date.substring(0, 1);
        String exp_yr = date.substring(3);
        int mth = Integer.parseInt(exp_mth);
        int yr = Integer.parseInt(exp_yr);
        //continue to process the payment bellow
        Card card = new Card( cardno, mth, yr, cvv);
        //checks the validity of the car
        if(card.isValid()){
            Charge charge = new Charge();
            charge.setAmount(amount);
            charge.setCurrency("USD");
            charge.setCard(card);
            charge.setEmail("kasonmobile@gmail.com");
            charge.setReference(getChargeReference());
            processCharge(charge);
        }
        else {
            //terminate the payment process
            Toast.makeText(getBaseContext(), "invalid card.", Toast.LENGTH_LONG).show();
        }
    }

    private String getChargeReference() {
        String user = Amplify.Auth.getCurrentUser().getUsername();
        Date date = new Date();

        return user + "#" + date.toString().replace(" ", "");
    }

    private void processCharge(Charge charge) {
        //perform the charging
        bar.setVisibility(View.VISIBLE);
        transaction = null;

        PaystackSdk.chargeCard(this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                //hides the progress bar
                bar.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(), "validating process...", Toast.LENGTH_LONG).show();
                validatePayment(transaction.getReference());
            }

            @Override
            public void beforeValidate(Transaction transaction1) {
                //hides the progress bar
                //bar.setVisibility(View.GONE);
                transaction = transaction1;
                Toast.makeText(getBaseContext(),"processing please wait...", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable error, Transaction transaction1) {
                // If an access code has expired, simply ask your server for a new one
                // and restart the charge instead of displaying error
                transaction = transaction1;
                /*

                if (error instanceof ExpiredAccessCodeException) {
                    processCharge(c);
                    return;
                }

                 */

                //hides the progress bar
                bar.setVisibility(View.GONE);

                if (transaction.getReference() != null) {
                    Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    validatePayment(transaction.getReference());
                } else {
                    Toast.makeText( getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void validatePayment(String reference) {
        //shows progress
        bar.setVisibility(View.VISIBLE);
        RestApi api = RestService.getInstance().create(RestApi.class);
        Call<DataResponse> responseCall = api.subValidate(reference);
        responseCall.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                //hides progress
                bar.setVisibility(View.GONE);
                if(response.isSuccessful())
                    Toast.makeText(getBaseContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                //hides progress
                bar.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(), "validation error. \n Your subscription will be resolved by our Admins within 24hours.", Toast.LENGTH_LONG).show();
            }
        });
    }

}
