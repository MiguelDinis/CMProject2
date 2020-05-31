package com.example.cmprojeto.ui.map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cmprojeto.HorizontalRecyclerViewAdapter;
import com.example.cmprojeto.ImageModel;
import com.example.cmprojeto.R;
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
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Toolbar toolbar;
    private RecyclerView mHorizontalRecyclerView;
    private HorizontalRecyclerViewAdapter horizontalAdapter;
    private LinearLayoutManager horizontalLayoutManager;
    private MarkerOptions place1, place2;
    private Polyline currentPolyline;
    SupportMapFragment mapFragment;
    private GeoApiContext mGeoApiContext;
    private Marker paraIR;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        place1 = new MarkerOptions().position(new LatLng(40.048611, -8.890201)).title("Location 1");
        place2 = new MarkerOptions().position(new LatLng(40.155185, -8.867252)).title("Location 2");

        place1.icon(BitmapDescriptorFactory.fromResource(R.drawable.start));
        place2.icon(BitmapDescriptorFactory.fromResource(R.drawable.finish));

        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mHorizontalRecyclerView = (RecyclerView) root.findViewById(R.id.horizontalRecyclerView);
        horizontalAdapter = new HorizontalRecyclerViewAdapter(fillWithData(), getContext());

        horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mHorizontalRecyclerView.setLayoutManager(horizontalLayoutManager);
        mHorizontalRecyclerView.setAdapter((RecyclerView.Adapter) horizontalAdapter);

        mapFragment.getMapAsync(this);

        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }
        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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
        paraIR = mMap.addMarker(place2);
        calculateDirections(paraIR);

    }

    //Directions and polylines to map

    private void calculateDirections(Marker marker){
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        40.048611,
                        -8.890201
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

        ImageModel imageModel1 = new ImageModel();
        imageModel1.setId(System.currentTimeMillis());
        imageModel1.setImageName("Trail 2");
        imageModel1.setImagePath(R.drawable.pic2);
        imageModelArrayList.add(imageModel1);

        ImageModel imageModel2 = new ImageModel();
        imageModel2.setId(System.currentTimeMillis());
        imageModel2.setImageName("Trail 3");
        imageModel2.setImagePath(R.drawable.pic3);
        imageModelArrayList.add(imageModel2);

        ImageModel imageModel3 = new ImageModel();
        imageModel3.setId(System.currentTimeMillis());
        imageModel3.setImageName("Trail 4");
        imageModel3.setImagePath(R.drawable.pic4);
        imageModelArrayList.add(imageModel3);

        ImageModel imageModel4 = new ImageModel();
        imageModel4.setId(System.currentTimeMillis());
        imageModel4.setImageName("Trail 5");
        imageModel4.setImagePath(R.drawable.pic5);
        imageModelArrayList.add(imageModel4);

        ImageModel imageModel5 = new ImageModel();
        imageModel5.setId(System.currentTimeMillis());
        imageModel5.setImageName("Trail 6");
        imageModel5.setImagePath(R.drawable.pic6);
        imageModelArrayList.add(imageModel5);
        return imageModelArrayList;
    }


}


