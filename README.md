This application demonstrate memory leak in Android AccessibilityService

*Environment*
| Software | Version |
| ----------- | ----------- |
| Android Studio   | Build #AI-213.7172.25.2113.9123335, built on September 30, 2022  |
|Runtime version   |11.0.13+0-b1751.21-8125866 amd64   |
|VM   |OpenJDK 64-Bit Server VM by JetBrains s.r.o.  |


*Application setup*

```
plugins {
    id 'com.android.application' version '7.3.1' apply false
    id 'com.android.library' version '7.3.1' apply false
    id 'org.jetbrains.kotlin.android' version '1.7.20' apply false
}
```

*dependencies*
```
    debugImplementation 'com.squareup.leakcanary:leakcanary-android:2.12'

    implementation 'androidx.core:core-ktx:1.7.0'
    implementation 'com.google.android.material:material:1.5.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.3'
```

*How to reproduce*

Follow this manual: https://developer.android.com/reference/android/accessibilityservice/AccessibilityService

At first create own AccessibilityService:
```
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
```

At second declare service:
```
    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.AccessibilityServiceLeak"
        tools:targetApi="31">
        <service
            android:name="rpetrov.test.accessibilityserviceleak.MyAccessibilityService"
            android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"
            android:exported="true">
            <intent-filter>
                <action android:name="android.accessibilityservice.AccessibilityService"/>
            </intent-filter>
        </service>
    </application>

```



You can just clone this application and enable/disable MyAccessibilityService:

![nable/disable MyAccessibilityService]([Screenshot_MyAccessibilityService.png])
*Where is the memory leak*

**Leak Canary**

There is Leak Canary trace:

```
    ====================================
    HEAP ANALYSIS RESULT
    ====================================
    1 APPLICATION LEAKS
    
    References underlined with "~~~" are likely causes.
    Learn more at https://squ.re/leaks.
    
    9330 bytes retained by leaking objects
    Displaying only 1 leak trace out of 5 with the same signature
    Signature: bd0178976084c8549ea1a5e0417e0d6ffe34eaa3
    ┬───
    │ GC Root: Global variable in native code
    │
    ├─ android.accessibilityservice.AccessibilityService$IAccessibilityServiceClientWrapper instance
    │    Leaking: UNKNOWN
    │    Retaining 2,5 kB in 22 objects
    │    mContext instance of rpetrov.test.accessibilityserviceleak.MyAccessibilityService
    │    ↓ AccessibilityService$IAccessibilityServiceClientWrapper.mContext
    │                                                              ~~~~~~~~
    ╰→ rpetrov.test.accessibilityserviceleak.MyAccessibilityService instance
    ​     Leaking: YES (ObjectWatcher was watching this because rpetrov.test.accessibilityserviceleak.
    ​     MyAccessibilityService received Service#onDestroy() callback and Service not held by ActivityThread)
    ​     Retaining 1,9 kB in 18 objects
    ​     key = 74f0b2eb-e18d-4ebf-a277-9a3c69a2ecc8
    ​     watchDurationMillis = 32730
    ​     retainedDurationMillis = 27728
    ​     mApplication instance of android.app.Application
    ​     mBase instance of android.app.ContextImpl
    ====================================
    0 LIBRARY LEAKS
    
    A Library Leak is a leak caused by a known bug in 3rd party code that you do not have control over.
    See https://square.github.io/leakcanary/fundamentals-how-leakcanary-works/#4-categorizing-leaks
    ====================================
    0 UNREACHABLE OBJECTS
    
    An unreachable object is still in memory but LeakCanary could not find a strong reference path
    from GC roots.
    ====================================
    METADATA
    
    Please include this in bug reports and Stack Overflow questions.
    
    Build.VERSION.SDK_INT: 33
    Build.MANUFACTURER: Google
    LeakCanary version: 2.12
    App process name: rpetrov.test.accessibilityserviceleak
    Class count: 22400
    Instance count: 165436
    Primitive array count: 119915
    Object array count: 21274
    Thread count: 16
    Heap total bytes: 22890573
    Bitmap count: 0
    Bitmap total bytes: 0
    Large bitmap count: 0
    Large bitmap total bytes: 0
    Stats: LruCache[maxSize=3000,hits=105875,misses=164396,hitRate=39%]
    RandomAccess[bytes=8393032,reads=164396,travel=62671432781,range=27581489,size=34512311]
    Heap dump reason: 5 retained objects, app is visible
    Analysis duration: 13514 ms
    Heap dump file path: /storage/emulated/0/Download/leakcanary-rpetrov.test.
    accessibilityserviceleak/2023-08-29_15-59-38_811.hprof
    Heap dump timestamp: 1693324796034
    Heap dump duration: 2245 ms
    ====================================

```


leak: AccessibilityService$IAccessibilityServiceClientWrapper -> MyAccessibilityService (context)
