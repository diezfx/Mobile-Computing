package com.example.felix.bluetooth_weather


import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.Manifest
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.le.ScanResult
import android.os.ParcelUuid
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.widget.TextView
import java.util.*
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    var gattService = null
    var humidity:TextView? = null
    var temperature:TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        humidity = findViewById(R.id.humidityout)
        temperature = findViewById(R.id.temperatureout)

        // activate bluetooth
        var mBluetoothManager: BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        var mBluetoothAdapter = mBluetoothManager.adapter


        // copy paste
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {

                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), Context.CONTEXT_INCLUDE_CODE)
            }
        } else {

        }


        if (mBluetoothAdapter.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

            startActivityForResult(enableBtIntent, Context.CONTEXT_INCLUDE_CODE);
        } else {
            Log.i("weatherapp", "bluetooth already started")
            startBluetoothScan()
        }

    }


    fun startBluetoothScan() {

        var mBluetoothManager: BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        var mBluetoothAdapter = mBluetoothManager.adapter
        var callback: ScanCallback = ScanCallbackWeather(this)

        //val filter = ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString("00000002-0000-0000-FDFD-FDFDFDFDFDFD")).build()
        val filter = ScanFilter.Builder().setDeviceName("IPVSWeather").build()
        var filterList:List<ScanFilter> = listOf(filter)
        val scanSettings = ScanSettings.Builder().setScanMode(2).build()
        Log.i("weatherapp", "start scanning")
        mBluetoothAdapter.bluetoothLeScanner.startScan(filterList, scanSettings, callback)


    }

    fun stopBluetoothScan() {

        var mBluetoothManager: BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        var mBluetoothAdapter = mBluetoothManager.adapter
        var callback: ScanCallback = ScanCallbackWeather(this)

        mBluetoothAdapter.bluetoothLeScanner.stopScan(callback)

    }


    fun connectGatt(result: ScanResult?){
        Log.d("weatherapp", "it works")
        var gattback:BluetoothGattCallback=GattCallbackWeather(this)
        var mBluetoothGatt=result?.device?.connectGatt(this, true, gattback,2);
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {


        // If request is cancelled, the result arrays are empty.
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            Log.d("weatherapp", "permission granted")
            startBluetoothScan()
            // permission was granted, yay! Do the
            // contacts-related task you need to do.
        } else {
            // permission denied, boo! Disable the
            // functionality that depends on this permission.
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("weatherapp", "Bluetooth Acess granted")
        startBluetoothScan()

    }


}
