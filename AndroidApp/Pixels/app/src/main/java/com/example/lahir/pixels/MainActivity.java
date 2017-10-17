package com.example.lahir.pixels;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button img1;
    int PICK_IMAGE_MULTIPLE = 1;
    ArrayList<String> imagesPathList;
    ArrayList<Bitmap> yourbitmap;
    ImageView Img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img1 = (Button) findViewById(R.id.pic1);
        img1.setOnClickListener(this);
        Img = (ImageView) findViewById(R.id.testImage);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select file to upload "), PICK_IMAGE_MULTIPLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == PICK_IMAGE_MULTIPLE){
                imagesPathList = new ArrayList<String>();
                Uri imagesPath = data.getData();
                /*for (int i=0;i<imagesPath.length;i++){
                    imagesPathList.add(imagesPath[i]);
                    yourbitmap.add(BitmapFactory.decodeFile(imagesPath[i]));

                }*/
                try{
                Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagesPath);
                sendBlobObj(bm);
                //Img.setImageBitmap(Img1);
                }catch(Exception e){}

            }
        }

    }
    void sendBlobObj(Bitmap bm){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] bArray = bos.toByteArray();
        String encodedImage = Base64.encodeToString(bArray, Base64.DEFAULT);
        ArrayList<String> nameValuePairs = new  ArrayList<String>();
        new MainActivity.InvokeWebService().execute(bm);
    }

    public class InvokeWebService extends AsyncTask<Bitmap,Integer,String>
    {
        @Override
        protected String doInBackground(Bitmap... bitmaps) {

            if (bitmaps[0] == null)
                return null;

            Bitmap bitmap = bitmaps[0];
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // convert Bitmap to ByteArrayOutputStream
            InputStream in = new ByteArrayInputStream(stream.toByteArray()); // convert ByteArrayOutputStream to ByteArrayInputStream

            URL url;
            String response = "";
            String requestUrl = "http://ec2-54-213-169-9.us-west-2.compute.amazonaws.com/pixelateWebService.php";
            StringBuilder str = new StringBuilder();
            StringBuilder result = new StringBuilder();
            //str.append("test=" + "parameter&");
            String encRequestUrl = null;
            try {
                encRequestUrl = URLEncoder.encode(strings[0], "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            str.append("?image1="+encRequestUrl);
            String mystring = str.toString();
            //requestUrl = requestUrl +mystring;
            try {
                url = new URL(requestUrl);
                HttpURLConnection myconnection = (HttpURLConnection) url.openConnection();
                myconnection.setReadTimeout(15000);
                myconnection.setConnectTimeout(15000);
                myconnection.setRequestMethod("GET");
                myconnection.setDoInput(true);
                myconnection.setDoOutput(true);

                if (200 == HttpURLConnection.HTTP_OK)
                {
                    InputStream in = url.openStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.e("abc" , result.toString());
            return result.toString();


        }
    }

}
