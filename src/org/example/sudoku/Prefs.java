package org.example.sudoku;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Prefs extends PreferenceActivity {
	@Override
	public void onCreate(Bundle sIS){
		super.onCreate(sIS);
		addPreferencesFromResource(R.xml.settings);
	}
}
