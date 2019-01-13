package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.NavigationBarFragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.AdsActivity;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data.User;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.MainActivity;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.R;

import static android.app.Activity.RESULT_OK;


public class AccountFragment extends Fragment {
    private ImageView logout;
    private ImageView changePicture;
    private ImageView saveBtn;
    private EditText editFname;
    private EditText editLname;
    private EditText editEmail;
    private EditText editPhone;
    private EditText editAddress;
    private User user = new User();
    private FirebaseFirestore db;

    public static final int GET_FROM_GALLERY = 1;
    public static final String TAG = "AccountFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        final View view =  inflater.inflate(R.layout.fragment_account, container, false);

        AdsActivity ref = (AdsActivity) AccountFragment.this.getActivity();
        final String userId = ref.getUserId();
        Log.d(TAG,"userid " + userId);


        db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("users").document(userId);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            user = documentSnapshot.toObject(User.class);
            Log.d(TAG, "proba " + user.getLastName() + " " + user.getFirstName());

            /*
                azert nezunk telefonszamot, mert egeszen biztosan kell legyen ilyen mezo
             */

            if(user.getPhoneNumber() != null){
                editFname = view.findViewById(R.id.fragment_acc_edit_fname);
                if(user.getFirstName() != null)
                {
                    editFname.setText(user.getFirstName());
                }
                editLname = view.findViewById(R.id.fragment_acc_edit_lname);
                if(user.getLastName() != null)
                {
                    editLname.setText(user.getLastName());
                }
                editEmail = view.findViewById(R.id.fragment_acc_edit_email);
                if(user.getEmail() != null)
                {
                    editEmail.setText(user.getEmail());
                }
                editPhone = view.findViewById(R.id.fragment_acc_edit_phone);
                if(user.getPhoneNumber() != null)
                {
                    editPhone.setText(user.getPhoneNumber());
                }
                editAddress = view.findViewById(R.id.fragment_acc_edit_address);
                if(user.getAddress() != null)
                {
                    editAddress.setText(user.getAddress());
                }
            }
        });

//        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
//
//            user.setFirstName(documentSnapshot.getString("firstName"));
//            user.setLastName(documentSnapshot.getString("lastName"));
//            user.setAddress(documentSnapshot.getString("address"));
//            user.setEmail(documentSnapshot.getString("email"));
//            user.setImageURL(documentSnapshot.getString("imageURL"));
//            user.setPhoneNumber(documentSnapshot.getString("phoneNumber"));
//
//            Log.d(TAG, "uclass1 " + user.getFirstName() + " " + user.getLastName());
//        });



        saveBtn = view.findViewById(R.id.fragment_acc_save);

        logout = view.findViewById(R.id.fragment_acc_logout_icon);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(getActivity(), MainActivity.class));

            }
        });

        changePicture = view.findViewById(R.id.fragment_acc_picture);
        changePicture.setOnClickListener(v -> {
           //startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), GET_FROM_GALLERY);

        });

//        changePicture = view.findViewById(R.id.fragment_acc_picture);
//        changePicture.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//               startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
//            }
//        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setFirstName(editFname.getText().toString());
                user.setLastName(editLname.getText().toString());
                user.setPhoneNumber(editPhone.getText().toString());
                user.setAddress(editAddress.getText().toString());
                user.setAddress(editEmail.getText().toString());

                db.collection("users").document(userId).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getActivity(), "Adatok sikeresen frissítve", Toast.LENGTH_SHORT).show();
                    }
                });

                db.collection("users").document(userId).set(user).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Az adatokat nem sikerült feltölteni", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        // TODO ...

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_FROM_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), uri);
                changePicture.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
