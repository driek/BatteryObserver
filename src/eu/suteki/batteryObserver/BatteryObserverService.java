package eu.suteki.batteryObserver;

import eu.suteki.batteryObserver.broadcastreceiver.BatteryBroadcastReceiver;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class BatteryObserverService extends Service
{

	@Override
	public void onCreate()
	{
		// Register the BatteryBroadcastReceiver for listening to changes of the battery level.
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		BatteryBroadcastReceiver bbr = new BatteryBroadcastReceiver();
		Intent batterystatus = registerReceiver(bbr, ifilter);
	}

	@Override
	public void onDestroy()
	{

	}

	@Override
	public IBinder onBind(Intent arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
