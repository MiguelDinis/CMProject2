package com.example.ghostrunner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.example.ghostrunner.firebase.SignInActivity;
import com.example.ghostrunner.models.Trail;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity  implements GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "MainActivity";
    public static final String ANONYMOUS = "anonymous";
    private String userId;

    //Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseFirestore db;

    private CollectionReference usersRef;
    private String mUsername;
    private String mPhotoUrl;
    private GoogleApiClient mGoogleApiClient;
    private List<Trail> trails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_map,  R.id.navigation_runners, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        /*
        //TRAILS TO TEST
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);

        trails = new ArrayList<>();
        trails.add(new Trail( "1",  "QMTp3OavCPVSZV5e1G7x5lEugG03",  "Um nome qualquer",  "endereço",  "asdaddas", "1.5km", formattedDate,  "https://contents.mediadecathlon.com/p1427463/640x0/27cr14/trail.jpg?k=3b52640a69d7a4dbb395121267e6ab91"));
        trails.add(new Trail( "2",  "QMTp3OavCPVSZV5e1G7x5lEugG03",  "Um nome qualquer",  "endereço",  "asdaddas", "1.5km", formattedDate,  "https://revistaatletismo.com/wp-content/uploads/2017/11/bast%C3%B5es.jpg"));
        trails.add(new Trail( "3",  "QMTp3OavCPVSZV5e1G7x5lEugG03",  "Um nome qualquer",  "endereço",  "asdaddas", "1.5km", formattedDate,  "https://www.traildozezere.com/uploads/2/1/8/6/21864160/p2_orig.png"));
        trails.add(new Trail( "4",  "QMTp3OavCPVSZV5e1G7x5lEugG03",  "Um nome qualquer",  "endereço",  "asdaddas", "1.5km", formattedDate,  "https://portalbr.akamaized.net/brasil/uploads/2018/02/02123404/shutterstock_501368236-1.jpg"));
           */
        //Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        /*
        for(Trail trail : trails){
            db.collection("Trails").document(trail.getId()).set(trail);
        }
           */
        //Populate db


        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            usersRef = db.collection("Users");
            userId = mFirebaseUser.getUid();
            //Log.i(TAG,userId);
            Task<QuerySnapshot> findUserQuery = usersRef.whereEqualTo("id",userId).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (mFirebaseUser.getPhotoUrl() != null) {
                                    mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
                                }
                                //If is the first time this user is authenticated
                                if(task.getResult().size() == 0){
                                    Intent initUserConfig = new Intent(getApplicationContext(), InitUserConfigs.class);
                                    initUserConfig.putExtra("id", userId);
                                    initUserConfig.putExtra("username", mUsername);
                                    initUserConfig.putExtra("imgUrl", mPhotoUrl);
                                    startActivity(initUserConfig);
                                }else{

                                }

                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,  this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    //Called by button on ProfileFragment
    public void signOutActivated(){
        mFirebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        mUsername = ANONYMOUS;
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }



    public String getUrlPhoto(){
        return mPhotoUrl;
    }

    public String getUserName(){
        return mUsername;
    }

    public String getUserId(){
        return userId;
    }


}
