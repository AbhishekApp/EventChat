package com.get.wazzon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.app.model.AdminMessage;
import com.app.model.ChatData;
import com.app.model.WonData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.mylist.adapters.WonAdapter;

import java.util.ArrayList;

import static com.get.wazzon.EventChatActivity.eventDetail;
import static com.get.wazzon.EventChatActivity.eventID;
import static com.get.wazzon.MyApp.FeaturedMsgLimit;
import static com.get.wazzon.MyApp.StadiumMsgLimit;

/**
 * Created by admin on 4/27/2017.
 */

public class WonHistory extends Fragment implements View.OnClickListener {

    Button btnRedeem;
    ListView listView;
    Firebase alanRef;
 //   Query alanQuery;
    ChildEventListener childEventListener;
    int WonHistoryLimit = 20;
    ArrayList<WonData> arrayList;
    ArrayList<String> mKeys;
    WonAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        alanRef = new Firebase(MyApp.FIREBASE_BASE_URL + "/WonHistory" + "/" + MyApp.getDeviveID(getActivity())).child("Meta");
       // alanQuery = alanRef.limitToLast(WonHistoryLimit);
        alanRef.keepSynced(true);
        View view = inflater.inflate(R.layout.won_history, container, false);
        init(view);
        MyApp.CustomEventAnalytics("fragment_selected",  "won");
        return view;

    }

    private void init(View view){
        btnRedeem = (Button) view.findViewById(R.id.btnWonList);
        listView = (ListView) view.findViewById(R.id.listWon);

        btnRedeem.setOnClickListener(this);
        mKeys = new ArrayList<String>();
        arrayList = new ArrayList<WonData>();
        adapter = new WonAdapter(getActivity(), arrayList);
        listView.invalidate();
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                WonData msg = dataSnapshot.getValue(WonData.class);
                Log.i("WonHistory", "Description : "+ msg);
//                arrayList.add(msg);
                String key = dataSnapshot.getKey();
                // Insert into the correct location, based on previousChildName
                if (previousChildName == null) {
                    arrayList.add(0, msg);
                    mKeys.add(0, key);

                } else {
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == arrayList.size()) {
                        arrayList.add(msg);
                        mKeys.add(key);
                    } else {
                        arrayList.add(nextIndex, msg);
                        mKeys.add(nextIndex, key);
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        alanRef.addChildEventListener(childEventListener);
    }

    @Override
    public void onClick(View v) {
        int redeemVal = MyApp.preferences.getInt("RedeemLimit", 0);
        int balance = MyApp.preferences.getInt("BalanceWon", 0);
        if(balance >= redeemVal){
//            Toast.makeText(getActivity(), "Amount will Redeem.", Toast.LENGTH_SHORT).show();
            Intent ii = new Intent(getActivity(), SignUpActivity.class);
            ii.putExtra("WonSignup", true);
            startActivity(ii);
        }else{
            Toast.makeText(getActivity(), "Your balance is less than "+redeemVal+" won.", Toast.LENGTH_SHORT).show();
        }

    }
}
