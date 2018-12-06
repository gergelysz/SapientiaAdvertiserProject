package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.NavigationBarFragments.AccountFragment;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.NavigationBarFragments.AddFragment;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.NavigationBarFragments.DetailsFragment;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.NavigationBarFragments.HomeFragment;

public class AdsActivity extends AppCompatActivity {

    private String userFirstName = null, userLastName = null, userPhoneNumber = null;
    private static final String TAG = "AdsActivity";
    private Button detailsBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);

        loadFragment(new HomeFragment());
        /**
         *   Adatok lekérése a felhasználóról a bejelentkeztetés során
         *   (MainActivity) - putExtra
         */


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            userFirstName = (String)bundle.get("USER_FIRSTNAME");
            userLastName = (String)bundle.get("USER_LASTNAME");
            userPhoneNumber = (String)bundle.get("USER_PHONENUMBER");
            Log.d(TAG, "User data: " + userFirstName + " " + userLastName + " " + userPhoneNumber);
        }
        /*detailsBtn=findViewById(R.id.detail_btn);
        detailsBtn.setOnClickListener((v)->{
            @Override
            public void onClick() {
                loadFragment(new DetailsFragment());
            }
        });*/
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_bar);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Fragment fragment = null;

                switch (menuItem.getItemId()) {
                    case R.id.bottom_navigation_bar_add:
                        fragment = new AddFragment();
                        break;
                    case R.id.bottom_navigation_bar_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.bottom_navigation_bar_account:
                        fragment = new AccountFragment();
                        break;
                }
                return loadFragment(fragment);
            }
        });
    }

    public boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
