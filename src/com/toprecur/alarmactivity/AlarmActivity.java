package com.toprecur.alarmactivity;

import java.io.ObjectOutputStream.PutField;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.AlarmClock;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AlarmActivity extends Activity {
	TimePicker myTimePicker;
	Button buttonstartSetDialog;
	Button buttonCancelAlarm;
	Button buttonSound;
	Button buttonSnooze;
	TextView textAlarmPrompt;
	private Context ctx;

	TimePicker timePicker;
	CheckBox optRepeat;
	Intent intent;
	AlarmManager alarmManager;
	// private Intent mAlarmReciever;
	private PendingIntent pendingIntent, pendingIntent1, pendingIntent2,
			pendingIntent3, pendingIntent4, pendingIntent5, pendingIntent6,
			pendingIntent7;
	private CheckBox chk_monday, chk_tuesday, chk_wednesday, chk_thursday,
			chk_friday, chk_saturday, chk_sunday;
	final static int RQS_1 = 1;
	private static final String TAG = AlarmActivity.class.getName();
	protected static final LayoutInflater layoutInflater = null;
	private boolean timerflag = false;

	String mypath = "/Settings/Sound/Phone ringtone/";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);

		ctx = this;

		timePicker = (TimePicker) findViewById(R.id.picker);
		// optRepeat = (CheckBox) findViewById(R.id.optrepeat);

		chk_monday = (CheckBox) findViewById(R.id.optrepeat1);

		chk_tuesday = (CheckBox) findViewById(R.id.optrepeat2);
		chk_wednesday = (CheckBox) findViewById(R.id.optrepeat3);
		chk_thursday = (CheckBox) findViewById(R.id.optrepeat4);

		chk_friday = (CheckBox) findViewById(R.id.optrepeat5);

		chk_saturday = (CheckBox) findViewById(R.id.optrepeat6);

		chk_sunday = (CheckBox) findViewById(R.id.optrepeat7);

		textAlarmPrompt = (TextView) findViewById(R.id.alarmprompt);

		buttonstartSetDialog = (Button) findViewById(R.id.startSetDialog);
		buttonstartSetDialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Calendar calNow = Calendar.getInstance();
				Calendar calSet = (Calendar) calNow.clone();

				calSet.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
				calSet.set(Calendar.MINUTE, timePicker.getCurrentMinute());
				calSet.set(Calendar.SECOND, 0);
				// calSet.set(Calendar.MILL ISECOND, 0);

				if (calSet.compareTo(calNow) <= 0) {
					// Today Set time passed, count to tomorrow
					calSet.add(Calendar.DATE, 1);
				}
				onChecked(calSet);
				setAlarm(calSet);
			}

		});

		buttonCancelAlarm = (Button) findViewById(R.id.cancel);
		buttonCancelAlarm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				cancelAlarm();
			}
		});

		// chk_monday.setOnCheckedChangeListener(this);

		buttonSound = (Button) findViewById(R.id.btn_sound);
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				AlarmActivity.this);

		buttonSound.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						AlarmActivity.this);

				Log.d("TAG", "button inside mobile");
				//
				// alertDialogBuilder
				// // .setMessage("Click yes to exit!")
				// .setCancelable(false).setPositiveButton("Sound",
				// new DialogInterface.OnClickListener() {
				// public void onClick(DialogInterface dialog,
				// int id) {
				// // if this button is clicked, close
				// // current activity
				// AlarmActivity.this.finish();
				// }
				// });
				// alertDialogBuilder.setPositiveButton("Sound", null );
				alertDialogBuilder
				// .setMessage("Click yes to exit!")
						.setCancelable(false).setPositiveButton("SdCard",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// Uri
										// uri=Uri.parse(Environment.getExternalStorageDirectory()+"mypath");
										// AlertDialog.Builder
										// alertDialogBuilder = new
										// AlertDialog.Builder(
										// AlarmActivity.this);
										// Log.d("TAG", "button inside sdCard");
										// AlarmActivity.this.finish();
										String uri = null;
										Intent intent = new Intent(
												RingtoneManager.ACTION_RINGTONE_PICKER);
										intent.putExtra(
												RingtoneManager.EXTRA_RINGTONE_TYPE,
												RingtoneManager.TYPE_NOTIFICATION);
										intent.putExtra(
												RingtoneManager.EXTRA_RINGTONE_TITLE,
												"Select Tone");
										// if (uri != null) {
										// intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
										// Uri.parse(uri));
										// } else {
										// intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
										// (Uri) null);
										// }
										startActivityForResult(intent, 0);
									}
								});
				alertDialogBuilder.show();
			}
		});
		buttonSnooze = (Button) findViewById(R.id.btn_snooze);
		// buttonSnooze.setOnClickListener(new OnClickListener() {
		// public void onClick(View v) {
		// Intent intent = new Intent(AlarmActivity.this, SnoozeActivity.class);
		// PendingIntent pendingIntent =
		// PendingIntent.getService(this, 0, intent,
		// PendingIntent.FLAG_UPDATE_CURRENT);
		//
		// long currentTimeMillis = System.currentTimeMillis();
		// long nextUpdateTimeMillis = currentTimeMillis + 5 *
		// DateUtils.MINUTE_IN_MILLIS;
		// Time nextUpdateTime = new Time();
		// nextUpdateTime.set(nextUpdateTimeMillis);
		//
		// AlarmManager alarmManager = (AlarmManager)
		// getSystemService(Context.ALARM_SERVICE);
		// alarmManager.set(AlarmManager.RTC, nextUpdateTimeMillis,
		// pendingIntent);
		// }
		// });

	}

	private void onChecked(final Calendar targetCal) {
		// Calendar calendar = Calendar.getInstance();
		// Calendar calSet = (Calendar) calendar.clone();
		// int day = calendar.get(Calendar.DAY_OF_WEEK);
		//
		// String d = Integer.toString(day);
		// String today = null;
		// Log.d(TAG,d);
		// if (day ==1 || day == 2 || day == 3 || day == 4 || day == 5
		// || day == 6 || day == 7) {
		//
		// alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
		// calSet.getTimeInMillis(), 24 * 60 * 60 * 1000,
		// pendingIntent);
		// Toast.makeText(ctx, "checked", + day + Toast.LENGTH_LONG).show();

		chk_monday
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (buttonView == chk_monday) {
							if (isChecked) {
								intent = new Intent(getBaseContext(),
										AlarmReciever.class);
								pendingIntent1 = PendingIntent.getBroadcast(
										getBaseContext(), 1, intent,
										Intent.FLAG_ACTIVITY_NEW_TASK);
								alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
								forday(1, pendingIntent1, targetCal);
								Toast.makeText(ctx, "checked" + buttonView,
										Toast.LENGTH_LONG).show();
							}
						}

					}
				});
		chk_tuesday
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (buttonView == chk_tuesday) {
							if (isChecked) {
								intent = new Intent(getBaseContext(),
										AlarmReciever.class);
								pendingIntent2 = PendingIntent.getBroadcast(
										getBaseContext(), 2, intent,
										Intent.FLAG_ACTIVITY_NEW_TASK);
								alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
								forday(2, pendingIntent2, targetCal);
								Toast.makeText(ctx, "checked" + buttonView,
										Toast.LENGTH_LONG).show();
							}
						}

					}
				});
		chk_wednesday
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (buttonView == chk_wednesday) {
							if (isChecked) {
								intent = new Intent(getBaseContext(),
										AlarmReciever.class);
								pendingIntent3 = PendingIntent.getBroadcast(
										getBaseContext(), 3, intent,
										Intent.FLAG_ACTIVITY_NEW_TASK);
								alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
								forday(3, pendingIntent3, targetCal);
								Toast.makeText(ctx, "checked",
										Toast.LENGTH_LONG).show();
							}
						}

					}
				});
		chk_thursday
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (buttonView == chk_thursday) {
							if (isChecked) {
								intent = new Intent(getBaseContext(),
										AlarmReciever.class);
								pendingIntent4 = PendingIntent.getBroadcast(
										getBaseContext(), 4, intent,
										Intent.FLAG_ACTIVITY_NEW_TASK);
								alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
								forday(4, pendingIntent4, targetCal);
								Toast.makeText(ctx, "checked",
										Toast.LENGTH_LONG).show();
							}
						}

					}
				});
		chk_friday
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (buttonView == chk_friday) {
							if (isChecked) {
								intent = new Intent(getBaseContext(),
										AlarmReciever.class);
								pendingIntent5 = PendingIntent.getBroadcast(
										getBaseContext(), 5, intent,
										Intent.FLAG_ACTIVITY_NEW_TASK);
								alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
								forday(5, pendingIntent5, targetCal);
								Toast.makeText(ctx, "checked",
										Toast.LENGTH_LONG).show();
							}
						}

					}
				});
		chk_saturday
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (buttonView == chk_saturday) {
							if (isChecked) {
								intent = new Intent(getBaseContext(),
										AlarmReciever.class);
								pendingIntent6 = PendingIntent.getBroadcast(
										getBaseContext(), 6, intent,
										Intent.FLAG_ACTIVITY_NEW_TASK);
								alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
								forday(6, pendingIntent6, targetCal);
								Toast.makeText(ctx, "checked",
										Toast.LENGTH_LONG).show();
							}
						}

					}
				});
		chk_sunday
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (buttonView == chk_sunday) {
							if (isChecked) {
								intent = new Intent(getBaseContext(),
										AlarmReciever.class);
								pendingIntent7 = PendingIntent.getBroadcast(
										getBaseContext(), 7, intent,
										Intent.FLAG_ACTIVITY_NEW_TASK);
								alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
								forday(7, pendingIntent7, targetCal);
								Toast.makeText(ctx, "checked",
										Toast.LENGTH_LONG).show();
							}
						}

					}
				});

		// AlertDialog.BUTTON_POSITIVE();
	}

	private void setAlarm(Calendar targetCal) {

		intent = new Intent(getBaseContext(), AlarmReciever.class);
		pendingIntent = PendingIntent.getBroadcast(getBaseContext(), RQS_1,
				intent, 0);
		// mAlarmReciever = new Intent(AlarmActivity.this,
		// AlarmReciever.class);
		// pendingIntent = PendingIntent.getBroadcast(
		// AlarmActivity.this, 0, mAlarmReciever, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
				pendingIntent);

		/*
		 * if (repeat) { alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
		 * targetCal.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES,
		 * pendingIntent);
		 */

		/*
		 * alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
		 * targetCal.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES,
		 * pendingIntent);
		 */

		textAlarmPrompt.setText("\n\n***\n" + "Alarm is set@ "
				+ targetCal.getTime() + "\n" + "Repeat every 5 minutes\n"
				+ "***\n");
		long hour = targetCal.getTimeInMillis();
		long minute = targetCal.getTimeInMillis();
		Intent i = new Intent(this, DataBaseActivity.class);
		i.putExtra("hour", hour);
		i.putExtra("minute", minute);
		startActivity(i);
		/*
		 * } else { alarmManager.set(AlarmManager.RTC_WAKEUP,
		 * targetCal.getTimeInMillis(), pendingIntent);
		 * 
		 * textAlarmPrompt.setText("\n\n***\n" + "Alarm is set@ " +
		 * targetCal.getTime() + "\n" + "One shot\n" + "***\n"); }
		 */

	}

	private void cancelAlarm() {

		textAlarmPrompt.setText("\n\n***\n" + "Alarm Cancelled! \n" + "***\n");

		Intent intent = new Intent(getBaseContext(), AlarmReciever.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getBaseContext(), RQS_1, intent, 0);
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);

	}

	/*
	 * private void setListener() {
	 * 
	 * chk_monday.setOnCheckedChangeListener(this);
	 * 
	 * chk_tuesday.setOnCheckedChangeListener(this);
	 * 
	 * chk_wednesday.setOnCheckedChangeListener(this);
	 * 
	 * chk_thursday.setOnCheckedChangeListener(this);
	 * 
	 * chk_friday.setOnCheckedChangeListener(this);
	 * 
	 * chk_sat.setOnCheckedChangeListener(this);
	 * 
	 * chk_sunday.setOnCheckedChangeListener(this);
	 * 
	 * timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
	 * 
	 * public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
	 * 
	 * // TODO Auto-generated method stub
	 * 
	 * timerflag = true;
	 * 
	 * }
	 * 
	 * });
	 * 
	 * }
	 */

	public void forday(int week, PendingIntent pendingIntent, Calendar targetCal) {

		Calendar calSet = Calendar.getInstance();

		int hour = timePicker.getCurrentHour();

		int minute = timePicker.getCurrentMinute();

		calSet.set(Calendar.DAY_OF_WEEK, week);

		calSet.set(Calendar.HOUR_OF_DAY, hour);

		calSet.set(Calendar.MINUTE, minute);

		calSet.set(Calendar.SECOND, 0);

		calSet.set(Calendar.MILLISECOND, 0);

		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				calSet.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7,
				pendingIntent);
		textAlarmPrompt.setText("\n\n***\n" + "Alarm is set@ "
				+ targetCal.getTime() + "\n" + "One shot\n" + "***\n");
	}

	public void inday(int day, PendingIntent pendingIntent, Calendar targetCal,
			long remainingMilisecondsToTopHour) {

		Calendar calSet = Calendar.getInstance();

		int hour = timePicker.getCurrentHour();

		int minute = timePicker.getCurrentMinute();

		// calSet.set(Calendar.DAY_OF_WEEK, week);

		calSet.set(Calendar.HOUR_OF_DAY, hour);

		calSet.set(Calendar.MINUTE, minute);

		calSet.set(Calendar.SECOND, 0);

		calSet.set(Calendar.MILLISECOND, 0);
		long firstTime = SystemClock.elapsedRealtime();
		firstTime += remainingMilisecondsToTopHour;
		long a = calSet.getTimeInMillis();

		// Schedule the alarm!
		AlarmManager am = (AlarmManager) ctx
				.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME,
				calSet.getTimeInMillis(), 1 * 60 * 60 * 1000, pendingIntent);

		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				calSet.getTimeInMillis(), AlarmManager.INTERVAL_HOUR * 12,
				pendingIntent);
		textAlarmPrompt.setText("\n\n***\n" + "Alarm is set@ "
				+ targetCal.getTime() + "\n" + "One shot\n" + "***\n");
	}
	// AlertDialog aDialog = new AlertDialog.Builder(this).create();
	// protected void onActivityResult(int requestCode, int resultCode,
	// Intent intent) {
	// // TODO Auto-generated method stub
	// super.onActivityResult(requestCode, resultCode, intent);
	// if (resultCode == RESULT_OK) {
	// Uri uri = intent
	// .getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
	// if (uri != null) {
	//
	// String ringTonePath = uri.toString();
	// Log.d("Ringtone Path",""+ ringTonePath);
	// // Set the Ringtone to Alarm
	// RingtoneManager.setActualDefaultRingtoneUri(AlarmActivity.this,
	// RingtoneManager.TYPE_ALARM, uri);
	//
	// // I am checking wether the selected ringtone is set for alarm or not
	//
	// Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
	// i.putExtra(AlarmClock.EXTRA_HOUR, 12); // 12 pm
	// i.putExtra(AlarmClock.EXTRA_MINUTES, 26); // 26 minutes
	// startActivity(i);
	// }
	// }

	// }

}
