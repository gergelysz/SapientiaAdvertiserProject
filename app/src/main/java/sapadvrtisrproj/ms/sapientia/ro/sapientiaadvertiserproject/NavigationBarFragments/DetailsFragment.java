package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.NavigationBarFragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Adapter.AdsListAdapter;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Adapter.IAdClickListener;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data.Ad;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.R;


public class DetailsFragment  extends Fragment {

    private static final String TAG = "DetailsFragment";
    private TextView detailTextTitle;
    private TextView detailTextShortDesc;
    private TextView detailTextLongDesc;
    private TextView detailTextPhoneNumber;
    private TextView detailTextLocation;
    //private ImageView imageView;
    //private List<Ad> adsList = new ArrayList<>();
    private AdsListAdapter adsListAdapter;
    private Ad adItem;

    public DetailsFragment(){

    }

    @SuppressLint("ValidFragment")
    public DetailsFragment(Ad adItem)
    {
        this.adItem=adItem;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);


        detailTextTitle = view.findViewById(R.id.detail_title);
        detailTextShortDesc = view.findViewById(R.id.detail_shortDesc);
        detailTextLongDesc = view.findViewById(R.id.detail_LongDesc);
        detailTextPhoneNumber = view.findViewById(R.id.detail_phoneNum);
        detailTextLocation = view.findViewById(R.id.detail_location);
        //imageView = (ImageView) view.findViewById(R.id.detail_image);
        Log.d(TAG,"before set");
        detailTextTitle.setText(adItem.getTitle());
      detailTextShortDesc.setText(adItem.getShortDesc());
      /*  detailTextLongDesc.setText(adItem.getLongDesc());
        detailTextLocation.setText(adItem.getLocation());*/
        //imageView.setImageDrawable(adItem.getImage().getDrawable());
        Log.d(TAG, "after set");

        return view;
    }


}