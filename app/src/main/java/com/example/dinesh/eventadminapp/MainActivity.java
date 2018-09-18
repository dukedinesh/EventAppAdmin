package com.example.dinesh.eventadminapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button NewEvents, ExistingEvents;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = (Toolbar) findViewById(R.id.toolbar);

        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("Event Admin");

        NewEvents = (Button)findViewById(R.id.new_event_btn);
        ExistingEvents = (Button)findViewById(R.id.manage_events);


        NewEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,NewEvent.class);
                startActivity(intent);



            }
        });


        ExistingEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,ExistingActivity.class);
                startActivity(intent);



            }
        });
    }
}
