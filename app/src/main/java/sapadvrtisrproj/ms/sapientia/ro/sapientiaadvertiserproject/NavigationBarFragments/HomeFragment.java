package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.NavigationBarFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Adapter.AdsListAdapter;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Adapter.IAdClickListener;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.AdsActivity;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data.Ad;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.R;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private List<Ad> adsList = new ArrayList<>();
    private AdsListAdapter adsListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "before loading view");

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Log.d(TAG, "after loading view");

        adsListAdapter = new AdsListAdapter(adsList, new IAdClickListener() {
            @Override
            public void onItemClickAction(Ad data) {

                Log.d(TAG, "before detail");
                DetailsFragment detail = new DetailsFragment(data);
                Log.d(TAG, "after detail");
                AdsActivity ref = (AdsActivity) HomeFragment.this.getActivity();
                ref.loadFragment(detail);
                Log.d(TAG, "after load fragment");

                /**
                 *   Updating the number of visitors
                 */

                int visitors = Integer.parseInt(data.getVisitedNumber());
                ++visitors;
                data.setVisitedNumber(String.valueOf(visitors));
                Log.d(TAG, "Updating visitedNumber in database, new value: " + String.valueOf(visitors) + ", title: " + data.getTitle() + ", ID: " + data.getId());
                db.collection("ads").document(data.getId()).update("visitedNumber", String.valueOf(visitors));
            }
        });

        recyclerView = view.findViewById(R.id.home_list_ads);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adsListAdapter);
        Log.d(TAG, "recyclerview set");

        db = FirebaseFirestore.getInstance();

        db.collection("ads").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {


                    Log.d(TAG, "getting ad data...");
                    Ad ad = documentSnapshot.toObject(Ad.class);
                    ad.setId(documentSnapshot.getId());
                    //ad.setVisibilityRight("0");
                    if (ad.getVisibilityRight()==null || ad.getVisibilityRight().equals("0")) {
                        adsList.add(ad);
                        adsListAdapter.notifyDataSetChanged();
                    }
                    Log.d(TAG, ad.getTitle() + " added to list and adapter notifydatasetchanged");



                }

                //ArrayAdapter<Ad> adapter = new ArrayAdapter<>(getContext(), R.layout.list_item, adsList);
                //adapter.notifyDataSetChanged();
                //recyclerView.setAdapter(adapter);
            }
        });

        return view;
    }
}
