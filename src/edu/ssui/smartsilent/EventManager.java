package edu.ssui.smartsilent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

public class EventManager {
	/**
	 * This class holds all the CalendarEvents.
	 */
	public static final String[] EVENT_PROJECTION = new String[] {
	   	 CalendarContract.Events.TITLE, 
	   	 CalendarContract.Events.EVENT_LOCATION, 
	   	 CalendarContract.Instances.BEGIN, 
	   	 CalendarContract.Instances.END, 
	   	 CalendarContract.Events.ALL_DAY
	    };
    private ArrayList<CalendarEvent> events;
    private Context mAppContext;
    private static final String PREFS_NAME = "MyPrefsFile";
    private SharedPreferences keywords;
    private ArrayList<String> mKeywords;


    
    public EventManager(Context ct){
    	mAppContext=ct;
    	events = new ArrayList<CalendarEvent>();
    	queryCalendar();
    }
    

    
    public ArrayList<CalendarEvent> getEvents(){
    	return events;
    }
    
    
    public ArrayList<CalendarEvent> getAllSilentEvent(){
    	ArrayList<CalendarEvent> result = new ArrayList<CalendarEvent>();
    	for(CalendarEvent c:events){
    		if(c.isSilent())
    			result.add(c);
    	}
    	return result;
    }
	
    /**
     * When a event manager is created. It will query the calendarcontract service to get all the events in the phone.
     * Because android has a bug with all day events. I have to wrote some extra methods to fix it. 
     * Details see https://code.google.com/p/android/issues/detail?id=71355 
     * http://stackoverflow.com/questions/12751865/list-events-for-specific-day-in-android-4-all-day-issue
     * 
     */
	public void queryCalendar() {
    	ContentResolver cr = this.mAppContext.getContentResolver();
    	Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon(); 
    	Calendar c_start= Calendar.getInstance();
    	 c_start.set(Calendar.HOUR_OF_DAY, 00);
         c_start.set(Calendar.MINUTE, 00);
    	Calendar c_end= Calendar.getInstance();
    	c_end.set(Calendar.HOUR_OF_DAY, 23);
        c_end.set(Calendar.MINUTE, 59);
    	//query today's events instances.
    	ContentUris.appendId(eventsUriBuilder, c_start.getTimeInMillis());
    	ContentUris.appendId(eventsUriBuilder, c_end.getTimeInMillis());
   	
    	Uri eventUri = eventsUriBuilder.build();
    	// resolve the query, this time also including a sort option.
    	Cursor eventCursor = cr.query(eventUri, EVENT_PROJECTION, null,null, CalendarContract.Instances.BEGIN + " ASC");
    	getKeywordslist();
    	while (eventCursor.moveToNext()) {
    	    String title = null;
    	    String location = null;
    	    String begin = null;
    	    String end =null;
    	    String allDay = null;
    	      
    	    // Get the field values.
    	    title = eventCursor.getString(0);
    	    location = eventCursor.getString(1);
    	    begin = eventCursor.getString(2);
    	    end = eventCursor.getString(3);
    	    allDay = eventCursor.getString(4);
    	    CalendarEvent temp = new CalendarEvent(title,location,begin,end,allDay);
    	    determineSilence(temp);
    	    if(fixAllDayIssue(temp)){
    	    	 events.add(temp);
    	    }
    	  }
    }
	
	/**
	 * Methods fix the all day issue.
	 * @param evt
	 * @return
	 */
	private boolean fixAllDayIssue(CalendarEvent evt){
		if(evt.isAllDay()){
			Calendar c_start= Calendar.getInstance();
	    	 c_start.set(Calendar.HOUR_OF_DAY, 00);
	         c_start.set(Calendar.MINUTE, 00);
	        long start=c_start.getTimeInMillis();
			Calendar temp = Calendar.getInstance();
			temp.setTime(evt.getBegin());
			long adjust= getTimeInUTC(temp);
			if(adjust<start){
				return true;
			}
			else{
				return false;
			}
			
		}
		else
			return true;
	}
	
	private long getTimezoneOffset(TimeZone zone, long date) {
	    long delta = zone.getOffset(date);
	    return delta;
	}

	private long getTimeInUTC(Calendar date) {
	    long time = date.getTimeInMillis();
	    time += getTimezoneOffset(date.getTimeZone(), time);
	    return time;
	}
	
	/**
	 * retrieve the keywords from keywords list
	 */
	
	private void getKeywordslist(){
		keywords = mAppContext.getSharedPreferences(PREFS_NAME, 0);
		Map<String, String> map=(Map<String, String>) keywords.getAll();
	    mKeywords= new ArrayList<String>(map.values());
	}
	
	/**
	 * Based on the keywords determine which events should be silent based on the event name and event location.
	 * @param evt
	 */
	private void determineSilence(CalendarEvent evt){
		for (String keyword: mKeywords){
			if(evt.getName()!=null && Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE).matcher(evt.getName()).find()){
				evt.setSilent(true);
				break;
			}	
			else if(evt.getLocation()!=null && Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE).matcher(evt.getLocation()).find()){
				evt.setSilent(true);
				break;
			}	
		}
	}
}
