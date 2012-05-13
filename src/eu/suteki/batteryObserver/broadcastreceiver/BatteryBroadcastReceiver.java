package eu.suteki.batteryObserver.broadcastreceiver;

import eu.suteki.batteryObserver.*;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.widget.Toast;

public class BatteryBroadcastReceiver extends BroadcastReceiver
{
	public static final String BATTERY_CHARGE = "battery_level";
	public static final String BATTERY_FROM = "battery_level_from";
	public static final String BATTERY_INTERVAL = "battery_level_interval";
	public static final String SHOW_SB_NOTI = "show_status_bar_notification";
	public static final String SHOW_TOAST = "show_toast";
	public static final String PREFS_NAME = "eu.suteki.batteryObserver";
	public static final int DEFAULT_THRESHOLD = 0;
	public static final int DEFAULT_INTERVAL = 2;
	public static final int DEFAULT_SCALE = 100;
	// The ID of the notification on the status bar for this application
	public static final int BATTERY_STATUSBAR_NOTIFICATION = 1;

	private static SharedPreferences settings;
	private int scale, level, status;
	private boolean showToast = false;
	private boolean showStatusbarNoti = false;

	@Override
	public void onReceive(Context context, Intent intent)
	{
		settings = context.getSharedPreferences(PREFS_NAME, 0);

		// Get the current level of the battery and the scale (max) of the
		// battery
		level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, DEFAULT_SCALE);
		status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		showStatusbarNoti = settings.getBoolean(SHOW_SB_NOTI, false);
		showToast = settings.getBoolean(SHOW_TOAST, false);

		// Calculate the remaining charge and give a notification if it was
		// changed.
		if (hasChargeChanged(context) && isNotificationOK(context))
		{
			if (showToast)
			{
				showBatteryChangedNotification(context, intent);
			} 
			if (showStatusbarNoti)
			{
				createStatusBarNotification(context);
			}
		}
	}
	
	/**
	 * Check if the battery is charging. 
	 * If charging, the status bar notification will be cancelled.
	 * @param context
	 * @return
	 */
	private boolean isCharging(Context context)
	{
		boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
				|| status == BatteryManager.BATTERY_STATUS_FULL;
		if (isCharging)
		{
			((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
			.cancel(BATTERY_STATUSBAR_NOTIFICATION);			
			return true;
		}
		return false;
	}

	/**
	 * Show a Toast message.
	 * 
	 * @param context
	 * @param intent
	 */
	private void showBatteryChangedNotification(Context context, Intent intent)
	{
		float batteryPct = level / (float) scale;
		String text = String.format(context.getString(R.string.charge_text), batteryPct * 100);
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	/**
	 * Method to check if the battery level has changed. Because it is possible
	 * the service has stopped, we check if the current charge is same as the
	 * previous (saved) one and if the battery is not currently charging. 
	 * Also save the new battery charge again.
	 * 
	 * @param context
	 *            The context needed for getting the preferences.
	 * @return
	 */
	private boolean hasChargeChanged(Context context)
	{
		if (isCharging(context))return false;
		int prevCharge = settings.getInt(BATTERY_CHARGE, -1);
		if (prevCharge < 0 || prevCharge != level)
		{
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt(BATTERY_CHARGE, level);
			editor.commit();
			return true;
		}
		return false;
	}

	/**
	 * Determine if a notification should be displayed depending on the user settings.
	 * @param context
	 * @return
	 */
	private boolean isNotificationOK(Context context)
	{
		int batteryFrom = settings.getInt(BATTERY_FROM, DEFAULT_THRESHOLD);
		int batteryInterval = settings.getInt(BATTERY_INTERVAL, DEFAULT_INTERVAL);
		int batteryPct = (level * 100) / scale;
		int remainder = (batteryFrom - batteryPct) % batteryInterval;
		if (batteryPct <= batteryFrom && remainder == 0)
		{
			return true;
		}
		return false;
	}

	/**
	 * Create a status bar notification which displays the battery charge.
	 * @param context
	 */
	private void createStatusBarNotification(Context context)
	{
		// The information for the status bar notification.
		String ns = Context.NOTIFICATION_SERVICE;
		int icon = R.drawable.ic_launcher; // icon from resources
		float batteryPct = level / (float) scale;
		String tickerText = String
				.format(context.getString(R.string.charge_text), batteryPct * 100); // Ticker-text
		long when = System.currentTimeMillis(); // notification time
		Context appcontext = context.getApplicationContext(); // application Context
		String contentTitle = context.getString(R.string.app_name); // message title
		String contentText = tickerText; // message text

		// An contentIntent is necessary 
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, null, 0);

		// Create Notification and display it with notification manager.
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
		Notification notification = new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(appcontext, contentTitle, contentText, contentIntent);
		notification.defaults |= Notification.DEFAULT_VIBRATE; // Use vibration
		// Cancel the notification when it is selected/clicked on.
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify(BATTERY_STATUSBAR_NOTIFICATION, notification);

	}

}
