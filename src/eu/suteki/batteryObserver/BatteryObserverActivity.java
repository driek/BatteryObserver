package eu.suteki.batteryObserver;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import static eu.suteki.batteryObserver.broadcastreceiver.BatteryBroadcastReceiver.PREFS_NAME;
import static eu.suteki.batteryObserver.broadcastreceiver.BatteryBroadcastReceiver.BATTERY_FROM;
import static eu.suteki.batteryObserver.broadcastreceiver.BatteryBroadcastReceiver.BATTERY_INTERVAL;
import static eu.suteki.batteryObserver.broadcastreceiver.BatteryBroadcastReceiver.SHOW_SB_NOTI;
import static eu.suteki.batteryObserver.broadcastreceiver.BatteryBroadcastReceiver.SHOW_TOAST;

public class BatteryObserverActivity extends Activity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initSettings();
		startService(new Intent(this, BatteryObserverService.class));
	}
	
	/**
	 * This method is invoked upon pressing the save settings button in the view.
	 * @param view
	 */
	public void saveSettings(View view)
	{
		EditText fromBattery = (EditText) findViewById(R.id.from_battery_level_input_field);
		EditText intervalBattery = (EditText) findViewById(R.id.interval_battery_level_input_field);
		if (fromBattery.getText().toString().length() == 0 || intervalBattery.toString().length() == 0)
		{
			Toast.makeText(this, getString(R.string.fill_in_valid_numbers), Toast.LENGTH_LONG).show();
			return;
		}
		CheckBox showToastCB = (CheckBox) findViewById(R.id.showToast);
		CheckBox showSBNotiCB = (CheckBox) findViewById(R.id.showSBNoti);
		int fromBatteryLevel = Integer.parseInt(fromBattery.getText().toString());
		if (fromBatteryLevel > 100)
		{
			fromBatteryLevel = 100;
			fromBattery.setText("100");
		}
		int intervalBatteryLevel = Integer.parseInt(intervalBattery.getText().toString());
		storePreferences(fromBatteryLevel, intervalBatteryLevel, 
				showToastCB.isChecked(), showSBNotiCB.isChecked());
		Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Initialize the settings page. Fill in the previously stored user preference values.
	 */
	private void initSettings()
	{
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		EditText fromBattery = (EditText) findViewById(R.id.from_battery_level_input_field);
		EditText intervalBattery = (EditText) findViewById(R.id.interval_battery_level_input_field);
		CheckBox showToastCB = (CheckBox) findViewById(R.id.showToast);
		CheckBox showSBNotiCB = (CheckBox) findViewById(R.id.showSBNoti);
		if (settings.getInt(BATTERY_FROM, -1) >= 0)
		{
			fromBattery.setText(""+settings.getInt(BATTERY_FROM, 0));
			intervalBattery.setText(""+settings.getInt(BATTERY_INTERVAL, 1));
			showToastCB.setChecked(settings.getBoolean(SHOW_TOAST, false));
			showSBNotiCB.setChecked(settings.getBoolean(SHOW_SB_NOTI, false));
		}
	}
	
	/**
	 * @param from
	 * @param interval
	 */
	private void storePreferences(int from, int interval, boolean showToast, boolean showSBNoti)
	{
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		Editor editor = settings.edit();
		editor.putBoolean(SHOW_TOAST, showToast);
		editor.putBoolean(SHOW_SB_NOTI, showSBNoti);
		editor.putInt(BATTERY_FROM, from);
		editor.putInt(BATTERY_INTERVAL, interval);
		editor.commit();
	}
	
}