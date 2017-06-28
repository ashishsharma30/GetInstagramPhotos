package com.ashish.getinstagramphotos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AllMediaFiles extends Activity {

    private InstagramSession mSession;
    private GridView gvAllImages;
    private HashMap<String, String> userInfo;
    private ArrayList<String> imageThumbList = new ArrayList<String>();
    private Context context;
    private int WHAT_FINALIZE = 0;
    private static int WHAT_ERROR = 1;
    private ProgressDialog pd;
    public static final String TAG_DATA = "data";
    public static final String TAG_IMAGES = "images";
    public static final String TAG_THUMBNAIL = "thumbnail";
    public static final String TAG_STANDARD = "standard_resolution";
    public static final String TAG_URL = "url";
    private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (pd != null && pd.isShowing())
				pd.dismiss();
			if (msg.what == WHAT_FINALIZE) {
				setImageGridAdapter();
			} else {
				Toast.makeText(context, "Check your network.",
						Toast.LENGTH_SHORT).show();
			}
			return false;
		}
	});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_media_list_files);
        gvAllImages = (GridView) findViewById(R.id.gvAllImages);
        userInfo = (HashMap<String, String>) getIntent().getSerializableExtra(
                "userInfo");
        context = AllMediaFiles.this;
        mSession = new InstagramSession(context);
        getAllMediaImages();
    }

    private void setImageGridAdapter() {
        gvAllImages.setAdapter(new MyGridListAdapter(context, imageThumbList));
    }

    private void getAllMediaImages() {
        pd = ProgressDialog.show(context, "", "Loading images...");
        new Thread(new Runnable() {

            @Override
            public void run() {
                int what = WHAT_FINALIZE;
                try {
                    // URL url = new URL(mTokenUrl + "&code=" + code);
                    JSONParser jsonParser = new JSONParser();

//					URL(string:"https://api.instagram.com/v1/users/self/media/recent/?access_token=\(assessToken)")!,
                    JSONObject jsonObject = jsonParser
                            .getJSONFromUrlByGet("https://api.instagram.com/v1/users/"
                                    + userInfo.get(InstagramApp.TAG_ID)
                                    + "/media/recent/?access_token="
                                    + mSession.getAccessToken()
                                    + "&count="
                                    + userInfo.get(InstagramApp.TAG_COUNTS));

                    /*JSONObject jsonObject = jsonParser
                            .getJSONFromUrlByGet("https://api.instagram.com/v1/users/"
                                    + userInfo.get(InstagramApp.TAG_ID)
                                    + "/media/recent/?client_id="
                                    + ApplicationData.CLIENT_ID
                                    + "&count="
                                    + userInfo.get(InstagramApp.TAG_COUNTS));*/

                    Log.e("JSONObject", "JSONObject Data : " + jsonObject.toString());

                    JSONArray data = jsonObject.getJSONArray(TAG_DATA);
                    for (int data_i = 0; data_i < data.length(); data_i++) {
                        JSONObject data_obj = data.getJSONObject(data_i);

                        JSONObject images_obj = data_obj
                                .getJSONObject(TAG_IMAGES);
//                        standard_resolution
                        Log.e("Ashish", "data_obj : " + data_obj);
                        JSONObject thumbnail_obj = images_obj
                                .getJSONObject(TAG_THUMBNAIL);

                        JSONObject standard_resolution = images_obj
                                .getJSONObject(TAG_STANDARD);
                        // String str_height =
                        // thumbnail_obj.getString(TAG_HEIGHT);
                        //
                        // String str_width =
                        // thumbnail_obj.getString(TAG_WIDTH);

                        String str_url = standard_resolution.getString(TAG_URL);
                        Log.e("Ashish", "Image URL : " + str_url);
                        imageThumbList.add(str_url);
                    }

                    System.out.println("jsonObject::" + jsonObject);

                } catch (Exception exception) {
                    exception.printStackTrace();
                    what = WHAT_ERROR;
                    Log.e("Exception", "Image URL Exception : " + exception.toString());
                }
                // pd.dismiss();
				handler.sendEmptyMessage(what);
            }
        }).start();
    }
}
