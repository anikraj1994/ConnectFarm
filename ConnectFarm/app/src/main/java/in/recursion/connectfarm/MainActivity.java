package in.recursion.connectfarm;

import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener  {




    //Define a request code to send to Google Play services
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;

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


        //TextView tv1= (TextView)findViewById(R.id.)



       Parse.initialize(this, "4n6JU0CQuBv0LzLoV3dSXDLZTPtPj5ZPd7wSlcRm", "Z20QwmvtdQL0qAjyyuBihIlbSynAUfdyUgoguVxn");
      //  Parse.initialize(this, "4n6JU0CQuBv0LzLoV3dSXDLZTPtPj5ZPd7wSlcRm", "Z20QwmvtdQL0qAjyyuBihIlbSynAUfdyUgoguVxn");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                        //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


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

                        Toast.makeText(MainActivity.this,"asd",Toast.LENGTH_SHORT).show();
                        new post().execute("asd");
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

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                ParseObject entry = (ParseObject) parent.getItemAtPosition(position);
                try {

                    Intent theintent = new Intent(MainActivity.this,Show.class);
                    theintent.putExtra("product", entry.getString("Product"));
                    theintent.putExtra("fn", entry.getString("Max"));
                    theintent.putExtra("rate", entry.getDouble("Rate_Per_Kg"));
                    startActivity(theintent);
                   // Toast.makeText(MainActivity.this,entry.getString("item_name"),Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Toast.makeText(getApplication(), "Link not available. sorry.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

class ReadCards extends AsyncTask<String, Void, ArrayList<GCard>> {
    // ProgressDialog pd;

    protected void onPreExecute() {
        //pd = new ProgressDialog(MainActivity.this);
        //pd.setMessage("loading");
        // pd.show();
    }

    protected ArrayList<GCard> doInBackground(String... ar) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Ads");
        // query.fromLocalDatastore();
        //query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    if(toast!=null){
                        if(toast.getView().isShown())
                            toast.cancel();}
                    toast.makeText(MainActivity.this,scoreList.size()+" web",Toast.LENGTH_SHORT).show();

                    ParseObject.unpinAllInBackground("Ads", new DeleteCallback() {
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.d("parse errorrr", "Errorw: " + e.getMessage());
                                return;
                            }

                            // Add the latest results for this query to the cache.
                            ParseObject.pinAllInBackground("Ads", scoreList);
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
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Ads");
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







    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }


    }

    /**
     * If connected get lat and long
     *
     */
    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
            /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * If locationChanges change lat and long
     *
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
    }



    class post extends AsyncTask<String, Void,List<ParseObject>> {
        // ProgressDialog pd;

        protected void onPreExecute() {
            //pd = new ProgressDialog(MainActivity.this);
            //pd.setMessage("loading");
            // pd.show();
        }

        protected List<ParseObject> doInBackground(String... ar) {
            try {
                String urlParameters =
                        "Name=" + URLEncoder.encode("XXX", "UTF-8") +
                                "&Loc=" + URLEncoder.encode("???", "UTF-8")+
                                "&Log=" + URLEncoder.encode("???", "UTF-8")+
                                "&Language=" + URLEncoder.encode("???", "UTF-8")+
                                "&ContactNo=" + URLEncoder.encode("???", "UTF-8")+
                                "&Address=" + URLEncoder.encode("???", "UTF-8");
                excutePost("https://connectfarmphp.herokuapp.com/register.php",urlParameters);

            }
            catch(Exception e){}
            return  null;
        }


        protected void onPostExecute(List<ParseObject> a) {
            // Log.d(""+result);

        }



        public String excutePost(String targetURL, String urlParameters)
        {
            URL url;
            HttpURLConnection connection = null;
            try {
                //Create connection
                url = new URL(targetURL);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                connection.setRequestProperty("Content-Length", "" +
                        Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches (false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream ());
                wr.writeBytes (urlParameters);
                wr.flush ();
                wr.close ();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                return response.toString();

            } catch (Exception e) {

                e.printStackTrace();
                return null;

            } finally {

                if(connection != null) {
                    connection.disconnect();
                }
            }
        }
    }
}
