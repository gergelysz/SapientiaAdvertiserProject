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
            }
        });

        recyclerView = view.findViewById(R.id.home_list_ads);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adsListAdapter);

        db = FirebaseFirestore.getInstance();

        db.collection("ads").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Ad ad = documentSnapshot.toObject(Ad.class);
                    adsList.add(ad);
                    adsListAdapter.notifyDataSetChanged();
                }
                //ArrayAdapter<Ad> adapter = new ArrayAdapter<>(getContext(), R.layout.list_item, adsList);
                //adapter.notifyDataSetChanged();
                //recyclerView.setAdapter(adapter);
            }
        });

        return view;
    }
}
