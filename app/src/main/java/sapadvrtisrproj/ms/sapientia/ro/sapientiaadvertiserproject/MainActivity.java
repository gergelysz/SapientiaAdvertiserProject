package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject;

import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data.User;

public class MainActivity extends AppCompatActivity {

    private EditText editTextLoginPhoneNumber, editTextCode;
    private Button buttonGetCode, buttonLogin;

    private Button redirect;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;

    private FirebaseFirestore mFirestore;

    private String phoneNumber;
    private String mVerificationId;
    private String verificationCode;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private FirebaseFirestore db;
    private ProgressDialog loadingBar;
    private ProgressBar progressBarGetCode;

    private String passFirstName, passLastName, passPhoneNumber;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        /**
//         *   Ideiglenes megoldás nem Androidosoknak
//         */
//
//        User tempUser = new User("Asd", "Asd2", "+400700000000");
//        Intent intent = new Intent(MainActivity.this, AdsActivity.class);
////        startActivity(new Intent(MainActivity.this, AdsActivity.class));
//        //
//        // /szandekosan kimasoltam az userid-t firebasebol,
//        // hogy tudjam emulatoroon, ezzel az ideiglenes modszerrel tesztelni
//
//        userId = "lGfJnXym7eTh9JnR19JW";
//        intent.putExtra("USER_FIRSTNAME", tempUser.getFirstName());
//        intent.putExtra("USER_LASTNAME", tempUser.getLastName());
//        intent.putExtra("USER_PHONENUMBER", tempUser.getPhoneNumber());
//        intent.putExtra("USER_ID", userId);
//        startActivity(intent);
//        finish();


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mFirestore = FirebaseFirestore.getInstance();

        buttonGetCode = findViewById(R.id.login_getcodebutton);
        buttonLogin = findViewById(R.id.login_button);

        editTextLoginPhoneNumber = findViewById(R.id.login_edittext_phone);
        editTextCode = findViewById(R.id.login_edittext_code);

        loadingBar = new ProgressDialog(this);

        editTextCode.setVisibility(View.INVISIBLE);
        buttonLogin.setVisibility(View.INVISIBLE);

        progressBarGetCode = findViewById(R.id.progressBarGetCode);
        progressBarGetCode.setVisibility(View.GONE);

//        phoneNumber = editTextLoginPhoneNumber.getText().toString().trim();


        /*
         *   Átirányítás a regisztrációs Activity-re, ha nincs
         *   még fiókja a felhasználónak.
         */

        redirect = findViewById(R.id.login_redirect);
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


//                    loadingBar.setTitle("Sapientia Advertiser");
//                    loadingBar.setMessage("Kérem várjon, a telefonszámát ellenőrizzük...");
//                    loadingBar.setCanceledOnTouchOutside(false);
//                    loadingBar.show();


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
                // Save verification ID and resending token so we can use them later
//                loadingBar.dismiss();
                progressBarGetCode.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                mVerificationId = verificationId;
                mResendToken = token;

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

                        // Régi átirányítás
                        //startActivity(new Intent(MainActivity.this, AdsActivity.class));
                        finish();
                    } else {
                        // Sign in failed, display a message and update the UI
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                // The verification code entered was invalid
//                            }
                        String message = Objects.requireNonNull(task.getException()).toString();
                        Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
