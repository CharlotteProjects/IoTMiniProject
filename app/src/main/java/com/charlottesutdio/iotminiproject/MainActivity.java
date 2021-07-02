package com.charlottesutdio.iotminiproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

    //region FireBase Setting

    private DatabaseReference database_LightMode;

    private final String path_Main_Manager = "Manager";
    private final String path_LEDOn = "LightOn";
    private final String path_FanOn = "FanOn";
    private final String path_LED = "LightMode";
    private final String path_Fan = "FanMode";
    private final String path_Temp = "NowTemp";
    private final String path_Humi = "NowHumi";

    private boolean afterInti = false;

    //endregion

    //region UI Setting

    private Spinner spinner_LightMode;
    private Spinner spinner_FanMode;
    private int nowLEDmode = 0;
    private int nowFanmode = 0;

    private TextView text_LEDOn;
    private TextView text_FanOn;
    private TextView text_LightMode;
    private TextView text_FanMode;
    private TextView text_NowTemp;
    private TextView text_NowHumi;

    private ImageView image_LEDOn;
    private ImageView image_FanOn;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_LEDOn = (TextView) findViewById(R.id.text_LightStateText);
        text_FanOn = (TextView) findViewById(R.id.text_FanStateText);
        text_LightMode = (TextView) findViewById(R.id.text_YourLightMode);
        text_FanMode = (TextView) findViewById(R.id.text_YourFanMode);
        text_NowTemp = (TextView) findViewById(R.id.text_DHTTemp);
        text_NowHumi = (TextView) findViewById(R.id.text_DHTHumi);

        image_LEDOn = (ImageView) findViewById(R.id.image_LightState);
        image_FanOn = (ImageView) findViewById(R.id.image_FanState);

        init_Firebase();

        init_SpinnerLightMode();
    }

    // init the Firebase get set data
    void init_Firebase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database_LightMode = database.getReference(path_Main_Manager);

        // Get Light On First
        database_LightMode.child(path_LEDOn).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if(Boolean.parseBoolean(task.getResult().getValue().toString())){
                        text_LEDOn.setText(R.string.on);
                        text_LEDOn.setTextColor(Color.GREEN);
                        image_LEDOn.setImageResource(R.drawable.light_on);
                    } else{
                        text_LEDOn.setText(R.string.off);
                        text_LEDOn.setTextColor(Color.RED);
                        image_LEDOn.setImageResource(R.drawable.light_off);
                    }
                }
                else {
                    Log.e(TAG, "msg: Error getting LED on data", task.getException());
                }
            }
        });

        // Get Fan OFF First
        database_LightMode.child(path_FanOn).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if(Boolean.parseBoolean(task.getResult().getValue().toString())){
                        text_FanOn.setText(R.string.on);
                        text_FanOn.setTextColor(Color.GREEN);
                        image_FanOn.setImageResource(R.drawable.fan_on);
                    } else{
                        text_FanOn.setText(R.string.off);
                        text_FanOn.setTextColor(Color.RED);
                        image_FanOn.setImageResource(R.drawable.fan_off);
                    }
                }
                else {
                    Log.e(TAG, "msg: Error getting Fan on data", task.getException());
                }
            }
        });

        // Get Light Mode First
        database_LightMode.child(path_LED).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    nowLEDmode = Integer.parseInt(task.getResult().getValue().toString());
                    setLightModeText(nowLEDmode);
                    afterInti = true;
                    Log.d(TAG, "msg: Get Data successful : " + nowLEDmode);
                }
                else {
                    Log.e(TAG, "msg: Error getting data", task.getException());
                }
            }
        });

        // Get Fan Mode First
        database_LightMode.child(path_Fan).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    nowFanmode = Integer.parseInt(task.getResult().getValue().toString());
                    setFanModeText(nowFanmode);
                    afterInti = true;
                    Log.d(TAG, "msg: Get Data successful : " + nowFanmode);
                }
                else {
                    Log.e(TAG, "msg: Error getting data", task.getException());
                }
            }
        });

        // Set the Listener for checking Light ON / OFF
        database_LightMode.child(path_LEDOn).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class)) {
                    text_LEDOn.setText(R.string.on);
                    text_LEDOn.setTextColor(Color.GREEN);
                    image_LEDOn.setImageResource(R.drawable.light_on);
                } else{
                    text_LEDOn.setText(R.string.off);
                    text_LEDOn.setTextColor(Color.RED);
                    image_LEDOn.setImageResource(R.drawable.light_off);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG,"msg: Light On Setting get wrong data.");
            }
        });

        // Set the Listener for checking Fan ON / OFF
        database_LightMode.child(path_FanOn).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class)) {
                    text_FanOn.setText(R.string.on);
                    text_FanOn.setTextColor(Color.GREEN);
                    image_FanOn.setImageResource(R.drawable.fan_on);
                } else{
                    text_FanOn.setText(R.string.off);
                    text_FanOn.setTextColor(Color.RED);
                    image_FanOn.setImageResource(R.drawable.fan_off);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG,"msg: Fan On Setting get wrong data.");
            }
        });

        // Set the Listener for checking Light Mode
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
                    setLightModeText(value);
                    Log.d(TAG,"msg: " + st);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG,"msg: Light Mode Setting get wrong data.");
            }
        });

        // Set the Listener for checking Fan Mode
        database_LightMode.child(path_Fan).addValueEventListener(new ValueEventListener() {
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
                            mode = "Opening Mode";
                            break;
                        case 2:
                            mode = "Closing Mode";
                            break;
                        default:
                            mode = "";
                            Log.e(TAG,"msg: Fan Mode Setting get wrong number.");
                            break;
                    }
                    String st = "Change the Fan Mode to : " + mode;
                    Toast.makeText(MainActivity.this, st, Toast.LENGTH_LONG).show();
                    setFanModeText(value);
                    Log.d(TAG,"msg: " + st);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG,"msg: Light Mode Setting get wrong data.");
            }
        });

        // Set the Listener for checking Now Temp
        database_LightMode.child(path_Temp).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(afterInti){
                    Float value = dataSnapshot.getValue(Float.class);
                    String st = value + " °C";
                    text_NowTemp.setText(st);
                    Log.d(TAG,"msg: Get the temp is : " + value);
                } else{
                    text_NowTemp.setText("- °C");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG,"msg: Get wrong temp data.");
            }
        });

        database_LightMode.child(path_Humi).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(afterInti){
                    Float value = dataSnapshot.getValue(Float.class);
                    String st = value + " %";
                    text_NowHumi.setText(st);
                    Log.d(TAG,"msg: Get the temp is : " + value);
                } else{
                    text_NowTemp.setText("- °C");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG,"msg: Get wrong humi data.");
            }
        });
    }

    // init the Light Mode Spinner
    void init_SpinnerLightMode(){

        //region init Spinner Light Mode

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

        //endregion

        //region init Spinner Fan Mode

        spinner_FanMode = (Spinner) findViewById(R.id.spinner_Fan);
        ArrayAdapter<CharSequence> adapterFan = ArrayAdapter.createFromResource(
                this,
                R.array.drop_down_Fan,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_FanMode.setAdapter(adapterFan);

        // init the spinner Listener, will send the choose to firebase.
        spinner_FanMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(afterInti){
                    database_LightMode.child(path_Fan).setValue(position);
                    Log.d(TAG,"msg: You selected the position is : " + position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set spinner position by Firebase data
        spinner_FanMode.setSelection(nowFanmode);

        //endregion

    }

    // set the Light Mode Text
    void setLightModeText(int number) {
        switch (number) {
            case 0:
                text_LightMode.setText("Auto Mode");
                text_LightMode.setTextColor(Color.BLUE);
                break;
            case 1:
                text_LightMode.setText("Lighting Mode");
                text_LightMode.setTextColor(Color.GREEN);
                break;
            case 2:
                text_LightMode.setText("Closing Mode");
                text_LightMode.setTextColor(Color.RED);
                break;
            default:
                Log.d(TAG, "mas: wrong number input to Light Mode Text.");
                break;
        }
    }

    // set the Light Mode Text
    void setFanModeText(int number) {
        switch (number) {
            case 0:
                text_FanMode.setText("Auto Mode");
                text_FanMode.setTextColor(Color.BLUE);
                break;
            case 1:
                text_FanMode.setText("OpeningMode");
                text_FanMode.setTextColor(Color.GREEN);
                break;
            case 2:
                text_FanMode.setText("Closing Mode");
                text_FanMode.setTextColor(Color.RED);
                break;
            default:
                Log.d(TAG, "mas: wrong number input to Fan Mode Text.");
                break;
        }
    }

}