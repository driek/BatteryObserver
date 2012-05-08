package eu.suteki.batteryObserver.broadcastreceiver;

import eu.suteki.batteryObserver.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.widget.Toast;

public class BatteryBroadcastReceiver extends BroadcastReceiver
{
	private static final String BATTERY_CHARGE = "batteryCharge";
	private static final String PREFS_NAME = "eu.suteki.batteryObserver";

	@Override
	public void onReceive(Context context, Intent intent)
	{

		// Get the current level of the battery and the scale (max) of the
		// battery
		int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		// Calculate the remaining charge and give a notification if it was changed.
		if (isChargeChanged(context, level))
		{
			float batteryPct = level / (float) scale;
			String text = String.format(context.getString(R.string.charge_text), batteryPct * 100);
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
	}

	/**
	 * Method to check if the battery level has changed. Because it is possible
	 * the service has stopped, we check if the current charge is same as the
	 * previous (saved) one. Also save the new battery charge again.
	 * 
	 * @param context The context needed for getting the preferences.
	 * @param charge The current charge of the battery
	 * @return
	 */
	private boolean isChargeChanged(Context context, int charge)
	{
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		int prevCharge = settings.getInt(BATTERY_CHARGE, -1);
		if (prevCharge < 0 || prevCharge != charge)
		{
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt(BATTERY_CHARGE, charge);
			editor.commit();
			return true;
		}
		return false;
	}

}
