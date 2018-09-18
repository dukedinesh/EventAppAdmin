package com.example.dinesh.eventadminapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ExistingActivity extends AppCompatActivity {

    private RecyclerView mUploadList;
    ProgressBar progressBar;
    private DatabaseReference mUsersDatabase;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing);


        toolbar = (Toolbar) findViewById(R.id.toolbar);

        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("Events");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUploadList = (RecyclerView) findViewById(R.id.upload_list);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Events");


        progressBar.setVisibility(View.VISIBLE);



        mUploadList.setLayoutManager(new LinearLayoutManager(this));
        mUploadList.setHasFixedSize(true);

        FirebaseRecyclerAdapter<UsersEventData, FriendsViewHolder> friendsRecyclerView = new FirebaseRecyclerAdapter<UsersEventData, FriendsViewHolder>(

                UsersEventData.class,
                R.layout.single_event_requested,
                FriendsViewHolder.class,
                mUsersDatabase
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, UsersEventData model, int position) {

                final TextView btn = (TextView) viewHolder.itemView.findViewById(R.id.event);
                final String list_user_id = getRef(position).getKey();

                if (getItemCount() > 0) {


                    mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            final String userName = dataSnapshot.child("event_name").getValue().toString();

                            viewHolder.setName(userName);


                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                   /* Intent intent = new Intent(this, ReqSingleEvents.class);
                                    intent.putExtra("event_id", list_user_id);
                                    startActivity(intent);*/

                                }
                            });

                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                  /*  Intent intent = new Intent(this, ReqSingleEvents.class);
                                    intent.putExtra("event_id", list_user_id);
                                    startActivity(intent);*/

                                }
                            });

                            progressBar.setVisibility(View.GONE);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        };

        mUploadList.setAdapter(friendsRecyclerView);

    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }


        public void setName(String name) {
            TextView userName = (TextView) mView.findViewById(R.id.event);
            userName.setText(name);
        }


    }

}

