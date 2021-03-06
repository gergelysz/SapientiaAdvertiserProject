package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject;

import android.app.ProgressDialog;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private EditText editTextLoginPhoneNumber, editTextCode;
    private Button buttonLogin;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;

    private String phoneNumber;
    private String mVerificationId;
    private String verificationCode;

    private FirebaseFirestore db;
    private ProgressDialog loadingBar;
    private ProgressBar progressBarGetCode;

    private String passFirstName, passLastName, passPhoneNumber;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Button buttonGetCode = findViewById(R.id.login_getcodebutton);
        buttonLogin = findViewById(R.id.login_button);

        editTextLoginPhoneNumber = findViewById(R.id.login_edittext_phone);
        editTextCode = findViewById(R.id.login_edittext_code);

        loadingBar = new ProgressDialog(this);

        editTextCode.setVisibility(View.INVISIBLE);
        buttonLogin.setVisibility(View.INVISIBLE);

        progressBarGetCode = findViewById(R.id.progressBarGetCode);
        progressBarGetCode.setVisibility(View.GONE);

        /*
         *   Átirányítás a regisztrációs Activity-re, ha nincs
         *   még fiókja a felhasználónak.
         */

        Button redirect = findViewById(R.id.login_redirect);
        redirect.setOnClickListener(v -> {
            Intent register = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(register);
        });

        buttonGetCode.setOnClickListener(v -> {
            phoneNumber = "+40" + editTextLoginPhoneNumber.getText().toString();

            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(MainActivity.this, "Kérem adja meg a telefonszámát!", Toast.LENGTH_SHORT).show();
            } else {

                progressBarGetCode.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

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
                        Log.d("Belépés", "-------- phoneNum doesn't exist -----------");
                        Log.d("Belépés", "-------- calling registration -----------");
                        Toast.makeText(MainActivity.this, "Nincs ez a szám regisztrálva!", Toast.LENGTH_SHORT).show();
                        progressBarGetCode.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    } else {

                        /*
                         *  Megkeressük a telefonszámot, majd a tulajdonosának nevét
                         */

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (Objects.requireNonNull(documentSnapshot.getString("phoneNumber")).equals(phoneNumber)) {
                                passPhoneNumber = documentSnapshot.getString("phoneNumber");
                                passFirstName = documentSnapshot.getString("firstName");
                                passLastName = documentSnapshot.getString("lastName");
                                userId = documentSnapshot.getId();
                                break;
                            }
                        }

                        Log.d("Belépés", "-------- phoneNum exists -----------");
                        Log.d("Belépés", "-------- sending login sms -----------");

                        /*
                         *   Ha már regisztrálva van a szám,
                         *   akkor csak küldünk egy ellenőrző kódot,
                         *   amivel már csak be kell lépjen felhasználó.
                         */


                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phoneNumber,
                                60,
                                TimeUnit.SECONDS,
                                MainActivity.this,
                                mCallbacks
                        );
                    }
                });
            }
        });

        buttonLogin.setOnClickListener(v -> {
            verificationCode = editTextCode.getText().toString();

            if (TextUtils.isEmpty(verificationCode)) {
                Toast.makeText(MainActivity.this, "Adja meg a kódot", Toast.LENGTH_SHORT).show();
            } else {

                loadingBar.setTitle("Sapientia Advertiser");
                loadingBar.setMessage("Kérem várjon, az ön által beírt kulcsot ellenőrizzük...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                signInWithPhoneAuthCredential(credential);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
//                loadingBar.dismiss();
                progressBarGetCode.setVisibility(View.GONE);

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
//                loadingBar.dismiss();
                progressBarGetCode.setVisibility(View.GONE);

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(MainActivity.this, "Sikertelen bejelentkezés!", Toast.LENGTH_SHORT).show();
                editTextCode.setVisibility(View.INVISIBLE);
                buttonLogin.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                progressBarGetCode.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                mVerificationId = verificationId;

                Toast.makeText(MainActivity.this, "A bejelentkezési kulcsot elküldtük...", Toast.LENGTH_SHORT).show();

                editTextCode.setVisibility(View.VISIBLE);
                buttonLogin.setVisibility(View.VISIBLE);
            }
        };

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        loadingBar.dismiss();
                        Toast.makeText(MainActivity.this, "Sikeresen belépett!", Toast.LENGTH_SHORT).show();

                        /*
                         *   Továbbadni a felhasználó nevét, telefonszámát
                         */

                        Intent intent = new Intent(MainActivity.this, AdsActivity.class);
                        intent.putExtra("USER_FIRSTNAME", passFirstName);
                        intent.putExtra("USER_LASTNAME", passLastName);
                        intent.putExtra("USER_PHONENUMBER", passPhoneNumber);
                        intent.putExtra("USER_ID", userId);
                        startActivity(intent);

                        finish();
                    } else {
                        String message = Objects.requireNonNull(task.getException()).toString();
                        Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
