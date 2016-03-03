package converter.currency.com.currencyconverter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class CurrencyReceiver extends BroadcastReceiver {
    private static final String TAG = "CurrencyReceiver";

    public static final String CURRENCY_AMOUNT = "currency_amount";
    public static final String CURRENCY_LOCALE = "currency_locale";
    public static final String CURRENCY_CONVERT_ACTION_REQ = "sneha.shridhar.custom.intent.currency.req";
    public static final String CURRENCY_CONVERT_ACTION_REPLY = "sneha.shridhar.custom.intent.currency.reply";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "action ==>receiving in converter to convert" + intent.getAction());
        String action = intent.getAction();

        // This app only handles the "request" broadcast event and not the "reply" command.
        if (intent.getAction().equals(CURRENCY_CONVERT_ACTION_REQ)) {
            // "Request" Broadcast
            Bundle b = intent.getExtras();

            Log.e(TAG, "currency amount = " + b.getFloat(CURRENCY_AMOUNT));
            Log.e(TAG, "currency Locale = " + b.getString(CURRENCY_LOCALE));

            Intent i = new Intent(context, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtras(b);
            context.startActivity(i);
        }

        if (intent.getAction().equals(CURRENCY_CONVERT_ACTION_REPLY)){
            //do nothing
        }
    }
}
