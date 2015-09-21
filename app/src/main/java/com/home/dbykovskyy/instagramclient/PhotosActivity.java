package com.home.dbykovskyy.instagramclient;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PhotosActivity extends Activity {

    public static final String CLIENT_ID = "fe1508ac6901490b9d84b638bc9625a2";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotoAdapter aPhotos;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        photos = new ArrayList<InstagramPhoto>();
        aPhotos = new InstagramPhotoAdapter(this, photos);

        ListView lvPhotos = (ListView) findViewById(R.id.lvPhotos);
        lvPhotos.setAdapter(aPhotos);
        fetchPopularData();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPopularData();
            }

        });

        // Configure the refreshing colors

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,

                android.R.color.holo_green_light,

                android.R.color.holo_orange_light, android.R.color.holo_red_light);

    }

    public void fetchPopularData(){

        String url =  "https://api.instagram.com/v1/media/popular?client_id="+CLIENT_ID;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, null, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray photoJson=null;
                aPhotos.clear();

                try {
                    photoJson = response.getJSONArray("data");

                    for(int i=0; i<photoJson.length(); i++){
                        InstagramPhoto photo = new InstagramPhoto();
                        ArrayList<Comment> comments =new ArrayList<Comment>();
                        //populate photo model
                        photo.setUseName(photoJson.getJSONObject(i).getJSONObject("user").getString("username"));
                        photo.setUserPicUrl(photoJson.getJSONObject(i).getJSONObject("user").getString("profile_picture"));
                        photo.setCaption(photoJson.getJSONObject(i).getJSONObject("caption").getString("text"));
                        photo.setImageUrl(photoJson.getJSONObject(i).getJSONObject("images").getJSONObject("standard_resolution").getString("url"));
                        photo.setImageWidth(photoJson.getJSONObject(i).getJSONObject("images").getJSONObject("standard_resolution").getString("width"));
                        photo.setImageHeight(photoJson.getJSONObject(i).getJSONObject("images").getJSONObject("standard_resolution").getString("height"));
                        photo.setLikesCount(photoJson.getJSONObject(i).getJSONObject("likes").getString("count"));
                        photo.setTimeStamp(photoJson.getJSONObject(i).getJSONObject("caption").getString("created_time"));
                        photo.setLikeStatus(false);

                        //populate comment model
                        JSONArray commentJson = photoJson.getJSONObject(i).getJSONObject("comments").getJSONArray("data");
                        for(int j=0; j<commentJson.length(); j++){
                            Comment comment = new Comment();

                            comment.setComment(commentJson.getJSONObject(j).getString("text"));
                            comment.setCommenterName(commentJson.getJSONObject(j).getJSONObject("from").getString("username"));
                            comment.setCommenterPic(commentJson.getJSONObject(j).getJSONObject("from").getString("profile_picture"));
                            comment.setTimeStamp(Long.parseLong(commentJson.getJSONObject(j).getString("created_time")));
                            comments.add(comment);
                        }
                        photo.setComments(comments);
                        photos.add(photo);
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

                aPhotos.addAll();
                swipeContainer.setRefreshing(false);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", "Fetch timeline error: " + throwable.toString());
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
