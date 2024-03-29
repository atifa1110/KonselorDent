package com.dentist.konselorhalodent.SignIn;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.dentist.konselorhalodent.Model.Konselors;
import com.dentist.konselorhalodent.Model.NodeNames;
import com.dentist.konselorhalodent.R;
import com.dentist.konselorhalodent.Utils.NetworkChangeListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navView;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.KONSELORS).child(currentUser.getUid());

        databaseReference.child(NodeNames.STATUS).setValue("Online");
        databaseReference.child(NodeNames.STATUS).onDisconnect().setValue("Offline");

        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_chat, R.id.navigation_info, R.id.navigation_profile).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_bottom);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
        databaseReference.child(NodeNames.STATUS).setValue("Online");
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkChangeListener);
        databaseReference.child(NodeNames.STATUS).onDisconnect().setValue("Offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.child(NodeNames.STATUS).onDisconnect().setValue("Offline");
    }
}