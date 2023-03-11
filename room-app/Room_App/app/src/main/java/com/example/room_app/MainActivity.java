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
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity {
    private Button confirmBtn;
    private String[] devices;
    private ArrayAdapter<String> arrayAdapter;
    private AutoCompleteTextView autoCompleteTextView;
    //private EditText usernameEditText;
    //private TextView usernameTextView;

    private int stateLed = 0;
    private int statusBluetooth = 0; // 0 if no device is connected otherwise 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //usernameEditText = findViewById(R.id.usernameEditText);
        confirmBtn = findViewById(R.id.confirmBtn);
        autoCompleteTextView = findViewById(R.id.selectionDevice);

        confirmBtn.setText("OFF");

        //usernameTextView = findViewById(R.id.usernameTextView);
        if(savedInstanceState != null) {
            //usernameTextView.setText(savedInstanceState.getString("username"));
        }
        logInfo("0) on create");
    }

    @Override
    protected void onStart() {
        super.onStart();

        confirmBtn.setOnClickListener(v -> {
            //final String username = usernameEditText.getText().toString();
            if (stateLed == 0) {
                confirmBtn.setText("OFF");
                stateLed = 1;
            } else {
                confirmBtn.setText("ON");
                stateLed = 0;
            }
            //usernameTextView.setText("Hello ".concat(username));
        });
        logInfo("1) on start");
    }

    @Override
    protected void onResume() {
        super.onResume();

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
                statusBluetooth = 1;
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

    private static void logInfo(String message) {
        Log.i(MainActivity.class.getSimpleName(), message);
    }

}