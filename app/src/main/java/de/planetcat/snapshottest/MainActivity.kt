package de.planetcat.snapshottest

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import com.google.android.gms.awareness.Awareness
import de.planetcat.snapshottest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

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
}