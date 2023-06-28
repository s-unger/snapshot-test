# snapshot-test
Minimal Reproducible Example of not working Android Awareness (Snapshot) API - Error 15 Timeout

Related Question:

I am using the snapshot API in a master thesis project to check if next user actions could be predicted based on meta-data like the user's activity.

However, when requesting user activity from the google awareness/snapshot API, I normaly get "error 15 - timeout", although in some rare cases (i.e. all six hours), it works for a very short time and I get activity results, after that it falls back to "error 15".

The error message looks like this:

```
com.google.android.gms.common.api.ApiException: 15:
at com.google.android.gms.common.internal.ApiExceptionUtil.fromStatus(com.google.android.gms:play-services-base@@18.0.1:3)
                                                                                                    at
com.google.android.gms.common.internal.zap.onComplete(com.google.android.gms:play-services-base@@18.0.1:4)
                                                                                                    at
com.google.android.gms.common.api.internal.BasePendingResult.zab(com.google.android.gms:play-services-base@@18.0.1:7)
                                                                                                    at
com.google.android.gms.common.api.internal.BasePendingResult.setResult(com.google.android.gms:play-services-base@@18.0.1:6)
                                                                                                    at
com.google.android.gms.internal.contextmanager.zzch.setResult(com.google.android.gms:play-services-awareness@@18.0.2:1)
                                                                                                    at
com.google.android.gms.internal.contextmanager.zzcp.zzi(com.google.android.gms:play-services-awareness@@18.0.2:2)
                                                                                                    at
com.google.android.gms.internal.contextmanager.zzcq.zza(com.google.android.gms:play-services-awareness@@18.0.2:9)
                                                                                                    at
com.google.android.gms.internal.contextmanager.zzb.onTransact(com.google.android.gms:play-services-awareness@@18.0.2:3)
                                                                                                    at
android.os.Binder.execTransactInternal(Binder.java:1179)
                                                                                                    at
android.os.Binder.execTransact(Binder.java:1143)
```

I created a Minimal reproducible example only using the snapshot API, which can be found here and re-creates the failure: https://github.com/s-unger/snapshot-test

The (in my oppinion) relevant code parts are:

Main Activity OnCreate:

```
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener {view ->
            Log.w("SnapshotTest", "Starting...")
            Awareness.getSnapshotClient(this).detectedActivity
                .addOnSuccessListener {
                    val activityResult = it.activityRecognitionResult
                    val activities = activityResult.probableActivities
                    var info = "SS"
                    for (activity in activities) {
                        info += " "  + activity.toString() + " " + activity.confidence
                    }
                    Log.w("SnapshotTest", info)
                    Snackbar.make(view, info, Snackbar.LENGTH_LONG).show()
                }.addOnFailureListener { exception ->
                    val info = "Snapshot failed: ${exception.message}"
                    Log.e("SnapshotTest", info, exception)
                    Snackbar.make(view, info, Snackbar.LENGTH_LONG).show()
                }.addOnCanceledListener {
                    Log.w("SnapshotTest", "Snapshot canceled.")
                    Snackbar.make(view, "Snapshot canceled.", Snackbar.LENGTH_LONG).show()
                }


        }

        findViewById<Button>(R.id.positionaktivieren)
            .setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ActivityCompat.requestPermissions(this, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ), 0)
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
                }

            }

        findViewById<Button>(R.id.activityaktivieren)
            .setOnClickListener {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    0)

            }
    }
```

Android Manifest:

```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
    <uses-permission android:name="com.google.android.gms.permission.ACTIVITY_RECOGNITION" />
    <uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.SnapshotTest"
        tools:targetApi="31">
        <meta-data
            android:name="com.google.android.awareness.API_KEY"
            android:value="CORRECTAPIKEY"
            />
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.SnapshotTest.NoActionBar">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>

            <meta-data
                android:name="android.app.lib_name"
                android:value="" />
        </activity>
    </application>

</manifest>
```
I would be very glad about your help, as I do not know anything more myself.

I tried the following things to fix it myself:

- Tripple-Checked Key and google Backend-Configuration (with restriction to my package,without restriction to my package, new key, etc.)
- Tripple-Checked Android permissions (only com.google.android.gms.permission.ACTIVITY_RECOGNITION, only android.permission.ACTIVITY_RECOGNITIO, both in combination and different orders)
- Throwing out all my other code (Thats why I now have a "minimum NOT working example...")
- Network analysis (but it looks I am not capable enough of finding out something this way, as it looks like the request is not made by the app itself, but somewhere in the android system)
