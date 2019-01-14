package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data.User;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextPhoneNum;
    private EditText editTextCode;

    private Button buttonSignUp;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;

    private FirebaseFirestore db;
    private CollectionReference dbPhoneNumbers;

    private String phoneNumber;
    private String mVerificationId;
    private String verificationCode;

    private String firstName;
    private String lastName;

    private ProgressBar progressBar;

    @SuppressLint("ShowToast")
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

        Button buttonGetCodeRegistration = findViewById(R.id.register_button_getcodebutton);

        buttonGetCodeRegistration.setOnClickListener(v -> {

            phoneNumber = editTextPhoneNum.getText().toString();
            phoneNumber = "+40" + phoneNumber;
            firstName = editTextFirstName.getText().toString();
            lastName = editTextLastName.getText().toString();

            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(RegistrationActivity.this, "Kérem adja meg a telefonszámát!", Toast.LENGTH_SHORT).show();
            } else {
                /*
                 *   Csatlakozás az adatbázis 'users' nevű collection-jára,
                 *   azon belül egy lekérdezés, amelyben keresünk még ugyanolyan
                 *   telefonszámokat az adatbázisban, majd regisztráljuk ha nincs vagy
                 *   értesítjük a felhasználót, hogy az a telefonszám már regisztrálva van.
                 */

                CollectionReference collectionReference = db.collection("users");
                Query query = collectionReference.whereEqualTo("phoneNumber", phoneNumber);

                query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("Regisztráció", "-------- phoneNum doesn't exist -----------");
                        Log.d("Regisztráció", "-------- registering -----------");

                        /*
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
                });
            }
        });

        buttonSignUp.setOnClickListener(v -> {
            verificationCode = editTextCode.getText().toString();

            if (TextUtils.isEmpty(verificationCode)) {
                Toast.makeText(RegistrationActivity.this, "Adja meg a kódot", Toast.LENGTH_SHORT);
            } else {
                dbPhoneNumbers = db.collection("users");
                User newUser = new User(
                        firstName,
                        lastName,
                        phoneNumber
                );

                dbPhoneNumbers.add(newUser)
                        .addOnSuccessListener(documentReference -> Toast.makeText(RegistrationActivity.this, "Új felhasználó hozzáadva!", Toast.LENGTH_LONG).show())
                        .addOnFailureListener(e -> Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_LONG));

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                signInWithPhoneAuthCredential(credential);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                dbPhoneNumbers = db.collection("users");
                User newUser = new User(
                        firstName,
                        lastName,
                        phoneNumber
                );

                dbPhoneNumbers.add(newUser)
                        .addOnSuccessListener(documentReference -> Toast.makeText(RegistrationActivity.this, "Új felhasználó hozzáadva!", Toast.LENGTH_LONG).show())
                        .addOnFailureListener(e -> Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_LONG));


                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(RegistrationActivity.this, "Sikertelen regisztrálás!", Toast.LENGTH_SHORT).show();
                editTextCode.setVisibility(View.INVISIBLE);
                buttonSignUp.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                mVerificationId = verificationId;

                Toast.makeText(RegistrationActivity.this, "A bejelentkezési kulcsot elküldtük...", Toast.LENGTH_SHORT).show();

                editTextCode.setVisibility(View.VISIBLE);
                buttonSignUp.setVisibility(View.VISIBLE);
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegistrationActivity.this, "Sikeresen belépett!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistrationActivity.this, AdsActivity.class));
                        finish();
                    } else {
                        String message = Objects.requireNonNull(task.getException()).toString();
                        Toast.makeText(RegistrationActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
