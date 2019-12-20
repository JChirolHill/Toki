package projects.chirolhill.juliette.finalprojectchirolhilljuliette;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import projects.chirolhill.juliette.finalprojectchirolhilljuliette.model.Mission;
import projects.chirolhill.juliette.finalprojectchirolhilljuliette.model.UserSingleton;

public class MissionActivity extends AppCompatActivity {
    private static final String TAG = "MissionActivityTag";
    public static final String EXTRA_MISSION_KIND = "extra_mission_kind";
    public static final String EXTRA_MISSION_COMPLETE = "extra_mission_complete";
    private static final String BUNDLE_MISSION_COMPLETE = "bundle_mission_complete";
    public static final int REQUEST_CODE = 0;

    private DatabaseReference missionRef;
    private FirebaseDatabase firebase;

    private TextView textMissionKind;
    private TextView textMissionType;
    private TextView textMissionContent;
    private TextView textCongrats;
    private Button btnDone;
    private Button btnAskToki;
    private Button btnLoadAnother;

    private String missionKind;
    private int missionNum;
    private int totalMissions;
    private Mission mission;
    private UserSingleton singleton;
    private boolean complete;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mission);

        // set references
        textMissionKind = findViewById(R.id.textMissionKind);
        textMissionType = findViewById(R.id.textMissionType);
        textMissionContent = findViewById(R.id.textMissionContent);
        textCongrats = findViewById(R.id.textCongrats);
        btnDone = findViewById(R.id.btnDone);
        btnAskToki = findViewById(R.id.btnAskToki);
        btnLoadAnother = findViewById(R.id.btnLoadAnother);

        // check if completed this mission from savedinstancestate
        complete = false;
        if(savedInstanceState != null) {
            if(savedInstanceState.getBoolean(BUNDLE_MISSION_COMPLETE)) {
                complete = true;

                // intent for activity result in main activity
                Intent i = new Intent();
                i.putExtra(EXTRA_MISSION_COMPLETE, true);
                setResult(RESULT_OK, i);

                showComplete();
            }
        }

        // get mission type from intent and display to UI
        Intent i = getIntent();
        missionKind = i.getStringExtra(EXTRA_MISSION_KIND);
        textMissionKind.setText(missionKind.toUpperCase());

        // start firebase connection and references
        firebase = FirebaseDatabase.getInstance();
        missionRef = firebase.getReference("missions");
        singleton = UserSingleton.getInstance(getApplicationContext());


        missionRef.child(missionKind).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // check if setup for this mission kind
                Log.d(TAG, "setup: " + singleton.setUp(missionKind));
                if(!singleton.setUp(missionKind)) {
                    missionNum = 0;
                    btnLoadAnother.setVisibility(View.GONE);
                }
                else {
                    missionNum = Mission.getNum(Math.toIntExact(dataSnapshot.getChildrenCount()));
                }
                totalMissions = Math.toIntExact(dataSnapshot.getChildrenCount());
                loadAnother(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // used to update bundle if rotate
                complete = true;

                // intent for activity result in main activity
                Intent i = new Intent();
                i.putExtra(EXTRA_MISSION_COMPLETE, true);
                setResult(RESULT_OK, i);

                // update the singleton and database
                SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                String uID = prefs.getString(UserSingleton.PREF_USER_ID, "123");
                Log.d(TAG, "uid: " + uID);

                singleton.increaseTimesCompleted(missionKind);

                // update UI
                showComplete();
            }
        });

        btnAskToki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AskTokiActivity.class);
                startActivity(i);
            }
        });

        btnLoadAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                missionRef.child(missionKind).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        missionNum = ((missionNum + 1) % totalMissions);
                        if(missionNum == 0) {
                            missionNum++;
                        }

                        loadAnother(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                });
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(BUNDLE_MISSION_COMPLETE, complete);
        super.onSaveInstanceState(outState);
    }

    private void loadAnother(DataSnapshot dataSnapshot) {
        mission = dataSnapshot.child(Integer.toString(missionNum)).getValue(Mission.class);

        textMissionType.setText(mission.getType());
        textMissionContent.setText(mission.getContent());
    }

    // UI shown when mission complete
    private void showComplete() {
        textMissionType.setVisibility(View.GONE);
        textMissionContent.setVisibility(View.GONE);
        textCongrats.setVisibility(View.VISIBLE);
        btnDone.setVisibility(View.GONE);
        btnLoadAnother.setVisibility(View.GONE);
    }
}
