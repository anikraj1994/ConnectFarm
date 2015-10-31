package in.recursion.connectfarm;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeLayout;
    Toast toast;
    ListView listview;
    AccountHeader headerResult;
    Drawer result;
    public MainActivity thisifier =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "OQKO0mfkmRpR6pqNegLCQO17Vk5zG3NT84jpddI8", "mHQHfFJm6gBoXyPGD71B6EoDdGL5BxK7Kzu8l8Mn");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName("Anik Raj").withEmail("anikraj1994@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withAccountHeader(headerResult)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Home"),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Settings")
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        return true;
                    }
                })
                .build();


        listview = (ListView)findViewById(R.id.listView);
        new ReadCardsLoc().execute("k");
        new ReadCards().execute("k");
        thisifier=this;
        swipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new ReadCards().execute("k");
            }
        });

        swipeLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
    }

class ReadCards extends AsyncTask<String, Void, ArrayList<GCard>> {
    // ProgressDialog pd;

    protected void onPreExecute() {
        //pd = new ProgressDialog(MainActivity.this);
        //pd.setMessage("loading");
        // pd.show();
    }

    protected ArrayList<GCard> doInBackground(String... ar) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Farmer");
        // query.fromLocalDatastore();
        //query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    if(toast!=null){
                        if(toast.getView().isShown())
                            toast.cancel();}
                    toast.makeText(MainActivity.this,scoreList.size()+" web",Toast.LENGTH_SHORT).show();

                    ParseObject.unpinAllInBackground("Farmer", new DeleteCallback() {
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.d("parse errorrr", "Errorw: " + e.getMessage());
                                return;
                            }

                            // Add the latest results for this query to the cache.
                            ParseObject.pinAllInBackground("Farmer", scoreList);
                        }
                    });

                    listview.setAdapter(new CustomAdapter2(MainActivity.this, R.layout.card1, scoreList));
                    swipeLayout.setRefreshing(false);
                } else {
                    swipeLayout.setRefreshing(false);
                    // new ReadCardsLoc().execute("k");
                    Log.d("parse errorrr", "Errorw: " + e.getMessage());
                }
            }
        });
        //  ArrayList<GCard> list = new ArrayList<GCard>();
        return null;
    }
    protected void onPostExecute(ArrayList<GCard> a) {


    }
}

class ReadCardsLoc extends AsyncTask<String, Void,List<ParseObject>> {
    // ProgressDialog pd;

    protected void onPreExecute() {
        //pd = new ProgressDialog(MainActivity.this);
        //pd.setMessage("loading");
        // pd.show();
    }

    protected List<ParseObject> doInBackground(String... ar) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Farmer");
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    if(toast!=null){
                        if(toast.getView().isShown())
                            toast.cancel();}
                    toast.makeText(MainActivity.this, scoreList.size() + " local", Toast.LENGTH_SHORT).show();
                    if (!scoreList.isEmpty()) {



                        listview.setAdapter(new CustomAdapter2(MainActivity.this,R.layout.card1,scoreList));
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
        return  null;
    }
    // return "You are at PostExecute";


    protected void onPostExecute(List<ParseObject> a) {
        // Log.d(""+result);
        //String[] s = a.toArray(new String[a.size()]);
        //  list=a;

        //TextView t = (TextView) findViewById(R.id.textView);
        //  t.setText("");
        //    for (int i = 0; i < a.size(); i++)list.add("anik"); list.notifyDataSetChanged();
        //   t.append(s[i] + "\n");  listview.setAdapter(adapter);
        //  pd.dismiss();
        // adapter.notifyDataSetChanged();
        //  ArrayAdapter<String> adapter=new ArrayAdapter<String>(MainActivity.this,
        //       android.R.layout.simple_list_item_1,
        //         a);
        //   if (!a.isEmpty()) {
        //     Resources res = getResources();
        //         CustomAdapter adapter = new CustomAdapter(thisifier, a, res);
        //         listview.setAdapter(adapter);
        //      } else Toast.makeText(thisifier, "no ads", Toast.LENGTH_SHORT).show();
        //      //  adapter.notifyDataSetChanged();listview.setAdapter(adapter);
        //      swipeLayout.setRefreshing(false);
//
    }
}
}