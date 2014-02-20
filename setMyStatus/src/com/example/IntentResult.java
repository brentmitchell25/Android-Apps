package com.example;


import com.example.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;


public class IntentResult extends Activity   {


	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);

	      setContentView(R.layout.result);
		  
	      Intent intent=getIntent();
		  String pkg = getPackageName(); 
		  String myMessage = intent.getStringExtra(pkg+".myMessage");
		  String myId = intent.getStringExtra(pkg+".myId");
	      
		  TextView tvmyName = (TextView)findViewById(R.id.nameText);
	      tvmyName.setText(myMessage);
	      
	      
	      TextView tvmyId = (TextView)findViewById(R.id.idText);
	      tvmyId.setText(myId);


	}






}