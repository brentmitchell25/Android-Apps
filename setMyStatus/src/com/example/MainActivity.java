package com.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.easy.facebook.android.facebook.FBLoginManager;

public class MainActivity extends Activity {

	Intent intent;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);

		intent = new Intent(getApplicationContext(), FacebookConnect.class);
		ImageView image = (ImageView) findViewById(R.id.facebookbut);
		// image.setOnClickListener(FacebookonListener);

		String permissions[] = { "read_stream", "publish_stream",
				"offline_access", "user_likes", "user_status", "user_groups",
				"friends_groups" };

		FBLoginManager fbManager = new FBLoginManager(this, R.layout.black,
				"1408599362708705", permissions);

		if (fbManager.existsSavedFacebook()) {
			// fbManager.loadFacebook();
			image.setImageResource(R.drawable.facebook_logout);
		} else
			image.setImageResource(R.drawable.facebook_login);

		image.setOnClickListener(FacebookonListener);
		image.setVisibility(View.VISIBLE);

	}

	private OnClickListener FacebookonListener = new OnClickListener() {
		public void onClick(View v) {
			try {

				ConnectivityManager cMgr = (ConnectivityManager) v.getContext()
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo = cMgr.getActiveNetworkInfo();
				String status = netInfo.getState().toString();

				if (status.equals("CONNECTED")) {
					startActivity(intent);
				} else {
					Toast.makeText(getApplicationContext(),
							"No connection available", Toast.LENGTH_SHORT)
							.show();
				}

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Connection refused",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	protected void onPause() {
		super.onPause();
	}

	protected void onResume() {
		super.onResume();
	}

}