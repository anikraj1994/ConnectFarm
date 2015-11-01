package in.recursion.connectfarm;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class Show extends AppCompatActivity {

    TextView tv1,tv2,tv3;
    String fn,product,r;
          double  rate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        Intent i= getIntent();
        fn = i.getStringExtra("fn");
        product = i.getStringExtra("product");
        rate = i.getDoubleExtra("rate", 0.0);
        r=rate+"";


         tv1=(TextView)findViewById(R.id.amount);
        tv2=(TextView)findViewById(R.id.name);
        tv3=(TextView)findViewById(R.id.number);


        Toast.makeText(Show.this, r+"   "+rate, Toast.LENGTH_SHORT).show();






    }


    public void click(View v){
        new post().execute(fn,product,r,tv1.getText().toString(),tv2.getText().toString(),tv3.getText().toString());
        Toast.makeText(Show.this, "   "+r, Toast.LENGTH_SHORT).show();
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
                        "fn=" + URLEncoder.encode(ar[0], "UTF-8") +
                                "&product=" + URLEncoder.encode(ar[1], "UTF-8")+
                                "&price=" + URLEncoder.encode(ar[2], "UTF-8")+
                                "&quantity=" + URLEncoder.encode(ar[3], "UTF-8")+

                                "&cusname=" + URLEncoder.encode(ar[4], "UTF-8")+
                                "&cusnum=" + URLEncoder.encode(ar[5], "UTF-8");
                excutePost("https://connectfarmphp.herokuapp.com/order.php", urlParameters);

            }
            catch(Exception e){
                Log.e("anik",e.toString());
                Log.e("anik ",ar[0] +" "+ar[1]+" "+ar[2]+" "+ar[3]+" "+ar[4]+" "+ar[5]);

                //Toast.makeText(Show.this, "error", Toast.LENGTH_SHORT).show();
            }
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
