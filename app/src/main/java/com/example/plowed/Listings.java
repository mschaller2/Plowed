package com.example.plowed;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Set;

import static com.example.plowed.App.CHANNEL_1_ID;

public class Listings extends AppCompatActivity {
    private FirebaseUser mUser;
    private TextView welcome;
    private NotificationManagerCompat notificationManager;
    private RecyclerView listings;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<ArrayList<String>> mDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataset = new ArrayList<>();
        setContentView(R.layout.listings_activity);
        welcome = (TextView) findViewById(R.id.listings);
        listings = (RecyclerView) findViewById(R.id.db_plowings);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        notificationManager = NotificationManagerCompat.from(this);
        userConfig();
        fetchListingsFromDB();

    }

    private void fetchListingsFromDB(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("assignments");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDataset.clear();
                for (DataSnapshot name : dataSnapshot.getChildren()){
                    ArrayList<String> temp = new ArrayList<>();
                    for (DataSnapshot listing : name.getChildren()){
                        temp.add(listing.getValue().toString());
                    }
                    mDataset.add(temp);
                }
                layoutManager = new LinearLayoutManager(getBaseContext()); // base context wrong?
                listings.setLayoutManager(layoutManager);
                mAdapter = new MyAdapter(mDataset);
                listings.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //Using this function from the user menu to dynamically adjust the welcome text
    private void userConfig(){
        // may be empty
        if (mUser.getDisplayName() != null){
            int ratingTotal = 0;
            if (getIntent().getParcelableArrayListExtra("ratings") != null){
               Object[] ratings = getIntent().getParcelableArrayListExtra("ratings").toArray();
                for (Object rating : ratings){
                    ratingTotal += (Integer.parseInt(rating.toString()));
                }
                double ratingAverage = (double) ratingTotal / ratings.length;
                DecimalFormat df = new DecimalFormat("#.##");
                welcome.setText(String.format(Locale.ENGLISH, "Listings for: %s\n" +
                                "Average Rating: %s\nTap on a listing for options\n",
                        mUser.getDisplayName(), df.format(ratingAverage)));
            }else{
                welcome.setText(String.format(Locale.ENGLISH, "Listings for: %s\nTap on a " +
                                "listing for options",
                        mUser.getDisplayName()));
            }
        }else{
            welcome.setText(R.string.welcome);
        }
    }
}
class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
    private ArrayList<ArrayList<String>> mDataset;

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public MyViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }
    public MyAdapter(ArrayList<ArrayList<String>> myDataset){
        mDataset = myDataset;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.individual_listing, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Assumes only one request/user which is reasonable but has edges
    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, final int position) {
        // fiddle with this
        final DatabaseReference db = FirebaseDatabase.getInstance().
                getReference(String.format("assignments/%s", mDataset.get(position).get(3)));
        holder.textView.setText(String.format("\nEmail: %s\nAddress: %s\nClient: %s",
                mDataset.get(position).get(1), mDataset.get(position).get(0),
                    mDataset.get(position).get(2)));
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder test = new AlertDialog.Builder(v.getContext());
                test.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                test.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.removeValue();
                        dialog.dismiss();
                    }
                });
                test.setIcon(R.drawable.logo);
                test.setMessage("Accept this listing?");
                test.create();
                test.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
