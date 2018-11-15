package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.NavigationBarFragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.AdsActivity;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.MainActivity;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.R;


public class AccountFragment extends Fragment {
    private ImageView logout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        final View view =  inflater.inflate(R.layout.fragment_account, container, false);
        logout = view.findViewById(R.id.fragment_acc_logout_icon);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(getActivity(), MainActivity.class));

            }
        });
        // TODO ...

        return view;
    }
}
