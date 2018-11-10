package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data.Ad;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.R;

public class AdsListAdapter extends RecyclerView.Adapter<AdsListAdapter.ViewHolder> {

    public List<Ad> adsList;

    public AdsListAdapter(List<Ad> adsList) {
        this.adsList = adsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.title.setText(adsList.get(i).getTitle());
        viewHolder.shortDesc.setText(adsList.get(i).getShortDesc());
        viewHolder.longDesc.setText(adsList.get(i).getLongDesc());
        viewHolder.phoneNum.setText(adsList.get(i).getPhoneNumber());
        viewHolder.location.setText(adsList.get(i).getLocation());
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


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            title = mView.findViewById(R.id.ad_title);
            shortDesc = mView.findViewById(R.id.ad_shortDesc);
            longDesc = mView.findViewById(R.id.ad_LongDesc);
            phoneNum = mView.findViewById(R.id.ad_phoneNum);
            location = mView.findViewById(R.id.ad_location);
        }
    }
}
