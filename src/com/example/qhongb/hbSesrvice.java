package com.example.qhongb;

import java.io.FileOutputStream;
import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
//import android.accessibilityservice.AccessibilityServiceInfo;
//import android.annotation.TargetApi;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
//import android.view.accessibility.AccessibilityManager;
//import android.widget.Toast;

public class hbSesrvice extends AccessibilityService {
	private static String LOGTAG ="wolf";
	static String  fileName = "mnt/sdcard/Y.txt";
	@Override
	protected void onServiceConnected() {
		// TODO Auto-generated method stub
		
		Log.d(LOGTAG, "SERVICE CONNECT");
		//writeFileSdcard(fileName,"SERVICE CONNECT");
		Toast.makeText(this, "服务连接上", Toast.LENGTH_LONG).show();
		super.onServiceConnected();
	}

	public void writeFileSdcard(String fileName, String message) {

		try {

			// FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);

			FileOutputStream fout = new FileOutputStream(fileName);

			byte[] bytes = message.getBytes();

			fout.write(bytes);

			fout.close();

		}

		catch (Exception e) {

			e.printStackTrace();

		}

	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(LOGTAG, "SERVICE DESTORY");
	Toast.makeText(this, "服务destory", Toast.LENGTH_LONG).show();
	}
	
	
	public void recycle(AccessibilityNodeInfo info) {  
		  String strTemp = String.valueOf(info.getChildCount());
		  Log.e(LOGTAG, strTemp);
        if (info.getChildCount() == 0) {  
            Log.e(LOGTAG, "child widget----------------------------" + info.getClassName());  
             
            Log.e(LOGTAG, "Text：" + info.getText());  
            Log.e(LOGTAG, "getViewIdResourceName:" + info.getViewIdResourceName());  
        } else {  
            for (int i = 0; i < info.getChildCount(); i++) {  
                if(info.getChild(i)!=null){  
                    recycle(info.getChild(i));  
                }  
            }  
        }  
    } 
  //PackageName: com.tencent.mm; MovementGranularity: 0; Action: 0 [ ClassName: com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI; Text: [微信]; ContentDescription: null; ItemCount: -1; CurrentItemIndex: -1; IsEnabled: true; IsPassword: false; IsChecked: false; IsFullScreen: true; Scrollable: false; BeforeText: null; FromIndex: -1; ToIndex: -1; ScrollX: -1; ScrollY: -1; MaxScrollX: -1; MaxScrollY: -1; AddedCount: -1; RemovedCount: -1; ParcelableData: null ]; recordCount: 0
	   @SuppressLint("NewApi")  
	    private void openPacket() {  
	        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();  
	       // recycle(nodeInfo);
	        String msg = "";
	        if (nodeInfo != null) {  
	            List<AccessibilityNodeInfo> list = nodeInfo  
	                    .findAccessibilityNodeInfosByText("红包详情");  
	           
	            for (AccessibilityNodeInfo n : list) {  
	                String text =n.getText().toString();
	                Log.e("wolf",text);
	            }  
	            
	            List<AccessibilityNodeInfo> listDetail =  nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b99");  
	            String strLen = String.valueOf(listDetail.size());
	            Log.e("wolf",strLen);
	            for (AccessibilityNodeInfo n : listDetail) {  
	                String text =n.getText().toString();
	                Log.e("wolf",text);
	            }        
	            List<AccessibilityNodeInfo> listDetailInfo =  nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aes");  
	              strLen = String.valueOf(listDetailInfo.size());
	            Log.e("wolf",strLen);
	            for (AccessibilityNodeInfo n : listDetailInfo) {  
	            	 
	            	 Log.e("wolf","===========================================");
	            	//n.getChildCount();
	            	 
	            	AccessibilityNodeInfo nameInfo = n.getChild(0);
	            	AccessibilityNodeInfo timeInfo = n.getChild(1);
	            	AccessibilityNodeInfo feeInfo  = n.getChild(2);
	 
	            	String sName = nameInfo.getText().toString();
	            	String sTime = timeInfo.getText().toString();
	            	String sFee = feeInfo.getText().toString();
	            	msg += sName+" : "+ sTime + " :"+ sFee;
	            	Log.e("wolf",sName+" : "+ sTime + " :"+ sFee);
	            	 
	            }        	            
	            
	        }  
	         Intent intent = new Intent();
	         intent.setClass(getApplicationContext(), MainActivity.class);
	         Bundle bundle = new Bundle();
	         bundle.putString("wolf", msg);
	         intent.putExtras(bundle);
	         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	         startActivity(intent);
	    } 
	 
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// TODO Auto-generated method stub
		//Toast.makeText(this, event.toString(), 1).show();
		Log.e(LOGTAG, "事件--->" + event);
		//writeFileSdcard(fileName,event.toString());
		int eventType = event.getEventType();
		switch (eventType) {
		case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
			String className = event.getClassName().toString();
			if (className.equals("com.tencent.mm.ui.LauncherUI")) {
				// 开始抢红包
				// getPacket();
			} else if (className
					.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
				Log.e(LOGTAG, "红包详情");
				// 开始打开红包
				 openPacket();
			}
			break;

		}

	}

	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub
		
	}

}
