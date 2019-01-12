package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.NavigationBarFragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Adapter.AdsListAdapter;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Adapter.IAdClickListener;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.AdsActivity;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data.Ad;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.PopUp;
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
    private String adId;
    private Button hideBtn;
    private Button deleteBtn;
    private Button appearBtn;
    private Button shareBtn;
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
        appearBtn=view.findViewById(R.id.appearBtn);
        shareBtn=view.findViewById(R.id.shareBtn);

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
        Log.d(TAG, "after set");


        // facebook share funcionality


        shareBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                facebookShare();
            }
        });

        //delete and hide functionality
        // if my userId is equal with the ad's user id I can delete or hide the ad, if not, the hide and delete buttons don't even appear
        DocumentReference ads = db.collection("ads").document(adItem.getId());
        ads.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Ad ad = documentSnapshot.toObject(Ad.class);
                if (ad.getUserId()!=null && adItem.getUserId()!=null){
                    if (ad.getUserId().equals(adItem.getUserId()))
                    {
                        if (ad.getVisibilityRight().equals("0")) {
                            hideBtn.setVisibility(View.VISIBLE);
                        }
                        if (ad.getVisibilityRight().equals("-1")){
                            appearBtn.setVisibility(View.VISIBLE);
                        }
                        deleteBtn.setVisibility(View.VISIBLE);
                        hideBtn.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick (View v){
                                // -1 means that I only want to hide the ad
                                hideBtn.setVisibility(View.INVISIBLE);
                                appearBtn.setVisibility(View.VISIBLE);
                                PopUp popUp=new PopUp(v, this);
                                    popUpCreate(v, "Do you want to hide the ad?", "-1");

                            }
                        });

                        deleteBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // -2 means that I want to delete the ad

                                    popUpCreate(v, "Do you want to delete the ad?", "-2");

                            }
                        });
                        appearBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                hideBtn.setVisibility(View.VISIBLE);
                                appearBtn.setVisibility(View.INVISIBLE);

                                    popUpCreate(v, "Do you want to appear the ad?", "0");

                            }
                        });
                    }
                }
            }
        });
        return view;
    }



    private void facebookShare(){
        String urlToShare = "https://play.google.com/store/apps/details?id=com.facebook.katana&hl=en";
        try {
            Intent mIntentFacebook = new Intent();
            mIntentFacebook.setClassName("com.facebook.katana", "com.facebook.composer.shareintent.ImplicitShareIntentHandlerDefaultAlias");
            mIntentFacebook.setAction("android.intent.action.SEND");
            mIntentFacebook.setType("text/plain");
            mIntentFacebook.putExtra("android.intent.extra.TEXT", urlToShare);
            startActivity(mIntentFacebook);
        } catch (Exception e) {
            e.printStackTrace();
            Intent mIntentFacebookBrowser = new Intent(Intent.ACTION_SEND);
            String mStringURL = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
            mIntentFacebookBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(mStringURL));
            startActivity(mIntentFacebookBrowser);
        }
    }

    private void popUpCreate(View v, String question, String hideOrDelete)
    {
        db = FirebaseFirestore.getInstance();
        DocumentReference adItem = db.collection("ads").document(adsList.get(itemNumber-1).getId());
        new AlertDialog.Builder(v.getContext(), R.style.MyDialogTheme)
                .setTitle("Title")
                .setMessage(question)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (adItem.getId()!=null) {
                            db.collection("ads").document(adItem.getId()).update("visibilityRight", hideOrDelete)

                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });
                        }


                    }})
                .setNegativeButton(android.R.string.no, null).show()
                .show();


    }

}


