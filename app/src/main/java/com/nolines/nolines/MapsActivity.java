package com.nolines.nolines;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.nolines.nolines.api.models.GuestHolder;
import com.nolines.nolines.api.models.Ride;
import com.nolines.nolines.api.models.RidesHolder;
import com.nolines.nolines.api.service.Updateable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        Updateable,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowCloseListener,
        GoogleMap.OnInfoWindowClickListener,
        DirectionCallback,
        View.OnClickListener{

    @BindView(R.id.dirFab) FloatingActionButton findDirectionsBtn;

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mLastLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;

    Marker mChildLocationMarker;
    Location mChildLastLocation = null;
    GuestHolder guest;
    RidesHolder rides;

    Marker lastSelectedMarker;
    Polyline currentDirections;
    boolean firstLocationFetch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFrag = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        //retain map layout if screen reset
        if (savedInstanceState == null) {
            // First incarnation of this activity.
            mapFrag.setRetainInstance(true);
        }

        mapFrag.getMapAsync(this);

        //Refresh the park's rides from the server
        rides = RidesHolder.getInstance(this);
        rides.registerListener(this);
        //Refresh the current guest from the server
        guest = GuestHolder.getInstance(this);
        guest.registerListener(this);

        //Start the directions button as hidden
        findDirectionsBtn.hide();
        findDirectionsBtn.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
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

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnInfoWindowCloseListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        enableMyLocation();

        //Start fetching the rides to display
        rides.refreshRides();
    }

    //Callback for each time thee current location is updated
    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                //Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
            }

            if(firstLocationFetch){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 14));
                firstLocationFetch = false;
            }

            // Add a marker for the guest's child location (if available)

            //mChildLastLocation = ;  //Get the new child location
            if(mChildLocationMarker != null) {
                mChildLocationMarker.remove();
                LatLng latLng = new LatLng(mChildLastLocation.getLatitude(), mChildLastLocation.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Your child");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mChildLocationMarker = mMap.addMarker(markerOptions);
            }
        }
    };

    //Enables the ability to use the user's current location and creates a LocationRequest object
    //To fetch the current location on a loop
    private void enableMyLocation() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(15000); // 15 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Permission to access the location is missing.
                PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
            } else if (mMap != null) {
                // Access to the location has been granted to the app.
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }
    }
    
    @Override
    public void onMapClick(LatLng point){
        if (mLastLocationMarker != null) {
            currentDirections.remove();
            mLastLocationMarker.remove();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 14));
            findDirectionsBtn.hide();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker){
        if (mLastLocationMarker != null) {
            mLastLocationMarker.remove();
        }
        if(marker.equals(lastSelectedMarker)){
            return true;
        }
        lastSelectedMarker = marker;
        findDirectionsBtn.show();
        marker.showInfoWindow();
        return false;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(Location location) {
    }

    @Override
    public void onInfoWindowClick(Marker marker){
        Toast.makeText(this, "Info Window Clicked!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowClose(Marker marker){
        //Toast.makeText(this, "Info Window Closed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    //When the rides are ready from the server update them on the map
    @Override
    public void onRidesUpdate(){
        // Add marker's for the park's rides
        if(!rides.isEmpty()) {
            for (int i = 0; i < rides.getRideList().size(); i++) {
                LatLng latLng = new LatLng(rides.getRideList().get(i).getLat(), rides.getRideList().get(i).getLon());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(rides.getRideList().get(i).getName());
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
//                if (!guest.getGuestObject().getTickets().isEmpty()) {
//                    for (Ticket ticket : guest.getGuestObject().getTickets()) {
//                        if (ticket.getRideName().equals(rides.getRideList().get(i).getName()))
//                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(
//                                    BitmapDescriptorFactory.HUE_YELLOW));
//                    }
//                }
                mMap.addMarker(markerOptions);
            }
        }
    }

    @Override
    public void onGuestUpdate(){

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        rides.unregisterListener(this);
        guest.unregisterListener(this);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    private void displayRoute(LatLng start, LatLng end){
        //Place current location marker
        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mLastLocationMarker = mMap.addMarker(markerOptions);

        //ApplicationInfo app = this.getPackageManager().getApplicationInfo("com.nolines.nolines", PackageManager.GET_META_DATA );
        //Bundle bundle = app.metaData;
        //GoogleDirection.withServerKey(bundle.getString("com.google.android.geo.API_KEY"));
        //TODO: USE ABOVE TO GET KEY INSTEAD
        GoogleDirection.withServerKey("AIzaSyAr2G3Jc26ZM7EPFB3rr5KMs44OmcThBHk")
                .from(start)
                .to(end)
                .transportMode(TransportMode.WALKING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if(direction.isOK()){
            Toast.makeText(this, "Getting directions...", Toast.LENGTH_SHORT).show();
            Route route = direction.getRouteList().get(0);
            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
            currentDirections = mMap.addPolyline(DirectionConverter.createPolyline(this,
                    directionPositionList, 5, Color.RED));
            setCameraWithinCoordinationBounds(route);
        }else{
            Toast.makeText(this, direction.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void setCameraWithinCoordinationBounds(Route route){
        LatLng sw = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng ne = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(sw, ne);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dirFab) {
            if(mLastLocationMarker != null)
                mLastLocationMarker.remove();
            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Your location");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mLastLocationMarker = mMap.addMarker(markerOptions);
            displayRoute(mLastLocationMarker.getPosition(), lastSelectedMarker.getPosition());
        }
    }

    //Class for our custom info window when marker clicked
    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{
        @BindView(R.id.markerpic) ImageView markerPic;
        @BindView(R.id.markername) TextView markerName;
        @BindView(R.id.markerinfo) TextView markerInfo;

        //private final View mWindow;
        private final View mContents;

        public CustomInfoWindowAdapter(){
            //mWindow = getLayoutInflater().inflate(R.layout.maps_custom_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.maps_custom_info_contents, null);
            ButterKnife.bind(this, mContents);
        }

        @Override
        public View getInfoWindow(Marker marker){
            //Add check here and return window, otherwise null will give just contents instead
            return null;
            //render(marker, mWindow);
            //return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker){
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view){
            //Make everything blank in case we don't find the ride
            markerPic.setImageResource(0);
            markerName.setText("");
            markerInfo.setText("");
            for(Ride ride : rides.getRideList()){
                if(marker.getTitle().equals(ride.getName())) {
                    //Picasso.get().load(ride.getPhotoURL()).into(markerPic);
                    Picasso.get()
                            .load("http://i.imgur.com/r6EAcbN.jpg")
                            .placeholder(R.drawable.logo)
                            .resize(200, 200)
                            .centerCrop()
                            .into(markerPic);

                    if( !ride.getName().equals("") ){
                        SpannableString titleText = new SpannableString(ride.getName());
                        titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                        markerName.setText(titleText);
                    }

                    if( !ride.getRideType().equals("") ) {
                        SpannableString infoText = new SpannableString(ride.getRideType());
                        infoText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, infoText.length(), 0);
                        markerInfo.setText(infoText);
                    }
                    break;
                }
            }
        }
    }
}
