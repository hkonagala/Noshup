package homeaway.com.placefinder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import homeaway.com.placefinder.SearchActivity;

/**
 * splash screen before the activity launch
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        finish();
    }
}
