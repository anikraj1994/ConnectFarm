package com.example.motu.localreg;

import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.Parse;
import com.parse.ParseObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    TextView tvIsConnected;
    EditText farmName, farmAddr, farmMob;
    Button btnSend;


    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public GoogleApiClient mGoogleApiClient;
    public LocationRequest mLocationRequest;
    public double currentLatitude;
    public double currentLongitude;

    Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        tvIsConnected = (TextView) findViewById(R.id.tv);
        farmName = (EditText) findViewById(R.id.fName);
        farmAddr = (EditText) findViewById(R.id.fAddr);
        farmMob = (EditText) findViewById(R.id.fMob);
        btnSend = (Button) findViewById(R.id.SendData);

        final Spinner spinner = (Spinner) findViewById(R.id.spinLan);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this,
                R.array.language_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "4n6JU0CQuBv0LzLoV3dSXDLZTPtPj5ZPd7wSlcRm", "Z20QwmvtdQL0qAjyyuBihIlbSynAUfdyUgoguVxn");


        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(MainActivity.this)
                .addOnConnectionFailedListener(MainActivity.this)
                        //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = farmName.getText().toString();
                String b = farmAddr.getText().toString();
                String c = "";
                String d = spinner.getSelectedItem().toString();
                CharSequence Mobno = farmMob.getText().toString();
                if(isValidPhoneNumber(Mobno)){
                    c = farmMob.getText().toString();
                    if(TextUtils.isEmpty(a)) {
                        farmName.setError("Fill the field");
                    }
                    else if(TextUtils.isEmpty(b)) {
                        farmAddr.setError("Fill the field");
                    }
                    else{
                        new ReadInfo().execute(a, b, c, d);
                        Toast.makeText(MainActivity.this,"User is sucessfully registered",Toast.LENGTH_SHORT).show();
                        farmName.setText("");
                        farmAddr.setText("");
                        farmMob.setText("");
                        spinner.setSelection(1);
                    }
                    //break;
                }else{
                    farmMob.setText("");
                    Toast.makeText(MainActivity.this,"Enter valid mobile number",Toast.LENGTH_SHORT).show();
                    //onClick(v);
                }

            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    private boolean isValid(CharSequence s ){
        if(TextUtils.isEmpty(s)) return false;
        return true;
    }

    private boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (phoneNumber.length()!=10) {
            return false;
        }
        else if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, MainActivity.this);
            mGoogleApiClient.disconnect();
        }


    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MainActivity.this);

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            Toast.makeText(MainActivity.this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        Toast.makeText(MainActivity.this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(MainActivity.this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
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
    /*
    class ReadInfo extends AsyncTask<String, Void, Void> {
        ProgressDialog pd;

        protected void onPreExecute() {
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("loading");
            pd.show();
        }

        protected Void doInBackground(String... ar) {
           /* ParseObject Farminfo = new ParseObject("Farmer");
            //Farminfo.put("Farmer_Name", ar[0]);
            //Farminfo.put("Farmer_Address", ar[1]);
            //Farminfo.put("Contact_No", ar[2]);
            Farminfo.put("Language",ar[3]);
            ParseGeoPoint point = new ParseGeoPoint(currentLatitude,currentLongitude);
            Farminfo.put("Farmer_Location", point);
            Farminfo.saveInBackground();
          //  Toast.makeText(MainActivity.this,"Data is submitted",Toast.LENGTH_SHORT).show();

                // Create a new HttpClient and Post Header

            return null;
        }
        // return "You are at PostExecute";


        protected void onPostExecute(Void a) {
            pd.dismiss();
        }


    }*/
    class ReadInfo extends AsyncTask<String, Void,List<ParseObject>> {
// ProgressDialog pd;

        protected void onPreExecute() {
//pd = new ProgressDialog(MainActivity.this);
//pd.setMessage("loading");
// pd.show();
        }

        protected List<ParseObject> doInBackground(String... ar) {
            try {
                String urlParameters =
                        "Name=" + URLEncoder.encode(ar[0], "UTF-8") +
                                "&Loc=" + URLEncoder.encode(currentLatitude+"", "UTF-8")+
                                "&Log=" + URLEncoder.encode(currentLatitude+"", "UTF-8")+
                                "&Language=" + URLEncoder.encode(ar[3], "UTF-8")+
                                "&ContactNo=" + URLEncoder.encode(ar[2], "UTF-8")+
                                "&Address=" + URLEncoder.encode(ar[1], "UTF-8");
                excutePost("https://connectfarmphp.herokuapp.com/register.php",urlParameters);

            }
            catch(Exception e){}
            return null;
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
