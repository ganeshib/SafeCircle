package com.example.safecircle

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.media.audiofx.BassBoost
import android.os.Bundle
import android.os.Looper
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.safecircle.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore

import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val permission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.READ_CONTACTS
    )
    private val permissionCode = 78

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val currentUser=FirebaseAuth.getInstance().currentUser
        val name=currentUser?.displayName.toString()
        val mail=currentUser?.email.toString()
        val phoneNumber=currentUser?.phoneNumber.toString()
        val imageUrl=currentUser?.photoUrl.toString()

        val db = Firebase.firestore

        // Create a new user with a first and last name
        val user = hashMapOf(
            "name" to name,
            "mail" to mail,
            "phoneNumber" to phoneNumber,
            "imageUrl" to imageUrl
        )

        db.collection("users").document(mail).set(user).addOnSuccessListener { documentReference ->
                Toast.makeText(this,"heyyyy",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Toast.makeText(this,"NOOOOOO",Toast.LENGTH_SHORT).show()
            }
        // Add a new document with a generated ID

        if(isAllPermissionGranted()){
            if(isLocationEnabled(this)){
                setUpLocationListener()
            }else{
                showGPSNotEnabledDialog(this)
            }
        }else{
            askForPermission()
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomBar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> replaceFrameWithFragment(HomeFragment())
                R.id.nav_gaurd -> replaceFrameWithFragment(GuardFragment())
                R.id.nav_dashboard -> replaceFrameWithFragment(MapsFragment())
                R.id.nav_profile -> replaceFrameWithFragment(profileFragment())
            }
            true
        }
        binding.bottomBar.selectedItemId = R.id.nav_home
    }

    private fun setUpLocationListener() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations) {
                        Log.d("Location89","onLocationResult: ${location.latitude}")
                        Log.d("Location89","onLocationResult: ${location.longitude}")

                        val currentUser=FirebaseAuth.getInstance().currentUser
                        val mail=currentUser?.email.toString()

                        val db = Firebase.firestore

                        // Create a new user with a first and last name
                        val locationData = mutableMapOf<String,Any>(
                           "lat" to location.latitude.toString(),
                            "long" to location.longitude.toString(),
                        )

                        db.collection("users").document(mail).update(locationData).addOnSuccessListener { documentReference ->

                        }.addOnFailureListener { e ->

                        }

                    }
                    // Few more things we can do here:
                    // For example: Update the location of user on server
                }
            },
            Looper.myLooper()
        )
    }

    fun isAllPermissionGranted(): Boolean {
        for(item in permission) {
            if(ContextCompat.checkSelfPermission(this,item) == PackageManager.PERMISSION_DENIED){
                return false
            }
        }
        return true
    }


    private fun askForPermission() {
        ActivityCompat.requestPermissions(this, permission, permissionCode)
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Function to show the "enable GPS" Dialog box
     */
    fun showGPSNotEnabledDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Enable GPS")
            .setMessage("required_for_this_app")
            .setCancelable(false)
            .setPositiveButton("enable_now") { _, _ ->
                context.startActivity(Intent(ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .show()
    }


    private fun replaceFrameWithFragment(fragment: Fragment) {
        val fragManager = supportFragmentManager
        val fragTrans = fragManager.beginTransaction()
        fragTrans.replace(R.id.container, fragment)
        fragTrans.commit()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionCode) {
            if (allPermissionGranted()) {
                // openCamera()
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun allPermissionGranted(): Boolean {
        for (item in permission) {
            if (ContextCompat.checkSelfPermission(this, item) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}
