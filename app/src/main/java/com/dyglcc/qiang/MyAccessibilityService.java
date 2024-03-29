package com.dyglcc.qiang;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;


/**
 * custom service
 */
public class MyAccessibilityService extends AccessibilityService {
    private static MyAccessibilityService instance;

    private static void setInstance(MyAccessibilityService service) {
        instance = service;
    }

    public static synchronized MyAccessibilityService getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setInstance(null);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) {
            return;
        }
        String pkgName = event.getPackageName() != null ? event.getPackageName().toString() : "";
        String clsName = event.getClassName() != null ? event.getClassName().toString() : "";
        String desc = event.getContentDescription() != null ? event.getContentDescription().toString() : "";
        String kText = event.getText() != null ? event.getText().toString() : "";

        Log.e("---tag--", "type=" + event.getEventType() + ",---clsName--" + clsName + ",---desc--" + desc + ",---kText--" + kText);
        if (!pkgName.equals(MainActivity.packageName)) {
            return;
        }
        AccessibilityNodeInfo nodeInfo = event.getSource();
        if (nodeInfo == null) {
            return;
        }

        if (pkgName.equals(MainActivity.packageName)) {
            if (clsName.equals(BaseContact.clsName_Main) && event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                setNodeClick(nodeInfo, BaseContact.text_find);

            } else if (clsName.equals(BaseContact.clsName_listView) && event.getEventType() == AccessibilityEvent.TYPE_VIEW_FOCUSED) {
                setNodeClick(nodeInfo, BaseContact.text_friend_circle);
            }
        }
    }

    @Override
    public void onInterrupt() {
    }

    /**
     * Auto-click after finding the node
     *
     * @param nodeInfo AccessibilityNodeInfo
     * @param text     the message of node
     */
    private void setNodeClick(AccessibilityNodeInfo nodeInfo, String text) {
        AccessibilityNodeInfo info = NodeUtils.findNodeByParentClick(nodeInfo, text);
        if (info != null) {
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }
}