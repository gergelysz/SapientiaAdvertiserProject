package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.AdsActivity;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data.Ad;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.R;

public class AdsListAdapter extends RecyclerView.Adapter<AdsListAdapter.ViewHolder> {

    private static final String TAG = "AdsListAdapter";
    public List<Ad> adsList;
    private IAdClickListener adClickListener;

    public AdsListAdapter(List<Ad> adsList, IAdClickListener adClickListener) {
        this.adsList = adsList;
        this.adClickListener = adClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        viewHolder.title.setText(adsList.get(i).getTitle());
        viewHolder.shortDesc.setText(adsList.get(i).getShortDesc());
        viewHolder.longDesc.setText(adsList.get(i).getLongDesc());
        viewHolder.phoneNum.setText(adsList.get(i).getPhoneNumber());
        viewHolder.location.setText(adsList.get(i).getLocation());
//        viewHolder.imageAd = adsList.get(i).getImage();
//        Glide.with(viewHolder.mView).load(viewHolder.image).into(viewHolder.imageAd);
//        viewHolder.image;

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
        public TextView longDesc;
        public TextView phoneNum;
        public TextView location;
        public String image;
//        public ImageView imageAd;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            title = mView.findViewById(R.id.detail_title);
            shortDesc = mView.findViewById(R.id.detail_shortDesc);
            longDesc = mView.findViewById(R.id.ad_LongDesc);
            phoneNum = mView.findViewById(R.id.detail_phoneNum);
            location = mView.findViewById(R.id.detail_location);
//            imageAd = mView.findViewById(R.id.detail_image);
        }
    }


}
