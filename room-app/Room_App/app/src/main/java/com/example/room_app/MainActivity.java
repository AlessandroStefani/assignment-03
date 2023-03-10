package com.example.room_app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static CharSequence CONNECTED = "YES";
    private static CharSequence NOT_CONNECTED = "NO";
    private Button confirmBtn;
    private ArrayList<String> devices = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private AutoCompleteTextView autoCompleteTextView;
    private TextView textIsConnected;
    private SeekBar seekBar;

    private int stateLed = 0;
    //all variable for Bluetooth
    private int statusBluetooth; // 0 if no device is connected otherwise 1
    private BluetoothAdapter bluetoothAdapter;

    private ArrayList<BluetoothDevice> nbDevice = new ArrayList<>();
    private final BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = null;
            logInfo("VENGO CHIAMATO??????");

            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                logInfo("Il device esiste?" + (device != null));
                nbDevice.add(device);

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {
                    int permissionCheck = checkSelfPermission("Manifest.permission.BLUETOOTH_CONNECT");;
                    if (permissionCheck != 0 ) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT,
                         }, PackageManager.PERMISSION_GRANTED);
                    }
                } else {
                    // Controlla se il nome del dispositivo trovato non ?? nullo
                    // e che non lo contenga gi?? nell'arraylist
                    if (device.getName() != null && !devices.contains(device.getName())) {
                        devices.add(device.getName());
                        logInfo(devices.toString());
                    }
                }

                arrayAdapter = new ArrayAdapter<String>(MainActivity.this,R.layout.dropdown_item,devices);
                autoCompleteTextView.setAdapter(arrayAdapter);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        confirmBtn = findViewById(R.id.confirmBtn);
        textIsConnected = findViewById(R.id.textAskConnection);
        seekBar = findViewById(R.id.seekBar);
        autoCompleteTextView = findViewById(R.id.selectionDevice);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        registerReceiver(br, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        confirmBtn.setText("OFF");

        logInfo("0) on create");
    }

    // Controlla se il Bluetooth ?? attivo
    private boolean isBluetoothOnline() {
        return (bluetoothAdapter != null || bluetoothAdapter.isEnabled());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isBluetoothOnline()) {
            searchDevice();
        }
        logInfo("1) on start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfBluetoothIsOn();

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            int permissionCheck = checkSelfPermission("Manifest.permission.BLUETOOTH_SCAN");;
            if (permissionCheck != 0 ) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_SCAN,
                }, PackageManager.PERMISSION_GRANTED);
            }
            return;
        } else {
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
        }

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
        unregisterReceiver(br);
        logInfo("5) on destroy");
    }

    private void searchDevice() {
        // Questo if serve per capire se abbiamo garantito i permessi o meno, se non lo abbiamo fatto
        // chiede all'utente di darglieli e se li ha gi?? inizia a scoprire i device attorno
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) !=
                PackageManager.PERMISSION_GRANTED) {
            int permissionCheck = checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            permissionCheck += checkSelfPermission("Manifest.permission.BLUETOOTH_SCAN");
            if (permissionCheck != 0 ) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.BLUETOOTH_SCAN}, PackageManager.PERMISSION_GRANTED);
            }
        } else {
            bluetoothAdapter.startDiscovery();
            if (bluetoothAdapter.isDiscovering()) {
                logInfo("" + (nbDevice == null));
                logInfo("SIIII STA SCOPRENDO COSE");
            } else {
                logInfo("NOOO NON STA SCOPRENDO COSE");
            }
        }
    }

    private void checkIfBluetoothIsOn() {
        // Check if the bluetooth is enabled
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            statusBluetooth = 0;
            textIsConnected.setText(NOT_CONNECTED);
            confirmBtn.setActivated(false);
            seekBar.setActivated(false);
            arrayAdapter = new ArrayAdapter<String>(MainActivity.this,R.layout.dropdown_item,new ArrayList<>());
            autoCompleteTextView.setAdapter(arrayAdapter);
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
    }

    private void createSocket() {

    }

    private void sendToOutput(String message) {

    }

    private static void logInfo(String message) {
        Log.i(MainActivity.class.getSimpleName(), message);
    }

}