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
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
//import android.accessibilityservice.AccessibilityServiceInfo;
//import android.annotation.TargetApi;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//import android.view.accessibility.AccessibilityManager;
//import android.widget.Toast;

public class hbSesrvice extends AccessibilityService {
	private static String LOGTAG = "wolf";
	private static DetailInfo detailInfo;
	private Map<String, String> mapTitle;
	private Map<String, LuckPerson> mapPersons;
	private Map<String, String> detManList;
	private String strCurHbTitle;
	// private AccessibilityHelper accessHelper ;
	static String fileName = "mnt/sdcard/Y.txt";
	private int iflag;
	private int iHbDetailFlag;

	private static final int UPDATE_PIC = 0x100;
	private int statusBarHeight;// 状态栏高度
	private View view;// 透明窗体
	private TextView text = null;
	private Button hideBtn = null;
	private Button updateBtn = null;
	private HandlerUI handler = null;
	private Thread updateThread = null;
	private Button detailBtn = null;
	private boolean viewAdded = false;// 透明窗体是否已经显示
	private boolean viewHide = false; // 窗口隐藏
	private WindowManager windowManager;
	private WindowManager.LayoutParams layoutParams;

	@Override
	protected void onServiceConnected() {
		// TODO Auto-generated method stub
		detailInfo = new DetailInfo();
		mapPersons = new HashMap<String, LuckPerson>();
		mapTitle = new HashMap<String, String>();
		strCurHbTitle = new String();
		Log.d(LOGTAG, "SERVICE CONNECT");
		// writeFileSdcard(fileName,"SERVICE CONNECT");
		Toast.makeText(this, "服务连接上", Toast.LENGTH_LONG).show();
		iflag = 0;
		iHbDetailFlag = 0;
		detManList = new HashMap<String, String>();
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

	/**
	 * 关闭悬浮窗
	 */
	public void removeView() {
		if (viewAdded) {
			windowManager.removeView(view);
			viewAdded = false;
		}
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		createFloatView();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		viewHide = false;
		refresh();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		removeView();
		iflag = 0;
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

			if (iScroll == 1) {
				AccessibilityHelper.performScrollDown(hbListViewNodeInfo);
			}

		}

	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// TODO Auto-generated method stub
		// Toast.makeText(this, event.toString(), 1).show();
		Log.e(LOGTAG, "事件--->" + event);
		String strHongbaoInfo = "com.tencent.mm:id/a13";
		String strInputInfo = "com.tencent.mm:id/z4";
		String strSendButtonInfo = "com.tencent.mm:id/z_";
		String className = "";
		// writeFileSdcard(fileName,event.toString());
		int eventType = event.getEventType();
		className = event.getClassName().toString();
		if (className.equals("android.widget.TextView")) {
			Log.e(LOGTAG, "事件--->" + event);
		}
		AccessibilityNodeInfo rootInfo = getRootInActiveWindow();
		switch (eventType) {
		case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:

			if (className.equals("com.tencent.mm.ui.LauncherUI")) {
				boolean isMmberUi = isMemberChatUi(rootInfo);	

			} else if (className
					.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
				Log.e(LOGTAG, "红包详情");

				// 开 
				if (iHbDetailFlag == 1) {
					 
					openPacket(1);
				}

			}
			break;
		case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
			// className = event.getClassName().toString();

			boolean isMmberUi = isMemberChatUi(rootInfo);
			Log.e("wolf", "iflag=" + String.valueOf(iflag));
			if (isMmberUi == true && iflag == 1) {
		 
			}
			if (isMmberUi == true && iflag == 2) {
 

			}

 
	 
/*			if (isMmberUi == true) {
				AccessibilityNodeInfo hongbaoInfo = AccessibilityHelper
						.findNodeInfosById(rootInfo, strHongbaoInfo);
				if (hongbaoInfo != null) {
					String strHongbaoTitle = "com.tencent.mm:id/a1m";
					AccessibilityNodeInfo hongbaotitle = AccessibilityHelper
							.findNodeInfosById(hongbaoInfo, strHongbaoTitle);
					if (hongbaotitle != null) {
						String strTitle = hongbaotitle.getText().toString();
						Log.e("wolf", "hongbao title=" + strTitle
								+ "mapTitle len=" + mapTitle.size());
						if (!mapTitle.containsKey(strTitle)) {
							mapTitle.put(strTitle, "hongbao title");
							strCurHbTitle = strTitle;
							detailInfo.iLastAction = 1;
							AccessibilityHelper.performClick(hongbaoInfo);

						} else {

						}
					}
				}

			} else {
				if (detailInfo.iLastAction == 2) {
					detailInfo.iLastAction = 3;
					openPacket(1);
				} else if (detailInfo.iLastAction == 3 && detailInfo.iEnd == 0) {
					openPacket(1);
				}
			}*/

			if (detailInfo.iEnd == 1 && iHbDetailFlag == 1) {
				Log.e(LOGTAG, "结束");
				String stsBack = "com.tencent.mm:id/fa";
				String strMsg = String.valueOf(mapPersons.size());
				ArrayList<LuckPerson> persons = new ArrayList<LuckPerson>();
				Iterator iterator = mapPersons.keySet().iterator();
				while (iterator.hasNext()) {
					Object key = iterator.next();
					LuckPerson p = mapPersons.get(key);
					Log.e(LOGTAG, p.toString());
					p.title = strCurHbTitle;
					persons.add(p);
				}
				Intent intent = new Intent();
				intent.setAction("wolf.test");
				Bundle bundle = new Bundle();
				bundle.putSerializable("user", persons);
				intent.putExtras(bundle);
				sendBroadcast(intent);
				Log.e(LOGTAG, "结束 map len=" + strMsg);
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

	private void createFloatView() {
		handler = new HandlerUI();
		UpdateUI update = new UpdateUI();
		updateThread = new Thread(update);
		updateThread.start(); // 开户线程

		view = LayoutInflater.from(this).inflate(R.layout.floatview, null);
		text = (TextView) view.findViewById(R.id.flowspeed);
		hideBtn = (Button) view.findViewById(R.id.hideBtn);
		updateBtn = (Button) view.findViewById(R.id.updateBtn);
		
		detailBtn = (Button) view.findViewById(R.id.getDetailBtn);
		windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
		/*
		 * LayoutParams.TYPE_SYSTEM_ERROR：保证该悬浮窗所有View的最上层
		 * LayoutParams.FLAG_NOT_FOCUSABLE:该浮动窗不会获得焦点，但可以获得拖动
		 * PixelFormat.TRANSPARENT：悬浮窗透明
		 */
		layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ERROR,
				LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
		// layoutParams.gravity = Gravity.RIGHT|Gravity.BOTTOM; //悬浮窗开始在右下角显示
		layoutParams.gravity = Gravity.LEFT | Gravity.TOP;

		/**
		 * 监听窗体移动事件
		 */
		view.setOnTouchListener(new OnTouchListener() {
			float[] temp = new float[] { 0f, 0f };

			public boolean onTouch(View v, MotionEvent event) {
				layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
				int eventaction = event.getAction();
				switch (eventaction) {
				case MotionEvent.ACTION_DOWN: // 按下事件，记录按下时手指在悬浮窗的XY坐标值
					temp[0] = event.getX();
					temp[1] = event.getY();
					break;

				case MotionEvent.ACTION_MOVE:
					refreshView((int) (event.getRawX() - temp[0]),
							(int) (event.getRawY() - temp[1]));
					break;

				}
				return true;
			}

		});

		hideBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//viewHide = true;
				// removeView();
				if (iflag == 3) {
					iflag = 4;	
				}
							
				iHbDetailFlag = 0;
				 
			}
		});

		detailBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				 
				// removeView();
				iHbDetailFlag = 1;
				System.out.println("----------hideBtn");
			}
		});
		updateBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				iflag = 1;
				detManList.clear(); 
				Toast.makeText(getApplicationContext(), "you click UpdateBtn",
						Toast.LENGTH_SHORT).show();

			}
		});
	}

	private void refreshView(int x, int y) {
		// 状态栏高度不能立即取，不然得到的值是0
		if (statusBarHeight == 0) {
			View rootView = view.getRootView();
			Rect r = new Rect();
			rootView.getWindowVisibleDisplayFrame(r);
			statusBarHeight = r.top;
		}

		layoutParams.x = x;
		// y轴减去状态栏的高度，因为状态栏不是用户可以绘制的区域，不然拖动的时候会有跳动
		layoutParams.y = y - statusBarHeight;// STATUS_HEIGHT;
		refresh();
	}

	/**
	 * 添加悬浮窗或者更新悬浮窗 如果悬浮窗还没添加则添加 如果已经添加则更新其位置
	 */
	private void refresh() {
		// 如果已经添加了就只更新view
		if (viewAdded) {
			windowManager.updateViewLayout(view, layoutParams);
		} else {
			windowManager.addView(view, layoutParams);
			viewAdded = true;
		}
	}

	/**
	 * 接受消息和处理消息
	 * 
	 * @author Administrator
	 * 
	 */
	class HandlerUI extends Handler {
		public HandlerUI() {

		}

		public HandlerUI(Looper looper) {
			super(looper);
		}

		/**
		 * 接收消息
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// 根据收到的消息分别处理
			if (msg.what == UPDATE_PIC) {

				if (!viewHide)
					refresh();
			} else {
				super.handleMessage(msg);
			}

		}

	}

	/**
	 * 更新悬浮窗的信息
	 * 
	 * @author Administrator
	 * 
	 */
	class UpdateUI implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 如果没有中断就一直运行
			while (!Thread.currentThread().isInterrupted()) {
				Message msg = handler.obtainMessage();
				msg.what = UPDATE_PIC; // 设置消息标识
				handler.sendMessage(msg);
				// 休眠1s
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
