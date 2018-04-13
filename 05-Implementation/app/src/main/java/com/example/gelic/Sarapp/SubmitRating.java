/*
This is a course requirement for CS 192 Software Engineering II under the supervision of Asst. Prof. Ma. Rowena C. Solamo of the Department of Computer Science, College of Engineering, University of the Philippines, Diliman for the AY 2017-2018.
Angelika Juliah S. Galang
*/

/* Code History:
Initial Code Authored by: Angelika Juliah S. Galang
Update 3/9/2018: Richelle Yap
*/

/* File Creation Date: (Sprint 2) 2/21/2018
     Development Group: Group 1
     Client Group: CS 192 Class
     Purpose of File: Creates the screen for submitting a rating
*/

/* Variable Descriptions:

*/

package com.example.gelic.Sarapp;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RatingBar;
import android.graphics.drawable.LayerDrawable;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SubmitRating extends AppCompatActivity {
     private Button submit;
     EditText rating;
     TextView subRating;
     RatingBar food_quality;
     RatingBar pricing;
     RatingBar service;
     RatingBar ambience;
   //  DBHandler dbHandler;

     TextView _name;
     TextView _location;
     TextView _cuisineType;
     TextView _rating;
     ImageView _img;

     int foodStoreId;
     Bundle extras;
     ArrayList<String> foodStoreList;
     byte [] byteArray;
     Bitmap bmp;

     float fq;
     float p;
     float s;
     float a;
     String c;

     /*
     Method Name: onCreate
     Creation Date: 2/21/2018
     Purpose:
     Calling Arguments:
     Required Files:
     Database Tables:
     Return Value: None
      */
     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_submit_rating);



          submit = findViewById(R.id.submitButt);
          subRating = findViewById(R.id.ratingText);
          food_quality = findViewById(R.id.food_quality);
          pricing = findViewById(R.id.pricing);
          service = findViewById(R.id.service);
          ambience = findViewById(R.id.ambience);

          _name = findViewById(R.id.foodStoreName);
          _location = findViewById(R.id.foodStoreLoc);
          _cuisineType = findViewById(R.id.foodStoreCuis);
          _rating = findViewById(R.id.foodStoreRat);
         // _img = findViewById(R.id.imageView);

          /*dbHandler = new DBHandler(this);
          try {
               dbHandler.createDB();
          } catch (IOException ioe) {
               throw new Error("Unable to create database");
          }
          try {
               dbHandler.openDB();
          } catch (SQLiteException sqle) {
               throw sqle;
          }*/
          extras = getIntent().getExtras();
          foodStoreId = extras.getInt("id");

          foodStoreList = extras.getStringArrayList("foodstore");
          byteArray = extras.getByteArray("image");
        //  bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        //  String rating1 = String.valueOf(extras.getFloat("rating"));

          _name.setText(foodStoreList.get(1));
          _location.setText(foodStoreList.get(2));
          _cuisineType.setText(foodStoreList.get(3));
          _rating.setText(foodStoreList.get(4));
          new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                    .execute(foodStoreList.get(5));
         // _img.setImageBitmap(bmp);
          //LayerDrawable stars = (LayerDrawable) food_quality.getProgressDrawable();
          //stars.getDrawable(2).setColorFilter(Color.parseColor("@colors/dark_orange"), PorterDuff.Mode.SRC_ATOP);



          submit.setOnClickListener(new View.OnClickListener() {

               /*
              Method Name: onClick
              Creation Date: 2/21/2018
              Purpose:
              Calling Arguments:
              Required Files:
              Database Tables:
              Return Value: None
               */
               @Override
               public void onClick(View view) {
                    rating = findViewById(R.id.submitText);

                    fq = food_quality.getRating();
                    p = pricing.getRating();
                    s = service.getRating();
                    a = ambience.getRating();
                    c = rating.getText().toString();

                    if (fq == 0 || p == 0 || s == 0 || a == 0 || c.isEmpty()){
                         Toast.makeText(SubmitRating.this, "Incomplete Rating Information", Toast.LENGTH_SHORT).show();
                    }
                    else {
                         Toast.makeText(SubmitRating.this, "Rating Submitted", Toast.LENGTH_SHORT).show();
                         subRating.setText("Food Quality: " + String.valueOf(fq) +
                                   "\nPricing: " + String.valueOf(p) +
                                   "\nService: " + String.valueOf(s) +
                                   "\nAmbience: " + String.valueOf(a) +
                                   "\nComment: " + c);

                         sendPost();
                         Log.d("post","post task executed");
                         // float ave = (fq + p + s + a) / 4;
                         //  ArrayList<Float> fs = dbHandler.getFoodStore(foodStoreId);
                         //  float curr_sum = fs.get(0) + ave;
                         //  float ctr = fs.get(1) + 1;
                         //  dbHandler.updateFoodStore(foodStoreId,curr_sum/ctr,curr_sum,ctr);

//                         Calendar calendar = Calendar.getInstance();
//                         String date = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
                         // dbHandler.addRating(date, fq, p, s, a, c, ave);
//                         int user_id = extras.getInt("user_id") + 1;
//                         Log.d("user_id",String.valueOf(user_id));
//                         Log.d("store_id",String.valueOf(foodStoreId));
                         // dbHandler.addRatingRelation(foodStoreId,  user_id);

                         Intent resultIntent = new Intent();
                         resultIntent.putExtras(extras);

                         setResult(RESULT_OK, resultIntent);
                         finish();



                    }


               }
          });


     }
     public void sendPost() {
          Thread thread = new Thread(new Runnable() {
               @Override
               public void run() {
                    try {
                         HttpURLConnection urlConnection = null;
                         JSONObject jsonInput = new JSONObject();

                         String site_url_json = "https://rocky-retreat-95836.herokuapp.com/user/";
                         URL url = new URL(site_url_json);

                         urlConnection = (HttpURLConnection) url.openConnection();
                         urlConnection.setRequestMethod("POST");
                         urlConnection.setRequestProperty("Content-Type","application/json;charset=UTF-8");
                         urlConnection.setRequestProperty("Accept", "application/json");
                         urlConnection.setDoOutput(true);
                         urlConnection.setDoInput(true);
                         urlConnection.connect();

                         jsonInput.put("foodquality",fq);
                         jsonInput.put("pricing",p);
                         jsonInput.put("service",s);
                         jsonInput.put("ambience",a);
                         jsonInput.put("comment",c);
                         jsonInput.put("food_store_id",foodStoreId);

                         Log.d("JSONpost",jsonInput.toString());
                         DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
                         //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                         os.writeBytes(jsonInput.toString());

                         os.flush();
                         os.close();

                         InputStream inputStream = urlConnection.getInputStream();
                         StringBuffer buffer = new StringBuffer();

                         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                         String line;
                         while ((line = reader.readLine()) != null) {
                              buffer.append(line);
                         }

                         String resultJson = buffer.toString();
                         Log.d("json_result_post", resultJson);

                         //Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                         //Log.i("MSG" , conn.getResponseMessage());

                         urlConnection.disconnect();
                    } catch (Exception e) {
                         Log.d("submitExc","submit exception");
                         e.printStackTrace();
                    }
               }
          });

          thread.start();
     }

}
