package appy.com.wazznowapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.app.model.ChatData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mylist.adapters.StadiumChatListAdapter;

import java.util.ArrayList;

/**
 * Created by admin on 8/2/2016.
 */
public class StadiumFragment extends Fragment implements View.OnClickListener {

    ListView listView;
    ImageView imgEmoji;
    ImageView send;
    StadiumChatListAdapter adapter;
    EditText etMsg;
    View viewLay;


    String mPhotoUrl;
    Firebase myFirebaseRef;
    Firebase alanRef;

    FragmentActivity activity;
    final static String firebaseURL = "https://wazznow-cd155.firebaseio.com/";
    private ValueEventListener mConnectedListener;
    private ValueEventListener mDataRetrieveListener;
    boolean cannedFlag = false;

    String userName="Abhi";

    public StadiumFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        myFirebaseRef = new Firebase(firebaseURL);
        alanRef = myFirebaseRef.child("users");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.stadium_chat, container, false);
        init(view);

        return view;

    }

    private void init(View v) {
        listView = (ListView) v.findViewById(R.id.listMain);
        imgEmoji = (ImageView) v.findViewById(R.id.imgEmoji);
        send = (ImageView) v.findViewById(R.id.imgSendChat);
        etMsg = (EditText) v.findViewById(R.id.etChatMsg);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        viewLay = (View) v.findViewById(R.id.viewLay);
//        al = new ArrayList<String>();
        imgEmoji.setOnClickListener(this);
        send.setOnClickListener(this);
        etMsg.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mConnectedListener = alanRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                 //   Toast.makeText(getActivity(), "Connected to Firebase", Toast.LENGTH_SHORT).show();
                    System.out.println("Firebase connected");
                } else {
                    System.out.println("Firebase not connected");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
             //   Toast.makeText(getActivity(), "error: " + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Server not connect ERROR : "+firebaseError.getMessage());
            }
        });

        adapter = new StadiumChatListAdapter(alanRef.limit(50), getActivity(), R.layout.chat_layout, "ABHI");

    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }catch (Exception e){}
    }

    @Override
    public void onStop() {
        super.onStop();
        alanRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imgEmoji) {
            if (viewLay.getVisibility() == View.VISIBLE) {
                viewLay.setVisibility(View.GONE);
            } else {
                viewLay.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(),"Emoji will be shown soon", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.etChatMsg) {
            viewLay.setVisibility(View.GONE);
        } else if (id == R.id.imgSendChat) {
            userName = MyApp.preferences.getString(SignUpActivity.USER_NAME,"");
            if(MyApp.USER_LOGIN || cannedFlag) {
                if(cannedFlag){
                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    if (viewLay.getVisibility() == View.VISIBLE) {
                        viewLay.setVisibility(View.GONE);
                    } else {
                        viewLay.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(),"Guest User can send only Canned Messages", Toast.LENGTH_SHORT).show();
                    }
                }else if(MyApp.USER_LOGIN){
                    String msg = etMsg.getText().toString();
                    if (!TextUtils.isEmpty(msg)) {
                   //     al.add(msg);
                        adapter.notifyDataSetChanged();
                        etMsg.setText("");
                        ChatData alan = new ChatData(userName, msg);
                        alanRef.push().setValue(alan);

                    }else{
                        Toast.makeText(getActivity(),"Blank message not send", Toast.LENGTH_SHORT).show();
                    }
                }

            }else{
                Intent ii = new Intent(getActivity(), SignUpActivity.class);
                startActivityForResult(ii, 111);
            }
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            String result = data.getStringExtra("result");
            Log.e("StadiumFragment", "Result : "+result);
            if(result.equals("Login")){
                cannedFlag = false;
                userName = data.getStringExtra("user");
                MyApp.USER_LOGIN = true;
            }else if(result.equals("Guest User")){
                cannedFlag = true;
                MyApp.USER_LOGIN = false;
            }
        }
    }


}

