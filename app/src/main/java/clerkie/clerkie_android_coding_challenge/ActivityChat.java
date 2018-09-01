package clerkie.clerkie_android_coding_challenge;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityChat extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "ActivityChat";

    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private ListView mMessageList;
    private ImageView mSendMessageButton;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    private FirebaseFirestore database;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // References for WiFi p2p
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        currentUser = mAuth.getCurrentUser();

        // Message UI references
        mMessageList = findViewById(R.id.chat_message_list);
        mSendMessageButton = findViewById(R.id.chat_message_send_button);

        // Listeners
        mSendMessageButton.setOnClickListener(this);

        // WiFi p2p setup
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            loadImageGallery();
        }

        database = FirebaseFirestore.getInstance();
        refreshMessagesListView();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // storage-related task you need to do.
//                    new LoadMediaToGalleryTask().execute(0);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onClick(View view){
        int id = view.getId();

        if (id == R.id.chat_message_send_button){
            sendMessage();
        }
    }

    private void loadImageGallery(){
        // Recycler view setup
        mRecyclerView = (RecyclerView) findViewById(R.id.media_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        // Get relevant columns for use later.
        String[] projection = {
                MediaStore.Images.Thumbnails.DATA,
        };

        // Return only video and image metadata.
        String selection = MediaStore.Images.Thumbnails.KIND + "=" + MediaStore.Images.Thumbnails.MINI_KIND;
        Uri queryUri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        Cursor c = getContentResolver().query(queryUri, projection, selection, null, MediaStore.Images.Thumbnails.DEFAULT_SORT_ORDER);
        mAdapter = new MediaAdapter(c);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void sendMessage(){
        String msg = ((EditText)findViewById(R.id.chat_message_text)).getText().toString();

        Date currentTime = Calendar.getInstance().getTime();
        long timestamp = currentTime.getTime();

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

        Map<String, Object> new_message = new HashMap<>();
        new_message.put(currentUser.getUid() + "_" + Long.toString(timestamp), msg);

        // Write a message to the database
        database = FirebaseFirestore.getInstance();
        database.collection("chat").document().set(new_message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ((EditText)findViewById(R.id.chat_message_text)).setText("");
                refreshMessagesListView();
            }
        }
        );
    }

    private void refreshMessagesListView(){
        database.collection("chat").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.isSuccessful()) {
//                        Toast.makeText(ActivityChat.this, task.getResult().size() + " ", Toast.LENGTH_LONG).show();
                        List<String> messages = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            for (String key : document.getData().keySet()){
                                String msg = (String)document.get(key);
                                messages.add(msg);
                            }
                        }

                        loadMessages(messages);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private ArrayAdapter<String> mArrayAdapter;
    private void loadMessages(List<String> messages){
        mArrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                messages );

        mArrayAdapter.notifyDataSetChanged();
        mMessageList.setAdapter(mArrayAdapter);
    }

}
