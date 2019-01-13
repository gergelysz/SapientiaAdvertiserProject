package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject;

import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.NavigationBarFragments.AccountFragment;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.NavigationBarFragments.AddFragment;
import sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.NavigationBarFragments.HomeFragment;

public class AdsActivity extends AppCompatActivity {

    private String userId = null;
    private static final String TAG = "AdsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);

        loadFragment(new HomeFragment());

        /*
         *   Adatok lekérése a felhasználóról a bejelentkeztetés során
         *   (MainActivity) - putExtra
         */

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String userFirstName = (String) bundle.get("USER_FIRSTNAME");
            String userLastName = (String) bundle.get("USER_LASTNAME");
            String userPhoneNumber = (String) bundle.get("USER_PHONENUMBER");
            userId = (String) bundle.get("USER_ID");
            Log.d(TAG, "User data: " + userFirstName + " " + userLastName + " " + userPhoneNumber + " " + userId);
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_bar);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {

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

    public String getUserId() {
        return userId;
    }
}
