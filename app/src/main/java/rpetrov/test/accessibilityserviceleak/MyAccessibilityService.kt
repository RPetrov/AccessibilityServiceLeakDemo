package rpetrov.test.accessibilityserviceleak

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?){
        Log.i("MyAccessibilityService", "onAccessibilityEvent")
    }

    override fun onInterrupt() {
        Log.i("MyAccessibilityService", "onInterrupt")
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("MyAccessibilityService", "onCreate")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i("MyAccessibilityService", "onServiceConnected")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("MyAccessibilityService", "onDestroy")
    }
}