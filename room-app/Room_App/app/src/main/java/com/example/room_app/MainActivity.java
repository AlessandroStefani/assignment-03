package com.example.room_app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import android.os.Build;
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

import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final String MESSAGE_TRY = "Hello World!";
    private static CharSequence CONNECTED = "YES";
    private static CharSequence NOT_CONNECTED = "NO";
    private static int MIN_DEGREES = 0;
    private static int MAX_DEGREES = 100;
    private Button confirmBtn;
    private Button updateBtn;

    private Button disconnectBtn;
    private AutoCompleteTextView autoCompleteTextView;
    private TextView textIsConnected;
    private SeekBar seekBar;

    private int stateLed = 0;

    //variables for Bluetooth
    private ArrayList<String> devices = new ArrayList<>();
    private HashMap<String, String> mapAddress = new HashMap<>();
    private ArrayAdapter<String> arrayAdapter;
    private int statusBluetooth; // 0 if no device is connected otherwise 1

    private boolean deviceIsConnected = false;
    private BluetoothAdapter bluetoothAdapter;

    private BluetoothSocket bluetoothSocket;

    private OutputStream outputStream;

    private ArrayList<BluetoothDevice> nbDevice = new ArrayList<>();
    private final BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = null;

            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                nbDevice.add(device);

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {
                    int permissionCheck = checkSelfPermission("Manifest.permission.BLUETOOTH_CONNECT");
                    ;
                    if (permissionCheck != 0) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT,
                        }, PackageManager.PERMISSION_GRANTED);
                    }
                } else {
                    if (device.getName() != null && !devices.contains(device.getName())) {
                        devices.add(device.getName());
                        mapAddress.put(device.getName(), device.getAddress());
                        //logInfo(devices.toString());
                        logInfo(mapAddress.toString());
                    }
                }

                arrayAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.dropdown_item, devices);
                autoCompleteTextView.setAdapter(arrayAdapter);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        confirmBtn = findViewById(R.id.confirmBtn);
        updateBtn = findViewById(R.id.updateButton);
        disconnectBtn = findViewById(R.id.disconnectButton);
        textIsConnected = findViewById(R.id.textAskConnection);

        seekBar = findViewById(R.id.seekBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBar.setMin(MIN_DEGREES);
            seekBar.setMax(MAX_DEGREES);
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (deviceIsConnected) {
                    sendMessageToToast("You are sending this value" + progress);
                    sendToOutput("servo:"+Integer.toString(progress)+"\n");
                    seekBar.setMax(MAX_DEGREES);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        autoCompleteTextView = findViewById(R.id.selectionDevice);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        registerReceiver(br, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        setAllListener();

        logInfo("0) on create");
    }

    // check if the bluetooth is active
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
            int permissionCheck = checkSelfPermission("Manifest.permission.BLUETOOTH_SCAN");
            ;
            if (permissionCheck != 0) {
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
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
        logInfo("5) on destroy");
    }

    private void searchDevice() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) !=
                PackageManager.PERMISSION_GRANTED) {
            int permissionCheck = checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            permissionCheck += checkSelfPermission("Manifest.permission.BLUETOOTH_SCAN");
            if (permissionCheck != 0) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.BLUETOOTH_SCAN}, PackageManager.PERMISSION_GRANTED);
            }
        } else {
            bluetoothAdapter.startDiscovery();
        }
    }

    private void checkIfBluetoothIsOn() {
        logInfo("IL DISPOSITIVO è CONNESSO"+ deviceIsConnected);
        if (deviceIsConnected) {
            confirmBtn.setEnabled(true);
            disconnectBtn.setEnabled(true);
            seekBar.setEnabled(true);
        } else {
            confirmBtn.setEnabled(false);
            seekBar.setEnabled(false);
            disconnectBtn.setEnabled(false);
        }

        // Check if the bluetooth is enabled
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            statusBluetooth = 0;
            textIsConnected.setText(NOT_CONNECTED);
            autoCompleteTextView.setText(R.string.Select);
            arrayAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.dropdown_item, new ArrayList<>());
            autoCompleteTextView.setAdapter(arrayAdapter);
        } else {
            statusBluetooth = 1;
            textIsConnected.setText(CONNECTED);
        }
    }

    // sets all the listeners of app's component
    private void setAllListener() {
        updateBtn.setOnClickListener(v -> {
            checkIfBluetoothIsOn();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                int permissionCheck = checkSelfPermission("Manifest.permission.BLUETOOTH_SCAN");
                if (permissionCheck != 0) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.BLUETOOTH_SCAN}, PackageManager.PERMISSION_GRANTED);
                }
            } else {
                if (isBluetoothOnline()) {
                    bluetoothAdapter.cancelDiscovery();
                    devices.clear();
                    mapAddress.clear();
                    bluetoothAdapter.startDiscovery();
                }
            }
        });

        confirmBtn.setOnClickListener(v -> {
            if (stateLed == 0) {
                confirmBtn.setText("ON");
                stateLed = 1;
            } else {
                confirmBtn.setText("OFF");
                stateLed = 0;
            }
            logInfo("IL DISPOSITIVO è CONNESSO?" + deviceIsConnected);
            if(deviceIsConnected) {
                //logInfo(Integer.toString(stateLed)+"\n");
                if (stateLed == 0) {
                    sendToOutput("off"+"\n");
                } else if (stateLed == 1) {
                    sendToOutput("on"+"\n");
                }
            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // getting text selected at a certain position
                String deviceSel = parent.getItemAtPosition(position).toString();
                // showing the text selected
                Toast.makeText(MainActivity.this,
                        "You selected: " + deviceSel + " with addr: " + mapAddress.get(deviceSel),
                        Toast.LENGTH_SHORT).show();

                // creation of socket using a thread
                Thread th1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        createSocket(mapAddress.get(deviceSel));
                    }
                });

                th1.start();
            }
        });

        disconnectBtn.setOnClickListener(v -> {
            if (deviceIsConnected) {
                try {
                    sendMessageToToast("Closing socket");
                    bluetoothSocket.close();
                } catch (Exception e) {
                    sendMessageToToast("Error during close of socket");
                }

                autoCompleteTextView.setText(R.string.Select);
                confirmBtn.setEnabled(false);
                seekBar.setEnabled(false);
                disconnectBtn.setEnabled(false);
            }
        });


    }

    private void sendMessageToToast(String message) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createSocket(String address) {
        BluetoothDevice bd = bluetoothAdapter.getRemoteDevice(address);

        try {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                int permissionCheck = checkSelfPermission("Manifest.permission.BLUETOOTH_SCAN");
                if (permissionCheck != 0) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.BLUETOOTH_SCAN}, PackageManager.PERMISSION_GRANTED);
                }
            } else {
                sendMessageToToast("Creation of socket");
                bluetoothSocket = bd.createRfcommSocketToServiceRecord(MainActivity.MY_UUID);
            }
        } catch (Exception e) {
            deviceIsConnected = false;
            sendMessageToToast("Failed to create socket");
        }

        try {
            sendMessageToToast("Trying to connect...");
            bluetoothSocket.connect();
            deviceIsConnected = true;
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    checkIfBluetoothIsOn();
                }
            });
        } catch (Exception e) {
            sendMessageToToast("Failed to connect");
            deviceIsConnected = false;
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    checkIfBluetoothIsOn();
                }
            });
            try {
                bluetoothSocket.close();
            } catch (Exception e2) {
                sendMessageToToast("Error during close of socket");
            }
        }

        try {
            sendMessageToToast("Getting outputStream...");
            outputStream = bluetoothSocket.getOutputStream();
        } catch (Exception e) {
            sendMessageToToast("Failed to get outputStream");
            try {
                bluetoothSocket.close();
            } catch (Exception e2) {
                sendMessageToToast("Error during close of socket");
            }
        }

    }

    private void sendToOutput(String message) {
        try {
            sendMessageToToast("Sending message...");
            outputStream.write(message.getBytes());
        } catch (Exception e) {
            sendMessageToToast("Error during send a message");
            try {
                bluetoothSocket.close();
            } catch (Exception e2) {
                sendMessageToToast("Error during close of socket");
            }
        }
    }

    private static void logInfo(String message) {
        Log.i(MainActivity.class.getSimpleName(), message);
    }

}