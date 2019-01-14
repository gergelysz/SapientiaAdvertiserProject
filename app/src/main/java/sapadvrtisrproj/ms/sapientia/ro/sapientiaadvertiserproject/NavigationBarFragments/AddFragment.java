package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.NavigationBarFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.util.Log;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
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
    private Uri photoURI;
    private final int PICK_IMAGE_REQUEST = 71;
    private String title, shortDesc, longDesc, phoneNum, location, image = "";


    static final int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add, container, false);

        db = FirebaseFirestore.getInstance();

        //Initialize Views
        //Button btnUpload = view.findViewById(R.id.btnUpload);
        Button btnChoose = view.findViewById(R.id.btnChoose);
        Button btnCamera=view.findViewById(R.id.btnCamera);
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

     //   btnUpload.setOnClickListener(v -> uploadImage());

        btnCamera.setOnClickListener(v->
                {takePhoto();
                }
        );

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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);


            }
        uploadImage();
    }


    static final int REQUEST_TAKE_PHOTO = 1;
    public void takePhoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       // takePictureIntent.setType("image/*");
        if (takePictureIntent.resolveActivity(this.getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this.getActivity(),
                        "com.example.android.fileprovider",
                        photoFile);
                filePath=photoURI;

              //  takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
               // startActivityForResult(Intent.createChooser(takePictureIntent, "Válassz képet"), REQUEST_TAKE_PHOTO);
               startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }

    }

    String mCurrentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        galleryAddPic();
        return imageFile;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.getActivity().sendBroadcast(mediaScanIntent);
    }


    private Uri getImageUri(Context context, Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);

        return Uri.parse(path);

    }
}
