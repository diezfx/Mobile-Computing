package com.example.felix.bluetooth_weather

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothProfile.STATE_CONNECTED
import android.bluetooth.BluetoothProfile.STATE_DISCONNECTED
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager




class GattCallbackWeather:BluetoothGattCallback(){


    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothDeviceAddress: String? = null
    private var mConnectionState = BluetoothProfile.STATE_DISCONNECTED

    private val STATE_DISCONNECTED = 0
    private val STATE_CONNECTING = 1
    private val STATE_CONNECTED = 2


    val ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
    val ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"
    val ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED"
    val ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE"
    val EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA"

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int,
                                         newState: Int) {
        val intentAction: String
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            intentAction = ACTION_GATT_CONNECTED
            mConnectionState = STATE_CONNECTED
            Log.i(TAG, "Connected to GATT server.")
            Log.i(TAG, "Attempting to start service discovery:" + gatt.discoverServices())

        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            intentAction = ACTION_GATT_DISCONNECTED
            mConnectionState = STATE_DISCONNECTED
            Log.i(TAG, "Disconnected from GATT server.")



        }
    }



    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        super.onServicesDiscovered(gatt, status)

        Log.d("weather app","hallo welt")

        for (g in gatt!!.services){
            Log.d("weather app", g.uuid.toString())

        }
    }

}