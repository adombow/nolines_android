package com.nolines.nolines;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.nolines.nolines.api.models.Ticket;
import com.nolines.nolines.api.service.Updateable;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback,
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

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.dirFab) FloatingActionButton findDirectionsBtn;
    private MainActivity appCompatActivity;
    private OnFragmentInteractionListener mListener;

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

    private GuestHolder guest;
    private RidesHolder rides;

    Marker lastSelectedMarker;
    Polyline currentDirections;
    boolean firstLocationFetch = true;
    int rideFromTicketView = -1;

    public MapsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapsFragment.
     */
    public static MapsFragment newInstance() {
        MapsFragment fragment = new MapsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        ButterKnife.bind(this, view);

        toolbar.setTitle(getString(R.string.map_fragment_title));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).setupActionBarDrawerToggle(toolbar);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getContext());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        //retain map layout if screen reset
        if (savedInstanceState == null) {
            // First incarnation of this activity.
            mapFrag.setRetainInstance(true);
        }

        mapFrag.getMapAsync(this);

        //Refresh the current guest from the server
        guest = GuestHolder.getInstance(this.getContext());
        guest.registerListener(this);

        //Start the directions button as hidden
        findDirectionsBtn.hide();
        findDirectionsBtn.setOnClickListener(this);

        Bundle args = getArguments();
        if(args != null){
            rideFromTicketView = args.getInt(MainActivity.RIDE_ID_ARGUMENT);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    //Callback for each time thee current location is updated
    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                mLastLocation = location;
            }

            if(firstLocationFetch){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 14));
                firstLocationFetch = false;
            }
            // Add a marker for the guest's child location (if available)
        }
    };

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
        mMap.setInfoWindowAdapter(new MapsFragment.CustomInfoWindowAdapter());

        enableMyLocation();
    }

    //Enables the ability to use the user's current location and creates a LocationRequest object
    //To fetch the current location on a loop
    private void enableMyLocation() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(15000); // 15 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Permission to access the location is missing.
                PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
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
            findDirectionsBtn.show();
            marker.showInfoWindow();
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
        Toast.makeText(this.getContext(), "Info Window Clicked!", Toast.LENGTH_SHORT).show();
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
            for (Ride ride : rides.getRideList()) {
                LatLng latLng = new LatLng(ride.getLat(), ride.getLon());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(ride.getName());
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                markerOptions.snippet(ride.getRideType() + "\n" + getString(R.string.wait_time) + ride.getWaitTime());
                if (guest != null && !guest.getGuestObject().getTickets().isEmpty()) {
                    for (Ticket ticket : guest.getGuestObject().getTickets()) {
                        if (ticket.getRide().getName().equals(ride.getName())) {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(
                                    BitmapDescriptorFactory.HUE_YELLOW));

                            Calendar cal = ticket.getLocalDatetimeFromTime();
                            SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
                            formatter.setTimeZone(TimeZone.getDefault());

                            String startTime = formatter.format(cal.getTime());
                            cal.add(Calendar.HOUR, 1);
                            String endTime = formatter.format(cal.getTime());

                            markerOptions.snippet(getString(R.string.ride_window_text, startTime, endTime) +
                                    "\n" + getString(R.string.wait_time) + ride.getWaitTime());
                        }
                    }
                }
                mMap.addMarker(markerOptions);
                if(rideFromTicketView == ride.getId()){
                    displayRouteFromCurrentLocation(latLng);
                }
            }
        }
    }

    @Override
    public void onGuestUpdate(){
        //Refresh the park's rides from the server
        rides = RidesHolder.getInstance(this.getContext());
        rides.registerListener(this);
        //Start fetching the rides to display
        rides.refreshRides();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy(){
        if(rides == null)
            rides = RidesHolder.getInstance(this.getContext());
        rides.unregisterListener(this);
        guest.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
        if(rides == null){
            //Refresh the park's rides from the server
            rides = RidesHolder.getInstance(this.getContext());
            rides.registerListener(this);
        }
        //Start fetching the rides to display
        rides.refreshRides();
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getFragmentManager(), "dialog");
    }

    private void displayRouteFromCurrentLocation(LatLng end){
        if(mLastLocationMarker != null)
            mLastLocationMarker.remove();
        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Your location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mLastLocationMarker = mMap.addMarker(markerOptions);

        //ApplicationInfo app = this.getPackageManager().getApplicationInfo("com.nolines.nolines", PackageManager.GET_META_DATA );
        //Bundle bundle = app.metaData;
        //GoogleDirection.withServerKey(bundle.getString("com.google.android.geo.API_KEY"));
        //TODO: USE ABOVE TO GET KEY INSTEAD
        GoogleDirection.withServerKey("AIzaSyAr2G3Jc26ZM7EPFB3rr5KMs44OmcThBHk")
                .from(mLastLocationMarker.getPosition())
                .to(end)
                .transportMode(TransportMode.WALKING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if(direction.isOK()){
            Toast.makeText(this.getContext(), "Getting directions...", Toast.LENGTH_SHORT).show();
            Route route = direction.getRouteList().get(0);
            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
            currentDirections = mMap.addPolyline(DirectionConverter.createPolyline(this.getContext(),
                    directionPositionList, 5, Color.RED));
            setCameraWithinCoordinationBounds(route);
        }else{
            Toast.makeText(this.getContext(), direction.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Toast.makeText(this.getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
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
            displayRouteFromCurrentLocation(lastSelectedMarker.getPosition());
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

            SpannableString titleText = new SpannableString(marker.getTitle());
            titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
            markerName.setText(titleText);

            titleText = new SpannableString(marker.getSnippet());
            titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, titleText.length(), 0);
            markerInfo.setText(titleText);

            for(Ride ride : rides.getRideList()){
                if(marker.getTitle().equals(ride.getName())) {
                    Picasso.get()
                            //.load("http://i.imgur.com/r6EAcbN.jpg")
                            .load(ride.getPhotoURL())
                            .resize(200, 200)
                            .centerCrop()
                            .into(markerPic);
                    break;
                }
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
