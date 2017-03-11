package com.example.ruben.takeme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class NewsActivity extends AppCompatActivity implements NewsFragment.OnNewsSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_create_new:
                startActivity(new Intent(this,CreateNewActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // NewsFragment Interface
    public void onNewSelected(Uri uri) {

        Intent intent = new Intent(this, NewDetailActivity.class)
                .setData(uri);
        startActivity(intent);
    }


}
