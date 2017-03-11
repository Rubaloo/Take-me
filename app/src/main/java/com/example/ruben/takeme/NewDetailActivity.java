package com.example.ruben.takeme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;


public class NewDetailActivity extends AppCompatActivity{


    private static final String NEW_DETAIL_SHARE_HASHTAG = " #TakeMe";
    private static final String TAKE_ME_INTERESTING_LINK = "https://www.petfinder.com";

    private ShareActionProvider mShareActionProvider;
    private String mDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_detail);
        if (savedInstanceState == null) {


            Bundle arguments = new Bundle();
            arguments.putParcelable(NewDetailFragment.NEW_DETAIL_URI, getIntent().getData());

            NewDetailFragment fragment = new NewDetailFragment();
            fragment.setArguments(arguments);


            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        mDetail = "Detail id";
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        mShareActionProvider.setShareIntent(createShareDetailIntent());

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



    private Intent createShareDetailIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, NEW_DETAIL_SHARE_HASHTAG + ": Take a look to: " + TAKE_ME_INTERESTING_LINK );
        return shareIntent;
    }
}
