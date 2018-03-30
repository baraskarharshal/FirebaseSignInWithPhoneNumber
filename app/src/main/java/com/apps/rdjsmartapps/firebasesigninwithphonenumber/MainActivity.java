package com.apps.rdjsmartapps.firebasesigninwithphonenumber;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText phoneNumber, secretCode;
    Button submit, verify;
    FirebaseAuth firebase;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    public static final String TAG = "MainActivity";
    String mVerificationId, mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        secretCode = (EditText) findViewById(R.id.secretCode);
        submit = (Button) findViewById(R.id.submit);
        verify = (Button) findViewById(R.id.verify);


        firebase = FirebaseAuth.getInstance();

        submit.setOnClickListener(this);
        verify.setOnClickListener(this);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                //mResendToken = token;

                // ...
            }
        };


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebase.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(getApplicationContext(), "Login successfull!", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = task.getResult().getUser();
                            finish();
                            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }



    // Verify phone number method

    void verifyPhoneNumber(String phNumber){
        Log.d(TAG, "Inside verifyPhoneNumber");

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }


    // Method verify code

    void verifyCode(String scCode){

        Log.d(TAG, "Inside verifyCode");
        if (TextUtils.isEmpty(mVerificationId)) {
            Toast.makeText(getApplicationContext(), "mVerificationId is null!", Toast.LENGTH_SHORT).show();
        }else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, scCode);
            signInWithPhoneAuthCredential(credential);
        }

    }

    // onClick method

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.submit){
            Log.d(TAG, "Inside onclick");
            String phNumber = phoneNumber.getText().toString();
            if(TextUtils.isEmpty(phNumber)){
                Toast.makeText(getApplicationContext(), "Invalid phone number!", Toast.LENGTH_SHORT).show();
            }
            else {
                verifyPhoneNumber(phNumber);
            }
        }

        if(view.getId() == R.id.verify){
            Log.d(TAG, "Inside onclick verify");
            String scCode = secretCode.getText().toString();
            if(TextUtils.isEmpty(scCode)){
                Toast.makeText(getApplicationContext(), "scCode variable is empty!", Toast.LENGTH_SHORT).show();
            }
            else {
                verifyCode(scCode);
            }


        }
    }
}
