package converter.currency.com.currencyconverter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    TextView amount;
  // TextView toLocale;
    TextView fromLocale;
    String toCurrencyLocale ;
    String toCurrencyLocalenew ;
    Button applyButton;
    Button dontApplyButton;
    Float currencyValue;
    String fromCurrencyLocale = "USD";
    TextView convertedTextView;
    public static final String CURRENCY_CONVERT_ACTION_REPLY = "sneha.shridhar.custom.intent.currency.reply";
    public static final String CURRENCY_CONVERT_REJECT = "rejected";
    public static final String CURRENCY_CONVERT_RESULT = "result";
    public static final String RESULT ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get references to the widgets from layout
        amount = (TextView) findViewById(R.id.currency_text);
        fromLocale = (TextView) findViewById(R.id.from_locale);
        applyButton = (Button) findViewById(R.id.apply_button);
        dontApplyButton = (Button) findViewById(R.id.dont_button);
        convertedTextView = (TextView) findViewById(R.id.converted_text);


        //get the values(currency and locale) from intent
        Bundle b = getIntent().getExtras();
        if(b!=null) {
            toCurrencyLocale = b.getString(CurrencyReceiver.CURRENCY_LOCALE);
            currencyValue = b.getFloat(CurrencyReceiver.CURRENCY_AMOUNT);
            amount.setText("" + currencyValue);
            fromLocale.setText(toCurrencyLocale);
        }


        //apply button
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertedTextView.setText("Fetching....");
                //Logic get the currency conversion from Google and convert the amount
                //Use www.google.com/finance/converter?a=100&from=usd&to=inr&format=json
                getConvertedValue();
               /* Intent intent = new Intent (CurrencyReceiver.CURRENCY_CONVERT_ACTION_REPLY);
                intent.putExtra("ReplyAnswer", Float.valueOf(convertedTextView.getText().toString())); // string
                sendBroadcast(intent);*/
                Log.e(TAG, "apply button sending indent");
                Intent intent = new Intent();
                intent.setAction(CURRENCY_CONVERT_ACTION_REPLY);
                Bundle b = new Bundle();
                b.putString("USD", toCurrencyLocale);
                intent.putExtras(b);
                sendBroadcast(intent);
                Log.e(TAG, "apply button indent sent");
            }
        });



        dontApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"I clicked do not");
                Intent intent = new Intent(CurrencyReceiver.CURRENCY_CONVERT_ACTION_REPLY);
                Bundle b = new Bundle();
                b.putString(CURRENCY_CONVERT_REJECT, "reject");
                intent.putExtras(b);
                sendBroadcast(intent);
            }
        });
    }


    private void getConvertedValue() {

        AsyncTask<Void, Void, String> currencyConverterTak = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String error = "Check Internent Connection!!";
                String response = null;
                if (toCurrencyLocale.equals("Indian Rupee"))
                {
                    toCurrencyLocalenew = "INR";
                } else if(toCurrencyLocale.equals("British Pound")) {
                    toCurrencyLocalenew = "GBP";
                }else if(toCurrencyLocale.equals("Euro")) {
                    toCurrencyLocalenew = "EUR";
                }
                String url_str = String.format("https://www.google.com/finance/converter?a=%s&from=%s&to=%s",
                        Float.valueOf(currencyValue), fromCurrencyLocale, toCurrencyLocalenew);
                URL url = null;
                try {
                    url = new URL(url_str);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    bufferedReader.close();
                    response = stringBuilder.toString();

                    Log.e(TAG, response);
                } catch (IOException e) {
                    e.printStackTrace();
                    response = error;
                } finally {
                    urlConnection.disconnect();
                }

                String result = parseHtmlResponse(response);
                Intent intent = new Intent(CurrencyReceiver.CURRENCY_CONVERT_ACTION_REPLY);
                Bundle b = new Bundle();
                b.putString(CURRENCY_CONVERT_REJECT, result);
                Log.e("receivedamountbsend:",currencyValue.toString());
                b.putString("amout_sent",currencyValue.toString());
                b.putString("locale_sent",toCurrencyLocale.toString());
                intent.putExtras(b);
                sendBroadcast(intent);

                return parseHtmlResponse(response);
            }

            @Override
            protected void onPostExecute(String value) {
                super.onPostExecute(value);
                convertedTextView.setText(value);

                /*Intent intent = new Intent();
                intent.setAction(CURRENCY_CONVERT_ACTION_REPLY);
                intent.putExtra("ReplyAnswer", "shit"); // string
                Log.e(TAG,"sending reply to receiver");
                sendBroadcast(intent);*/



            }


        };

        currencyConverterTak.execute();
    }

    //parse the html response and get only the converted value from response.
    private static String parseHtmlResponse(String response) {
        String[] results = response.split("<span class=bld>");
        if(results[1] == null) {
            return "Conversion Error!";
        }else{
            String[] values = results[1].split("</span>");
            if(values[0] == null){
                return "Conversion Error!";
            }else{
                return values[0];
            }
        }
    }


}
