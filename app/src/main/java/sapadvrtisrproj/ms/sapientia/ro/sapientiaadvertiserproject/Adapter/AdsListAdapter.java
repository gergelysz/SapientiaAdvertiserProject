package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data.Ad;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data.HomeFragmentLoadingNumber;
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

    public int itemNumber;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "create viewholder");
        itemNumber = i;
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        itemNumber = i;
        Log.d(TAG, "Set ad: " + adsList.get(i).getTitle() + " linkImage: " + adsList.get(i).getImage());
        db = FirebaseFirestore.getInstance();

        viewHolder.title.setText(adsList.get(i).getTitle());
        viewHolder.shortDesc.setText(adsList.get(i).getShortDesc());
        Glide.with(viewHolder.mView).load(adsList.get(i).getImage()).into(viewHolder.imageAd);
        String userId = HomeFragmentLoadingNumber.getUserId();
        db = FirebaseFirestore.getInstance();
        DocumentReference ads = db.collection("ads").document(adsList.get(i).getId());
        ads.get().addOnSuccessListener(documentSnapshot -> {
            Ad ad = documentSnapshot.toObject(Ad.class);
            try {
                if (ad.getUserId().equals(userId)) {
                    viewHolder.usersAd.setVisibility(View.VISIBLE);
                    viewHolder.layout.setBackgroundColor(Color.WHITE);
                    viewHolder.title.setTextColor(Color.parseColor("#003300"));
                    viewHolder.shortDesc.setTextColor(Color.parseColor("#003300"));
                    viewHolder.reportBtn.setVisibility(View.INVISIBLE);

                }
            } catch (NullPointerException exc) {

            }
        });
        viewHolder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "onclick");
            if (adClickListener != null) {
                Ad ad = adsList.get(viewHolder.getAdapterPosition());
                Log.d(TAG, ad.toString());
                adClickListener.onItemClickAction(ad);
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
        public ImageView imageAd;
        public TextView usersAd;
        public ImageButton reportBtn;
        public RelativeLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            title = mView.findViewById(R.id.ad_title);
            shortDesc = mView.findViewById(R.id.ad_shortDesc);
            imageAd = mView.findViewById(R.id.ad_image);
            reportBtn = mView.findViewById(R.id.reportBtn);
            usersAd = mView.findViewById(R.id.usersAd);
            String userId = HomeFragmentLoadingNumber.getUserId();

            layout = mView.findViewById(R.id.itemListed);


            reportBtn.setOnClickListener(v -> popUpCreate(v, "Do you want to report this ad?", "-3"));
        }

        private void popUpCreate(View v, String question, String hideOrDelete) {
            db = FirebaseFirestore.getInstance();
            DocumentReference adItem = db.collection("ads").document(adsList.get(itemNumber - 1).getId());
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
                    })
                    .setNegativeButton(android.R.string.no, null).show()
                    .show();
        }
    }


}
