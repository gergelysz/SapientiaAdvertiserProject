package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.NavigationBarFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.AdsActivity;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data.Ad;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.R;

import static android.app.Activity.RESULT_OK;

public class AddFragment extends Fragment {

    private FirebaseFirestore db;
    private StorageReference storageReference;
    private EditText editTextTitle;
    private EditText editTextShortDesc;
    private EditText editTextLongDesc;
    private EditText editTextPhoneNumber;
    private EditText editTextLocation;
    private ImageView imageView;
    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;
    private String title, shortDesc, longDesc, phoneNum, location, image = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add, container, false);

        db = FirebaseFirestore.getInstance();

        //Initialize Views
        Button btnUpload = view.findViewById(R.id.btnUpload);
        Button btnChoose = view.findViewById(R.id.btnChoose);
        Button buttonAdd = view.findViewById(R.id.button_addfragment_add);

        imageView = view.findViewById(R.id.imgView);

        editTextTitle = view.findViewById(R.id.edittext_addfragment_title);
        editTextShortDesc = view.findViewById(R.id.edittext_addfragment_short_desc);
        editTextLongDesc = view.findViewById(R.id.edittext_addfragment_long_desc);
        editTextPhoneNumber = view.findViewById(R.id.edittext_addfragment_phone_number);
        editTextLocation = view.findViewById(R.id.edittext_addfragment_location);
        // get storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // saving the ad's information
        buttonAdd.setOnClickListener(v -> {

            title = editTextTitle.getText().toString();
            shortDesc = editTextShortDesc.getText().toString();
            longDesc = editTextLongDesc.getText().toString();
            phoneNum = editTextPhoneNumber.getText().toString();
            location = editTextLocation.getText().toString();
            AdsActivity ref = (AdsActivity) AddFragment.this.getActivity();
            assert ref != null;
            String userId = ref.getUserId();
            if (!title.equals("")) {
                Ad ad = new Ad(userId, title, shortDesc, longDesc, phoneNum, location, "0", image, "0");
                db.collection("ads").add(ad).addOnSuccessListener(documentReference -> Toast.makeText(getActivity(), "Advertisement feltöltve", Toast.LENGTH_LONG).show());
                //  reload ads list
                Fragment homeFragment = new HomeFragment();
                assert getFragmentManager() != null;
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, homeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                editTextTitle.setError("A cím kitöltése kötelező");
                editTextTitle.setHint("Töltse ki a cím mezőt");
            }
        });
        // onclick listener to uploading image
        btnChoose.setOnClickListener(v -> chooseImage());

        btnUpload.setOnClickListener(v -> uploadImage());

        return view;
    }

    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Feltöltés...");
            progressDialog.show();

            /*
             *   Random ID a képnek
             */

            String imageId = UUID.randomUUID().toString();

            final StorageReference ref = storageReference.child("images/" + imageId);
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Feltöltve!", Toast.LENGTH_SHORT).show();
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {

                            /* a feltöltött kép letöltési linkjének tárolása az 'image'
                             * változóba, amelyet az 'Advert' feltöltésével tárolunk az adatbázisban
                             */

                            image = uri.toString();
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Feltöltés sikertelen:  " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Feltöltve " + (int) progress + "%");
                    });
        }
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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getApplicationContext().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
