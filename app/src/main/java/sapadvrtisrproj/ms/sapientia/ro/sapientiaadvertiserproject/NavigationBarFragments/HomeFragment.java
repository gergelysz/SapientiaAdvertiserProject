package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.NavigationBarFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Adapter.AdsListAdapter;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.AdsActivity;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data.Ad;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.R;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data.UserHelper;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FirebaseFirestore db;
    private List<Ad> adsList = new ArrayList<>();
    private AdsListAdapter adsListAdapter;
    private String userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "before loading view");

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d(TAG, "after loading view");
        adsListAdapter = new AdsListAdapter(adsList, data -> {
            Log.d(TAG, "before detail");
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
          /*  int visitors = Integer.parseInt(data.getVisitedNumber());
            ++visitors;
            data.setVisitedNumber(String.valueOf(visitors));
            mDatabase.getReference().child("ads").child(data.getId()).setValue(data);*/
            AdsActivity ref = (AdsActivity) HomeFragment.this.getActivity();
            DetailsFragment detail = new DetailsFragment(data);
            Log.d(TAG, "after detail");

            assert ref != null;
            ref.loadFragment(detail);
            Log.d(TAG, "after load fragment");

            /*
             *   Updating the number of visitors
             */

            int visitors = Integer.parseInt(data.getVisitedNumber());
            ++visitors;
            data.setVisitedNumber(String.valueOf(visitors));
            Log.d(TAG, "Updating visitedNumber in database, new value: " + String.valueOf(visitors) + ", title: " + data.getTitle() + ", ID: " + data.getId());
            db.collection("ads").document(data.getId()).update("visitedNumber", String.valueOf(visitors));
        });


        AdsActivity ref = (AdsActivity) HomeFragment.this.getActivity();
        userId = "";
        assert ref != null;
        userId += ref.getUserId();
        UserHelper userHelper = new UserHelper(userId);
        userId = userHelper.getUserId();
        RecyclerView recyclerView = view.findViewById(R.id.home_list_ads);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adsListAdapter);
        Log.d(TAG, "recyclerview set");

        db = FirebaseFirestore.getInstance();

        db.collection("ads").addSnapshotListener((queryDocumentSnapshots, e) -> {
            adsList.clear();
            assert queryDocumentSnapshots != null;
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Log.d(TAG, "getting ad data...");

                Ad ad = documentSnapshot.toObject(Ad.class);
                assert ad != null;
                ad.setId(documentSnapshot.getId());
                // Here I will test if the ad is available for the actual user
                // if we didn't set up anything for the visibility right, it means that it is available
                // if we set up 0, it means that it is available
                if (ad.getVisibilityRight() == null || ad.getVisibilityRight().equals("0")) {
                    adsList.add(ad);
                    adsListAdapter.notifyDataSetChanged();
                } else {
                    // if visibility right is 0, it means that it is available just for that user, which created that ad
                    if (ad.getVisibilityRight().equals("-1")) {
                        if (userId != null) {
                            try {
                                // if the ad's user id is equal with user which is logged in, it means that the user can see the ad
                                if (ad.getUserId().equals(userId)) {
                                    adsList.add(ad);
                                    adsListAdapter.notifyDataSetChanged();
                                }
                            } catch (NullPointerException exc) {
                                // if we get a null exception error, it means that at userId in database we didn't set up anything
                                // I implemented that in that case the user can see the ad, but we can modify this
                                adsList.add(ad);
                                adsListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                Log.d(TAG, ad.getTitle() + " added to list and adapter notifydatasetchanged");
            }

        });

        return view;

    }

}