package com.example.ghostrunner.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Formatter;
import java.util.Locale;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ghostrunner.CLocation;
import com.example.ghostrunner.HorizontalRecyclerViewAdapter;
import com.example.ghostrunner.IBaseGpsListener;
import com.example.ghostrunner.ImageModel;
import com.example.ghostrunner.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;

import de.nitri.gauge.Gauge;


public class MapFragment extends Fragment implements OnMapReadyCallback, IBaseGpsListener {

    private static GoogleMap mMap;
    private static MarkerOptions place1, place2;
    private static Marker fromCoords, toCoords;
    private static GeoApiContext mGeoApiContext;
    private static int trailIDpressed;
    private Toolbar toolbar;
    private RecyclerView mHorizontalRecyclerView;
    private HorizontalRecyclerViewAdapter horizontalAdapter;
    private LinearLayoutManager horizontalLayoutManager;
    private Polyline currentPolyline;
    SupportMapFragment mapFragment;
    static LatLng coordsStart, coordsEnd;
    TextView txtTimer;
    TextView txtTimerStopped;
    Gauge gauge;
    int curvalue;
    private View root;
    private boolean show = false;
    private boolean startTimer = false;
    Handler customHandler = new Handler();
    long startTime = 0L, timeInMilliseconds = 0L, timeSwapBuff = 0L, updateTime = 0L;
    private Boolean mLocationPermissionsGranted = false;
    private static final String TAG = "MapFragment";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;


    Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updateTime / 1000);
            int mins = secs / 60;
            int hours = mins / 60;
            secs %= 60;
            int milliseconds = (int) (updateTime % 1000);
            txtTimer.setText(String.format("%2d", hours) + ":" + String.format("%2d", mins) + ":" + String.format("%2d", secs) + ":" + String.format("%3d", milliseconds));
            customHandler.postDelayed(this, 0);
        }
    };

    public MapFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_map, container, false);


        RecyclerView recycler = root.findViewById(R.id.horizontalRecyclerView);
        recycler.setVisibility(View.INVISIBLE);

        gauge = (Gauge) root.findViewById(R.id.gauge);

        curvalue = 0;
        gauge.setValue(curvalue);

        getLocationPermission();


        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        this.updateSpeed(null);


        CheckBox chkUseMetricUntis = (CheckBox) root.findViewById(R.id.chkMetricUnits);
        chkUseMetricUntis.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                MapFragment.this.updateSpeed(null);
            }
        });



        txtTimer = (TextView) root.findViewById(R.id.time);
        final Button buttonadd = root.findViewById(R.id.addBtn);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final Button button = root.findViewById(R.id.showhideBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(show) {
                    mMap.clear();
                    RecyclerView recycler = root.findViewById(R.id.horizontalRecyclerView);
                    recycler.setVisibility(View.INVISIBLE);
                    button.setText("Show Trails");
                    buttonadd.setText("Add Trail");
                    Drawable img = buttonadd.getContext().getResources().getDrawable( R.drawable.ic_add);
                    buttonadd.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
                    Drawable img2= button.getContext().getResources().getDrawable( R.drawable.ic_show);
                    button.setCompoundDrawablesWithIntrinsicBounds( img2, null, null, null);
                    show = false;
                }
                else{
                    RecyclerView recycler = root.findViewById(R.id.horizontalRecyclerView);
                    recycler.setVisibility(View.VISIBLE);
                    button.setText("Hide Trails");
                    buttonadd.setText("Start Trail");
                    Drawable img = buttonadd.getContext().getResources().getDrawable( R.drawable.ic_play);
                    buttonadd.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
                    Drawable img2 = button.getContext().getResources().getDrawable( R.drawable.ic_hide);
                    button.setCompoundDrawablesWithIntrinsicBounds( img2, null, null, null);
                    show = true;
                }
            }
        });
        final ImageButton buttonstartTimer = root.findViewById(R.id.starttimer);
        buttonstartTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!startTimer){
                    startTimer = true;
                    buttonstartTimer.setImageResource(R.drawable.ic_pause);
                    startTime = SystemClock.uptimeMillis();
                    customHandler.postDelayed(updateTimerThread,0);

                }else {
                    startTimer = false;
                    buttonstartTimer.setImageResource(R.drawable.ic_play);
                    timeSwapBuff+=timeInMilliseconds;
                    customHandler.removeCallbacks(updateTimerThread);
                }

            }
        });
        final ImageButton buttonstopTimer = root.findViewById(R.id.endpausetimer);
        buttonstopTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                txtTimerStopped = (TextView) root.findViewById(R.id.timeStoped);
                txtTimerStopped.setText(txtTimer.getText());
                swapFragment();


            }
        });

        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }
        return root;
    }
    private void swapFragment(){
        DescriptionFragment descriptionFragment = new DescriptionFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mapFragemnt, descriptionFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    public static void idPressed (LatLng coordsStart, LatLng coordsEnd) {

        MapFragment mapF = new MapFragment();
        MapFragment.coordsStart = coordsStart;
        MapFragment.coordsEnd = coordsEnd;
        mapF.showTrail();
    }

    public void  showTrail()
    {
        mMap.clear();

        place1 = new MarkerOptions().position(coordsStart).title("Location 1");
        place2 = new MarkerOptions().position(coordsEnd).title("Location 2");

        place1.icon(BitmapDescriptorFactory.fromResource(R.drawable.start));
        place2.icon(BitmapDescriptorFactory.fromResource(R.drawable.finish));
        mMap.addMarker(place1);
        mMap.addMarker(place2);
        mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(new LatLng(40.048611, -8.890201)).
                tilt(60).
                zoom(12).
                bearing(20).
                build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        fromCoords = mMap.addMarker(place1);
        toCoords = mMap.addMarker(place2);

        calculateDirections(fromCoords, toCoords);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

        }
        mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(new LatLng(40.048611, -8.890201)).
                tilt(60).
                zoom(12).
                bearing(20).
                build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    //Directions and polylines to map

    private void calculateDirections(Marker fromCoords, Marker toCoords){
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                toCoords.getPosition().latitude,
                toCoords.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        fromCoords.getPosition().latitude,
                        fromCoords.getPosition().longitude
                )
        );

        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {

                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {

            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                for(DirectionsRoute route: result.routes){
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    try{
                        polyline.setColor(ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark));
                        polyline.setClickable(true);
                    }catch(IllegalStateException e){
                        Log.e("MAPFRAGMENT", e.toString());
                    }

                }
            }
        });
    }

    public ArrayList<ImageModel> fillWithData() {
        ArrayList<ImageModel> imageModelArrayList = new ArrayList<>();
        ImageModel imageModel0 = new ImageModel();
        imageModel0.setId(System.currentTimeMillis());
        imageModel0.setImageName("Trail 1");
        imageModel0.setImagePath(R.drawable.pic);
        imageModelArrayList.add(imageModel0);
        imageModel0.setCoordsStart(new LatLng(40.048611, -8.890201));
        imageModel0.setCoordsEnd(new LatLng(40.155185, -8.867252));

        ImageModel imageModel1 = new ImageModel();
        imageModel1.setId(System.currentTimeMillis());
        imageModel1.setImageName("Trail 2");
        imageModel1.setImagePath(R.drawable.pic2);
        imageModelArrayList.add(imageModel1);
        imageModel1.setCoordsStart(new LatLng(40.049611, -8.890201));
        imageModel1.setCoordsEnd(new LatLng(40.165185, -8.867252));

        ImageModel imageModel2 = new ImageModel();
        imageModel2.setId(System.currentTimeMillis());
        imageModel2.setImageName("Trail 3");
        imageModel2.setImagePath(R.drawable.pic3);
        imageModelArrayList.add(imageModel2);
        imageModel2.setCoordsStart(new LatLng(40.048511, -8.890201));
        imageModel2.setCoordsEnd(new LatLng(40.255185, -8.867252));

        ImageModel imageModel3 = new ImageModel();
        imageModel3.setId(System.currentTimeMillis());
        imageModel3.setImageName("Trail 4");
        imageModel3.setImagePath(R.drawable.pic4);
        imageModelArrayList.add(imageModel3);
        imageModel3.setCoordsStart(new LatLng(40.048611, -8.890201));
        imageModel3.setCoordsEnd(new LatLng(40.155185, -8.867252));

        ImageModel imageModel4 = new ImageModel();
        imageModel4.setId(System.currentTimeMillis());
        imageModel4.setImageName("Trail 5");
        imageModel4.setImagePath(R.drawable.pic5);
        imageModelArrayList.add(imageModel4);
        imageModel4.setCoordsStart(new LatLng(40.048511, -8.890201));
        imageModel4.setCoordsEnd(new LatLng(40.255185, -8.867252));

        ImageModel imageModel5 = new ImageModel();
        imageModel5.setId(System.currentTimeMillis());
        imageModel5.setImageName("Trail 6");
        imageModel5.setImagePath(R.drawable.pic6);
        imageModelArrayList.add(imageModel5);
        imageModel5.setCoordsStart(new LatLng(40.048611, -8.890201));
        imageModel5.setCoordsEnd(new LatLng(40.155185, -8.867252));
        return imageModelArrayList;
    }

    public void finish()
    {

        System.exit(0);
    }

    private void updateSpeed(CLocation location) {
        // TODO Auto-generated method stub
        float nCurrentSpeed = 0;

        if(location != null)
        {
            location.setUseMetricunits(this.useMetricUnits());
            nCurrentSpeed = location.getSpeed();
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');

        String strUnits = "m/h";
        if(this.useMetricUnits())
        {
            strUnits = "m/s";
        }

        TextView txtCurrentSpeed = (TextView) root.findViewById(R.id.txtCurrentSpeed);
        txtCurrentSpeed.setText(strCurrentSpeed + " " + strUnits);
    }

    private boolean useMetricUnits() {
        // TODO Auto-generated method stub
        CheckBox chkUseMetricUnits = (CheckBox) root.findViewById(R.id.chkMetricUnits);
        return chkUseMetricUnits.isChecked();
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        if(location != null)
        {
            CLocation myLocation = new CLocation(location, this.useMetricUnits());
            this.updateSpeed(myLocation);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGpsStatusChanged(int event) {
        // TODO Auto-generated method stub

    }



    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }
    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mHorizontalRecyclerView = (RecyclerView) root.findViewById(R.id.horizontalRecyclerView);
        horizontalAdapter = new HorizontalRecyclerViewAdapter(fillWithData(), getContext());

        horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mHorizontalRecyclerView.setLayoutManager(horizontalLayoutManager);
        mHorizontalRecyclerView.setAdapter((RecyclerView.Adapter) horizontalAdapter);

        mapFragment.getMapAsync(this);
    }
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }
}


