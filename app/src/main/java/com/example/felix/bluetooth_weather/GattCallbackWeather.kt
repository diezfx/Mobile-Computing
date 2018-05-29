package com.example.felix.bluetooth_weather

import android.bluetooth.*
import android.bluetooth.BluetoothProfile.STATE_CONNECTED
import android.bluetooth.BluetoothProfile.STATE_DISCONNECTED
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.os.ParcelUuid
import android.widget.TextView
import java.util.*



class GattCallbackWeather(val mainActivity: MainActivity) :BluetoothGattCallback(){


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

    override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
        super.onCharacteristicRead(gatt, characteristic, status)
        if(characteristic?.uuid.toString() == "00002A6F-0000-3512-2118-0009af100700"){
            mainActivity.humidity?.text = characteristic?.value.toString()
        } else{
            mainActivity.temperature?.text = characteristic?.value.toString()
        }

    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
        super.onCharacteristicChanged(gatt, characteristic)

        Log.i("weatherapp", "Notify")
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        super.onServicesDiscovered(gatt, status)

        Log.i("weather app","hallo welt")

        // service
        var uuid = UUID.fromString("00000002-0000-0000-FDFD-FDFDFDFDFDFD")

        var service: BluetoothGattService? = gatt?.getService(uuid)

        // characteristic: humidity
        gatt?.setCharacteristicNotification(service?.getCharacteristic(UUID.fromString("00002A6F-0000-3512-2118-0009af100700")),true)
        gatt?.readCharacteristic(service?.getCharacteristic(UUID.fromString("00002A6F-0000-3512-2118-0009af100700")))

        // characteristic: temperature
        gatt?.setCharacteristicNotification(service?.getCharacteristic(UUID.fromString("00002A1C-0000-3512-2118-0009af100700")), true)
        gatt?.readCharacteristic(service?.getCharacteristic(UUID.fromString("00002A1C-0000-3512-2118-0009af100700")))


    }

}