package com.example.v10t1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Site> sites = new ArrayList<>();
    private EditText inputField;
    private WebView window;
    private String urlEnd, urlStart = "http://";
    private Context context;
    private int siteid = 0;
    private int tracker = 0;
    //limit for how many times you can move back and forth on the site
    private int limit = 10;
    private View v;
    private ListIterator iter = sites.listIterator();
    private Toast toast;
    private Button fwd, bwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        inputField = findViewById(R.id.input);
        window = findViewById(R.id.webView);
        window.setWebViewClient(new WebViewClient());
        fwd = findViewById(R.id.forward);
        bwd = findViewById(R.id.backward);
        toast = Toast.makeText(MainActivity.this," ",Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0,100);
        inputField.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                try {
                    searchSite(v);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        });
    }

    public void moveForward(View v) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(50);
        if(iter.hasNext()) {
            if(!iter.hasPrevious()){
                iter.next();
            }
            Site nextSite = (Site) iter.next();
            window.loadUrl(urlStart + nextSite.getSiteName());
            inputField.setText(nextSite.getSiteName());
            urlEnd = nextSite.getSiteName();
            if(!iter.hasNext()){
                fwd.setEnabled(false);
                iter.previous();
            }
            bwd.setEnabled(true);
            tracker++;
        }
        else {
            //shouldn't occur ever
            toast.setText("cant move forward");
            toast.show();
        }
    }

    public void moveBackward(View v) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(50);
        if(iter.hasPrevious() && tracker > 1) {
            if(!iter.hasNext()){
                iter.previous();
            }
            Site prevSite = (Site) iter.previous();
            window.loadUrl(urlStart + prevSite.getSiteName());
            inputField.setText(prevSite.getSiteName());
            urlEnd = prevSite.getSiteName();
            tracker--;
            if(!iter.hasPrevious() || tracker == 1){
               bwd.setEnabled(false);
               iter.next();
            }
            fwd.setEnabled(true);
        }
        else {
            //shouldn't occur ever
            toast.setText("cant move backward");
            toast.show();
        }
    }

    public void searchSite(View v) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(50);
        urlEnd = inputField.getText().toString();
        if( urlEnd.matches("index.html")){
            window.getSettings().setJavaScriptEnabled(true);
            window.loadUrl("file:///android_asset/"+urlEnd);
        } else {
            window.loadUrl(urlStart + urlEnd);
            int i = 0;
            if(iter.hasPrevious()){
                iter.previous();
            }
            while (iter.hasNext()) {
                Site site = (Site) iter.next();
                i++;
                if(i > tracker) {
                    iter.remove();
                }
            }
            //if recent sites over the limit don't increase tracker
            if(tracker < limit + 1) {
                iter.add(new Site(siteid++, urlEnd));
                tracker++;
            }
            else {
                iter.add(new Site(siteid++, urlEnd));
            }
            iter.previous();
            if(iter.hasPrevious()){
                bwd.setEnabled(true);
            }
            fwd.setEnabled(false);
        }
    }

    public void refresh(View v){
        window.loadUrl(urlStart + urlEnd);
    }

    public void shoutOut(View v){
        window.evaluateJavascript("javascript:shoutOut()",null);
    }

    public void initialize(View v){ window.evaluateJavascript("javascript:initialize()",null); }
}