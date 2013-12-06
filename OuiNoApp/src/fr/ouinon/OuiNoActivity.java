package fr.ouinon;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Put a neutral part on the left without GetureDetector, then one relative with
 * the GestureDetector
 * 
 * @author Jorge
 * 
 */
public class OuiNoActivity extends Activity {

	public static final String TAG = "OuiNonActivity";
	public static final String SENSIBILITE = "sensibilite";

	private SharedPreferences preferences = null;
	private SharedPreferences.Editor prefEditor = null;

	private Button ouiButton = null;
	private Button noButton = null;

	private MediaPlayer mp = null;
	private boolean reproduciendo = false;

	private float sensibleDistance = 100.0f;

	private float ouiX = 0.0f;
	private float ouiY = 0.0f;
	private float nonX = 0.0f;
	private float nonY = 0.0f;

	private static Context context = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;

		setContentView(R.layout.main_new);

		preferences = getSharedPreferences("preference", MODE_PRIVATE);
		sensibleDistance = preferences.getFloat(SENSIBILITE, 100.0f);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		ouiButton = (Button) findViewById(R.id.botonoui);
		ouiButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					if (ouiX != 0.0f | ouiY != 0.0f) {
						float distanceX = Math.abs(ouiX - event.getX());
						float distanceY = Math.abs(ouiY - event.getY());
						if (distanceX > sensibleDistance
								| distanceY > sensibleDistance) {
							playSoundAndIluminateScreen(true);
						}

					} else {
						ouiX = event.getX();
						ouiY = event.getY();
					}
				}
				return true;
			}
		});

		noButton = (Button) findViewById(R.id.botonno);
		noButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					if (nonX != 0.0f | nonY != 0.0f) {
						float distanceX = Math.abs(nonX - event.getX());
						float distanceY = Math.abs(nonY - event.getY());
						if (distanceX > sensibleDistance
								| distanceY > sensibleDistance) {
							playSoundAndIluminateScreen(false);
						}

					} else {
						nonX = event.getX();
						nonY = event.getY();
					}
				}
				return true;
			}
		});

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		return super.onTouchEvent(me);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.settings:
			final Dialog dialogo = new Dialog(context);
			dialogo.setContentView(R.layout.modifytime);
			dialogo.setTitle("Sensibility du bouton");

			final EditText temps = (EditText) dialogo
					.findViewById(R.id.sensibilite);
			temps.setText(String.valueOf(sensibleDistance));

			Button cancelarButton = (Button) dialogo
					.findViewById(R.id.butoncancell);
			cancelarButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialogo.dismiss();
				}
			});

			Button aceptarButton = (Button) dialogo
					.findViewById(R.id.butonaccept);
			aceptarButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!temps.getText().toString().contentEquals("")) {

						float sensibiliteNouveau = Float.parseFloat(temps
								.getText().toString());
						if (sensibiliteNouveau > 0.0f) {

							sensibleDistance = sensibiliteNouveau;
							prefEditor = preferences.edit();
							prefEditor
									.putFloat(SENSIBILITE, sensibiliteNouveau);
							prefEditor.commit();
							Toast.makeText(
									context,
									"Sensibility modifier a "
											+ String.valueOf(sensibiliteNouveau),
									Toast.LENGTH_SHORT).show();

						} else {
							Toast.makeText(context,
									"Le sensibility doit etre superieur a 1",
									Toast.LENGTH_SHORT).show();
						}

					} else {
						Toast.makeText(context,
								"Le sensibility doit etre superieur a 1",
								Toast.LENGTH_SHORT).show();
					}

					dialogo.dismiss();
				}
			});
			dialogo.show();
			break;

		default:
			break;
		}
		return true;
	}

	/**
	 * Method which play the sound according to the boolean received
	 * 
	 * @param ouiOrNon
	 *            boolean. true means "oui" was moved, false means "non" was
	 *            moved
	 */
	public void playSoundAndIluminateScreen(boolean ouiOrNon) {
		if (!reproduciendo) {
			if (ouiOrNon) {
				ouiButton.setBackgroundResource(R.drawable.green);
				mp = MediaPlayer.create(context, R.raw.oui2);
			} else {
				noButton.setBackgroundResource(R.drawable.red);
				mp = MediaPlayer.create(context, R.raw.non2);
			}
			reproduciendo = true;
			mp.start();
			mp.setOnCompletionListener(new OnCompletionListener() {
				// @Override
				public void onCompletion(MediaPlayer arg0) {
					reproduciendo = false;
					noButton.setBackgroundResource(R.drawable.red_button);
					ouiButton.setBackgroundResource(R.drawable.green_buton);
					ouiX = 0.0f;
					ouiY = 0.0f;
					nonX = 0.0f;
					nonY = 0.0f;
				}
			});
		}
	}

}