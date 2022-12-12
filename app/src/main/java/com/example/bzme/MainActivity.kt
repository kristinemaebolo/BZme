package com.example.bzme

import android.Manifest
import android.app.*
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bzme.DB.DBHelper
import com.example.bzme.Helper.Data
import com.example.bzme.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.app.ActivityCompat.requestPermissions

import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.navigation.navOptions
import com.example.bzme.Model.Notification

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var helperData = Data()
    private var db = DBHelper(this)
    private var notification = Notification(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.createChannel()
        }

        // Check Permissions and request if not yet granted
        checkAppPermission(Manifest.permission.READ_PHONE_STATE, 100)
        checkAppPermission(Manifest.permission.SEND_SMS, 101)
        checkAppPermission(Manifest.permission.RECEIVE_SMS, 102)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard
            )
        )

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.mipmap.logob_round)
        supportActionBar?.setDisplayUseLogoEnabled(false)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 1){
            for(i in permissions){
                Log.d("perm", i)

            }
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION ","ACCESS")
            }else{
                Log.d("PERMISSION ","DENIED")
            }
            if(grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION ","ACCESS")
            }else{
                Log.d("PERMISSION ","DENIED")
            }

            if(grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION ","ACCESS")
            }else{
                Log.d("PERMISSION ","DENIED")
            }
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun showTimePicker(view: android.view.View) {
        val textView = findViewById<EditText>(view.id)
        val cal = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            textView.setText( SimpleDateFormat("HH:mm").format(cal.time))
        }

        TimePickerDialog(
            this,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()
    }

    fun showDatePicker(view: android.view.View) {
        val created_at = findViewById<TextView>(view.id)

        val dat = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
                dat.set(Calendar.MONTH,month)
                dat.set(Calendar.DAY_OF_MONTH,day)
                dat.set(Calendar.YEAR,year)
                var date = SimpleDateFormat("MM/dd/yyyy").format(dat.time)
                created_at.setText(date)
        }

        var datePicker = DatePickerDialog(this,dateSetListener,dat.get(Calendar.YEAR), dat.get(Calendar.MONTH),dat.get(Calendar.DAY_OF_MONTH))
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis())
        datePicker.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun submitActivity(view: android.view.View) {
        var reply = findViewById<TextView>(R.id.reply)
        var title = findViewById<EditText>(R.id.title)
        var createdate = findViewById<EditText>(R.id.create_date)
        var toTime = findViewById<EditText>(R.id.to_time)
        var fromTime = findViewById<EditText>(R.id.from_time)

        if(reply.text.toString().isNotEmpty()
            && title.text.toString().isNotEmpty()
            && createdate.text.toString().isNotEmpty()
            && toTime.text.toString().isNotEmpty()
            && fromTime.text.toString().isNotEmpty()){

            if(helperData.validateTime(fromTime.text.toString(), toTime.text.toString())
                && helperData.validateDate(createdate.text.toString())){
                db.addActivity(
                    title.text.toString(),
                    reply.text.toString(),
                    toTime.text.toString(),
                    fromTime.text.toString(),
                    createdate.text.toString())

                // Clear Fields
                title.setText("")
                reply.setText("")
                toTime.setText("")
                fromTime.setText("")
                createdate.setText("")

                navigateToFragment(R.id.navigation_home)
            }else{
                Toast.makeText(this,"Invalid Date or Time", Toast.LENGTH_LONG).show()
            }

        }else{
            Toast.makeText(this,"Please fill all fields!", Toast.LENGTH_LONG).show()
        }

    }

    private fun checkAppPermission(permission: String, requestCode: Int){
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(this, arrayOf(permission), requestCode)
        }
    }

    private fun navigateToFragment(target: Int){
        findNavController(R.id.nav_host_fragment_activity_main).navigate(
            target,
            null,
            navOptions { // Use the Kotlin DSL for building NavOptions
                anim {
                    enter = android.R.animator.fade_in
                    exit = android.R.animator.fade_out
                }
            }
        )
    }
}