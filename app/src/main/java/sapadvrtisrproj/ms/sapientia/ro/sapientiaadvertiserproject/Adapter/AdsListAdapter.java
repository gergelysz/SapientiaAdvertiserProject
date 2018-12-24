package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data.Ad;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.R;

public class AdsListAdapter extends RecyclerView.Adapter<AdsListAdapter.ViewHolder> {

    private static final String TAG = "AdsListAdapter";
    public List<Ad> adsList;
    private IAdClickListener adClickListener;
    private FirebaseFirestore db;
    public AdsListAdapter(List<Ad> adsList, IAdClickListener adClickListener) {
        this.adsList = adsList;
        this.adClickListener = adClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "create viewholder");
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        Log.d(TAG, "Set ad: " + adsList.get(i).getTitle() + " linkImage: " + adsList.get(i).getImage());
        db = FirebaseFirestore.getInstance();
        /*DocumentReference ads = db.collection("ads").document(adsList.get(i).getId());
        ads.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
             @Override
              public void onSuccess(DocumentSnapshot documentSnapshot) {
                 Ad ad = documentSnapshot.toObject(Ad.class);
                 if (ad.getUserId()!=null && adsList.get(i).getUserId()!=null) {
                     if (ad.getUserId().equals(adsList.get(i).getUserId())) {
                         // actualUser = viewHolder.mView.findViewById(actualUser);
                     }
                 }
             }

        });*/
        viewHolder.title.setText(adsList.get(i).getTitle());
        viewHolder.shortDesc.setText(adsList.get(i).getShortDesc());
//        viewHolder.longDesc.setText(adsList.get(i).getLongDesc());
        viewHolder.phoneNum.setText(adsList.get(i).getPhoneNumber());
        viewHolder.location.setText(adsList.get(i).getLocation());
//        viewHolder.imageAd = adsList.get(i).getImage();
//        Glide.with(viewHolder.mView).load(viewHolder.image).into(viewHolder.imageAd);
        Glide.with(viewHolder.mView).load(adsList.get(i).getImage()).into(viewHolder.imageAd);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onclick");
                if (adClickListener != null) {
                    Ad ad = adsList.get(viewHolder.getAdapterPosition());
                    Log.d(TAG, ad.toString());
                    adClickListener.onItemClickAction(ad);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return adsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public TextView title;
        public TextView shortDesc;
//        public TextView longDesc;
        public TextView phoneNum;
        public TextView location;
//        public String image;
        public ImageView imageAd;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            title = mView.findViewById(R.id.ad_title);
            shortDesc = mView.findViewById(R.id.ad_shortDesc);
//            longDesc = mView.findViewById(R.id.ad_longDesc);
            phoneNum = mView.findViewById(R.id.ad_phoneNum);
            location = mView.findViewById(R.id.ad_location);
            imageAd = mView.findViewById(R.id.ad_image);
        }
    }


}
