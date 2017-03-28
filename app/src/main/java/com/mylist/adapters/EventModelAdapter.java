package com.mylist.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.model.EventDetail;
import com.app.model.MyUtill;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.get.wazzon.MyApp;
import com.get.wazzon.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;



/**
 * Created by manish on 9/17/2016.
 */
public class EventModelAdapter extends BaseAdapter {

    MyUtill myUtill;
    Context con;
    ArrayList<EventDetail> alList;
    ViewHolder viewHolder;
    SharedPreferences preferences;
    String mycolor[];
    int colorIndex;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public EventModelAdapter(Context con, ArrayList<EventDetail> alList){
            this.con = con;
            this.alList = alList;
            preferences = MyApp.preferences;
            myUtill = new MyUtill();
            mycolor = con.getResources().getStringArray(R.array.mycolor);
            colorIndex = 0;
    }

    @Override
    public int getCount() {
        return alList.size();
    }

    @Override
    public Object getItem(int position) {
        return alList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        viewHolder = new ViewHolder();
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.main_row, null);
            viewHolder.img = (ImageView) view.findViewById(R.id.imgRowMain);
            viewHolder.tvCateName = (TextView) view.findViewById(R.id.tvCatRow);
            viewHolder.tvHour = (TextView) view.findViewById(R.id.tvTimeRow);
            viewHolder.tvEventName = (TextView) view.findViewById(R.id.tvEventNameRow);
            viewHolder.tvNoOfTune = (TextView) view.findViewById(R.id.tvEventTunedRow);
            viewHolder.tvEventPlace = (TextView) view.findViewById(R.id.tvEventLocRow);
            viewHolder.imgChat = (ImageView) view.findViewById(R.id.imgChatRow);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        EventDetail detail = alList.get(position);
        viewHolder.tvCateName.setText(detail.getCategory_name());
        viewHolder.tvEventName.setText(detail.getEvent_title());

        viewHolder.tvEventPlace.setText(detail.getEvent_meta());

        if(!TextUtils.isEmpty(detail.getEvent_image_url())) {
            downloadImageURL(detail.getEvent_id(), viewHolder.img);
        }


            String strTime = String.valueOf(myUtill.getTimeDifference(detail.getEvent_start())).trim();

            if (strTime.contains("Ago")) {
                if(myUtill.isTimeBetweenTwoTime(detail.getEvent_start(),detail.getEvent_exp())){
                    viewHolder.tvHour.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    viewHolder.tvHour.setText("LIVE !\t\t");
                }else {
                    viewHolder.tvHour.setCompoundDrawablesWithIntrinsicBounds(R.drawable.anti, 0, 0, 0);
                    viewHolder.tvHour.setText("\t\t\t\t");
                }
            } else {
                if (strTime.contains("More")) {
                    viewHolder.tvHour.setCompoundDrawablesWithIntrinsicBounds(R.drawable.clock, 0, 0, 0);
                    viewHolder.tvHour.setText("\t\t\t\t");
                } else if (strTime.contains("to go")) {
                    viewHolder.tvHour.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    viewHolder.tvHour.setText(strTime);
                }
            }
        /*}*/


        if(detail.getSubscribed_user().equalsIgnoreCase("0")) {
            //viewHolder.tvNoOfTune.setVisibility(View.GONE);
            String subscribed_user = detail.getSubscribed_user();
            int iSubscribedUser = Integer.parseInt(subscribed_user);
            subscribed_user = new String("<b> " + iSubscribedUser);
            viewHolder.tvNoOfTune.setText( subscribed_user + " </b>  Tuned In");
        }
        else {
            String subscribed_user = detail.getSubscribed_user();
//            if (groupRec != null && detail.getCatergory_id() != null) {
                if (MyApp.preferences.getBoolean(detail.getCatergory_id(), false)) {
                    try {
                        viewHolder.imgChat.setImageResource(R.mipmap.chat_subscribe);
                        int iSubscribedUser = Integer.parseInt(subscribed_user);
                        iSubscribedUser--;
                        subscribed_user = new String("<b> You + </b> " + iSubscribedUser);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }else{
                    viewHolder.imgChat.setImageResource(R.mipmap.chat_icon);
                }
            viewHolder.tvNoOfTune.setVisibility(View.VISIBLE);
            viewHolder.tvNoOfTune.setText(Html.fromHtml( "<b> "+subscribed_user + " </b> Tuned In"));
        }

        try{
            if(colorIndex >= mycolor.length-1){
                colorIndex = 0;
            }
            view.setBackgroundColor(Color.parseColor(mycolor[colorIndex++]));
        }catch (Exception ex){
            Log.e("EventModelAdapter","Set Background Color ERROR: "+ex.toString());
        }
        return view;
    }

    String imgURL = "";
    public void downloadImageURL(String fileName, final ImageView img){
        try{
            StorageReference storageRef = storage.getReferenceFromUrl(MyApp.FIREBASE_IMAGE_URL);
            StorageReference pathReference = storageRef.child(fileName+".jpg");
            Glide.with(con)
            .using(new FirebaseImageLoader())
            .load(pathReference)
            .placeholder(R.drawable.def_orig_)
            .diskCacheStrategy(DiskCacheStrategy.SOURCE) //Unfortunately there isn't any way to influence the contents of the cache directly.
                                 // You cannot either remove an item explicitly, or force one to be kept.
                                 // In practice with an appropriate disk cache size you usually don't need to worry about doing either.
                                 // If you display your image often enough, it won't be evicted.
                                 // If you try to cache additional items and run out of space in the cache, older items will be evicted automatically to make space.
            .into(img);
           // Glide.with(con).load(uri).into(img);
        }catch (Exception ex){
            Log.e("EventModelAdapter", "EventModelAdapter DownloadImageURL ERROR: "+ex.toString());
        }
    }

    class ViewHolder{
        ImageView img, imgChat;
        TextView tvCateName, tvHour, tvEventName, tvNoOfTune, tvEventPlace;
    }

}
