package projects.chirolhill.juliette.finalprojectchirolhilljuliette;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ProgressActivity extends AppCompatActivity {
    private Button btnRecord;

    private int userID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_progress);

        btnRecord = findViewById(R.id.btnRecord);

        Intent i = new Intent();
        userID = i.getIntExtra(MainActivity.EXTRA_USER_ID, 0);

        // display the recordings pulled from database

        // create recording and store in database
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
