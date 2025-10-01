package com.customresumegen.app;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final int REQUEST_CHECK_SETTINGS = 2000;
    CardView resumeCard;
    ProgressBar progressBar;
    MaterialButton generateResume, fontColor, backgroundColor;
    private TextView bodyText, fontSize, currentLocation;
    private SeekBar fontSizeBar;
    private int selectedSize = 14;
    private int selectedFontColor = Color.BLACK;
    private int selectedBackgroundColor = Color.WHITE;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        currentLocation = findViewById(R.id.currentLatAndLong);
        resumeCard = findViewById(R.id.resumeCard);
        bodyText = findViewById(R.id.bodyText);
        fontSize = findViewById(R.id.fontSize);
        fontSizeBar = findViewById(R.id.fontSizeBar);
        fontColor = findViewById(R.id.btnFontColor);
        backgroundColor = findViewById(R.id.btnBgColor);
        generateResume = findViewById(R.id.generate);


        fontColor.setOnClickListener(view -> showColorDialog("Font"));

        backgroundColor.setOnClickListener(view -> showColorDialog("Background"));

        fontSize.setText(String.valueOf(selectedSize));
        fontSizeBar.setProgress(selectedSize);
        setupSeekBar();

        generateResume.setOnClickListener(view -> {
            bodyText.setTextSize(selectedSize);
            bodyText.setTextColor(selectedFontColor);
            resumeCard.setBackgroundTintList(ColorStateList.valueOf(selectedBackgroundColor));
            fetchResume();
        });

        setLocation();
        fetchResume();
    }

    private void setupSeekBar() {
        fontSizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedSize = Math.max(progress, 10);
                fontSize.setText(String.valueOf(selectedSize));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // not needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // not needed
            }
        });
    }

    private void showColorDialog(String purpose) {
        List<ColorItem> colors = ColorData.getAllColors();
        String[] colorNames = new String[colors.size()];
        for (int i = 0; i < colors.size(); i++) {
            colorNames[i] = colors.get(i).getName();
        }

        new AlertDialog.Builder(this)
                .setTitle("Select a Color for " + purpose)
                .setItems(colorNames, (dialog, which) -> {
                    String hex = colors.get(which).getHexCode();
                    int color = Color.parseColor(hex);

                    if (purpose.equals("Font")) {
                        selectedFontColor = color;
                        Toast.makeText(this, colorNames[which] + " selected for font color", Toast.LENGTH_SHORT).show();
                    } else if (purpose.equals("Background")) {
                        selectedBackgroundColor = color;
                        Toast.makeText(this, colorNames[which] + " selected for resume background", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void fetchResume() {
        progressBar.setVisibility(VISIBLE);
        ResumeApi api = RetrofitClient.getRetrofitInstance().create(ResumeApi.class);
        Call<Resume> call = api.getResume("Jeevandeep Saini");

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Resume> call, @NonNull Response<Resume> response) {
                if (response.isSuccessful() && response.body() != null) {
                    progressBar.setVisibility(GONE);
                    displayResume(response.body());
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<Resume> call, @NonNull Throwable t) {
                progressBar.setVisibility(GONE);
                bodyText.setText("Error fetching resume: " + t.getMessage());
            }
        });
    }

    private void displayResume(Resume resume) {
        StringBuilder builder = new StringBuilder();

        // Personal Info
        builder.append("===== Personal Info =====\n");
        builder.append("Name: ").append(resume.name).append("\n");
        builder.append("Phone: ").append(resume.phone).append("\n");
        builder.append("Email: ").append(resume.email).append("\n");
        builder.append("Twitter: ").append(resume.twitter).append("\n");
        builder.append("Address: ").append(resume.address).append("\n\n");

        // Summary
        builder.append("===== Summary =====\n");
        builder.append(resume.summary).append("\n\n");

        // Skills
        builder.append("===== Skills =====\n");
        for (String skill : resume.skills) {
            builder.append("• ").append(skill).append("\n");
        }
        builder.append("\n");

        // Projects
        builder.append("===== Projects =====\n");
        if (resume.projects != null) {
            for (Resume.Project project : resume.projects) {
                builder.append(project.title)
                        .append(" (")
                        .append(project.startDate)
                        .append(" - ")
                        .append(project.endDate)
                        .append(")\n");
                builder.append(project.description).append("\n\n");
            }
        }

        bodyText.setText(builder.toString());
    }

    private void setLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                5000
        ).setMinUpdateIntervalMillis(2000)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    String text = "Latitude: " + location.getLatitude()
                            + "\nLongitude: " + location.getLongitude();
                    currentLocation.setText(text);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        } else {
            turnOnLocation();
        }
    }

    private void turnOnLocation() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, locationSettingsResponse -> startLocationUpdates());

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ((ResolvableApiException) e).startResolutionForResult(
                            MainActivity.this,
                            REQUEST_CHECK_SETTINGS
                    );
                } catch (Exception sendEx) {
                    sendEx.printStackTrace();
                }
            }
        });
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location must be enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}