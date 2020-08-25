package com.marsanpat.alba;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.marsanpat.alba.Controller.MessageController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_log, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        attemptClientConnection();
    }

    private void attemptClientConnection(){
        //Trying to connect with remote server
        MessageController controller = MessageController.getInstance();
        int returnCode = controller.startClient();
        if(returnCode == 0){
            Toast.makeText(getBaseContext(),"Connection with server successful", Toast.LENGTH_LONG).show();
        }else if(returnCode == -1){
            Toast.makeText(getBaseContext(),"Couldn't connect with remote server", Toast.LENGTH_LONG).show();
        }
    }

}