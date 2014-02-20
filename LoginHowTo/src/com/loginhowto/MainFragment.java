package com.loginhowto;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.widget.LoginButton;

public class MainFragment extends Fragment {
	;
	private static final String TAG = "MainFragment";
	private UiLifecycleHelper uiHelper;
	private Button queryButton;
	private Button multiQueryButton;
	private TextView textviewMessage;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main, container, false);

		LoginButton authButton = (LoginButton) view
				.findViewById(R.id.authButton);
		authButton.setFragment(this);
		authButton.setReadPermissions(Arrays.asList("user_likes",
				"user_status", "user_groups", "friends_groups"));

		queryButton = (Button) view.findViewById(R.id.queryButton);
		multiQueryButton = (Button) view.findViewById(R.id.multiQueryButton);
		textviewMessage = (TextView) view.findViewById(R.id.message);

		queryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String fqlQuery = "SELECT message FROM stream WHERE source_id = 117769191690001 LIMIT 10";
				Bundle params = new Bundle();
				params.putString("q", fqlQuery);
				Session session = Session.getActiveSession();
				Request request = new Request(session, "/fql", params,
						HttpMethod.GET, new Request.Callback() {
							public void onCompleted(Response response) {
								GraphObject graphObject = response
										.getGraphObject();
								if (graphObject != null) {
									StringBuilder userInfo = new StringBuilder(
											"");
									JSONObject jsonObject = graphObject
											.getInnerJSONObject();
									try {
										JSONArray array = jsonObject
												.getJSONArray("data");
										for (int i = 0; i < array.length(); i++) {
											JSONObject object = (JSONObject) array
													.get(i);
											Log.d(TAG,
													"message = "
															+ object.get("message"));

											userInfo.append(object
													.get("message") + "\n\n");
										}
									} catch (JSONException e) {

										e.printStackTrace();
									}
									textviewMessage.setText(userInfo);
									textviewMessage.setVisibility(View.VISIBLE);
									Log.i(TAG, "Result: " + response.toString());
								}
							}
						});
				Request.executeBatchAsync(request);
			}
		});

		multiQueryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { // Perform when Multi-Query button is
											// hit
				// Queries Facebook
				String fqlQuery = "{"
						+ "'query1': 'SELECT post_id,actor_id,message FROM stream WHERE source_id = 117769191690001 LIMIT 13','query2': 'SELECT uid,name FROM user WHERE uid IN (SELECT actor_id FROM #query1) LIMIT 100'"
						+ "}";
				Bundle params = new Bundle();
				params.putString("q", fqlQuery);
				Session session = Session.getActiveSession();
				Request request = new Request(session, "/fql", params,
						HttpMethod.GET, new Request.Callback() {
							public void onCompleted(Response response) {
								GraphObject graphObject = response
										.getGraphObject();
								if (graphObject != null) {
									StringBuilder userInfo = new StringBuilder(
											"");
									// Parses JSON object into string, then sets
									// the text to display it
									try {

										GraphObject go = response
												.getGraphObject();
										JSONObject jso = go
												.getInnerJSONObject();
										JSONArray data = jso
												.getJSONArray("data")
												.getJSONObject(0)
												.getJSONArray("fql_result_set");
										JSONArray data2 = jso
												.getJSONArray("data")
												.getJSONObject(1)
												.getJSONArray("fql_result_set");

										for (int i = 0; i < data.length(); i++) {
											JSONObject query1 = data
													.getJSONObject(i);
											JSONObject query2 = null;
											if (data2.getJSONObject(i) != null)
												query2 = data2.getJSONObject(i);
											else
												break;

											if (!query1.getString("message")
													.equals("")) {
												userInfo.append(query1
														.getString("post_id")
														+ "\n");
												userInfo.append(query1
														.getString("actor_id")
														+ "\n");
												userInfo.append(query1
														.getString("message")
														+ "\n");

												userInfo.append(query2
														.getString("uid")
														+ "\n");
												userInfo.append(query2
														.getString("name")
														+ "\n\n");
											}

											Log.i(TAG,
													"Result: "
															+ userInfo
																	.toString());

										}
									} catch (JSONException e) {
										e.printStackTrace();
									}

									textviewMessage.setText(userInfo);
									textviewMessage.setVisibility(View.VISIBLE);
									Log.i(TAG, "Result: " + response.toString());
								}
							}
						});
				Request.executeBatchAsync(request);
			}
		});

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		// For scenarios where the main activity is launched and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}

		uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			queryButton.setVisibility(View.VISIBLE);
			multiQueryButton.setVisibility(View.VISIBLE);
		} else if (state.isClosed()) {
			queryButton.setVisibility(View.INVISIBLE);
			multiQueryButton.setVisibility(View.INVISIBLE);
		}
	}

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
}
