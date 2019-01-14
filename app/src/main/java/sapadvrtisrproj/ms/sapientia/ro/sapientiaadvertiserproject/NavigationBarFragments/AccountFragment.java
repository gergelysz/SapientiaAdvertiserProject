package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.NavigationBarFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.AdsActivity;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data.User;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.MainActivity;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.R;

import static android.app.Activity.RESULT_OK;


public class AccountFragment extends Fragment {
    private ImageView changePicture;
    private EditText editFname;
    private EditText editLname;
    private EditText editEmail;
    private EditText editPhone;
    private EditText editAddress;
    private User user = new User();
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private Uri uri;
    private String pic = "";

    private final int PICK_IMAGE_REQUEST = 71;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String TAG = "AccountFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_account, container, false);

        editFname = view.findViewById(R.id.fragment_acc_edit_fname);
        editLname = view.findViewById(R.id.fragment_acc_edit_lname);
        editEmail = view.findViewById(R.id.fragment_acc_edit_email);
        editPhone = view.findViewById(R.id.fragment_acc_edit_phone);
        editAddress = view.findViewById(R.id.fragment_acc_edit_address);
        ImageView saveBtn = view.findViewById(R.id.fragment_acc_save);
        ImageView logout = view.findViewById(R.id.fragment_acc_logout_icon);
        changePicture = view.findViewById(R.id.fragment_acc_picture);

        AdsActivity ref = (AdsActivity) AccountFragment.this.getActivity();
        assert ref != null;
        final String userId = ref.getUserId();
        Log.d(TAG, "userid " + userId);

        FirebaseStorage storage;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        db = FirebaseFirestore.getInstance();

        DocumentReference documentReference = db.collection("users").document(userId);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            user = documentSnapshot.toObject(User.class);
            /*
                azert nezunk telefonszamot, mert egeszen biztosan kell legyen ilyen mezo
             */
            assert user != null;
            if (user.getPhoneNumber() != null) {
                if (user.getFirstName() != null) {
                    editFname.setText(user.getFirstName());
                }
                if (user.getLastName() != null) {
                    editLname.setText(user.getLastName());
                }
                if (user.getEmail() != null) {
                    editEmail.setText(user.getEmail());
                }
                if (user.getPhoneNumber() != null) {
                    editPhone.setText(user.getPhoneNumber());
                }
                if (user.getAddress() != null) {
                    editAddress.setText(user.getAddress());
                }
                if (user.getImageURL() != null) {
                    Glide.with(view).load(user.getImageURL()).into(changePicture);
                }
            }
        });

        logout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        changePicture.setOnClickListener(v -> {
            chooseImage();
//            uploadImage();
        });

        saveBtn.setOnClickListener(v -> {
            user.setFirstName(editFname.getText().toString());
            user.setLastName(editLname.getText().toString());
            user.setPhoneNumber(editPhone.getText().toString());
            user.setAddress(editAddress.getText().toString());
            user.setEmail(editEmail.getText().toString());
            user.setImageURL(pic);

            db.collection("users").document(userId).set(user).addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Adatok sikeresen frissítve", Toast.LENGTH_SHORT).show());

            db.collection("users").document(userId).set(user).addOnFailureListener(e -> Toast.makeText(getActivity(), "Az adatokat nem sikerült feltölteni", Toast.LENGTH_SHORT).show());
        });

        return view;
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Válassz képet"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getApplicationContext().getContentResolver(), uri);
                changePicture.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            assert extras != null;
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            changePicture.setImageBitmap(imageBitmap);
        }
        uploadImage();
    }

    private void uploadImage() {

        if (uri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Feltöltés...");
            progressDialog.show();

            StorageReference ref = storageReference.child("imageURL" + UUID.randomUUID().toString());
            ref.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "A kép sikeresen feltöltve", Toast.LENGTH_SHORT).show();
                        ref.getDownloadUrl().addOnSuccessListener(uri -> pic = uri.toString());
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Feltöltés sikertelen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Feltöltve " + (int) progress + "%");
                    });
        }
    }
}
