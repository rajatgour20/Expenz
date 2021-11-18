package com.example.s;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class passcode extends AppCompatActivity implements View.OnClickListener {
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button button5;
    Button button6;
    Button button7;
    Button button8;
    Button button9;
    Button button0;
    ImageButton backbutton;
    String confirmpass = "";

    TextView mPinCodeField, PasscodeLockpromt;
    String pin = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);
        mPinCodeField = findViewById(R.id.pin_field);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 1);
        }

        //setup the keyboard
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);
        button9 = findViewById(R.id.button9);

        backbutton = findViewById(R.id.button_erase);
        PasscodeLockpromt = findViewById(R.id.passcodelock_prompt);

        button0.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);
        backbutton.setOnClickListener(this);
        SharedPreferences preferences = getSharedPreferences("p", MODE_PRIVATE);
        String finalpasscode = preferences.getString("passcode", "");
        if (!finalpasscode.equals("")) {
            PasscodeLockpromt.setText("ENTER PASSCODE");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button0) {
            mPinCodeField.setText(mPinCodeField.getText() + "0");
            check();
        } else if (v.getId() == R.id.button1) {
            mPinCodeField.setText(mPinCodeField.getText() + "1");
            check();
        } else if (v.getId() == R.id.button2) {
            mPinCodeField.setText(mPinCodeField.getText() + "2");
            check();
        } else if (v.getId() == R.id.button3) {
            mPinCodeField.setText(mPinCodeField.getText() + "3");
            check();
        } else if (v.getId() == R.id.button4) {
            mPinCodeField.setText(mPinCodeField.getText() + "4");
            check();
        } else if (v.getId() == R.id.button5) {
            mPinCodeField.setText(mPinCodeField.getText() + "5");
            check();
        } else if (v.getId() == R.id.button6) {
            mPinCodeField.setText(mPinCodeField.getText() + "6");
            check();
        } else if (v.getId() == R.id.button7) {
            mPinCodeField.setText(mPinCodeField.getText() + "7");
            check();
        } else if (v.getId() == R.id.button8) {
            mPinCodeField.setText(mPinCodeField.getText() + "8");
            check();
        } else if (v.getId() == R.id.button9) {
            mPinCodeField.setText(mPinCodeField.getText() + "9");
            check();
        } else if (v.getId() == R.id.button_erase) {
            if (mPinCodeField.getText().length() > 0)
                mPinCodeField.setText(mPinCodeField.getText().subSequence(0, mPinCodeField.getText().length() - 1));
        }
    }

    private void check() {
        SharedPreferences preferences = getSharedPreferences("p", MODE_PRIVATE);
        String finalpasscode = preferences.getString("passcode", "");
        if (!finalpasscode.equals("")) {
            if (mPinCodeField.getText().length() == 4) {
                String mpin = mPinCodeField.getText().toString();
                if (finalpasscode.trim().equals(mpin.trim())) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Wrong Passcode", Toast.LENGTH_SHORT).show();
                    PasscodeLockpromt.setText("AGAIN ENTER YOUR PASSCODE");
                    mPinCodeField.setText("");
                }
            }
        } else {
            if (pin.equals("")) {
                if (mPinCodeField.getText().length() == 4) {
                    pin = mPinCodeField.getText().toString();
                    mPinCodeField.setText("");
                    PasscodeLockpromt.setText("CONFIRM PASSCODE");
                }
            } else {

                if (mPinCodeField.getText().length() == 4) {
                    if (pin.equals(mPinCodeField.getText().toString().trim())) {
                        startActivity(new Intent(this, MainActivity.class));
                        SharedPreferences.Editor editor = getSharedPreferences("p", MODE_PRIVATE).edit();
                        editor.putString("passcode", pin);
                        editor.commit();

                        finish();
                    } else {
                        Toast.makeText(this, "Passcode didn't Match", Toast.LENGTH_SHORT).show();
                        pin = "";
                        PasscodeLockpromt.setText("AGAIN SET YOUR PASSCODE");
                        mPinCodeField.setText("");
                    }
                }
            }
        }
    }
}