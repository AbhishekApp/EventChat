package appy.com.wazznowapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.mylist.adapters.StadiumChatListAdapter;

/**
 * Created by admin on 8/2/2016.
 */
public class HousePartyFragment extends Fragment implements View.OnClickListener{

    ListView listView;
    ImageView imgEmoji;
    ImageView send;
    StadiumChatListAdapter adapter;
    EditText etMsg;
    View viewLay;


    public HousePartyFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onClick(View v) {

    }
}
