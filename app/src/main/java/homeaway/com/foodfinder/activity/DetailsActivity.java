package homeaway.com.foodfinder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import homeaway.com.foodfinder.R;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //retrieve intent values

        Intent intent = getIntent();
        intent.getStringExtra("name");
        intent.getStringExtra("ID");
        intent.getStringExtra("latitude");
        intent.getStringExtra("longitude");

        Toast.makeText(this, intent.getStringExtra("name") + " " +
        intent.getStringExtra("ID") + " " +
        intent.getStringExtra("latitude") +" " +
        intent.getStringExtra("longitude"), Toast.LENGTH_LONG).show();
    }
}
