package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.NavigationBarFragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Adapter.AdsListAdapter;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.AdsActivity;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data.Ad;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data.UserHelper;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.R;


public class DetailsFragment extends Fragment {

    private static final String TAG = "DetailsFragment";
    private FirebaseFirestore db;
    private String adId;
    private ImageButton hideBtn;
    private ImageButton deleteBtn;
    private ImageButton appearBtn;
    //private List<Ad> adsList = new ArrayList<>();
    private AdsListAdapter adsListAdapter;
    private Ad adItem;
    private String userId = "";

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

        TextView detailTextTitle = view.findViewById(R.id.detail_title);
        TextView detailTextShortDesc = view.findViewById(R.id.detail_shortDesc);
        TextView detailTextLongDesc = view.findViewById(R.id.detail_LongDesc);
        TextView detailTextPhoneNumber = view.findViewById(R.id.detail_phoneNum);
        TextView detailTextLocation = view.findViewById(R.id.detail_location);
        ImageView imageView = view.findViewById(R.id.detail_image);
        TextView visitors_number = view.findViewById(R.id.visitors_number);
        hideBtn = view.findViewById(R.id.hideBtn);
        deleteBtn = view.findViewById(R.id.deleteBtn);
        appearBtn = view.findViewById(R.id.appearBtn);
        ImageButton shareBtn = view.findViewById(R.id.shareBtn);

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

        AdsActivity ref = (AdsActivity) DetailsFragment.this.getActivity();
        userId = "";
        assert ref != null;
        userId += ref.getUserId();
        UserHelper userHelper = new UserHelper(userId);
        userId = userHelper.getUserId();
        /*
        facebook share funcionality
        */

        shareBtn.setOnClickListener(v -> facebookShare());
        /*
        delete and hide functionality
        if my userId is equal with the ad's user id I can delete or hide the ad, if not, the hide and delete buttons don't even appear
        */

        DocumentReference ads = db.collection("ads").document(adItem.getId());
        ads.get().addOnSuccessListener(documentSnapshot -> {
            Ad ad = documentSnapshot.toObject(Ad.class);
            if (ad.getUserId() != null) {
                if (ad.getUserId().equals(userId)) {
                    if (ad.getVisibilityRight().equals("0")) {
                        hideBtn.setVisibility(View.VISIBLE);
                    }
                    if (ad.getVisibilityRight().equals("-1")) {
                        appearBtn.setVisibility(View.VISIBLE);
                    }
                    deleteBtn.setVisibility(View.VISIBLE);
                    hideBtn.setOnClickListener(v -> {
                        // -1 means that I only want to hide the ad
                        hideBtn.setVisibility(View.INVISIBLE);
                        appearBtn.setVisibility(View.VISIBLE);
                        popUpCreate(v, "Do you want to hide the ad?", "-1");

                    });

                    deleteBtn.setOnClickListener(v -> {
                        // -2 means that I want to delete the ad

                        popUpCreate(v, "Do you want to delete the ad?", "-2");

                    });
                    appearBtn.setOnClickListener(v -> {
                        hideBtn.setVisibility(View.VISIBLE);
                        appearBtn.setVisibility(View.INVISIBLE);

                        popUpCreate(v, "Do you want to appear the ad?", "0");

                    });
                }
            }
        });
        return view;
    }

    private void popUpCreate(View v, String question, String hideOrDelete) {
        new AlertDialog.Builder(v.getContext(), R.style.MyDialogTheme)
                .setTitle("Title")
                .setMessage(question)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    if (adItem.getId() != null) {
                        db.collection("ads").document(adItem.getId()).update("visibilityRight", hideOrDelete)

                                .addOnSuccessListener(aVoid -> {

                                });
                    }
                    Intent intent = new Intent(getActivity(), AdsActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton(android.R.string.no, null).show()
                .show();


    }


    private void facebookShare() {
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


}


