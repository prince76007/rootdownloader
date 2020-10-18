package my.awesome.rootdownloader;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashudevs.facebookurlextractor.FacebookExtractor;
import com.ashudevs.facebookurlextractor.FacebookFile;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;


public class DownloadVideo extends AppCompatActivity implements View.OnKeyListener,View.OnClickListener{

    EditText urlText,fileName;
    TextView urlEg;
    Button downloadButton;
    LinearLayout downloadLayoutLinear;
    String pathname,siteName;
    Intent intent;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1&& grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED&& grantResults[1]==PackageManager.PERMISSION_GRANTED){
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                startDownload();
            }
        }else {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent=this.getIntent();
        siteName=intent.getStringExtra("site");
        setTitle(siteName);
        setContentView(R.layout.activity_download_video);
        urlEg=findViewById(R.id.urlEg);
        switch(siteName){
            case "By File Link":
                urlEg.setText("URL eg: http://abcxyz.com/videosong.mp4");
                break;
            case "Facebook":
                urlEg.setText("URL eg: https://www.facebook.com/watch/?v=1234");
                break;
            case "Instagram":
                urlEg.setText("URL eg: https://www.instagram.com/p/ABC/?igshid=123");
                break;
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        downloadButton=findViewById(R.id.downloadButton);
        downloadButton.setClickable(false);
        downloadButton.setFocusable(false);
        downloadLayoutLinear=findViewById(R.id.downloadLayoutLinear);
        urlText= findViewById(R.id.urlEditText);
        fileName=findViewById(R.id.fileName);
        downloadLayoutLinear.setOnClickListener(this);
        fileName.setOnKeyListener(this);

        urlText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 10 && fileName.getText().toString().length() > 4) {
                    downloadButton.setClickable(true);
                } else {
                    downloadButton.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fileName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("ResourceType")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 4 && urlText.getText().toString().length() > 10) {
                    downloadButton.setClickable(true);
                } else {
                    downloadButton.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public void startDownload(){
        String urlString=urlText.getText().toString().trim();
        switch (siteName){
            case "By File Link":
                //byLink
                downloadByUrl(urlString);
                break;

            case "Facebook":
                //fb
                @SuppressLint("StaticFieldLeak") FacebookExtractor facebookExtractor = new FacebookExtractor(DownloadVideo.this, urlString, false) {
                    @Override
                    protected void onExtractionComplete(FacebookFile facebookFile) {
                       downloadByUrl(facebookFile.getHdUrl());
                    }

                    @Override
                    protected void onExtractionFail(Exception Error) {
                  showToast("Unable to Extract!");
                    }
                };
                break;

            case "Instagram":
                Pattern p=Pattern.compile("(.*)/\\?");
                Matcher m=p.matcher(urlString);
                if (m.find()){
                   downloadByUrl(m.group(1)+"/?__a=1");
                }else
                    showToast("Invalid! Please Check The Link");
                break;
        }
    }

    public void downloadByUrl(String downloadUrl){
        showToast("Downloading Start!");
        Download download = new Download();
        download.execute(downloadUrl);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN){
            if (downloadButton.isClickable())
            startDownload();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.downloadButton) {
            downloadButton.setClickable(false);
            downloadButton.setFocusable(false);
            pathname = fileName.getText().toString().trim();
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(DownloadVideo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                startDownload();
            }
        }else{
            if (urlText.didTouchFocusSelect() || fileName.didTouchFocusSelect()) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public void showToast(String string){
        Toast.makeText(DownloadVideo.this,string,Toast.LENGTH_LONG).show();
    }
    class Download extends AsyncTask<String,String, Boolean> {

        boolean downloaded=true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Boolean doInBackground(String... strings) {
            String instaUrl="";
            boolean downloading=true;
            try{
                if (siteName.equals("Instagram")){
                    try {
                        String result="";
                        URL url= new URL(strings[0]);
                        HttpsURLConnection httpURLConnection= (HttpsURLConnection)url.openConnection();
                        httpURLConnection.connect();
                        InputStream inputStream= httpURLConnection.getInputStream();
                        InputStreamReader inputStreamReader= new InputStreamReader(inputStream);
                        int read =inputStreamReader.read();
                        while(read!=-1){
                            result+=(char)read;
                            read=inputStreamReader.read();
                        }
                        Pattern pa= Pattern.compile("video_url\":\"(.*)\",\"video_view_count");
                        Matcher ma= pa.matcher(result);
                        if (ma.find()){
                            instaUrl=ma.group(1);
                        }
                    }catch (Exception e){
                        showToast("Unable to Extract!");
                        e.printStackTrace();
                    }
                }
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri= Uri.parse((siteName.equals("Instagram")? instaUrl:strings[0]));
                DownloadManager.Request request= new DownloadManager.Request(uri);
                request.setTitle("Root Downloader");
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,pathname);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDescription(pathname);
                request.setVisibleInDownloadsUi(true);
                downloadManager.enqueue(request);
                DownloadManager.Query query= new DownloadManager.Query();
                Cursor c=null;
                if (query!=null){
                        query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL|DownloadManager.STATUS_RUNNING|DownloadManager.STATUS_FAILED|DownloadManager.STATUS_PAUSED|DownloadManager.STATUS_PENDING);
                }else {
                    downloading=false;
                    downloaded=false;
                        return downloading;
                }
                while (downloading){
                    c=downloadManager.query(query);
                    if (c.moveToFirst()){
                        int status=c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        if(status==DownloadManager.STATUS_SUCCESSFUL){
                            downloaded=true;
                            downloading=false;
                                return downloading;
                        }
                        if(status==DownloadManager.STATUS_FAILED){
                            downloaded=false;
                            downloading=false;
                            return downloading;
                        }
                    }
                }
                return null;
            }catch (Exception e){
                e.printStackTrace();
                downloading=false;
                downloaded=false;
                return downloaded;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            String status = "";
            if (!aBoolean) {
                if (downloaded) {
                    status = "Downloading Successful!";
                    downloadButton.setFocusable(true);
                    downloadButton.setClickable(true);
                    urlText.setText("");
                    fileName.setText("");
                } else {
                    status="Downloading Failed!";

                }
                showToast(status);
            }
        }
    }



}