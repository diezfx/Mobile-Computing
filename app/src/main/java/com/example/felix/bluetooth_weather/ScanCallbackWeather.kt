package com.example.felix.bluetooth_weather

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log

class ScanCallbackWeather(val mainActivity: MainActivity) :ScanCallback() {




    override fun onScanFailed(errorCode: Int) {
        Log.d("weatherapp",errorCode.toString())
        Log.d("weatherapp", "Failed")

    }

    override fun onScanResult(callbackType: Int, result: ScanResult?) {

        Log.d("weatherapp",result.toString())
        Log.d("weatherapp", result?.device?.name)
        super.onScanResult(callbackType, result)

        mainActivity.connectGatt(result)
    }

    override fun onBatchScanResults(results: MutableList<ScanResult>?) {
        super.onBatchScanResults(results)
        Log.d("weatherapp", "Batch")
        Log.d("weatherapp",results?.size.toString())
    }
}