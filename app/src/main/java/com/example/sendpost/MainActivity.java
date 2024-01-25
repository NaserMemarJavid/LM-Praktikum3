package com.example.sendpost;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private OkHttpClient client;
    private String postURL;
    private TextView textView;
    private Location currentLocation = new Location("dummyProvider");
    private TextView textView1;
    private String stringLatitude = "0.0";
    private String stringLongitude = "0.0";

    private int intIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                PackageManager.PERMISSION_GRANTED
        );

        // Initialize variables
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        client = new OkHttpClient();
        textView = findViewById(R.id.dataview);
        postURL = "https://reqres.in/api/users";

        // Set up location request
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(100);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult.getLastLocation() != null) {
                    currentLocation = locationResult.getLastLocation();
                    if (shouldSendLocation()) {
                        post();
                    }
                }
            }
        };

        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

        // Set up button click listener
        Button buttonPost = findViewById(R.id.btnPost);
        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
    }

    private boolean shouldSendLocation() {
        // Teil 1.c: Energy-Efficient Sensor Management
        double maxSpeed = 2.0; // Configurable maximum speed
        if (currentLocation.getSpeed() <= maxSpeed) {
            return true;
        }

        // Teil 1.d: Stillness-Aware Sensor Management
        if (isDeviceEssentiallyStill()) {
            return false;
        }

        return false;
    }

    private boolean isDeviceEssentiallyStill() {
        // Implement logic to determine if the device is essentially still based on accelerometer data
        // Replace the following placeholder with your actual logic
        return false;
    }

    private void post() {
        // Use locationInfo as needed, for example, in your FormBody.Builder
        RequestBody requestBody = new FormBody.Builder()
                .add("longitude", String.valueOf(currentLocation.getLongitude()))
                .add("latitude", String.valueOf(currentLocation.getLatitude()))
                .build();

        Request request = new Request.Builder().url(postURL).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //assert response.body() != null;
                            textView.setText(response.body().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
    }

  


    public void buttonGetLocation(View view){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    stringLatitude = Double.toString(location.getLatitude());
                    stringLongitude = Double.toString(location.getLongitude());
                } else {
                    stringLatitude = "null";
                    stringLongitude = "null";
                }
            }
        });
        textView1.setText("Index: "+ intIndex+
                "\nLatitude"+stringLatitude+
                "\nLongitude"+stringLongitude);
        intIndex++;

    }

}





