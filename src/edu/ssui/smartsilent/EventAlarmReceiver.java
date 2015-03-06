package edu.ssui.smartsilent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.content.SharedPreferences;

/**
 * This class is set to set up the alarm so the phone will be silent or un-silent at certain time.
 * @author Leo Ying
 *
 */

public class EventAlarmReceiver extends WakefulBroadcastReceiver {
    private SharedPreferences reqCodes;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub.
        Intent service = new Intent(context, EventSchedulingService.class);
        service.putExtra("silent", intent.getExtras().getBoolean("silent"));
        startWakefulService(context, service);
	}
	
    public void setAlarm(Context context) {
    	/**
    	 * This method set up alarm to the android system. 
    	 * Before it set up the alarm it will cancel all the previous alarms so that in case the user delete an event or change the silent attribute the alarm will be properly adjusted
    	 * For each calendar event, two alarms will be set up. The first is to silent the phone when event starts. The other is to bring back the volume when event is finished. 
    	 * 
    	 */
        ArrayList<CalendarEvent> mEvents= new ArrayList<CalendarEvent>();
        EventManager em = new  EventManager(context);
        mEvents= em.getAllSilentEvent();
        int i=0;
        cancelAlarm(context);
        SharedPreferences.Editor editor = reqCodes.edit();
        for(CalendarEvent c: mEvents){
        	PendingIntent alarmIntent;
        	AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        	Intent intent = new Intent(context, EventAlarmReceiver.class);
        	intent.putExtra("silent", true);
        	alarmIntent = PendingIntent.getBroadcast(context, i, intent, 0);
            editor.putInt(String.valueOf(i), i);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(c.getBegin());
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            i++;            
            AlarmManager alarmMgr1 = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent1 = new Intent(context, EventAlarmReceiver.class);
        	intent1.putExtra("silent", false);
        	PendingIntent alarmIntent1;
            alarmIntent1 = PendingIntent.getBroadcast(context, i, intent1, 0);
            editor.putInt(String.valueOf(i), i);
            editor.commit();
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(c.getEnd());
            alarmMgr1.set(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), alarmIntent1);
            i++;
        }
    }
    
    private void cancelAlarm(Context context){
    	/** The cancel alarm uses sharedpreferences to retrieve the request code from the events are previously put into alarm.
    	 * In order to cancel an alarm, the app has to recreate pendingintents that are same as before then cancel it.
    	 * 
    	 */
    	reqCodes = context.getSharedPreferences("reqCodes", 0);
		Map<String, Integer> map=(Map<String, Integer>) reqCodes.getAll();
		ArrayList<Integer> reqestCodes= new ArrayList<Integer>(map.values());
    	boolean temp =true;
    	System.out.println(map);
		for (Integer r:reqestCodes){
			int i =r;
			Intent intent = new Intent(context, EventAlarmReceiver.class);
        	intent.putExtra("silent", temp);
        	PendingIntent.getBroadcast(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT).cancel();
        	temp=!temp;
		}
    }
}
