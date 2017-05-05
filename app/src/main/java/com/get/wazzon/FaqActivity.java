package com.get.wazzon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.model.FaqModel;
import com.app.model.MyUtill;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by admin on 5/4/2017.
 */

public class FaqActivity extends AppCompatActivity {

    ActionBar actionBar;
    Button btnRedeem;
    ListView listView;
    ArrayList<FaqModel> alModel;
    FaqAdapter adapter;
    String faqURL = MyApp.FIREBASE_BASE_URL+"/Faq.json";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.won_history);

        init();
    }

    private void init(){
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.faq));
        alModel = new ArrayList<FaqModel>();
        btnRedeem = (Button) findViewById(R.id.btnWonList);
        btnRedeem.setVisibility(View.GONE);
        listView = (ListView) findViewById(R.id.listWon);
        adapter = new FaqAdapter();
        listView.setAdapter(adapter);
        FaqTask task = new FaqTask();
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    class FaqAdapter extends BaseAdapter{

        ViewHolder viewHolder;

        FaqAdapter(){
            viewHolder = new ViewHolder();
        }

        @Override
        public int getCount() {
            return alModel.size();
        }

        @Override
        public Object getItem(int position) {
            return alModel.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null){
                LayoutInflater inflater = (LayoutInflater) FaqActivity.this.getSystemService(FaqActivity.this.LAYOUT_INFLATER_SERVICE);
                view = (View)inflater.inflate(R.layout.faq_row, null);
                viewHolder.tvQuestion = (TextView) view.findViewById(R.id.faqQuestion);
                viewHolder.tvAnswer= (TextView) view.findViewById(R.id.faqAnswer);
                view.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.tvQuestion.setText(alModel.get(position).getQuestion());
            viewHolder.tvAnswer.setText(alModel.get(position).getAnswer());
            return view;
        }

        class ViewHolder{
            TextView tvQuestion;
            TextView tvAnswer;
        }
    }


    class FaqTask extends AsyncTask<Void,Void,Void>{
        HttpURLConnection urlConnection;
        JSONArray jsonArray;
        MyUtill myUtill;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(FaqActivity.this);
            dialog.setTitle("Loading...");
            dialog.show();
            myUtill = new MyUtill();
        }

        @Override
        protected Void doInBackground(Void... params) {
            jsonArray = myUtill.getJSONFromServer(faqURL);
            for(int i = 0; i < jsonArray.length() ; i++){
                try {
                    FaqModel model = new FaqModel();
                    JSONObject jObj = jsonArray.getJSONObject(i);
                    model.setQuestion(jObj.optString("q"));
                    model.setAnswer(jObj.optString("a"));
                    alModel.add(model);
                }catch (JSONException ex){}
                 catch (Exception ex){}
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
            dialog.hide();
            dialog.cancel();
        }
    }
}
