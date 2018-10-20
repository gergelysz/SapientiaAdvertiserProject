package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data.User;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextPhoneNum;
    private EditText editTextCode;

    private Button buttonGetCodeRegistration;
    private Button buttonSignUp;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;

    private FirebaseFirestore db;
    private CollectionReference dbPhoneNumbers;
    private DocumentReference documentReference;

    private String phoneNumber;
    private String mVerificationId;
    private String verificationCode;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private String firstName;
    private String lastName;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        editTextFirstName = findViewById(R.id.register_editText_firstName);
        editTextLastName = findViewById(R.id.register_editText_lastName);
        editTextPhoneNum = findViewById(R.id.register_editText_phoneNumber);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        editTextCode = findViewById(R.id.register_edittext_code);
        buttonSignUp = findViewById(R.id.register_button);

        editTextCode.setVisibility(View.INVISIBLE);
        buttonSignUp.setVisibility(View.INVISIBLE);

        buttonGetCodeRegistration = findViewById(R.id.register_button_getcodebutton);

        buttonGetCodeRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = editTextPhoneNum.getText().toString();
                firstName = editTextFirstName.getText().toString();
                lastName = editTextLastName.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(RegistrationActivity.this, "Kérem adja meg a telefonszámát!", Toast.LENGTH_SHORT).show();
                } else {
//                    loadingBar.setTitle("Sapientia Advertiser");
//                    loadingBar.setMessage("Kérem várjon, a telefonszámát ellenőrizzük...");
//                    loadingBar.setCanceledOnTouchOutside(false);
//                    loadingBar.show();

                    /**
                     *   Csatlakozás az adatbázis 'users' nevű collection-jára,
                     *   azon belül egy lekérdezés, amelyben keresünk még ugyanolyan
                     *   telefonszámokat az adatbázisban, majd regisztráljuk ha nincs vagy
                     *   értesítjük a felhasználót, hogy az a telefonszám már regisztrálva van.
                     */

                    CollectionReference collectionReference = db.collection("users");
                    Query query = collectionReference.whereEqualTo("phoneNumber", phoneNumber);

                    query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.isEmpty()) {
                                Log.d("Regisztráció", "-------- phoneNum doesn't exist -----------");
                                Log.d("Regisztráció", "-------- registering -----------");

                                /**
                                 *   Ha még nincs regisztrálva a telefonszám,
                                 *   akkor küldünk sms-ben egy ellenőrző kódot,
                                 *   amivel a felhasználó véglegesítheti a
                                 *   regisztrációját.
                                 */

                                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                        phoneNumber,
                                        60,
                                        TimeUnit.SECONDS,
                                        RegistrationActivity.this,
                                        mCallbacks
                                );
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Ez a szám már regisztrálva van!", Toast.LENGTH_SHORT).show();
                                Log.d("Regisztráció", "-------- phoneNum exists -----------");
                                Log.d("Regisztráció", "-------- sending login sms -----------");


                            }
                        }
                    });


                }
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificationCode = editTextCode.getText().toString();

                if (TextUtils.isEmpty(verificationCode)) {
                    Toast.makeText(RegistrationActivity.this, "Adja meg a kódot", Toast.LENGTH_SHORT);
                } else {

//                    loadingBar.setTitle("Sapientia Advertiser");
//                    loadingBar.setMessage("Kérem várjon, az ön által beírt kulcsot ellenőrizzük...");
//                    loadingBar.setCanceledOnTouchOutside(false);
//                    loadingBar.show();

//                    dbPhoneNumbers = db.collection("users");
//                    User newUser = new User(
//                            firstName,
//                            lastName,
//                            phoneNumber
//                    );
//
//                    dbPhoneNumbers.add(newUser)
//                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                @Override
//                                public void onSuccess(DocumentReference documentReference) {
//                                    Toast.makeText(RegistrationActivity.this, "Új felhasználó hozzáadva!", Toast.LENGTH_LONG);
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_LONG);
//                        }
//                    });

                    // TBD

//                    Map<String, Object> map = new HashMap<>();
//                    map.put("firstName", firstName);
//                    map.put("lastName", lastName);
//                    map.put("phoneNumber", phoneNumber);
//
//                    db.collection("users").document().set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(getApplicationContext(), "successfull", Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    });

                    //PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    //signInWithPhoneAuthCredential(credential);
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
//                loadingBar.dismiss();
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                // Old
//                Map<String, String> map = new HashMap<>();
//                map.put("firstName", firstName);
//                map.put("lastName", lastName);
//                map.put("phoneNumber", phoneNumber);

//                db.collection("users").document().set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
////                            Toast.makeText(getApplicationContext(), "successfull", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });


                // New
                dbPhoneNumbers = db.collection("users");
                User newUser = new User(
                        firstName,
                        lastName,
                        phoneNumber
                );

                dbPhoneNumbers.add(newUser)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(RegistrationActivity.this, "Új felhasználó hozzáadva!", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_LONG);
                    }
                });


                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
//                loadingBar.dismiss();
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(RegistrationActivity.this, "Sikertelen regisztrálás!", Toast.LENGTH_SHORT).show();
                editTextCode.setVisibility(View.INVISIBLE);
                buttonSignUp.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // Save verification ID and resending token so we can use them later
//                loadingBar.dismiss();
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                mVerificationId = verificationId;
                mResendToken = token;

                Toast.makeText(RegistrationActivity.this, "A bejelentkezési kulcsot elküldtük...", Toast.LENGTH_SHORT).show();

                editTextCode.setVisibility(View.VISIBLE);
                buttonSignUp.setVisibility(View.VISIBLE);
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            loadingBar.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Sikeresen belépett!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegistrationActivity.this, AdvertsActivity.class));
                            finish();
                        } else {
                            // Sign in failed, display a message and update the UI
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                // The verification code entered was invalid
//                            }
                            String message = task.getException().toString();
                            Toast.makeText(RegistrationActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
