package ce.yildiz.edu.tr.calendar.views;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ce.yildiz.edu.tr.calendar.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";

    // New variables for Current Place Picker
    private PlacesClient mPlacesClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // A default location (Istanbul, Turkey) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(41.0082, 28.9784);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private Button selectButton;

    private GoogleMap mMap;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.MapsActivity_Fragment_Map);
        mapFragment.getMapAsync(this);

        // Initialize the Places client
        String apiKey = getString(R.string.google_maps_key);
        Places.initialize(getApplicationContext(), apiKey);
        mPlacesClient = Places.createClient(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        geocoder = new Geocoder(this);

        defineViews();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        // Enable the zoom controls for the map
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Prompt the user for permission.
        getLocationPermission();

        defineListeners();

        getDeviceLocation();
    }


    private void defineViews() {
        selectButton = (Button) findViewById(R.id.MapsActivity_Button_Select);
    }

    private void defineListeners() {
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.i(TAG, latLng.toString());
                mMap.addMarker(new MarkerOptions().position(latLng));
                try {
                    mMap.clear();
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (!addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        String streetAddress = address.getAddressLine(0);
                        Log.i(TAG, streetAddress);

                        mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(streetAddress)
                                .draggable(true));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                Log.d(TAG, "onMarkerDragStart: ");
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                Log.d(TAG, "onMarkerDrag: ");
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.d(TAG, "onMarkerDragEnd: ");
                LatLng latLng = marker.getPosition();

                try {
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if(!addresses.isEmpty()){
                        Address address = addresses.get(0);
                        String addressString = address.getAddressLine(0);
                        marker.setTitle(addressString);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        mLocationPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

//    private void getCurrentPlaceLikelihoods() {
//        // Use fields to define the data types to return.
//        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
//                Place.Field.LAT_LNG);
//
//        // Get the likely places - that is, the businesses and other points of interest that
//        // are the best match for the device's current location.
//        @SuppressWarnings("MissingPermission") final FindCurrentPlaceRequest request =
//                FindCurrentPlaceRequest.builder(placeFields).build();
//        Task<FindCurrentPlaceResponse> placeResponse = mPlacesClient.findCurrentPlace(request);
//        placeResponse.addOnCompleteListener(this,
//                new OnCompleteListener<FindCurrentPlaceResponse>() {
//                    @Override
//                    public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
//                        if (task.isSuccessful()) {
//                            FindCurrentPlaceResponse response = task.getResult();
//                            // Set the count, handling cases where less than 5 entries are returned.
//                            int count;
//                            if (response.getPlaceLikelihoods().size() < M_MAX_ENTRIES) {
//                                count = response.getPlaceLikelihoods().size();
//                            } else {
//                                count = M_MAX_ENTRIES;
//                            }
//
//                            int i = 0;
//                            mLikelyPlaceNames = new String[count];
//                            mLikelyPlaceAddresses = new String[count];
//                            mLikelyPlaceAttributions = new String[count];
//                            mLikelyPlaceLatLngs = new LatLng[count];
//
//                            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
//                                Place currPlace = placeLikelihood.getPlace();
//                                mLikelyPlaceNames[i] = currPlace.getName();
//                                mLikelyPlaceAddresses[i] = currPlace.getAddress();
//                                mLikelyPlaceAttributions[i] = (currPlace.getAttributions() == null) ?
//                                        null : TextUtils.join(" ", currPlace.getAttributions());
//                                mLikelyPlaceLatLngs[i] = currPlace.getLatLng();
//
//                                String currLatLng = (mLikelyPlaceLatLngs[i] == null) ?
//                                        "" : mLikelyPlaceLatLngs[i].toString();
//
//                                Log.i(TAG, String.format("Place " + currPlace.getName()
//                                        + " has likelihood: " + placeLikelihood.getLikelihood()
//                                        + " at " + currLatLng));
//
//                                i++;
//                                if (i > (count - 1)) {
//                                    break;
//                                }
//                            }
//
//
//                            // COMMENTED OUT UNTIL WE DEFINE THE METHOD
//                            // Populate the ListView
//                            // fillPlacesList();
//                        } else {
//                            Exception exception = task.getException();
//                            if (exception instanceof ApiException) {
//                                ApiException apiException = (ApiException) exception;
//                                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
//                            }
//                        }
//                    }
//                });
//    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if(mLastKnownLocation==null){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            }else{
                                Log.d(TAG, "Latitude: " + mLastKnownLocation.getLatitude());
                                Log.d(TAG, "Longitude: " + mLastKnownLocation.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        }

                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

}
