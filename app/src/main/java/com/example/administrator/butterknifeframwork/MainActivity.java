package com.example.administrator.butterknifeframwork;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.BindView;
import com.example.administrator.butterknifeframwork.R;
import com.example.inject.InjectView;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.text1)
    TextView textview1;
    @BindView(R.id.text2)
    TextView textview2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InjectView.bind(this);
        textview1.setText("666666");
        Toast.makeText(this,"--->  "+textview1,Toast.LENGTH_SHORT).show();
    }
}
