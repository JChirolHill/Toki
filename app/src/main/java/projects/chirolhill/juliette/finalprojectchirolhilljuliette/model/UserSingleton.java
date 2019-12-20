package projects.chirolhill.juliette.finalprojectchirolhilljuliette.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UserSingleton {
    private static final String TAG = "UserSingleton";
    public static final String PREF_USER_ID = "pref_user_id";

    private static UserSingleton singleton;

    private String uID;
    private Map<String, MissionStats> stats;
    private final String [] types = {"grammar", "vocab", "listening", "speaking", "reading", "writing"};

    private DatabaseReference usersRef;
    private FirebaseDatabase firebase;

    private UserSingleton(String uID) {
        // set data members
        this.uID = uID;
        stats = new HashMap();

        // set up connection to DB
        firebase = FirebaseDatabase.getInstance();
        usersRef = firebase.getReference("userCompletion");
    }

    // if not yet instantianted, need to fetch info from DB
    public static UserSingleton getInstance(Context c) {
        if(singleton == null) {
            // get user id from prefs
            SharedPreferences prefs = c.getSharedPreferences("Settings", Context.MODE_PRIVATE);

            // create singleton obj
            singleton = new UserSingleton(prefs.getString(PREF_USER_ID, "123"));
        }
        // fill in info from DB to stay up to date
        singleton.fetchFromDB();

        return singleton;
    }

    // refresh stats map based on value from DB
    private void fetchFromDB() {
        usersRef.child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.d(TAG, "datasnapshotvalue: " + dataSnapshot.getValue().toString());
                for(int i=0; i<types.length; i++) {
                    if(dataSnapshot.child(types[i]).getValue() != null) {
                        if(!stats.containsKey(types[i])) { // need to create this item in map first if DNE
                            stats.put(types[i], new MissionStats(types[i]));
                        }
                        stats.get(types[i]).setTimesCompleted(Math.toIntExact((long)dataSnapshot.child(types[i]).getValue()));
                        Log.d(TAG, types[i] + " set to " + stats.get(types[i]).getTimesCompleted());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    // increments the num times completed for certain mission kind in user object and in DB
    public void increaseTimesCompleted(String missionKind) {
        // need to create this item in map first if DNE in stats
        if(!stats.containsKey(missionKind)) {
            stats.put(missionKind, new MissionStats(missionKind));
        }

        MissionStats currStats = stats.get(missionKind);

        // update object
        currStats.incrementTimesCompleted();
        Log.d(TAG, "currstats updated val: " + currStats.getTimesCompleted() + "\nupdated stats val: " + stats.get(missionKind).getTimesCompleted());

        // update db
        usersRef.child(uID).child(missionKind).setValue(currStats.getTimesCompleted());
        Log.d(TAG, "updated database with " + currStats.toString());
    }

    public String missionLeastCompleted() {
        // did not do all mission types yet
        if(stats.size() < types.length) {
            for(int i=0; i<types.length; i++) {
                if(!stats.containsKey(types[i])) {
                    return types[i];
                }
            }
        }

        // all missions done at least once, get one with lest times completed
        int smallest = 10000;
        String smallestMission = "speaking";
        for (Map.Entry<String, MissionStats> entry : stats.entrySet()) {
            Log.d(TAG, "Key : " + entry.getKey() + " Value : " + entry.getValue().getTimesCompleted());
            if(smallest > entry.getValue().getTimesCompleted()) {
                smallest = entry.getValue().getTimesCompleted();
                smallestMission = entry.getKey();
                Log.d(TAG, "set smallest to: " + smallestMission);
            }
        }

        return smallestMission;
    }

    public boolean setUp(String mission) {
        return stats.containsKey(mission);
    }
}



