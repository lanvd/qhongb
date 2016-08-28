package com.example.qhongb;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	 TextView tv;
	 Button bt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.textView1);
		bt = (Button) findViewById(R.id.button1);
		bt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DBManager dbm = new DBManager(getBaseContext());	 
				List<LuckPerson> perons = dbm.query();
				String msg = "";
				for(LuckPerson p: perons) {
					msg += p.personName +" ÇÀµ½" + p.money+ "\n";
				}
				tv.setText(msg);
			}
			
		});
		Bundle msgBundle = this.getIntent().getBundleExtra("kate");
		if (msgBundle != null) {
			tv.setText(msgBundle.getString("wolf"));			
		}
		
		
		 
	}
	 
	 
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
