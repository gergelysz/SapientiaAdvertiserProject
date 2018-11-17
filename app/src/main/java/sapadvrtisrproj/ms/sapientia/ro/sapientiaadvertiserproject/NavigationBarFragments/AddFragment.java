package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.NavigationBarFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data.Ad;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.R;

import static android.app.Activity.RESULT_OK;

public class AddFragment extends Fragment {

    private FirebaseFirestore db;
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    private Button buttonAdd;

    private EditText editTextTitle;
    private EditText editTextShortDesc;
    private EditText editTextLongDesc;
    private EditText editTextPhoneNumber;
    private EditText editTextLocation;
    private Button btnChoose, btnUpload;
    private ImageView imageView;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;
    private String title, shortDesc, longDesc, phoneNum, location, image = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add, container, false);

        db = FirebaseFirestore.getInstance();

        //Initialize Views
        btnChoose = (Button) view.findViewById(R.id.btnChoose);
        btnUpload = (Button) view.findViewById(R.id.btnUpload);
        imageView = (ImageView) view.findViewById(R.id.imgView);
        editTextTitle = view.findViewById(R.id.edittext_addfragment_title);
        editTextShortDesc = view.findViewById(R.id.edittext_addfragment_short_desc);
        editTextLongDesc = view.findViewById(R.id.edittext_addfragment_long_desc);
        editTextPhoneNumber = view.findViewById(R.id.edittext_addfragment_phone_number);
        editTextLocation = view.findViewById(R.id.edittext_addfragment_location);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        buttonAdd = view.findViewById(R.id.button_addfragment_add);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title = editTextTitle.getText().toString();
                shortDesc = editTextShortDesc.getText().toString();
                longDesc = editTextLongDesc.getText().toString();
                phoneNum = editTextPhoneNumber.getText().toString();
                location = editTextLocation.getText().toString();

                Ad ad = new Ad(title, shortDesc, longDesc, phoneNum, location, image);
                db.collection("ads").add(ad).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getActivity(), "Advertisement feltöltve", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        return view;

    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                            image=filePath.toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });

        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
