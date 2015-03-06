package edu.ssui.smartsilent;


import android.app.IntentService;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;


/**
 * This class silent the phone or bring back the volume when the systems alarm calls it.
 * @author Leo Ying
 *
 */
public class EventSchedulingService extends IntentService {
	
	public EventSchedulingService() {
		super("EventSchedulingService");
    }
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle b = intent.getExtras();
		boolean silence = b.getBoolean("silent");
		if(silence){
			AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		}
		else
		{
			AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);

			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
		}
        EventAlarmReceiver.completeWakefulIntent(intent);

	}

}
