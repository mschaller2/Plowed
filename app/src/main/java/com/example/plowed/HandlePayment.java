package com.example.plowed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


public class HandlePayment extends AppCompatActivity {

    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private PaymentsClient paymentsClient;
    private View googlePayButton;
    private EditText amount;
    private EditText note;
    private EditText plowerEmail;
    private boolean[] changeCheck;

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeCheck = new boolean[]{false, false, false};
        setContentView(R.layout.handle_payment);
        plowerEmail = (EditText) findViewById(R.id.upi_id);
        note = (EditText) findViewById(R.id.note);
        amount = (EditText) findViewById(R.id.amount_et);
        TextWatcher watcher1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeCheck[0] = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        TextWatcher watcher2 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeCheck[1] = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        TextWatcher watcher3 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeCheck[2] = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        plowerEmail.addTextChangedListener(watcher1);
        note.addTextChangedListener(watcher2);
        amount.addTextChangedListener(watcher3);
        googlePayButton = (View) findViewById(R.id.google_pay_button);
        Wallet.WalletOptions walletOptions = new Wallet.WalletOptions.Builder().setEnvironment
                (WalletConstants.ENVIRONMENT_TEST).build();
        paymentsClient = Wallet.getPaymentsClient(this, walletOptions);
        IsReadyToPayRequest readyToPayRequest = IsReadyToPayRequest.fromJson(baseConfigurationJson().toString());
        Task<Boolean> task = paymentsClient.isReadyToPay(readyToPayRequest);
        task.addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> completeTask) {
                if(completeTask.isSuccessful()){
                    showGooglePayButton(completeTask.getResult());
                }else{
                    Log.e("error", "paymentsclient onComplete");
                }
            }
        });
        googlePayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changeCheck[0] && changeCheck[1] && changeCheck[2]){
                    loadPaymentData();
                }else{
                    Toast.makeText(getBaseContext(), "Entries cannot be left blank", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static JSONObject baseConfigurationJson(){
        try {
            return new JSONObject()
                    .put("apiVersion", 2)
                    .put("apiVersionMinor", 0)
                    .put("allowedPaymentMethods",
                            new JSONArray().put(getCardPaymentMethod()));
        }catch(Exception e){
            Log.e("baseConfigJson", e.toString());
            return null;
        }
    }
    private void showGooglePayButton(boolean userIsReadyToPay){
        if(userIsReadyToPay){
            googlePayButton.setVisibility(View.VISIBLE);
        } else {
            //don't show
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadPaymentData(){
        final JSONObject paymentRequestJson = baseConfigurationJson();
        try{
            paymentRequestJson.put("transactionInfo", new JSONObject()
                    .put("totalPrice", "0.01")
                    .put("totalPriceStatus", "FINAL")
                    .put("currencyCode", "USD"));
            paymentRequestJson.put("merchantInfo", new JSONObject()
                    .put("merchantId", "0123456789")
                    .put("merchantName", "PlowedInc"));
            }catch(Exception e){
                Log.e("loadpaymentdata", e.toString());
            }
        final PaymentDataRequest request = PaymentDataRequest.fromJson(paymentRequestJson.toString());
        AutoResolveHelper.resolveTask(paymentsClient.loadPaymentData(request),this, LOAD_PAYMENT_DATA_REQUEST_CODE);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static JSONObject getCardPaymentMethod() throws JSONException {
        final String[] networks = new String[] {"VISA", "AMEX"};
        final String[] authMethods = new String[] {"PAN_ONLY", "CRYPTOGRAM_3DS"};
        JSONObject card = new JSONObject();
        card.put("type", "CARD");
        card.put("tokenizationSpecification", getTokenizationSpec());
        card.put("parameters", new JSONObject().put("allowedAuthMethods", new JSONArray(authMethods)).put("allowedCardNetworks", new JSONArray(networks)));
        return card;
    }

    private static JSONObject getTokenizationSpec() throws JSONException {
        return new JSONObject() {{
            put("type", "PAYMENT_GATEWAY");
            put("parameters", new JSONObject() {
                {
                    put("gateway", "example");
                    put("gatewayMerchantId", "exampleGatewayMerchantId");
                }
            });
        }};
    }
        /**
         * Handle a resolved activity from the Google Pay payment sheet.
         *
         * @param requestCode Request code originally supplied to AutoResolveHelper in requestPayment().
         * @param resultCode  Result code returned by the Google Pay API.
         * @param data        Intent from the Google Pay API containing payment or error data.
         * @see <a href="https://developer.android.com/training/basics/intents/result">Getting a result
         * from an Activity</a>
         */
        @Override
    public void onActivityResult ( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        handlePaymentSuccess(paymentData);
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user cancelled the payment attempt
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        if (status != null) {
                            Log.e("payment", status.toString());
                        }
                        break;
                }
                // Re-enables the Google Pay payment button.
                googlePayButton.setClickable(true);
        }
    }
    /**
     * PaymentData response object contains the payment information, as well as any additional
     * requested information, such as billing and shipping address.
     *
     * @param paymentData A response object returned by Google after a payer approves payment.
     * @see <a href="https://developers.google.com/pay/api/android/reference/
     * object#PaymentData">PaymentData</a>
     */
    private void handlePaymentSuccess(PaymentData paymentData) {

        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        final String paymentInfo = paymentData.toJson();
        if (paymentInfo == null) {
            return;
        }

        try {
            JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".

            final JSONObject tokenizationData = paymentMethodData.getJSONObject("tokenizationData");
            final String tokenizationType = tokenizationData.getString("type");
            final String token = tokenizationData.getString("token");

            if ("PAYMENT_GATEWAY".equals(tokenizationType) && "examplePaymentMethodToken".equals(token)) {
                new AlertDialog.Builder(this)
                        .setTitle("Google Pay Confirmation")
                        // todo make sure to validate entry of text here
                        .setMessage(String.format("$%s will be sent to %s:\n%s", amount.getText(),
                                plowerEmail.getText(), note.getText()))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(getApplicationContext(), RequestService.class));
                            }
                        })
                        .create()
                        .show();
            }
            Log.d("Google Pay token: ", token);
        } catch (JSONException e) {
            throw new RuntimeException("The selected garment cannot be parsed from the list of elements");
        }
    }
}

