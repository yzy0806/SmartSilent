package edu.ssui.smartsilent;
import java.util.ArrayList;
import java.util.Map;

import edu.ssui.smartsilent.R;
import android.app.Activity;
import android.app.ListFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class EventListFragment extends ListFragment  {
	 /**
     * This class holds the main fragment for the app.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ArrayList<CalendarEvent> mEvents; 
    
    private ArrayList<String> mKeywords;
    private static final String PREFS_NAME = "MyPrefsFile";
    private EditText mTaskInput;
    private SharedPreferences keywords;
    private Button submit;
    private Button delete;

    EventAlarmReceiver alarm = new EventAlarmReceiver();
    
    /**
  * Returns a new instance of this fragment for the given section
  * number
  */
    public static EventListFragment newInstance(int sectionNumber) {
    	EventListFragment fragment = new EventListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * create the view for the main fragment
     * Based on the choice on the navigation drawer fragment, different view will be inflated.
     * There are two different views. The first is the calendar events view which display all the events today and if they are silent.
     * The second one is a list view to display all the keywords
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	int i = getArguments().getInt(ARG_SECTION_NUMBER);
    	if(i==1){
    		View rootView = inflater.inflate(R.layout.list_item_event, container, false);
    		EventManager em = new  EventManager(getActivity());
    		mEvents= em.getEvents();
    		CalendarEventAdapter adapter = new CalendarEventAdapter(mEvents);
    		setListAdapter(adapter);
            alarm.setAlarm(this.getActivity());
    		return rootView;
    	}
    	else{
    		View rootView = inflater.inflate(R.layout.setting, container, false);
    		keywords = getActivity().getSharedPreferences(PREFS_NAME, 0);
    		mTaskInput = (EditText) rootView.findViewById(R.id.task_input);
    		submit=(Button) rootView.findViewById(R.id.submit_button);
    		submit.setOnClickListener(new View.OnClickListener() {
    	           public void onClick(View view) {
    	        	   createKeyword(view);    
    	            }
    	         });
    		updateUI();
    		return rootView;
    	}
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
    
    /**
     * This method allows user to put into new keywords on the setting view and store the keywords into sharedpreferences.
     * @param v
     */
    public void createKeyword(View v) {
        if (mTaskInput.getText().length() > 0){
            String value = new String(mTaskInput.getText().toString());
            SharedPreferences.Editor editor = keywords.edit();
            String key = new String("");
            key=value;
            editor.putString(key, value);
            editor.commit();
            updateUI();
            mTaskInput.setText("");
        }
    }
    
    /**
     * This method allows user to delete keywords on the setting view and delete the keywords from sharedpreferences.
     * @param v
     */
    public void deleteKeyword(View view){
    	 View v = (View) view.getParent();
    	 TextView keywordTextView = (TextView) v.findViewById(R.id.keywordTextView);
    	 String key = keywordTextView.getText().toString();
    	 SharedPreferences.Editor editor = keywords.edit();
    	 editor.remove(key);
         editor.commit();
         updateUI();
    }
    
    /**
     * This method update the setting view after user add or delete a keyword.
     */
        
    private void updateUI(){
    	Map<String, String> map=(Map<String, String>) keywords.getAll();
    	mKeywords= new ArrayList<String>(map.values());
    	KeywordAdapter adapter = new KeywordAdapter(mKeywords);
		setListAdapter(adapter);
    }
    
    
    /**
     * This is the CalendarEvent listview adapter
     * It sets up how each CalendarEvent item looks.
     * @author Leo Ying
     *
     */
    private class CalendarEventAdapter extends ArrayAdapter<CalendarEvent> {
        public CalendarEventAdapter(ArrayList<CalendarEvent> CalendarEvents) {
            super(getActivity(), 0, CalendarEvents);
        }
        
        @Override
        
        public View getView(int position, View convertView, ViewGroup parent) {
            // if no convertView available, inflate one.
            if(convertView == null)
                convertView = getActivity().getLayoutInflater().inflate(R.layout.event_item, null);
            // Configure the view for this CalendarEvent
            CalendarEvent c = getItem(position);
            TextView titleTextView = (TextView)convertView.findViewById(R.id.title);
            titleTextView.setText(c.getName());
            TextView location = (TextView)convertView.findViewById(R.id.location);
            location.setText(c.getLocation());
            location.setTextColor(getResources().getColor(R.color.light_yellow));
            TextView silent = (TextView)convertView.findViewById(R.id.silent);
            //different words and color to indicate if a event is silent
            if(c.isSilent()){
            	silent.setText("Silent");
            	silent.setTextColor(getResources().getColor(R.color.light_red));
            }
            else{
            	silent.setText("Not Silent");
            	silent.setTextColor(getResources().getColor(R.color.light_grey));
            }
            
            //different words and colors to indicate if a event is all day
            if(c.isAllDay()){
                TextView allday = (TextView)convertView.findViewById(R.id.start);
                allday.setText("All Day");
                allday.setTextColor(getResources().getColor(R.color.light_blue));
                TextView end = (TextView)convertView.findViewById(R.id.end);
                end.setText("");
                return  convertView;
            }
            else{
                TextView start = (TextView)convertView.findViewById(R.id.start);
                start.setText(DateFormat.format("hh:mm aa",c.getBegin()));
                start.setTextColor(getResources().getColor(R.color.light_green));
                TextView end = (TextView)convertView.findViewById(R.id.end);
                end.setText(DateFormat.format("hh:mm aa",c.getEnd()));
                end.setTextColor(getResources().getColor(R.color.light_green));
                return  convertView;
            }
        }
        
    }
    /**
     * This is the keyword listview adapter
     * It sets up how each keyword item looks.
     * @author Leo Ying
     *
     */
    
    public class KeywordAdapter extends ArrayAdapter<String> { 
    	public KeywordAdapter(ArrayList<String> Keywords) {
            super(getActivity(), 0, Keywords);
        }
    	public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
                convertView = getActivity().getLayoutInflater().inflate(R.layout.keyword_item, null);
            String k = getItem(position);
            TextView titleTextView = (TextView)convertView.findViewById(R.id.keywordTextView);
            titleTextView.setText(k);
    		delete = (Button) convertView.findViewById(R.id.delete_button);
    		delete.setOnClickListener(new View.OnClickListener() {
 	           public void onClick(View view) {
 	        	   deleteKeyword(view);    
 	            }
 	         });
            return  convertView;
        }
    	}
	
}

