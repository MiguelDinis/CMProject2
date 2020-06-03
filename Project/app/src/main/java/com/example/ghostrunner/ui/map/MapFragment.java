package com.example.ghostrunner.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Formatter;
import java.util.Locale;

import android.location.Location;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

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
import com.example.ghostrunner.MainActivity;
import com.example.ghostrunner.R;
import com.example.ghostrunner.models.Trail;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.nitri.gauge.Gauge;


public class MapFragment extends Fragment implements OnMapReadyCallback, IBaseGpsListener {

    private static GoogleMap mMap;
    private static MarkerOptions place1;
    private static MarkerOptions place2;
    private static Marker ghost;
    private static Marker fromCoords, toCoords;
    private static GeoApiContext mGeoApiContext;
    private static int trailIDpressed;
    private static List<LatLng> pointsChoose;
    private static boolean trailPressed;
    private Toolbar toolbar;
    private RecyclerView mHorizontalRecyclerView;
    private HorizontalRecyclerViewAdapter horizontalAdapter;
    private HorizontalRecyclerViewAdapter horizontalAdapter2;
    private LinearLayoutManager horizontalLayoutManager;
    private Polyline currentPolyline;
    SupportMapFragment mapFragment;
    static GeoPoint coordsStart, coordsEnd;

    TextView txtTimerStopped;

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
    /////////////////
    private Gauge gauge;
    private TextView txtCurrentSpeed;
    private TextView txtTimer;
    private ImageButton buttonstartTimer;
    private ImageButton buttonstopTimer;
    private Button buttonadd;
    RecyclerView recycler;
    private String userId;
    private MainActivity main;
    private List<Bitmap> trails;
    private HorizontalRecyclerViewAdapter trailsAdapter;
    private FirebaseFirestore db;
    ArrayList<Trail> imageModelArrayList;
    Location previousLocation;
    ArrayList<Polyline> polylines;
    ArrayList<LatLng> allLatLngs;
    ArrayList<GeoPoint> test;
    private boolean onTrack;
    private float speedSum = 0;
    private int speedCount = 0;
    private int countIterTrail = 0;
    private  LatLng nextPoint;

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
        onTrack = false;
        trailPressed = false;
        imageModelArrayList = new ArrayList<>();
        recycler = root.findViewById(R.id.horizontalRecyclerView);
        txtCurrentSpeed = (TextView) root.findViewById(R.id.txtCurrentSpeed);
        txtCurrentSpeed.setVisibility(View.INVISIBLE);
        recycler.setVisibility(View.INVISIBLE);
        txtTimer = (TextView) root.findViewById(R.id.time);
        gauge = (Gauge) root.findViewById(R.id.gauge);
        main =  ((MainActivity) this.requireActivity());
        userId = main.getUserId();
        db = FirebaseFirestore.getInstance();
        polylines = new ArrayList<>();
        allLatLngs = new ArrayList<>();
        curvalue = 0;
        gauge.setValue(curvalue);
        gauge.setVisibility(View.INVISIBLE);
        getLocationPermission();

        //Get friends list
        DocumentReference docRef = db.collection("Users").document(userId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> trailsToShow = (List<String>) documentSnapshot.get("friends");
                if(trailsToShow == null){
                    trailsToShow = new ArrayList<String>();
                    trailsToShow.add(userId);
                }else{
                    trailsToShow.add(userId);
                    updateTrailList(trailsToShow);
                }
            }

        });


        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }


        this.updateSpeed(null);


        CheckBox chkUseMetricUntis = (CheckBox) root.findViewById(R.id.chkMetricUnits);
        chkUseMetricUntis.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                MapFragment.this.updateSpeed(null);
            }
        });


        txtTimer.setVisibility(View.INVISIBLE);
        buttonadd = root.findViewById(R.id.addBtn);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final Button button = root.findViewById(R.id.showhideBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(show) {
                    mMap.clear();
                    recycler = root.findViewById(R.id.horizontalRecyclerView);
                    recycler.setVisibility(View.INVISIBLE);
                    button.setText("Show Trails");
                    buttonadd.setText("Add Trail");
                    Drawable img = buttonadd.getContext().getResources().getDrawable( R.drawable.ic_add);
                    buttonadd.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
                    Drawable img2= button.getContext().getResources().getDrawable( R.drawable.ic_show);
                    button.setCompoundDrawablesWithIntrinsicBounds( img2, null, null, null);
                    show = false;
                    onTrack = false;
                    trailPressed = false;
                }
                else{
                    recycler = root.findViewById(R.id.horizontalRecyclerView);



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
        buttonadd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(buttonadd.getText().equals("Start Trail")) {
                    if(trailPressed){
                        onTrack = true;
                        gauge.setVisibility(View.VISIBLE);
                        txtCurrentSpeed.setVisibility(View.VISIBLE);
                        txtTimer.setVisibility(View.VISIBLE);
                        buttonstartTimer.setVisibility(View.VISIBLE);
                        buttonstopTimer.setVisibility(View.VISIBLE);
                        recycler.setVisibility(View.INVISIBLE);
                        button.setVisibility(View.INVISIBLE);
                        buttonadd.setVisibility(View.INVISIBLE);
                        startTimer = true;
                        updateGhost();
                        buttonstartTimer.setImageResource(R.drawable.ic_pause);
                        startTime = SystemClock.uptimeMillis();
                        customHandler.postDelayed(updateTimerThread,0);
                    }
                    else{
                        Toast.makeText(getContext(), "Please select a Trail!", Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    onTrack= true;
                    gauge.setVisibility(View.VISIBLE);
                    txtCurrentSpeed.setVisibility(View.VISIBLE);
                    txtTimer.setVisibility(View.VISIBLE);
                    buttonstartTimer.setVisibility(View.VISIBLE);
                    buttonstopTimer.setVisibility(View.VISIBLE);
                    recycler.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.INVISIBLE);
                    buttonadd.setVisibility(View.INVISIBLE);
                    startTimer = true;
                    buttonstartTimer.setImageResource(R.drawable.ic_pause);
                    startTime = SystemClock.uptimeMillis();
                    customHandler.postDelayed(updateTimerThread,0);
                }
            }
        });
        buttonstartTimer = root.findViewById(R.id.starttimer);
        buttonstartTimer.setVisibility(View.INVISIBLE);
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
        buttonstopTimer = root.findViewById(R.id.endpausetimer);
        buttonstopTimer.setVisibility(View.INVISIBLE);
        buttonstopTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //txtTimerStopped.setText(txtTimer.getText());
                if( allLatLngs.size() != 0)
                    swapFragment();
                else
                    Toast.makeText(getContext(), "You need to move a little bit", Toast.LENGTH_SHORT).show();


            }
        });

        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }
        return root;
    }

    public void updateTrailList(List<String> trailsToShow){
        trails = new ArrayList<>();
        for(String trail : trailsToShow){
            db.collection("Trails")
                    .whereEqualTo("id", trail)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (final QueryDocumentSnapshot document : task.getResult()) {
                                    Picasso.get().load(document.get("urlPhoto").toString()).into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            Log.i(TAG,bitmap.toString());
                                            trails.add(bitmap);
                                            for (Bitmap bit : trails){
                                                Trail traill = new Trail(document.get("id").toString(),document.get("trailName").toString(),
                                                        document.get("address").toString(),document.get("description").toString(),document.get("duration").toString(),
                                                        document.get("distance").toString(),document.get("speed").toString(),
                                                        document.get("date").toString(),document.get("urlPhoto").toString(),(GeoPoint) document.get("coordStart"),
                                                        (GeoPoint) document.get("coordEnd"), (List<GeoPoint>) document.get("trailPoints"));


                                                imageModelArrayList.add(traill);

                                            }
                                            mHorizontalRecyclerView = (RecyclerView) root.findViewById(R.id.horizontalRecyclerView);
                                            horizontalAdapter = new HorizontalRecyclerViewAdapter(imageModelArrayList,trails, getContext());
                                            horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                                            mHorizontalRecyclerView.setLayoutManager(horizontalLayoutManager);
                                            mHorizontalRecyclerView.setAdapter((RecyclerView.Adapter) horizontalAdapter);
                                        }

                                        @Override
                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                        }
                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                                        }
                                    });

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }
    private void swapFragment(){

        DescriptionFragment descriptionFragment = new DescriptionFragment();
        Bundle args = new Bundle();
        args.putString("Duration", txtTimer.getText().toString());
        float meanSpeed = speedSum/speedCount;
        args.putString("Speed", String.valueOf(meanSpeed));
        args.putParcelable("CoordStart", allLatLngs.get(0));
        args.putParcelable("CoordEnd", allLatLngs.get(allLatLngs.size()-1));
        args.putParcelableArrayList("TrailPoints", allLatLngs);
        descriptionFragment.setArguments(args);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mapFragemnt, descriptionFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    public static void idPressed (GeoPoint coordsStart, GeoPoint coordsEnd, List<LatLng> points) {

        MapFragment mapF = new MapFragment();
        MapFragment.coordsStart = coordsStart;
        MapFragment.coordsEnd = coordsEnd;
        MapFragment.pointsChoose = points;
        MapFragment.trailPressed = true;
        mapF.showTrail();
    }

    public void  updateGhost()
    {





            final Handler h = new Handler();
            final int delay = 1000;

            h.postDelayed(new Runnable(){
                public void run(){
                    if(countIterTrail < pointsChoose.size()) {
                        nextPoint = pointsChoose.get(countIterTrail);
                        if (ghost != null) {
                            ghost.remove();
                        }
                        ghost = mMap.addMarker(new MarkerOptions().position(nextPoint).title("Ghost").icon(BitmapDescriptorFactory.fromResource(R.drawable.ghost)));
                        ghost.setPosition(nextPoint);
                        countIterTrail++;
                        //Log.i(TAG, " "+nextPoint);

                        h.postDelayed(this, delay);
                    }
                }
            }, delay);


    }


    public void  showTrail()
    {   trailPressed = true;
        mMap.clear();
        double lat1 = coordsStart.getLatitude();
        double lng1 = coordsStart.getLongitude ();
        LatLng latLng1 = new LatLng(lat1, lng1);
        double lat2 = coordsEnd.getLatitude();
        double lng2 = coordsEnd.getLongitude ();
        LatLng latLng2 = new LatLng(lat2, lng2);
        place1 = new MarkerOptions().position(latLng1).title("Location 1");
        place2 = new MarkerOptions().position(latLng2).title("Location 2");

        place1.icon(BitmapDescriptorFactory.fromResource(R.drawable.start));
        place2.icon(BitmapDescriptorFactory.fromResource(R.drawable.finish));
        mMap.addMarker(place1);
        mMap.addMarker(place2);
        mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(latLng1).
                tilt(60).
                zoom(18).
                bearing(20).
                build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        fromCoords = mMap.addMarker(place1);
        toCoords = mMap.addMarker(place2);

        for (int z = 0; z < pointsChoose.size() - 1; z++) {
            LatLng src = pointsChoose.get(z);
            LatLng dest = pointsChoose.get(z + 1);
            mMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(src.latitude, src.longitude),
                            new LatLng(dest.latitude, dest.longitude))
                    .width(20).color(Color.GREEN).geodesic(true));
        }
        //calculateDirections(fromCoords, toCoords);
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
            gauge.moveToValue(nCurrentSpeed);
            speedSum+=nCurrentSpeed;
            speedCount++;
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



        txtCurrentSpeed.setText(strCurrentSpeed + " " + strUnits);
    }

    private boolean useMetricUnits() {
        // TODO Auto-generated method stub
        CheckBox chkUseMetricUnits = (CheckBox) root.findViewById(R.id.chkMetricUnits);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        if(location != null)
        {
            if(onTrack) {
                LatLng newpos = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition.Builder positionBuilder = new CameraPosition.Builder();
                positionBuilder.target(newpos);
                positionBuilder.zoom(20f);
                positionBuilder.bearing(location.getBearing());
                positionBuilder.tilt(60);
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(positionBuilder.build()));


                PolylineOptions lineOptions = new PolylineOptions()
                        .add(new LatLng(previousLocation.getLatitude(), previousLocation.getLongitude()))
                        .add(new LatLng(location.getLatitude(), location.getLongitude()))
                        .color(Color.RED)
                        .width(20);
                // add the polyline to the map
                Polyline polyline = mMap.addPolyline(lineOptions);
                // set the zindex so that the poly line stays on top of my tile overlays
                polyline.setZIndex(1000);
                // add the poly line to the array so they can all be removed if necessary

                polylines.add(polyline);
                // add the latlng from this point to the array

                allLatLngs.add(new LatLng(location.getLatitude(), location.getLongitude()));

                // check if the positions added is a multiple of 100, if so, redraw all of the polylines as one line (this helps with rendering the map when there are thousands of points)


                previousLocation = location;
                CLocation myLocation = new CLocation(location, this.useMetricUnits());
                this.updateSpeed(myLocation);
            }
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
                            previousLocation = currentLocation;
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


