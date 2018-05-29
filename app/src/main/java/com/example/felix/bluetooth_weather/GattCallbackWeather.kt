package com.example.felix.bluetooth_weather

import android.app.Activity
import android.bluetooth.*
import android.bluetooth.BluetoothProfile.STATE_CONNECTED
import android.bluetooth.BluetoothProfile.STATE_DISCONNECTED
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.os.ParcelUuid
import android.widget.TextView
import java.nio.ByteBuffer
import java.util.*
import android.bluetooth.BluetoothGattDescriptor
import android.support.annotation.IntegerRes
import kotlin.experimental.inv


class GattCallbackWeather(val mainActivity: MainActivity) : BluetoothGattCallback() {


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


    var tempNotify=false
    var humidNotify=false


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

    fun bytesToUnsignedShort(byte1: Byte, byte2: Byte, bigEndian: Boolean): Int {
        if (bigEndian)
            return (((byte1.toInt() and 255) shl 8) or (byte2.toInt() and 255))


        return (((byte2.toInt() and 255) shl 8) or (byte1.toInt() and 255))

    }

    override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
        super.onCharacteristicRead(gatt, characteristic, status)
        if (characteristic?.uuid.toString() == "00002a6f-0000-1000-8000-00805f9b34fb") {
            mainActivity.runOnUiThread({


                var humidity = bytesToUnsignedShort(characteristic!!.value[0], characteristic!!.value[1], false)

                var hum = humidity / Math.pow(2.0, 14.0)

                mainActivity.humidity?.text = hum.toString()


            })

//4E 0C 00 FE000C4E

            var uuid = UUID.fromString("00000002-0000-0000-FDFD-FDFDFDFDFDFD")

            var service: BluetoothGattService? = gatt?.getService(uuid)
            gatt?.readCharacteristic(service?.getCharacteristic(UUID.fromString("00002a1c-0000-1000-8000-00805f9b34fb")))

        } else {
            val buffer = ByteBuffer.wrap(characteristic!!.value)
            var exponent=characteristic!!.value[4].inv()
            exponent.plus(1)



            Log.i("exponent", exponent.toString())

            mainActivity.runOnUiThread({
                mainActivity.temperature?.text = exponent.unaryPlus().toString()
            })

            var binaryString="1."

            for (i  in 3 downTo 1) {


                    //hexString+= String.format("%08B", byte)

                var byte=characteristic!!.value[i]

                binaryString+=String.format("%8s", Integer.toBinaryString(byte.toInt() and 0xFF )).replace(' ', '0')




            }


            Log.i("binary string", binaryString)




        }

    }

    override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
        super.onDescriptorWrite(gatt, descriptor, status)
        // service

        if(humidNotify==false) {
            var uuid = UUID.fromString("00000002-0000-0000-FDFD-FDFDFDFDFDFD")

            var service: BluetoothGattService? = gatt?.getService(uuid)

            val descriptor = service?.getCharacteristic(UUID.fromString("00002a6f-0000-1000-8000-00805f9b34fb"))?.getDescriptor(
                    UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
            descriptor?.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            gatt?.writeDescriptor(descriptor)

            //characteristic: temperature
            gatt?.setCharacteristicNotification(service?.getCharacteristic(UUID.fromString("00002a1c-0000-1000-8000-00805f9b34fb")), true)
            //temperatur
            //00002902-0000-1000-8000-00805f9b34fb
            //humidity
            //00002904-0000-1000-8000-00805f9b34fb
            //00002902-0000-1000-8000-00805f9b34fb

            humidNotify=true
        }

    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
        super.onCharacteristicChanged(gatt, characteristic)
        Log.i("weather app",characteristic.toString())
        gatt?.readCharacteristic(characteristic)


    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        super.onServicesDiscovered(gatt, status)

        Log.i("weather app", "hallo welt")

        // service
        var uuid = UUID.fromString("00000002-0000-0000-FDFD-FDFDFDFDFDFD")

        var service: BluetoothGattService? = gatt?.getService(uuid)


        val descriptor = service?.getCharacteristic(UUID.fromString("00002a6f-0000-1000-8000-00805f9b34fb"))?.getDescriptor(
                UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
        descriptor?.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
        gatt?.writeDescriptor(descriptor)


        // characteristic: humidity
        gatt?.setCharacteristicNotification(service?.getCharacteristic(UUID.fromString("00002a6f-0000-1000-8000-00805f9b34fb")),true)
        gatt?.readCharacteristic(service?.getCharacteristic(UUID.fromString("00002a6f-0000-1000-8000-00805f9b34fb")))




    }


}