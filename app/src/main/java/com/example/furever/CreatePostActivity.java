package com.example.furever;

import android.Manifest;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {
    private static final String TAG = "CreatePostActivity";

    private TextInputEditText editBreed;
    private AutoCompleteTextView spinnerSex, spinnerAge;
    private AutoCompleteTextView editAddress;
    private TextInputEditText editDescription;
    private Button btnCreatePost;
    private ProgressBar progressBar;
    
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private PlacesClient placesClient;
    private AutocompleteSessionToken sessionToken;
    
    // Map to store place predictions and their details
    private final Map<String, AutocompletePrediction> predictionMap = new HashMap<>();

    private String selectedSex = "";
    private String selectedAge = "";
    private String selectedAddress = "";
    private double selectedLatitude = 0.0;
    private double selectedLongitude = 0.0;

    // Predefined lists for dropdowns
    private final List<String> sexOptions = Arrays.asList("Male", "Female");

    private final List<String> ageRanges = Arrays.asList(
        "Puppy (0-1 year)", 
        "Young (1-3 years)", 
        "Adult (3-7 years)", 
        "Senior (7+ years)"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Create Post");
        }

        // Initialize Places API with error handling
        try {
            if (!Places.isInitialized()) {
                String apiKey = getString(R.string.google_maps_api_key);
                Log.d(TAG, "Initializing Places with key prefix: " + apiKey.substring(0, 5) + "...");
                Places.initialize(getApplicationContext(), apiKey);
            }
            placesClient = Places.createClient(this);
            sessionToken = AutocompleteSessionToken.newInstance();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Places: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing Places API. Some features may not work.", Toast.LENGTH_LONG).show();
        }

        // Request location permissions
        requestLocationPermissions();

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Log.e(TAG, "No user is signed in!");
            Toast.makeText(this, "Please sign in to create a post", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize views
        editBreed = findViewById(R.id.spinnerBreed);
        spinnerSex = findViewById(R.id.spinnerSex);
        spinnerAge = findViewById(R.id.spinnerAge);
        editDescription = findViewById(R.id.editDescription);
        editAddress = findViewById(R.id.editAddress);
        btnCreatePost = findViewById(R.id.btnCreatePost);
        progressBar = findViewById(R.id.progressBar);

        // Setup dropdown adapters
        setupDropdowns();
        
        // Setup Places Autocomplete
        setupPlacesAutocomplete();

        // Setup click listener
        btnCreatePost.setOnClickListener(v -> createPost());
    }
    
    private void requestLocationPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                
                requestPermissions(
                    new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    100
                );
            }
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Location permission granted");
            } else {
                Log.d(TAG, "Location permission denied");
                Toast.makeText(this, "Location permission is needed for address autocomplete to work properly", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void setupPlacesAutocomplete() {
        // Make editAddress a regular editable field
        editAddress.setFocusable(true);
        editAddress.setFocusableInTouchMode(true);
        
        try {
            editAddress.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Not needed
                }
    
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 2) {
                        try {
                            getPlacePredictions(s.toString());
                        } catch (Exception e) {
                            Log.e(TAG, "Error getting predictions: " + e.getMessage());
                        }
                    }
                }
    
                @Override
                public void afterTextChanged(Editable s) {
                    // Not needed
                }
            });
            
            // Set item click listener for selection
            editAddress.setOnItemClickListener((parent, view, position, id) -> {
                try {
                    String item = (String) parent.getItemAtPosition(position);
                    editAddress.setText(item);
                    selectedAddress = item;
                    
                    if (predictionMap.containsKey(item)) {
                        AutocompletePrediction prediction = predictionMap.get(item);
                        if (prediction != null) {
                            getPlaceDetails(prediction.getPlaceId());
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in item selection: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up Places autocomplete: " + e.getMessage());
        }
    }
    
    private void getPlacePredictions(String query) {
        if (placesClient == null) {
            Log.e(TAG, "Places client is null");
            return;
        }
        
        try {
            // Create a prediction request
            FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                    .setTypeFilter(TypeFilter.ADDRESS)
                    .setSessionToken(sessionToken)
                    .setQuery(query)
                    .build();
    
            placesClient.findAutocompletePredictions(predictionsRequest)
                    .addOnSuccessListener((response) -> {
                        List<String> predictions = new ArrayList<>();
                        
                        for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                            String address = prediction.getFullText(null).toString();
                            predictions.add(address);
                        }
                        
                        // Update the dropdown with predictions
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_dropdown_item_1line, predictions);
                        editAddress.setAdapter(adapter);
                        
                        if (predictions.size() > 0 && editAddress.isFocused()) {
                            editAddress.showDropDown();
                        }
                    })
                    .addOnFailureListener((exception) -> {
                        Log.e(TAG, "Place prediction failed: " + exception.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in getPlacePredictions: " + e.getMessage());
        }
    }
    
    private void getPlaceDetails(String placeId) {
        // Define which fields to request
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, 
                Place.Field.ADDRESS, Place.Field.LAT_LNG);

        // Create a fetch request
        com.google.android.libraries.places.api.net.FetchPlaceRequest request = 
                com.google.android.libraries.places.api.net.FetchPlaceRequest.builder(placeId, placeFields)
                .setSessionToken(sessionToken)
                .build();

        // Make the API call
        placesClient.fetchPlace(request)
                .addOnSuccessListener((response) -> {
                    Place place = response.getPlace();
                    selectedAddress = place.getAddress();
                    LatLng latLng = place.getLatLng();
                    if (latLng != null) {
                        selectedLatitude = latLng.latitude;
                        selectedLongitude = latLng.longitude;
                    }
                    Log.d(TAG, "Place details fetched: " + selectedAddress + 
                            " (" + selectedLatitude + ", " + selectedLongitude + ")");
                })
                .addOnFailureListener((exception) -> {
                    Log.e(TAG, "Failed to fetch place details: " + exception.getMessage());
                });
    }

    private void setupDropdowns() {
        try {
            // Setup sex dropdown
            ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_item, sexOptions);
            spinnerSex.setAdapter(sexAdapter);
            spinnerSex.setOnItemClickListener((parent, view, position, id) -> {
                selectedSex = sexOptions.get(position);
                Log.d(TAG, "Selected sex: " + selectedSex);
            });

            // Setup age dropdown
            ArrayAdapter<String> ageAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_item, ageRanges);
            spinnerAge.setAdapter(ageAdapter);
            spinnerAge.setOnItemClickListener((parent, view, position, id) -> {
                selectedAge = ageRanges.get(position);
                Log.d(TAG, "Selected age: " + selectedAge);
            });

        } catch (Exception e) {
            Log.e(TAG, "Error setting up dropdowns: ", e);
            Toast.makeText(this, "Error setting up form", Toast.LENGTH_SHORT).show();
        }
    }

    private void createPost() {
        Log.d(TAG, "Attempting to create post...");
        
        // Get breed directly from the edit text
        String breed = editBreed.getText().toString().trim();
        
        // Use the selected values for sex and age
        String sex = selectedSex;
        String age = selectedAge;
        String description = editDescription.getText().toString().trim();
        
        // Get address from the field if we don't have a selected one yet
        String address = "";
        if (editAddress.getText() != null) {
            address = editAddress.getText().toString().trim();
            selectedAddress = address;
        }

        Log.d(TAG, "Values - Breed: " + breed + 
              ", Sex: " + sex + 
              ", Age: " + age + 
              ", Address: " + address);

        if (breed.isEmpty() || sex.isEmpty() || age.isEmpty() || description.isEmpty()) {
            String message = "Please fill in all required fields";
            Log.w(TAG, "Validation failed: " + message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (address.isEmpty()) {
            Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnCreatePost.setEnabled(false);

        try {
            // Create and save post - using 0.0 for lat/lng if not available
            Post post = new Post(
                currentUser.getUid(), 
                currentUser.getEmail(), 
                breed, 
                sex, 
                age, 
                description, 
                address, 
                selectedLatitude,
                selectedLongitude
            );
            
            Log.d(TAG, "Saving post to Firestore...");
            db.collection("posts")
                    .add(post)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Post created with ID: " + documentReference.getId());
                        Toast.makeText(CreatePostActivity.this, "Post created successfully",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error creating post: ", e);
                        progressBar.setVisibility(View.GONE);
                        btnCreatePost.setEnabled(true);
                        Toast.makeText(CreatePostActivity.this, "Failed to create post: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in createPost: ", e);
            progressBar.setVisibility(View.GONE);
            btnCreatePost.setEnabled(true);
            Toast.makeText(this, "Error creating post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 