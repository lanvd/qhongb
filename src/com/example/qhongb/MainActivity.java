package com.example.qhongb;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	 TextView tv;
	 Button bt;
	 Button btClear;
	 ListView lv;
	 Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	  
		bt = (Button) findViewById(R.id.button1);
		btClear = (Button) findViewById(R.id.button2);
		context = this.getApplicationContext();
		lv = (ListView) findViewById(R.id.listview);
		bt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
 			DBManager dbm = new DBManager(getBaseContext());	 
				List<LuckPerson> perons = dbm.query();
				String msg = "";
				List<HashMap<String,String>> mylist=new ArrayList<HashMap<String,String>>();
				
				
				for(LuckPerson p: perons) {
					msg += p.personName +" ÇÀµ½" + p.money+ "\n";
					HashMap<String,String> map1=new HashMap<String,String>();
					map1.put("ptitle",p.title);
					map1.put("pname",p.personName);
					map1.put("ptime", p.time);
					map1.put("pmoney", p.money);
					mylist.add(map1);
				}
				Log.e("wolf",msg);
				//tv.setText(msg); 
				SimpleAdapter listAdapter=new  SimpleAdapter (context,mylist,R.layout.itemlistview,
					    new String[]{"ptitle","pname","ptime","pmoney"},new int[]{R.id.ptitle, R.id.pname,R.id.ptime,R.id.pmoney});
				 lv.setAdapter(listAdapter);
				
			}
			
		});

		btClear.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DBManager dbm = new DBManager(getBaseContext());	 
				dbm.onDeleteTable("luckDetail");
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
