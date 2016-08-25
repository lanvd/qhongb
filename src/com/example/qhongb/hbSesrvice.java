package com.example.qhongb;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
//import android.accessibilityservice.AccessibilityServiceInfo;
//import android.annotation.TargetApi;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;
//import android.view.accessibility.AccessibilityManager;
//import android.widget.Toast;

public class hbSesrvice extends AccessibilityService {
	private static String LOGTAG ="WOLFLOG";
	@Override
	protected void onServiceConnected() {
		// TODO Auto-generated method stub
		super.onServiceConnected();
		Log.d(LOGTAG, "SERVICE CONNECT");
		//Toast.makeText(this, "服务连接上", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(LOGTAG, "SERVICE DESTORY");
	//	Toast.makeText(this, "服务destory", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent arg0) {
		// TODO Auto-generated method stub
		  Log.d(LOGTAG, "事件--->" + arg0 );
		
	}

	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub
		
	}

}
