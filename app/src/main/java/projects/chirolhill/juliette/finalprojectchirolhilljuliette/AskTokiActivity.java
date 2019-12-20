package projects.chirolhill.juliette.finalprojectchirolhilljuliette;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import projects.chirolhill.juliette.finalprojectchirolhilljuliette.model.TextMessage;
import projects.chirolhill.juliette.finalprojectchirolhilljuliette.model.UserSingleton;

public class AskTokiActivity extends AppCompatActivity {
    private static final String TAG = AskTokiActivity.class.getSimpleName();

    private EditText editChatbox;
    private Button btnSend;

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;

    private List<TextMessage> messageList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ask_toki);

        editChatbox = findViewById(R.id.edittext_chatbox);
        btnSend = findViewById(R.id.button_chatbox_send);

        messageList = new ArrayList<>();
        messageList.add(new TextMessage("Hi, my name is Toki.  Ask me any questions you have about language learning!", false));

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, messageList);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = editChatbox.getText().toString().trim();
                if(question != "") {
                    // clear chatbox
                    editChatbox.setText("");

                    // add to array user question and notify adapter
                    messageList.add(new TextMessage(question, true));
                    Log.d(TAG, messageList.size() + messageList.get(0).getMessage());
                    mMessageAdapter.notifyDataSetChanged();
                    Log.d(TAG, messageList.size() + messageList.get(0).getMessage());

                    mMessageRecycler.smoothScrollToPosition(mMessageRecycler.getAdapter().getItemCount());

                    // get user id from prefs
                    SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
                    String uID = prefs.getString(UserSingleton.PREF_USER_ID, "123");

                    // add to list answer
                    Log.d(TAG, "pressed on ask toki button with input " + question);
                    new DialogFlowRequest(getApplicationContext(), uID).getAnswer(question);
                }
            }
        });
    }

    private class DialogFlowRequest {
        private RequestQueue queue;
        private final String BASE_URL = "https://api.dialogflow.com/v1/query?v=20150910";

        private String uID;
        private String tokiAnswer;

        // need to pass in context
        public DialogFlowRequest(Context context, String uID) {
            Log.d(TAG, "created dialog request obj");
            queue = Volley.newRequestQueue(context);

            this.uID = uID;
        }

        public String getAnswer(String question) {
            String url = (BASE_URL + "&lang=en&sessionId=" + uID + "&query=" + question).replaceAll("\\s", "%20");
            volleyGETRequest(url);
            return tokiAnswer;
        }

        public void volleyGETRequest(String url) {
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String ans = response.getJSONObject("result").getJSONObject("fulfillment").getString("speech");
                                Log.d(TAG, ans);
                                tokiAnswer = ans;
                                messageList.add(new TextMessage(tokiAnswer, false));
                                Log.d(TAG, "newest element in list: " + messageList.get(messageList.size() - 1).getMessage());
                                mMessageAdapter.notifyDataSetChanged();

                                mMessageRecycler.smoothScrollToPosition(mMessageRecycler.getAdapter().getItemCount());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null && networkResponse.data != null) {
                                String jsonError = new String(networkResponse.data);
                                Log.d(TAG, "Error Response: " + jsonError);
                            }
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    String mApiKey = "Bearer e7dcf4baabf14e58839980bcbc0a56c7";
                    headers.put("Authorization", mApiKey);
                    return headers;
                }
            };

            queue.add(getRequest);
        }
    }
}