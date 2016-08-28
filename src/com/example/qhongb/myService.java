package com.example.qhongb;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver; 
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class myService extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		@SuppressWarnings("unchecked")
		ArrayList<LuckPerson> persons = (ArrayList<LuckPerson>) intent.getSerializableExtra("user");
		DBManager dbm = new DBManager(context);	 
		dbm.add(persons);		
		
  
	}

}
