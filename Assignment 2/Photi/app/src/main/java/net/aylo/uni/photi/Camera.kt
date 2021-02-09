package net.aylo.uni.photi

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Camera : AppCompatActivity() {

    private var imageCapture: ImageCapture? = null
    private val REQUEST_CODE_PERMISSIONS = 10
    private val TAG = "Photi"
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private var REQUEST_LOCATION_CODE = 101
    private var UPDATE_INTERVAL: Long = 1000
    private lateinit var googleApiClient: GoogleApiClient
    private var gpslatitude = ""
    private var gpslongitude = ""

    private lateinit var prefs : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        /*
            Uses the Location API that Google Play provides
         */
        googleApiClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .build()

        where()

        outputDirectory = getOutputDirectory()
        /*
            Executes camera action in a separate thread - more performance, remains interactive
         */
        cameraExecutor = Executors.newSingleThreadExecutor()

        btnImgCapture.setOnClickListener {
            takePhoto()
        }

        prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE)
        editor = prefs.edit()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera(selectCamera(prefs.getString("camera", "BACK").toString()))
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_PERMISSIONS)
        }
    }

    /*
        Starts Google API Client on activity Start
    */
    override fun onStart() {
        super.onStart()
        googleApiClient.connect()
    }

    /*
        Stops connection if it exists
    */
    override fun onStop() {
        super.onStop()
        if (googleApiClient.isConnected) googleApiClient.disconnect()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.cameramenu, menu)
        return true
    }

    /*
        When an item is selected, it will override default actions so that I can set my own
            On swapCamera -> change the local storage value to the opposite of what it is (adding the default if empty) and reloads the activity to get the change to propagate.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_swapCamera ->
            {
                if (prefs.getString("camera", "BACK").toString() == "BACK") {
                    editor.putString("camera", "FRONT")
                } else {
                    editor.putString("camera", "BACK")
                }
                editor.apply()
                this.recreate()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /*
        If the Create Post Activity passes back a requestCode of 1 and a resultCode of OK, then also finish this activity, else continue

        Used so that the user passes back to the main activity when posted, rather than back to the camera
    */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                finish()
            }
        }
    }


    /*
        Check if permissions are granted for the Camera
            If yes - start camera
            If no - tell the user and end the activity
    */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray ) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera(selectCamera(prefs.getString("camera", "BACK").toString()))
        } else {
            Toast.makeText(this, "Permissions not granted by the user", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun startCamera(cameraSelector: CameraSelector) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(viewFinder.createSurfaceProvider()) }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    /*
        If 'camera' is "back", return the Back Camera Selector, else turn the Front Camera Selector
     */
    private fun selectCamera(camera: String): CameraSelector {
        return if (camera == "BACK") {
            CameraSelector.DEFAULT_BACK_CAMERA
        } else {
            CameraSelector.DEFAULT_FRONT_CAMERA
        }
    }

    /*
        Save the file to disk and tell the user that is happening

        If an error occurs - log it

        If all is good - start the create Post Activity by passing the saved URI of the image to the 'startPostIntent' method
    */
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(outputDirectory, SimpleDateFormat(FILENAME_FORMAT, Locale.UK).format(System.currentTimeMillis()) + ".jpg")
        val outputOpts = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        Snackbar.make(findViewById(android.R.id.content), "Saving Image...", Snackbar.LENGTH_LONG).setAction("Action", null).show()

        imageCapture.takePicture(outputOpts,
            ContextCompat.getMainExecutor(this), object :
                ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) { Log.e(TAG, "Photo capture failed: ${exc.message}", exc) }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    var savedUri = Uri.fromFile(photoFile)

                    startPostIntent(savedUri)
                }
            })
    }

    /*
        Create an intent for the activity and pass through the location and uri of the image, then start it with a request code of 1 (to check if 'posting' the image was successful)
    */
    private fun startPostIntent(photoURI: Uri) {
        intent = Intent(this, CreatePost::class.java).apply {
            putExtra("EXTRA_Latitude", gpslatitude)
            putExtra("EXTRA_Longitude", gpslongitude)
            putExtra("EXTRA_URI", photoURI)
        }
        startActivityForResult(intent, 1)
    }

    /*
        Get the output directory, if it doesn't exist, create it
     */
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let { File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    /*
        Check if Location Service exists, then check if permitted (failing on any FALSE in that), then gets location
     */
    private fun where() {
        if (!(getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(LocationManager.GPS_PROVIDER))
            noGPS()
        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            noPermissions()
        else {
            getLocation()
        }
    }

    /*
        Output alert to activate if no GPS service found
     */
    private fun noGPS() {
        AlertDialog.Builder(this)
            .setTitle("Enable Location")
            .setMessage("Location settings is 'Off'.\nPlease enable to use app")
            .setPositiveButton("Location settings") { _, _ -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()
    }

    /*
        Output alert to activate if GPS is not permitted
    */
    private fun noPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog.Builder(this)
                .setTitle("Location permission required")
                .setMessage("Please allow access to Location to continue using this app")
                .setPositiveButton("OK") { _, _ -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE) }
                .setNegativeButton("Cancel") { _, _ -> }
                .show()
        } else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE)
    }

    /*
        Suppresses any errors regarding missing permissions

        Uses a fused location client to get the most accurate location whilst using the least battery / resources (combines wifi and gps)

        Calls back to locationCallback on success
     */
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL)
            .setNumUpdates(1)

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(location: LocationResult?) {
            val here = location?.lastLocation
            gpslatitude = here?.latitude.toString()
            gpslongitude = here?.longitude.toString()

        }
    }

}

