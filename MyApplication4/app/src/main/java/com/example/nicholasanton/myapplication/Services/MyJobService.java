package com.example.nicholasanton.myapplication.Services;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import com.example.nicholasanton.myapplication.DataHandler;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.Objects;

import static com.example.nicholasanton.myapplication.Views.ActivitesListeners.inMeeting;

public class MyJobService extends JobService{
	private static final String TAG = "MyJobService";
	private DataHandler db;

	@Override
	public boolean onStartJob(JobParameters jobParameters) {
//		String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
//		Log.d(TAG, currentDateTimeString);
//		Toast.makeText(this, "This is 6:03", Toast.LENGTH_SHORT).show();
		db = new DataHandler(this);
		if (Objects.requireNonNull(jobParameters.getExtras()).getInt("StartEnd") == 0){
			Log.d("TEST : ", "starting service");
			db.insertLog("Turning Do Not Disturb Mode On");
			requestDoNotDisturbPermissionOrSetDoNotDisturbApi23AndUp();
			inMeeting = true;
		} else if (jobParameters.getExtras().getInt("StartEnd") == 1){
			Log.d("TEST : ", "stopping service");
			inMeeting = false;
			db.insertLog("Turning Do Not Disturb Mode Off");
			turnOffDoNotDisturb();
		}

		return false;
	}

	@Override
	public boolean onStopJob(JobParameters jobParameters) {
		Log.d(TAG, "Job cancelled!");
		return false;
	}

	private void requestDoNotDisturbPermissionOrSetDoNotDisturbApi23AndUp() {
		//TO SUPPRESS API ERROR MESSAGES IN THIS FUNCTION, since Ive no time to figrure our Android SDK suppress stuff
		if( Build.VERSION.SDK_INT < 21 ) {
			return;
		}

		AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

	}

	private void turnOffDoNotDisturb() {
		//TO SUPPRESS API ERROR MESSAGES IN THIS FUNCTION, since Ive no time to figrure our Android SDK suppress stuff
		if( Build.VERSION.SDK_INT < 21 ) {
			return;
		}

		AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	}
}