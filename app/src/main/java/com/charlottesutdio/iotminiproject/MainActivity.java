package com.charlottesutdio.iotminiproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private DatabaseReference database_LightMode;

    private final String path_Main_Manager = "Manager";
    private final String path_LED = "LEDmode";

    private Spinner spinner_LightMode;
    private int nowLEDmode = 0;

    private boolean afterInti = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_Firebase();

        init_SpinnerLightMode();
    }

    // init the Firebase get set data
    void init_Firebase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database_LightMode = database.getReference(path_Main_Manager);

        // When onCreate will get the data first and set the choosed
        database_LightMode.child(path_LED).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    nowLEDmode = Integer.parseInt(task.getResult().getValue().toString());
                    afterInti = true;
                    Log.d(TAG, "msg: Get Data successful : " + nowLEDmode);
                }
                else {
                    Log.e(TAG, "msg: Error getting data", task.getException());
                }
            }
        });

        // Set the Listener for check when the value change successful.
        database_LightMode.child(path_LED).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(afterInti){
                    Integer value = dataSnapshot.getValue(Integer.class);
                    String mode;
                    switch(value){
                        case 0:
                            mode = "Auto Mode";
                            break;
                        case 1:
                            mode = "Lighting Mode";
                            break;
                        case 2:
                            mode = "Closing Mode";
                            break;
                        default:
                            mode = "";
                            Log.e(TAG,"msg: Light Mode Setting get wrong number.");
                            break;
                    }
                    String st = "Change the Light Mode to : " + mode;
                    Toast.makeText(MainActivity.this, st, Toast.LENGTH_LONG).show();
                    Log.d(TAG,"msg: " + st);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG,"msg: Light Mode Setting get wrong data.");
            }
        });
    }

    // init the Light Mode Spinner
    void init_SpinnerLightMode(){
        spinner_LightMode = (Spinner) findViewById(R.id.spinner_Light);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.drop_down_Light,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_LightMode.setAdapter(adapter);

        // init the spinner Listener, will send the choose to firebase.
        spinner_LightMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(afterInti){
                    database_LightMode.child(path_LED).setValue(position);
                    Log.d(TAG,"msg: You selected the position is : " + position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set spinner position by Firebase data
        spinner_LightMode.setSelection(nowLEDmode);
    }
}