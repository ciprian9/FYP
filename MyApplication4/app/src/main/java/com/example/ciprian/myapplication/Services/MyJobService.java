package com.example.ciprian.myapplication.Services;

/**
 * Will turn do not disturb on or off depending on the time that the user has selected
 * */

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import com.example.ciprian.myapplication.DataHandler;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.Objects;

import static com.example.ciprian.myapplication.Views.ActivitesListeners.inMeeting;

public class MyJobService extends JobService{
	private static final String TAG = "MyJobService";

	//Checks weather to turn the Do not Disturb on or off
	@Override
	public boolean onStartJob(JobParameters jobParameters) {
		//Data handler used for logging and debugging
		DataHandler db = new DataHandler(this);
		if (Objects.requireNonNull(jobParameters.getExtras()).getInt("StartEnd") == 0){
			Log.d(TAG, "starting service");
			db.insertLog("Turning Do Not Disturb Mode On");
			//call on do not disturb to switch it to on
			requestDoNotDisturbPermissionOrSetDoNotDisturbApi23AndUp();
			//set boolean in meeting to true meaning that the meeting has started
			inMeeting = true;
		} else if (jobParameters.getExtras().getInt("StartEnd") == 1){
			Log.d(TAG , "stopping service");
			//set inMeeting boolean to false meeting has ended
			inMeeting = false;
			db.insertLog("Turning Do Not Disturb Mode Off");
			//switch do not disturb mode to off
			turnOffDoNotDisturb();
		}

		return false;
	}

	@Override
	public boolean onStopJob(JobParameters jobParameters) {
		//In case the job needs to be cancelled on stop job gets called
		Log.d(TAG, "Job cancelled!");
		return false;
	}

	private void requestDoNotDisturbPermissionOrSetDoNotDisturbApi23AndUp() {
		//if the version is lower then API 21 do not disturb mode can not be switched to on
		if( Build.VERSION.SDK_INT < 21 ) {
			return;
		}
		//get an audio manager instance
		AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		//swtich the ringer to silent mode
		audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

	}

	private void turnOffDoNotDisturb() {
		if( Build.VERSION.SDK_INT < 21 ) {
			return;
		}
		AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		//switch the ringer to normal mode
		audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	}
}