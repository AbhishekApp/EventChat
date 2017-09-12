package com.get.wazzon;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by admin on 6/28/2017.
 */

public class PreviewImage extends Activity implements View.OnClickListener/*, View.OnTouchListener*/{

//    Button  btnCancel;
    ImageView imgPreview,btnSelect;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_image);

        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        btnSelect = (ImageView) findViewById(R.id.btnSelect);
        imgBack = (ImageView) findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);
//        btnCancel = (Button) findViewById(R.id.btnCancel);
        Uri fileUri = getIntent().getBundleExtra("FileData").getParcelable("FileURI");
        try {
            imgPreview.setImageURI(fileUri);
        }catch (Exception ex){
            Log.e("PreviewImage", "Path ERROR : "+ex.toString());
        }
        btnSelect.setOnClickListener(this);
//        btnCancel.setOnClickListener(this);
//        imgPreview.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnSelect){
            setResult(Activity.RESULT_OK);
        }else if(id == R.id.img_back){
            finish();
        }else{
            setResult(Activity.RESULT_CANCELED);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
    }

//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        gestureDetector.onTouchEvent(event);
//        return false;
//    }

//    private GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
//        @Override
//        public boolean onDoubleTap(MotionEvent e) {
//
//            //return false;
//            return super.onDoubleTap(e);
//        }
//        // implement here other callback methods like onFling, onScroll as necessary
//    });
}
