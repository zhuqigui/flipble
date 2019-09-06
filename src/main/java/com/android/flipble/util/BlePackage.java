package com.android.flipble.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.android.flipble.logcat.Slog;

import java.lang.reflect.Method;

public class BlePackage {

    public static String TAG = BlePackage.class.getSimpleName();
    public static BluetoothAdapter bluetoothAdapter;

    public static String targetDeviceName = "X3C";

    // 检查蓝牙当前状态
    public static Boolean checkBluetoothAdapter() {
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (bluetoothAdapter.isEnabled()) {
            //startScan();
        }
        return bluetoothAdapter.isEnabled();
    }

    // 打开蓝牙
    public static boolean openBle() {
        if (!checkBluetoothAdapter()) {
            bluetoothAdapter.enable();
            startScan();
        }
        return bluetoothAdapter.isEnabled();
    }

    // 关闭蓝牙
    public static void closeBle() {
        if (checkBluetoothAdapter()) {
            bluetoothAdapter.disable();
        }
    }

    // 开始扫描蓝牙设备
    public static void startScan() {
        if (!checkBluetoothAdapter()) {
            Slog.d("liu", "Ble no open!");
            return;
        }
        bluetoothAdapter.startLeScan(leScanCallback);
        Slog.d("liu", "Ble no open, start len scan.");
    }

    public static BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.i(TAG, "device address is " + device.getAddress() + " , rssi is " + rssi);
            String address = device.getAddress();
            if (rssi < -80) {
                Log.i(TAG, "rssi < MinRssi");
                return;
            }
            if (getScanRecordUUID(scanRecord)) {
                BleStream.open();
            }
        }
    };

    // 扫描过滤设备
    protected static boolean getScanRecordUUID(byte[] scanRecord) {
        try {
            Class<?> ScanRecord = Class.forName("android.bluetooth.le.ScanRecord");
            Method parseFromBytes = ScanRecord.getMethod("parseFromBytes", byte[].class);
//            ScanRecord s = (ScanRecord) parseFromBytes.invoke(null, (Object) scanRecord);
//            if (s == null || s.getServiceUuids() == null) {
//                return false;
//            }
//            for (ParcelUuid id : s.getServiceUuids()) {
//                if (id.toString().startsWith("0000ff0")) {
//                    return true;
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
