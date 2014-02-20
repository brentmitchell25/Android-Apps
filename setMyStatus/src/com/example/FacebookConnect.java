package com.example;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.easy.facebook.android.apicall.GraphApi;
import com.easy.facebook.android.data.Feed;
import com.easy.facebook.android.error.EasyFacebookError;
import com.easy.facebook.android.facebook.FBLoginManager;
import com.easy.facebook.android.facebook.Facebook;
import com.easy.facebook.android.facebook.LoginListener;

public class FacebookConnect extends Activity implements LoginListener {

	private FBLoginManager fbManager;
	Intent intentResult;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intentResult = new Intent(getApplicationContext(), IntentResult.class);

		shareFacebook();
	}

	public void shareFacebook() {
		String permissions[] = { "read_stream", "publish_stream",
				"offline_access", "user_likes", "user_status", "user_groups",
				"friends_groups" };

		fbManager = new FBLoginManager(this, R.layout.black,
				"1408599362708705", permissions);

		if (fbManager.existsSavedFacebook()) {

			fbManager.loadFacebook();
		} else {

			fbManager.login();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		fbManager.loginSuccess(data);
	}

	public void loginFail() {
		fbManager.displayToast("Login failed!");

	}

	public void logoutSuccess() {
		fbManager.displayToast("Logout success!");
	}

	public void loginSuccess(Facebook facebook) {

		GraphApi graphApi = new GraphApi(facebook);

		String response = "";

		try {
			List<Feed> c = graphApi.getPostFriends(
					graphApi.getGroup("kuengineering").getId(), 10);
			for (int i = 0; i < c.size(); i++)
				response += c.get(i).getMessage() + "\n\n";

		} catch (EasyFacebookError e) {
			e.toString();
		}

		String pkg = getPackageName();
		intentResult.putExtra(pkg + ".myMessage", response);
		startActivity(intentResult);

	}

}