package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.NavigationBarFragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Adapter.AdsListAdapter;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Adapter.IAdClickListener;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.AdsActivity;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data.Ad;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.R;


public class DetailsFragment extends Fragment {

    private static final String TAG = "DetailsFragment";
    private FirebaseFirestore db;
    private TextView detailTextTitle;
    private TextView detailTextShortDesc;
    private TextView detailTextLongDesc;
    private TextView detailTextPhoneNumber;
    private TextView detailTextLocation;
    private TextView visitors_number;
    private ImageView imageView;
    private Button hideBtn;
    private Button deleteBtn;
    //private List<Ad> adsList = new ArrayList<>();
    private AdsListAdapter adsListAdapter;
    private Ad adItem;

    public DetailsFragment() {

    }

    @SuppressLint("ValidFragment")
    public DetailsFragment(Ad adItem) {
        this.adItem = adItem;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        db = FirebaseFirestore.getInstance();

        detailTextTitle = view.findViewById(R.id.detail_title);
        detailTextShortDesc = view.findViewById(R.id.detail_shortDesc);
        detailTextLongDesc = view.findViewById(R.id.detail_LongDesc);
        detailTextPhoneNumber = view.findViewById(R.id.detail_phoneNum);
        detailTextLocation = view.findViewById(R.id.detail_location);
        imageView = view.findViewById(R.id.detail_image);
        visitors_number=view.findViewById(R.id.visitors_number);
        hideBtn=view.findViewById(R.id.hideBtn);
        deleteBtn=view.findViewById(R.id.deleteBtn);

        Log.d(TAG, "details ad: " + adItem.getTitle());

        Log.d(TAG, "before set");

        detailTextTitle.setText(adItem.getTitle());
        Log.d(TAG, "after setTitle");
        detailTextShortDesc.setText(adItem.getShortDesc());
        detailTextLongDesc.setText(adItem.getLongDesc());
        detailTextLocation.setText(adItem.getLocation());
        detailTextPhoneNumber.setText(adItem.getPhoneNumber());
        visitors_number.setText(adItem.getVisitedNumber());
        Glide.with(view).load(adItem.getImage()).into(imageView);
//        imageView.setImageDrawable(adItem.getImage().getDrawable());
        Log.d(TAG, "after set");
        //delete and hide functionality

        hideBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                db.collection("ads").document(adItem.getId()).update("visibilityRight", "-1");
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Title")
                        .setMessage("Do you really want to hide this add?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent=new Intent(getActivity(), AdsActivity.class);
                                startActivity(intent);
                            }})
                        .setNegativeButton("No", null).show();


            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("ads").document(adItem.getId()).update("visibilityRight", "-2");

            }
        });
        return view;
    }

    private void popUpDelete(View v)
    {
        new AlertDialog.Builder(v.getContext())
                .setTitle("Title")
                .setMessage("Do you really want to whatever?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent=new Intent(getActivity(), AdsActivity.class);
                        startActivity(intent);
                    }})
                .setNegativeButton("No", null).show();
    }


}


