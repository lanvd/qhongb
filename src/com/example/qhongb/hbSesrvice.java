package com.example.qhongb;

import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	private static String LOGTAG = "wolf";
	private static DetailInfo detailInfo;
	private List<String> listHhTitle ;
	private Map<String, LuckPerson> mapPersons;
	// private AccessibilityHelper accessHelper ;
	static String fileName = "mnt/sdcard/Y.txt";

	@Override
	protected void onServiceConnected() {
		// TODO Auto-generated method stub
		detailInfo = new DetailInfo();
		mapPersons = new HashMap<String, LuckPerson>();
		listHhTitle = new ArrayList<String>();
		Log.d(LOGTAG, "SERVICE CONNECT");
		// writeFileSdcard(fileName,"SERVICE CONNECT");
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
			Log.e(LOGTAG,
					"child widget----------------------------"
							+ info.getClassName());

			Log.e(LOGTAG, "Text：" + info.getText());
			Log.e(LOGTAG,
					"getViewIdResourceName:" + info.getViewIdResourceName());
		} else {
			for (int i = 0; i < info.getChildCount(); i++) {
				if (info.getChild(i) != null) {
					recycle(info.getChild(i));
				}
			}
		}
	}

	private boolean isMemberChatUi(AccessibilityNodeInfo nodeInfo) {
		if (nodeInfo == null) {
			return false;
		}
		String id = "com.tencent.mm:id/ces";
		int wv = AccessibilityHelper.getWechatVersion(getApplicationContext());
		if (wv <= 680) {
			id = "com.tencent.mm:id/ew";
		} else if (wv <= 700) {
			id = "com.tencent.mm:id/cbo";
		}
		String title = null;
		AccessibilityNodeInfo target = AccessibilityHelper.findNodeInfosById(
				nodeInfo, id);
		if (target != null) {
			title = String.valueOf(target.getText());
		}
		List<AccessibilityNodeInfo> list = nodeInfo
				.findAccessibilityNodeInfosByText("返回");

		if (list != null && !list.isEmpty()) {
			AccessibilityNodeInfo parent = null;
			for (AccessibilityNodeInfo node : list) {
				if (!"android.widget.ImageView".equals(node.getClassName())) {
					continue;
				}
				String desc = String.valueOf(node.getContentDescription());
				if (!"返回".equals(desc)) {
					continue;
				}
				parent = node.getParent();
				break;
			}
			if (parent != null) {
				parent = parent.getParent();
			}
			if (parent != null) {
				if (parent.getChildCount() >= 2) {
					AccessibilityNodeInfo node = parent.getChild(1);
					if ("android.widget.TextView".equals(node.getClassName())) {
						title = String.valueOf(node.getText());
					}
				}
			}
		}

		if (title != null && title.endsWith(")")) {
			Log.e("wolf", "群标题=" + title);
			return true;
		}
		return false;
	}

	// iActionId =1 state change 2 content change
	// PackageName: com.tencent.mm; MovementGranularity: 0; Action: 0 [
	// ClassName: com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI; Text:
	// [微信]; ContentDescription: null; ItemCount: -1; CurrentItemIndex: -1;
	// IsEnabled: true; IsPassword: false; IsChecked: false; IsFullScreen: true;
	// Scrollable: false; BeforeText: null; FromIndex: -1; ToIndex: -1; ScrollX:
	// -1; ScrollY: -1; MaxScrollX: -1; MaxScrollY: -1; AddedCount: -1;
	// RemovedCount: -1; ParcelableData: null ]; recordCount: 0
	@SuppressLint("NewApi")
	private void openPacket(int iActionId) {
		AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
		final String strListViewId = "com.tencent.mm:id/b9b"; // hongbao
																// listview
		final String strEndId = "com.tencent.mm:id/b8k"; // 查看我的红包记录
		int iScroll = 0;
		// recycle(nodeInfo);
		String msg = "";

		/*
		 * try { Thread.sleep(1000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

		if (nodeInfo != null) {
			AccessibilityNodeInfo hbListViewNodeInfo = AccessibilityHelper
					.findNodeInfosById(nodeInfo, strListViewId);
			AccessibilityNodeInfo hbEndInfo = AccessibilityHelper
					.findNodeInfosById(nodeInfo, strEndId);

			if (hbEndInfo != null) {
				Log.e(LOGTAG, "结束node找到");
				detailInfo.iEnd = 1;
			}

			if (hbListViewNodeInfo != null) {
				Log.e(LOGTAG, "找到红包list ");
				if (hbEndInfo != null) {
					Log.e(LOGTAG, "找到红包list  结束node找到");
					iScroll = 0;
				} else {
					Log.e(LOGTAG, "找到红包list  结束node没有需要scroll");
					iScroll = 1;
					// AccessibilityHelper.performScrollDown(hbListViewNodeInfo);
				}
			}

			List<AccessibilityNodeInfo> list = nodeInfo
					.findAccessibilityNodeInfosByText("红包详情");

			for (AccessibilityNodeInfo n : list) {
				String text = n.getText().toString();
				Log.e("wolf", text);
			}
			// b99 领取12/20个
			List<AccessibilityNodeInfo> listDetail = nodeInfo
					.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b99");
			String strLen = String.valueOf(listDetail.size());
			Log.e("wolf", strLen);
			for (AccessibilityNodeInfo n : listDetail) {
				String text = n.getText().toString();
				Log.e("wolf", text);
			}
			List<AccessibilityNodeInfo> listDetailInfo = nodeInfo
					.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aes");
			strLen = String.valueOf(listDetailInfo.size());
			Log.e("wolf", strLen);
			ArrayList<LuckPerson> persons = new ArrayList<LuckPerson>();
			for (AccessibilityNodeInfo n : listDetailInfo) {

				Log.e("wolf", "===========================================");
				// n.getChildCount();

				AccessibilityNodeInfo nameInfo = n.getChild(0);
				AccessibilityNodeInfo timeInfo = n.getChild(1);
				AccessibilityNodeInfo feeInfo = n.getChild(2);

				String sName = "";
				String sTime = "";
				String sFee = "";
				if (nameInfo != null) {
					sName = nameInfo.getText().toString();
				} else {
					continue;
				}
				if (timeInfo != null) {
					sTime = timeInfo.getText().toString();
				}
				if (feeInfo != null) {
					sFee = feeInfo.getText().toString();
				}

				msg += sName + " : " + sTime + " :" + sFee;
				LuckPerson p = new LuckPerson("", "", sName, sTime, sFee);
				mapPersons.put(sName, p);
				persons.add(p);
				Log.e("wolf", sName + " : " + sTime + " :" + sFee);

			}
/*			Intent intent = new Intent();
			intent.setAction("wolf.test");
			Bundle bundle = new Bundle();
			bundle.putSerializable("user", persons);
			intent.putExtras(bundle);
			sendBroadcast(intent);*/
			if (iScroll == 1) {
				AccessibilityHelper.performScrollDown(hbListViewNodeInfo);
			}

		}
		/*
		 * Intent intent = new Intent();
		 * intent.setClass(getApplicationContext(), MainActivity.class); Bundle
		 * bundle = new Bundle(); bundle.putString("wolf", msg);
		 * intent.putExtra("kate",bundle);
		 * intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * 
		 * 
		 * startActivity(intent);
		 */

	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// TODO Auto-generated method stub
		// Toast.makeText(this, event.toString(), 1).show();
		Log.e(LOGTAG, "事件--->" + event);
		String className = "";
		// writeFileSdcard(fileName,event.toString());
		int eventType = event.getEventType();
		switch (eventType) {
		case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
			className = event.getClassName().toString();
			AccessibilityNodeInfo rootInfo = getRootInActiveWindow();
			if (className.equals("com.tencent.mm.ui.LauncherUI")) {
				// 开始抢红包
				// getPacket();
				String strHongbaoInfo ="com.tencent.mm:id/a13";
				boolean isMmberUi = isMemberChatUi(rootInfo);
				if (isMmberUi == true){
					AccessibilityNodeInfo hongbaoInfo = AccessibilityHelper
							.findNodeInfosById(rootInfo, strHongbaoInfo);
					String strHongbaoTitle ="com.tencent.mm:id/a1m";
					AccessibilityNodeInfo hongbaotitle = AccessibilityHelper
							.findNodeInfosById(hongbaoInfo, strHongbaoTitle);
					String strTitle =   hongbaotitle.getText().toString();
					
					if ( !listHhTitle.contains(listHhTitle) ) {
						listHhTitle.add(strTitle);
						AccessibilityHelper.performClick(hongbaoInfo);
					}
				}
                
                
			} else if (className
					.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
				Log.e(LOGTAG, "红包详情");
				// 开始打开红包	 
				if (detailInfo.iLastAction == 0) {
					detailInfo.iLastAction =1;
					openPacket(1);
				}
				
				
			}
			break;
		case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
			// className = event.getClassName().toString();
			openPacket(2);
			if (detailInfo.iEnd == 1) {
				Log.e(LOGTAG, "结束");
				String stsBack ="com.tencent.mm:id/fa";
				String strMsg = String.valueOf(mapPersons.size());

				ArrayList<LuckPerson> persons = new ArrayList<LuckPerson>();
				Iterator iterator = mapPersons.keySet().iterator();
				while (iterator.hasNext()) {
					Object key = iterator.next();
					LuckPerson p = mapPersons.get(key);
					Log.e(LOGTAG, p.toString());
					persons.add(p);
				}
				Intent intent = new Intent();
				intent.setAction("wolf.test");
				Bundle bundle = new Bundle();
				bundle.putSerializable("user", persons);
				intent.putExtras(bundle);
				sendBroadcast(intent);
				Log.e(LOGTAG, "结束 map len=" + strMsg);
				detailInfo.iEnd =2 ;
				AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
				AccessibilityNodeInfo hBack = AccessibilityHelper
						.findNodeInfosById(nodeInfo, stsBack);	
				detailInfo = new DetailInfo();
				AccessibilityHelper.performClick(hBack);
			}
			break;
		}

	}

	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub

	}

}
