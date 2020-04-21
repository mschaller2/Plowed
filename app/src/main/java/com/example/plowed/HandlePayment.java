package com.example.plowed;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HandlePayment extends AppCompatActivity {
//    EditText amount, note, name, upivirtualid;
//    Button send;
//    String TAG ="main";
//    final int UPI_PAYMENT = 0;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private PaymentsClient paymentsClient;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static JSONObject baseConfigurationJson() throws JSONException {
        return new JSONObject()
                .put("apiVersion", 2)
                .put("apiVersinMinor", 8)
                .put("allowedPaymentMethods",
                        new JSONArray().put(getCardPaymentMethod()));
    }
    private static JSONObject getGatewayTokenizationSpecification() throws JSONException {
        return new JSONObject() {{      put("type", "PAYMENT_GATEWAY");
            put("parameters", new JSONObject() {
                {
                    put("gateway", "example");
                    put("gatewayMerchantId", "exampleGatewayMerchantId");
                }
            });
        }};
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.handle_payment);
        Wallet.WalletOptions walletOptions = new Wallet.WalletOptions.Builder().
                setEnvironment(WalletConstants.ENVIRONMENT_TEST). //The test environment doesn't need an account -- production does
                build();

        paymentsClient = Wallet.getPaymentsClient(this, walletOptions);
        paymentsClient = createPaymentsClient(this);
        try {
            isReadyToPay();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Uri uri =
//                new Uri.Builder()
//                        .scheme("upi")
//                        .authority("pay")
//                        .appendQueryParameter("pa", "your-merchant-vpa@xxx")       // virtual ID
//                        .appendQueryParameter("pn", "your-merchant-name")          // name
//                        .appendQueryParameter("mc", "your-merchant-code")          // optional
//                        .appendQueryParameter("tr", "your-transaction-ref-id")     // optional
//                        .appendQueryParameter("tn", "your-transaction-note")       // any note about payment
//                        .appendQueryParameter("am", "your-order-amount")           // amount
//                        .appendQueryParameter("cu", "INR")                         // currency
//                        .appendQueryParameter("url", "your-transaction-url")  // optional
//                        .build();

//        send = (Button) findViewById(R.id.button);
//        amount = (EditText)findViewById(R.id.amount_et);
//        note = (EditText)findViewById(R.id.note);
//        name = (EditText) findViewById(R.id.name);
//        upivirtualid =(EditText) findViewById(R.id.upi_id);

//        send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Getting the values from the EditTexts
//                if (TextUtils.isEmpty(name.getText().toString().trim())){
//                    Toast.makeText(HandlePayment.this," Name is invalid", Toast.LENGTH_SHORT).show();
//
//                }else if (TextUtils.isEmpty(upivirtualid.getText().toString().trim())){
//                    Toast.makeText(HandlePayment.this," UPI ID is invalid", Toast.LENGTH_SHORT).show();
//
//                }else if (TextUtils.isEmpty(note.getText().toString().trim())){
//                    Toast.makeText(HandlePayment.this," Note is invalid", Toast.LENGTH_SHORT).show();
//
//                }else if (TextUtils.isEmpty(amount.getText().toString().trim())){
//                    Toast.makeText(HandlePayment.this," Amount is invalid", Toast.LENGTH_SHORT).show();
//                }else{
//
//                    payUsingUpi(name.getText().toString(), upivirtualid.getText().toString(),
//                            note.getText().toString(), amount.getText().toString());
//
//                }
//
//
//            }
//        });
    }

    public static PaymentsClient createPaymentsClient(Activity activity) {
        Wallet.WalletOptions walletOptions =
                new Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build();
        return Wallet.getPaymentsClient(activity, walletOptions);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void isReadyToPay() throws JSONException {
        IsReadyToPayRequest readyToPayRequest = IsReadyToPayRequest.fromJson(baseConfigurationJson().toString());
        Task<Boolean> task = paymentsClient.isReadyToPay(readyToPayRequest);
        task.addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if(task.isSuccessful()){
                    showGooglePlayButton(task.getResult());
                } else {
                    // handle the error accordingly
                }
            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static JSONObject getCardPaymentMethod() throws JSONException {
        final String[] networks = new String[] {"VISA", "AMEX"};
        final String[] authMethods = new String[] {"PAN_ONLY", "CRYPTOGRAM_3DS"};
        JSONObject card = new JSONObject();
        card.put("type", "CARD");
        card.put("tokenizationSpecification", getGatewayTokenizationSpecification());
        card.put("parameters", new JSONObject().put("allowedAuthMethods", new JSONArray(authMethods)).put("allowedCardNetworks", new JSONArray(networks)));
        return card;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadPaymentData() throws JSONException {
        final JSONObject paymentRequestJson = baseConfigurationJson();
        paymentRequestJson.put("transactionInfo", new JSONObject()
                    .put("totalPrice", "123.45")
                    .put("totalPriceStatus", "FINAL")
                    .put("currencyCode", "USD"));
        paymentRequestJson.put("merchantInfo", new JSONObject()
                    .put("merchantId", "0123456789")
                    .put("merchantName", "Example Merchant"));

        final PaymentDataRequest request = PaymentDataRequest.fromJson(paymentRequestJson.toString());
        AutoResolveHelper.resolveTask(paymentsClient.loadPaymentData(request),this, LOAD_PAYMENT_DATA_REQUEST_CODE);
    }

    private void showGooglePlayButton(boolean userIsReadyToPay){
        if(userIsReadyToPay){
            //show google pay button
        } else {
            //don't show
        }
    }

//    void payUsingUpi(  String name,String upiId, String note, String amount) {
//        Log.e("main ", "name "+name +"--up--"+upiId+"--"+ note+"--"+amount);
//        Uri uri = Uri.parse("upi://pay").buildUpon()
//                .appendQueryParameter("pa", upiId)
//                .appendQueryParameter("pn", name)
//                //.appendQueryParameter("mc", "")
//                //.appendQueryParameter("tid", "02125412")
//                //.appendQueryParameter("tr", "25584584")
//                .appendQueryParameter("tn", note)
//                .appendQueryParameter("am", amount)
//                .appendQueryParameter("cu", "INR")
//                //.appendQueryParameter("refUrl", "blueapp")
//                .build();
//        String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
//        int GOOGLE_PAY_REQUEST_CODE = 123;
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(uri);
//        intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
//        activity.startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);
//
////        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
////        upiPayIntent.setData(uri);
////
////        // will always show a dialog to user to choose an app
////        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
////
////        // check if intent resolves
////        if(null != chooser.resolveActivity(getPackageManager())) {
////            startActivityForResult(chooser, UPI_PAYMENT);
////        } else {
////            Toast.makeText(HandlePayment.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
////        }
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("main ", "response "+resultCode );
//        /*
//       E/main: response -1
//       E/UPI: onActivityResult: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
//       E/UPIPAY: upiPaymentDataOperation: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
//       E/UPI: payment successfull: 922118921612
//         */
//        switch (requestCode) {
//            case UPI_PAYMENT:
//                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
//                    if (data != null) {
//                        String trxt = data.getStringExtra("response");
//                        Log.e("UPI", "onActivityResult: " + trxt);
//                        ArrayList<String> dataList = new ArrayList<>();
//                        dataList.add(trxt);
//                        upiPaymentDataOperation(dataList);
//                    } else {
//                        Log.e("UPI", "onActivityResult: " + "Return data is null");
//                        ArrayList<String> dataList = new ArrayList<>();
//                        dataList.add("nothing");
//                        upiPaymentDataOperation(dataList);
//                    }
//                } else {
//                    //when user simply back without payment
//                    Log.e("UPI", "onActivityResult: " + "Return data is null");
//                    ArrayList<String> dataList = new ArrayList<>();
//                    dataList.add("nothing");
//                    upiPaymentDataOperation(dataList);
//                }
//                break;
//        }
//    }
//
//    private void upiPaymentDataOperation(ArrayList<String> data) {
//        if (isConnectionAvailable(HandlePayment.this)) {
//            String str = data.get(0);
//            Log.e("UPIPAY", "upiPaymentDataOperation: "+str);
//            String paymentCancel = "";
//            if(str == null) str = "discard";
//            String status = "";
//            String approvalRefNo = "";
//            String response[] = str.split("&");
//            for (int i = 0; i < response.length; i++) {
//                String equalStr[] = response[i].split("=");
//                if(equalStr.length >= 2) {
//                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
//                        status = equalStr[1].toLowerCase();
//                    }
//                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
//                        approvalRefNo = equalStr[1];
//                    }
//                }
//                else {
//                    paymentCancel = "Payment cancelled by user.";
//                }
//            }
//
//            if (status.equals("success")) {
//                //Code to handle successful transaction here.
//                Toast.makeText(HandlePayment.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
//                Log.e("UPI", "payment successfull: "+approvalRefNo);
//            }
//            else if("Payment cancelled by user.".equals(paymentCancel)) {
//                Toast.makeText(HandlePayment.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
//                Log.e("UPI", "Cancelled by user: "+approvalRefNo);
//
//            }
//            else {
//                Toast.makeText(HandlePayment.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
//                Log.e("UPI", "failed payment: "+approvalRefNo);
//
//            }
//        } else {
//            Log.e("UPI", "Internet issue: ");
//
//            Toast.makeText(HandlePayment.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public static boolean isConnectionAvailable(Context context) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivityManager != null) {
//            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
//            if (netInfo != null && netInfo.isConnected()
//                    && netInfo.isConnectedOrConnecting()
//                    && netInfo.isAvailable()) {
//                return true;
//            }
//        }
//        return false;
//    }


}
