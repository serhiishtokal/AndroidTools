package com.shtokal.tools;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
public class HomeActivity extends AppCompatActivity {
    Button btnLogout;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    Adapter adapter;
    ArrayList<DetailsData> items;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //logout
        btnLogout = findViewById(R.id.logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intToMain = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intToMain);
            }
        });

        //list
        items = new ArrayList<>();
        items.add(new DetailsData("Flashlight", "3-modes flashlight", R.drawable.flashlight_icon));
        items.add(new DetailsData("Metal Detector", "Used for metal detection", R.drawable.metal_detector));
        items.add(new DetailsData("Level", "Measurement of the level ", R.drawable.level_icon));
        items.add(new DetailsData("Compass", "Used for navigation and orientation", R.drawable.compass_icon));
        items.add(new DetailsData("Ruler", "Calculation of the length", R.drawable.ruler_icon));
        items.add(new DetailsData("QR code scaner", "QR code scaner", R.drawable.qr_icon));
        items.add(new DetailsData("Cardiograph", "Heart rate monitor", R.drawable.cardiograph));
        items.add(new DetailsData("Sound Meter", "Level of sound", R.drawable.sound));
      //  items.add(new DetailsData("Accelerometer", "Accelerometer", R.drawable.accelerometer_icon));
        items.add(new DetailsData("Vibration Meter", "Measurement of vibration", R.drawable.accelerometer_icon));


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, items);
        recyclerView.setAdapter(adapter);

    }
}