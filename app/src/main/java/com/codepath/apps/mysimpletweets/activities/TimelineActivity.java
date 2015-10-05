package com.codepath.apps.mysimpletweets.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.adapter.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.fragments.ComposeDialog;
import com.codepath.apps.mysimpletweets.models.PersistTweet;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity implements ComposeDialog.Listener{

    private TwitterClient client;
    private ListView lvTweets;
    private TweetsArrayAdapter aTweets;
    private ArrayList<Tweet> tweets;
    private SwipeRefreshLayout swipeContainer;

    private int visibleThreshold = 3;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = false;
    // Sets the starting page index
    private int startingPageIndex = 0;
    private int itemCount = 0;
    private int itemsPerPage = 8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        client = TwitterApplication.getRestClient();
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<Tweet>();
        aTweets = new TweetsArrayAdapter( this, tweets);
        lvTweets.setAdapter(aTweets);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        Configuration dbConfiguration = new Configuration.Builder(this).setDatabaseName("tweets.db").create();
        ActiveAndroid.initialize(dbConfiguration);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                //        send API req
                resetScreen();
                populateTimeline(true);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        lvTweets.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                ;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount <= 0) {
                    return;
                }

//                if (currentPage == 0) {
//                    return;
//                }
//
//                // If the total item count is zero and the previous isn't, assume the
//                // list is invalidated and should be reset back to initial state
//                if (totalItemCount < previousTotalItemCount) {
//                    currentPage = startingPageIndex;
//                    previousTotalItemCount = totalItemCount;
////                    if (totalItemCount == 0) {
////                        loading = true;
////                    }
//                }
//                // If it’s still loading, we check to see if the dataset count has
//                // changed, if so we conclude it has finished loading and update the current page
//                // number and total item count.
//                if (loading && (totalItemCount > previousTotalItemCount)) {
////                    loading = false;
//                    previousTotalItemCount = totalItemCount;
//                    currentPage++;
//                }

                // If it isn’t currently loading, we check to see if we have breached
                // the visibleThreshold and need to reload more data.
                // If we do need to reload some more data, we execute onLoadMore to fetch the data.
                int lastVisibleItem = firstVisibleItem + visibleItemCount;
                if (!loading && ((totalItemCount - lastVisibleItem) <= visibleThreshold)) {
                    onLoadMore(totalItemCount);
                }
            }
        });
        resetScreen();
//        loading = true;
        populateTimeline(true);
    }

    private void resetScreen() {
        aTweets.clear();
//        this.currentPage = 0;
        this.previousTotalItemCount = 0;
        this.loading = false;
        this.startingPageIndex = 0;
        this.itemCount = 0;
    }

    private void onLoadMore(int totalItemCount) {
        populateTimeline(false);
    }

    private void populateTimeline(boolean getNew) {
        if (loading) {
            return;
        }
        loading = true;
        if (!isNetworkAvailable()) {
            loadFromDB();
            return;
        }
        final boolean isNew = getNew;
        client.getHometime(getNew, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                ArrayList<Tweet> tweets = Tweet.fromJSONArray(json, isNew);
                aTweets.addAll(tweets);
                Log.v("DEBUG", json.toString());
                loading = false;
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.v("DEBUG", "Failure", throwable);
                loading = false;
                swipeContainer.setRefreshing(false);
            }
        });
    }

    public void loadFromDB() {
        List<PersistTweet> queryResults = new Select().from(PersistTweet.class)
                .orderBy("createdat DESC").limit(25).execute();
        for(int i =0; i < queryResults.size();i++) {
            aTweets.add(Tweet.fromDB(queryResults.get(i)));
        }
        loading = false;
        swipeContainer.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    public void onComposeAction(MenuItem mi) {
        FragmentManager fm = TimelineActivity.this.getFragmentManager();
        ComposeDialog composeDialog = new ComposeDialog();
//        Bundle args = new Bundle();
//        args.putParcelable("settingsData", settingSData);
//        composeDialog.setArguments(args);
        composeDialog.setListener(TimelineActivity.this);
        composeDialog.show(fm, "settings_dialog");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateTimeline() {
        aTweets.clear();
        resetScreen();
        populateTimeline(true);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
