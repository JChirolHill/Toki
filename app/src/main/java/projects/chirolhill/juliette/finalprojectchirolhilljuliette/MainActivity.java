package projects.chirolhill.juliette.finalprojectchirolhilljuliette;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

//import projects.chirolhill.juliette.finalprojectchirolhilljuliette.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import projects.chirolhill.juliette.finalprojectchirolhilljuliette.model.Mission;
import projects.chirolhill.juliette.finalprojectchirolhilljuliette.model.UserSingleton;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivityTag";
    public static final String EXTRA_USER_ID = "extra_user_id";

    private TextView textTodayMission;

    private Button btnDone;
//    private Button btnProgress;
    private Button btnAskToki;

    private ImageButton btnGrammar;
    private ImageButton btnListening;
    private ImageButton btnReading;
    private ImageButton btnWriting;
    private ImageButton btnVocab;
    private ImageButton btnSpeaking;

    private UserSingleton singleton;
    private String missionLeastCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // start firebase connection and references
        singleton = UserSingleton.getInstance(getApplicationContext());

        textTodayMission = findViewById(R.id.textTodayMission);

        btnDone = findViewById(R.id.btnDone);
//        btnProgress = findViewById(R.id.btnProgress);
        btnAskToki = findViewById(R.id.btnAskToki);

        btnGrammar = findViewById(R.id.btnGrammar);
        btnListening = findViewById(R.id.btnListening);
        btnReading = findViewById(R.id.btnReading);
        btnWriting = findViewById(R.id.btnWriting);
        btnVocab = findViewById(R.id.btnVocab);
        btnSpeaking = findViewById(R.id.btnSpeaking);

        // set tags for each button so know which intent to launch for mission activity
        btnGrammar.setTag("grammar");
        btnListening.setTag("listening");
        btnReading.setTag("reading");
        btnWriting.setTag("writing");
        btnVocab.setTag("vocab");
        btnSpeaking.setTag("speaking");

        // initialize random mission banner
        initializeBanner();

        // launch intent to progress activity
//        btnProgress.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), ProgressActivity.class);
//                startActivity(i);
//            }
//        });

        // launch intent to ask Toki activity
        btnAskToki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AskTokiActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && data != null) {
            if(requestCode == MissionActivity.REQUEST_CODE) {
                initializeBanner();
            }
        }
    }

    public void onClick(View v) {
        Intent i = new Intent(getApplicationContext(), MissionActivity.class);
        i.putExtra(MissionActivity.EXTRA_MISSION_KIND, v.getTag().toString());
        startActivityForResult(i, MissionActivity.REQUEST_CODE);
    }

    private void initializeBanner() {
        Log.d(TAG, "fetching least completed mission");
        missionLeastCompleted = singleton.missionLeastCompleted();
        btnDone.setText(missionLeastCompleted);
        btnDone.setTag(missionLeastCompleted);
    }
}
