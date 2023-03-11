package com.example.room_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import android.bluetooth.*;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity {
    private static CharSequence CONNECTED = "YES";
    private static CharSequence NOT_CONNECTED = "NO";
    private Button confirmBtn;
    private String[] devices;
    private ArrayAdapter<String> arrayAdapter;
    private AutoCompleteTextView autoCompleteTextView;
    private TextView textIsConnected;
    private SeekBar seekBar;

    private int stateLed = 0;
    //all variable for Bluetooth
    private int statusBluetooth; // 0 if no device is connected otherwise 1
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        confirmBtn = findViewById(R.id.confirmBtn);
        textIsConnected = findViewById(R.id.textAskConnection);
        seekBar = findViewById(R.id.seekBar);
        autoCompleteTextView = findViewById(R.id.selectionDevice);

        confirmBtn.setText("OFF");

        logInfo("0) on create");
    }

    @Override
    protected void onStart() {
        super.onStart();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Check if the bluetooth is enabled
        if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            statusBluetooth = 0;
            textIsConnected.setText(NOT_CONNECTED);
            confirmBtn.setActivated(false);
            seekBar.setActivated(false);
        } else {
            statusBluetooth = 1;
            textIsConnected.setText(CONNECTED);
            confirmBtn.setActivated(true);
            seekBar.setActivated(true);

            confirmBtn.setOnClickListener(v -> {
                if (stateLed == 0) {
                    confirmBtn.setText("OFF");
                    stateLed = 1;
                } else {
                    confirmBtn.setText("ON");
                    stateLed = 0;
                }
            });
        }

        logInfo("1) on start");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Creazione dei array di stringhe fatto qui
        devices = getResources().getStringArray(R.array.Device);

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, devices);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // getting text selected at a certain position
                String label = parent.getItemAtPosition(position).toString();
                // showing the text selected
                Toast.makeText(MainActivity.this, "You selected: " + label, Toast.LENGTH_SHORT).show();
            }
        });

        logInfo("2) on resume (the activity comes to foreground)");
    }

    @Override
    protected void onPause() {
        super.onPause();
        logInfo("3) on pause (the user is leaving the activity)");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        logInfo("3-1) on restart");
    }


    @Override
    protected void onStop() {
        super.onStop();
        logInfo("4) on stop (the activity is no longer visible to the user)");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        logInfo("4-5) on save instance");
//        savedInstanceState.putString("username", usernameTextView.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logInfo("5) on destroy");
    }

    private void searchDevice() {

    }

    private void createSocket() {

    }

    private void sendToOutput(String message) {

    }

    private static void logInfo(String message) {
        Log.i(MainActivity.class.getSimpleName(), message);
    }

}