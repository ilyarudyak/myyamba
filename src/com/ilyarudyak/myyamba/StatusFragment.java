package com.ilyarudyak.myyamba;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;

public class StatusFragment extends Fragment 
implements OnClickListener {
	
	private static final String TAG = "status_activity";
	
	private EditText tweetEditText;
	private Button tweetButton;
	private TextView counterTextView;
	
	private SharedPreferences prefs;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_status, null, false);
		
        tweetEditText = (EditText) v.findViewById(R.id.tweetEditText);
        tweetButton = (Button) v.findViewById(R.id.tweetButton);
        counterTextView = (TextView) 
        		v.findViewById(R.id.characterCounterTextView);
        
        // set listener
        tweetButton.setOnClickListener(this);
        
        // update character counter
        tweetEditText.addTextChangedListener(new TextWatcher() {

    		@Override
    		public void afterTextChanged(Editable tweetText) {

    			int count = 140 - tweetEditText.length();
//    			Log.d(TAG, "after text changed");
    			counterTextView.setText(Integer.toString(count));
    		}

    		@Override
    		public void beforeTextChanged(CharSequence s, int start, int count,
    				int after) {
    			
    		}

    		@Override
    		public void onTextChanged(CharSequence s, int start, int before,
    				int count) {
    			
    		}
        	
        });
        
		return v;
		
	}// end of onCreateView()
	
	@Override
	public void onClick(View v) {

		String tweet = tweetEditText.getText().toString();
		Log.d(TAG, tweet);
		
        // post to http://yamba.marakana.com
        new PostTask().execute(tweet);
	}

    private class PostTask extends
    	AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			try {
				
				// get shared prefs
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(getActivity());
				String username = prefs.getString("username", "");
				String password = prefs.getString("password", "");
				
				// Check that username and password are not empty
				// If empty, Toast a message to set login info and bounce to
				if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
					getActivity().startActivity(
							new Intent(getActivity(), SettingsActivity.class));
					return "Please update your username and password";
				}
				
				YambaClient cloud = new YambaClient(username, password);
				cloud.postStatus(params[0]);

				Log.d(TAG, "Successfully posted to the cloud: " + params[0]);
				return "posting successfull";
				
			} catch (YambaClientException e) {
				e.printStackTrace();
				return "posting failed";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			Toast toast = Toast.makeText(StatusFragment.this.getActivity(), result, 
					Toast.LENGTH_LONG);
			toast.show();
			
			Log.d(TAG, result);
		}
    	
		
    }




}






































