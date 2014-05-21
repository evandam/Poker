package ecv.poker.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import ecv.poker.R;

/**
 * All SharedPreferences are saved from this activity.
 * Preferences are loading onCreate to set the views,
 * and saved onStop to be used by the game
 * 
 * @author evan
 *
 */
public class SettingsActivity extends Activity {

	private SharedPreferences settings;
	private CheckBox audioCheck;
	private EditText simsText;
	private EditText anteText;
	private EditText chipsText;
	private EditText bluffText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		settings = getSharedPreferences(getClass().getName(), MODE_PRIVATE);
		
		boolean audioEnabled = settings.getBoolean(
				getString(R.string.enable_sound), true);
		audioCheck = (CheckBox) findViewById(R.id.check_audio);
		audioCheck.setChecked(audioEnabled);
		
		int numSims = settings.getInt("simulations", 500);
		simsText = (EditText) findViewById(R.id.simulations);
		simsText.setText(numSims+"");
		
		int chipStack = settings.getInt("chips", 1000);
		chipsText = (EditText) findViewById(R.id.chipStack);
		chipsText.setText(chipStack+"");
		
		int ante = settings.getInt("ante", 10);
		anteText = (EditText) findViewById(R.id.ante);
		anteText.setText(ante+"");
		
		int bluffFrequency = settings.getInt("bluff", 20);
		bluffText = (EditText) findViewById(R.id.bluff_frequency);
		bluffText.setText(bluffFrequency+"");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Editor editor = settings.edit();
		editor.putBoolean(getString(R.string.enable_sound),
				audioCheck.isChecked());
		editor.putInt("simulations", Integer.parseInt(simsText.getText().toString()));
		editor.putInt("chips", Integer.parseInt(chipsText.getText().toString()));
		editor.putInt("ante", Integer.parseInt(anteText.getText().toString()));
		editor.putInt("bluff", Integer.parseInt(bluffText.getText().toString()));
		editor.commit();
	}
}
