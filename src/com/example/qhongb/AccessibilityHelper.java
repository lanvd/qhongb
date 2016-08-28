package com.example.qhongb;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.reflect.Field;
import java.util.List;

public final class AccessibilityHelper {
	public static final String WECHAT_PACKAGENAME = "com.tencent.mm";

	private AccessibilityHelper() {
	}

	public static int getWechatVersion(Context context) {
		PackageInfo mWechatPackageInfo = null;
		try {
			mWechatPackageInfo = context.getPackageManager().getPackageInfo(
					WECHAT_PACKAGENAME, 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
		if (mWechatPackageInfo == null) {
			return 0;
		}
		return mWechatPackageInfo.versionCode;

	}

	/** 閫氳繃id鏌ユ壘 */
	public static AccessibilityNodeInfo findNodeInfosById(
			AccessibilityNodeInfo nodeInfo, String resId) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			List<AccessibilityNodeInfo> list = nodeInfo
					.findAccessibilityNodeInfosByViewId(resId);
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}
		}
		return null;
	}

	/** 閫氳繃鏂囨湰鏌ユ壘 */
	public static AccessibilityNodeInfo findNodeInfosByText(
			AccessibilityNodeInfo nodeInfo, String text) {
		List<AccessibilityNodeInfo> list = nodeInfo
				.findAccessibilityNodeInfosByText(text);
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	/** 閫氳繃缁勪欢鍚嶅瓧鏌ユ壘 */
	public static AccessibilityNodeInfo findNodeInfosByClassName(
			AccessibilityNodeInfo nodeInfo, String className) {
		if (TextUtils.isEmpty(className)) {
			return null;
		}
		for (int i = 0; i < nodeInfo.getChildCount(); i++) {
			AccessibilityNodeInfo node = nodeInfo.getChild(i);
			if (className.equals(node.getClassName())) {
				return node;
			}
		}
		return null;
	}

	/** 鎵剧埗缁勪欢 */
	public static AccessibilityNodeInfo findParentNodeInfosByClassName(
			AccessibilityNodeInfo nodeInfo, String className) {
		if (nodeInfo == null) {
			return null;
		}
		if (TextUtils.isEmpty(className)) {
			return null;
		}
		if (className.equals(nodeInfo.getClassName())) {
			return nodeInfo;
		}
		return findParentNodeInfosByClassName(nodeInfo.getParent(), className);
	}

	private static final Field sSourceNodeField;

	static {
		Field field = null;
		try {
			field = AccessibilityNodeInfo.class
					.getDeclaredField("mSourceNodeId");
			field.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		sSourceNodeField = field;
	}

	public static long getSourceNodeId(AccessibilityNodeInfo nodeInfo) {
		if (sSourceNodeField == null) {
			return -1;
		}
		try {
			return sSourceNodeField.getLong(nodeInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static String getViewIdResourceName(AccessibilityNodeInfo nodeInfo) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			return nodeInfo.getViewIdResourceName();
		}
		return null;
	}

	/**
	 * 杩斿洖涓荤晫闈簨浠�/ public static void performHome(AccessibilityService service)
	 * { if(service == null) { return; }
	 * service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME); }
	 * 
	 * /** 杩斿洖浜嬩欢
	 */
	public static void performBack(AccessibilityNodeInfo nodeInfo) {
		if (nodeInfo == null) {
			return;
		}
		//nodeInfo.performAction(AccessibilityService.GLOBAL_ACTION_BACK);
	}

	/** 鐐瑰嚮浜嬩欢 */
	public static void performClick(AccessibilityNodeInfo nodeInfo) {
		if (nodeInfo == null) {
			return;
		}
		if (nodeInfo.isClickable()) {
			nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
		} else {
			performClick(nodeInfo.getParent());
		}
	}
	public static void performScrollDown(AccessibilityNodeInfo nodeInfo) {
		if (nodeInfo == null) {
			return;
		}
		if (nodeInfo.isScrollable()) {
			Bundle bundle = new Bundle();
	 
			nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD );
		} else {
			performClick(nodeInfo.getParent());
		}
	}	
}