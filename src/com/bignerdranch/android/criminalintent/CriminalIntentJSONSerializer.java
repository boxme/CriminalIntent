package com.bignerdranch.android.criminalintent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;

/*
 * Creating and parsing the model objects as JSON
 */
public class CriminalIntentJSONSerializer {
	private Context mContext;
	private String mFilename;
	
	public CriminalIntentJSONSerializer(Context context, String filename) {
		mContext = context;
		mFilename = filename;
	}
	
	public void saveCrimes(ArrayList<Crime> crimes) throws JSONException, IOException{
		//Build an array in JSON
		JSONArray array = new JSONArray();
		for (Crime crime : crimes) {
			array.put(crime.toJSON());
		}
		
		//Write the file to disk
		Writer writer = null;
		try {
			OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	/*
	 * Saved to external storage. Always use context.getExternalFilesDir();
	 */
	public void savedToExternal(ArrayList<Crime> crimes) throws JSONException, IOException {
		JSONArray array = new JSONArray();
		for (Crime crime : crimes) {
			array.put(crime.toJSON());
		}
		
		Writer writer = null;
		try {
			File file = mContext.getExternalFilesDir(mFilename);
			OutputStream out = new FileOutputStream(file);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
		} catch (Exception e) {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	public ArrayList<Crime> loadCrimes() throws JSONException, IOException {
		ArrayList<Crime> crimes = new ArrayList<Crime>();
		BufferedReader reader = null;
		try {
			//Open and read the file into a StringBuilder
			InputStream input = mContext.openFileInput(mFilename);
			reader = new BufferedReader(new InputStreamReader(input));
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				//Line breaks are omitted and irrelevant
				jsonString.append(line);
			}
			
			//Parse the JSON using JSONTokener
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			
			//Build the array of crimes from JSONObject
			for (int i = 0; i < array.length(); i++) {
				crimes.add(new Crime(array.getJSONObject(i)));
			}
			
		} catch (FileNotFoundException e) {
			// Ignore this one, it only happens when starting fresh
		} finally {
				if (reader != null) {
					reader.close();
				}
		}
		return crimes;
	}
	
	/*
	 * Retrieve from external storage
	 */
	public ArrayList<Crime> loadFromExternal() throws JSONException, IOException {
		ArrayList<Crime> crimes = new ArrayList<Crime>();
		BufferedReader reader = null;
		try {
			File file = mContext.getExternalFilesDir(mFilename);
			InputStream in = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
			
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			
			for (int i = 0; i < array.length(); ++i) {
				crimes.add(new Crime(array.getJSONObject(i)));
			}
		} catch (FileNotFoundException e) {
			// Will not have to handle this as a check has been done before
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		
		return crimes;
	}
}
