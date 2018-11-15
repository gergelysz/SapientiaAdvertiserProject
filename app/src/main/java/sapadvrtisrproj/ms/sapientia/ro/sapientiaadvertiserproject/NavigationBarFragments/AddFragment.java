package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.NavigationBarFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data.Ad;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.R;

public class AddFragment extends Fragment {

    private FirebaseFirestore db;

    private Button buttonAdd;

    private EditText editTextTitle;
    private EditText editTextShortDesc;
    private EditText editTextLongDesc;
    private EditText editTextPhoneNumber;
    private EditText editTextLocation;

    private String title, shortDesc, longDesc, phoneNum, location, image = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add, container, false);

        db = FirebaseFirestore.getInstance();

        editTextTitle = view.findViewById(R.id.edittext_addfragment_title);
        editTextShortDesc = view.findViewById(R.id.edittext_addfragment_short_desc);
        editTextLongDesc = view.findViewById(R.id.edittext_addfragment_long_desc);
        editTextPhoneNumber = view.findViewById(R.id.edittext_addfragment_phone_number);
        editTextLocation = view.findViewById(R.id.edittext_addfragment_location);



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

        return view;

    }
}
