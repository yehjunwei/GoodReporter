package tony.com.goodreporter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView mListView;
    private NewsListAdapter mNewsAdapter;
    private ArrayList<News> mNewsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mNewsList = new ArrayList<News>();
        mListView = (ListView) findViewById(R.id.main_list_view);
        mNewsAdapter = new NewsListAdapter(mNewsList);
        mListView.setAdapter(mNewsAdapter);
//
//        ParseObject testObject = new ParseObject("ReporterName");
//        testObject.put("name", "shitty");
//        testObject.saveInBackground();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);

        if(ParseUser.getCurrentUser() != null) {
            getFacebookPosts();
        }
    }

    private void getFacebookPosts() {
        mNewsList.clear();
//        Log.d(TAG, "fb access token = " + AccessToken.getCurrentAccessToken().getToken());
        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/feed",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                            /* handle the result */
//                        Log.d(TAG, response.toString());
                        JSONObject graphObject = response.getJSONObject();
                        JSONArray posts = graphObject.optJSONArray("data");
                        if(posts != null) {
                            for(int i = 0 ; i < posts.length() ; ++i) {
                                JSONObject post = posts.optJSONObject(i);
                                if(post != null) {
                                    Log.d(TAG, post.toString());
                                    String id = post.optString("id");
                                    getPostDetail(id);
                                }
                            }
                        }
                    }
                }
        ).executeAsync();
    }

    private void getPostDetail(final String id) {
        Bundle params = new Bundle();
        params.putString("fields", "id,name,link,message,created_time");
    /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/"+id,
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                    /* handle the result */
                        JSONObject obj = response.getJSONObject();
                        Log.d(TAG, "post detail: " + obj.toString());
                        String link = obj.optString("link");
                        String created_time = obj.optString("created_time");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                        if(!TextUtils.isEmpty(link)) {
                            News news = new News();
                            try {
                                long millis = sdf.parse(created_time).getTime();
                                news.time = Utils.convertMillisToDateString(millis, "yyyy.MM.dd", Utils.TIME_DEFAULT_TIMEZONE, false);

                            } catch (java.text.ParseException e) {
                                e.printStackTrace();
                            }
                            news.name = obj.optString("name");
                            news.link = link;
                            news.message = obj.optString("message");
                            mNewsList.add(news);
                            mNewsAdapter.notifyDataSetChanged();
                        }
                    }
                }
        ).executeAsync();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            LoginManager.getInstance().logOut();
            ParseUser.logOut();
            Intent intent = new Intent();
            intent.setClass(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (ParseUser.getCurrentUser() == null)
            menu.getItem(0).setEnabled(false);
        else
            menu.getItem(0).setEnabled(true);
        return true;
    }

    class NewsListAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private ArrayList<News> mItems;

        public NewsListAdapter(ArrayList<News> newsList) {
            mItems = newsList;
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return ((mItems == null) ? 0 : mItems.size());
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.main_list_item, null);
                holder.time = (TextView) convertView.findViewById(R.id.main_list_item_time);
                holder.message = (TextView) convertView.findViewById(R.id.main_list_item_title);
                holder.link = (TextView) convertView.findViewById(R.id.main_list_item_link);
                if (convertView != null)
                    convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // set data
            holder.time.setText(mItems.get(position).time);
            holder.message.setText(mItems.get(position).message);
            String link = mItems.get(position).link;
            String name = mItems.get(position).name;
            holder.link.setText(Html.fromHtml("<a href=\""+link+"\">"+name+"</a> "));
            holder.link.setMovementMethod(LinkMovementMethod.getInstance());

            return convertView;
        }
    }

    class ViewHolder {
        TextView time;
        TextView message;
        TextView link;
    }
}
