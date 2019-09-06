//=============================================================================
//
// Copyright 2016 Ximmerse, LTD. All rights reserved.
//
//=============================================================================

package com.android.flipble.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import com.android.flipble.logcat.Slog;

import java.nio.charset.Charset;
import java.util.UUID;

class UUID_Group {
    public UUID service;
    public UUID characteristicRead;
    public UUID characteristicWrite;
    public UUID descriptorNotification;

    public UUID_Group(String service, String characteristicRead, String characteristicWrite, String descriptorNotification) {
        this.service = UUID.fromString(service);
        this.characteristicWrite = UUID.fromString(characteristicWrite);
        this.characteristicRead = UUID.fromString(characteristicRead);
        this.descriptorNotification = UUID.fromString(descriptorNotification);
    }

    public BluetoothGattCharacteristic getWriteCharacteristic(BluetoothGatt gatt) {
        BluetoothGattService srv = gatt.getService(service);
        if (srv != null) {
            return srv.getCharacteristic(characteristicWrite);
        }
        return null;
    }

    public BluetoothGattCharacteristic getReadCharacteristic(BluetoothGatt gatt) {
        if (gatt == null) {
            return null;
        }
        BluetoothGattService srv = gatt.getService(service);
        if (srv != null) {
            return srv.getCharacteristic(characteristicRead);
        }
        return null;
    }
}

public class BleStream {

    //#region Static

    public static final String TAG = BleStream.class.getSimpleName();

    protected static final byte[] ZERO = new byte[0];

    public static final int REQUEST_SELECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;

    static UUID_Group[] sDefaultUUIDs = new UUID_Group[]{
            //DK01
            new UUID_Group(
                    "0000faea-0000-1000-8000-00805f9b34fb",
                    "0000faec-0000-1000-8000-00805f9b34fb",
                    "0000faeb-0000-1000-8000-00805f9b34fb",
                    "00002902-0000-1000-8000-00805f9b34fb"
            ),
            //DK04
            new UUID_Group(
                    "0000f000-0000-1000-8000-00805f9b34fb",
                    "0000f001-0000-1000-8000-00805f9b34fb",
                    "0000f002-0000-1000-8000-00805f9b34fb",
                    "00002902-0000-1000-8000-00805f9b34fb"
            ),
            //daydream
            new UUID_Group(
                    "0000fe55-0000-1000-8000-00805f9b34fb",
                    "00000001-1000-1000-8000-00805f9b34fb",
                    "00000003-1000-1000-8000-00805f9b34fb",
                    "00002902-0000-1000-8000-00805f9b34fb"
            )
    };

    static UUID_Group sBatteryUUID = new UUID_Group(
            "0000180f-0000-1000-8000-00805f9b34fb",
            "00002A19-0000-1000-8000-00805f9b34fb",
            "00000000-0000-1000-8000-00805f9b34fb",
            "00002902-0000-1000-8000-00805f9b34fb"
    );

    static UUID_Group sTemperatureUUID = new UUID_Group(
            "00001809-0000-1000-8000-00805f9b34fb",
            "00002A1C-0000-1000-8000-00805f9b34fb",
            "00000000-0000-1000-8000-00805f9b34fb",
            "00002902-0000-1000-8000-00805f9b34fb"
    );

    static UUID[] sDeviceInfoUUID = {
            UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb")
            // Model Number String
            , UUID.fromString("00002A24-0000-1000-8000-00805f9b34fb")
            // Serial Number String
            , UUID.fromString("00002A25-0000-1000-8000-00805f9b34fb")
            // Firmware Revision String
            , UUID.fromString("00002A28-0000-1000-8000-00805f9b34fb")
            // Hardware Revision String
            , UUID.fromString("00002A27-0000-1000-8000-00805f9b34fb")
            // Software Revision String
            , UUID.fromString("00002A26-0000-1000-8000-00805f9b34fb")
            // Manufacturer Name String
            , UUID.fromString("00002A29-0000-1000-8000-00805f9b34fb")
    };


    public static final int CalibrationChannel = 0;
    static UUID_Group[] sOtherChannelUUIDs = new UUID_Group[]{
            //IMU Calibration
            new UUID_Group(
                    "0000f010-0000-1000-8000-00805f9b34fb",
                    "0000f011-0000-1000-8000-00805f9b34fb",
                    "0000f012-0000-1000-8000-00805f9b34fb",
                    "00002902-0000-1000-8000-00805f9b34fb"
            )
    };


    //#endregion Static

    //#region Fields

    // For Unity... -->

    protected static boolean mIsOpen = false;
    protected static String mAddress = "";


    // Bluetooth
    protected static boolean mIsEnabled = true;
    protected static BluetoothAdapter mBluetoothAdapter = null;
    protected static BluetoothManager mBluetoothManager = null;
    protected static BluetoothGatt mBluetoothGatt = null;
    protected static BluetoothDevice mDevice = null;

    protected static int mUUIDsId = -1;

    public static int getUUIDsId() {
        return mUUIDsId;
    }

    public static boolean slienceMode = false;
    protected static Context mContext;
    protected static byte[] mBufferRead = ZERO;
    protected static Jugger sJuggler = null;
    protected static String[] mDeviceInfos = {"null", "null", "null", "null", "null", "null"};

    //#endregion Fields

    public BleStream(Context context) {
        mContext = context;
        sJuggler = new Jugger(mContext);

    }

    //#region IStreamable

    public static void showDeviceList() {
		/*
		Intent newIntent = new Intent(mContext, DeviceListActivity.class);
		newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//
		DeviceListActivity.setOnSelectDeviceListener(mSelectDeviceListener);
		mContext.startActivity(newIntent);
	}

	public DeviceListActivity.OnSelectDevice mSelectDeviceListener=new DeviceListActivity.OnSelectDevice(){
		@Override
		public void onSelectDevice(String address) {
			open(address);
		}
		*/
    }

    ;


    public static void open() {
        open(mAddress);
    }

    public static void open(String deviceAddress) {
        if (mBluetoothAdapter == null) {
            initBleSerialPort();
        }

        if (deviceAddress != null && !deviceAddress.equals("")) {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
            int connectionState = mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT);

            if (connectionState != BluetoothProfile.STATE_DISCONNECTED) {
                Log.e(TAG, "NOT ...BluetoothProfile.STATE_DISCONNECTED....");
                return;

            }

            if (BluetoothAdapter.checkBluetoothAddress(deviceAddress)) {
                mDevice = mBluetoothAdapter.getRemoteDevice(mAddress = deviceAddress);
                mBluetoothGatt = mDevice.connectGatt(mContext, false, mGattCallback);

                //TODO Set Device State is OPENING;
            } else if (!slienceMode) {
                showDeviceList();
            }
        }
    }

    public void close() {
        if (mDevice != null) {
            onClose();
        }
    }

    protected static void onClose() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();//disconnect();//
        }
        // Clean up
        mBluetoothGatt = null;
        mDevice = null;
        //
    }


    public int write(byte[] value, int offset, int count) {
        if (offset <= 0) {
            if (!writeRXCharacteristic(value)) {
                Log.e(TAG, "write value error");
            }
        } else {
            byte[] tmpBuffer = new byte[count];
            // public static void arraycopy(Object src, int srcPos, Object dest, int destPos, int length) ;
            //                src    pos   dest    pos  len
            System.arraycopy(value, offset, tmpBuffer, 0, count);
            if (!writeRXCharacteristic(tmpBuffer)) {
                Log.e(TAG, "write value2 error");
            }
        }
        return count;//???
    }


    //#endregion IStreamable

    //#region Bluetooth

    public static int initBleSerialPort() {
        mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager == null) {
            return -1;
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return -1;
        }

        return 0;
    }


    protected static BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i(TAG, "**** onConnectionStateChange ****");
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                boolean result = mBluetoothGatt.discoverServices();
                Log.e(TAG, "Attempting to start service discovery:" + result);

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.e(TAG, mAddress + " is Disconnected by system...");
                onClose();
                Log.e(TAG, "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG,"**** onServicesDiscovered ****");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "mBluetoothGatt = " + mBluetoothGatt);
                // VIP
                enableTXNotification(mIsEnabled);
                // When mIsOpen is true,it means all functions of stream is ready.
                mIsOpen = true;
                //TODO  set devcie state is connected
            } else {
                Log.e(TAG, "onServicesDiscovered received: " + status);
            }
        }

        /** Callback triggered as a result of a remote characteristic notification.*/
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "**** onCharacteristicChanged ****");
            UUID uuid = characteristic.getUuid();
            if (uuid.compareTo(sDefaultUUIDs[mUUIDsId].characteristicRead) == 0) {
                mBufferRead = characteristic.getValue();
                //TODO 3DOF��button�� event decoder

            } else if (uuid.compareTo(sBatteryUUID.characteristicRead) == 0) {
                mBufferRead = characteristic.getValue();
                //TODO Battery event decoder

            } else if (uuid.compareTo(sTemperatureUUID.characteristicRead) == 0) {
                mBufferRead = characteristic.getValue();
                //TODO Temperature event, Filp not support

            } else if (uuid.compareTo(sOtherChannelUUIDs[CalibrationChannel].characteristicRead) == 0) {
                mBufferRead = characteristic.getValue();
                //TODO Calibration,Filp not support
            }
        }

        /** Callback reporting the result of a characteristic read operation.*/
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "**** onCharacteristicRead ****");
            if (status == 0) {
                //
                UUID uuid = characteristic.getUuid();
                int index = -1;
                for (int i = 0, imax = sDeviceInfoUUID.length - 1; i < imax; ++i) {
                    if (uuid.compareTo(sDeviceInfoUUID[1 + i]) == 0) {
                        index = i;
                        break;
                    }
                }
                //
                if (index != -1) {
                    mDeviceInfos[index] = new String(characteristic.getValue(), Charset.forName("ascii"));
                    //LogCatHelper.LOGI(TAG,String.format("onCharacteristicRead() UUID:%s Value:%s",sDeviceInfoUUID[1+index].toString(),mDeviceInfos[index]));
                    // Invoke event.
//					if(index==((sDeviceInfoUUID.length-1)-1)&&mDeviceInfoReadListener!=null){
//						mDeviceInfoReadListener.run();
//					}
                }
            }
        }

    };

    protected static Runnable mReadDeviceInfosDelayed = new Runnable() {
        protected int mIndex = 0;

        @Override
        public void run() {
            //
            if (mBluetoothGatt == null) {
                return;
            }
            //
            BluetoothGattService srv = mBluetoothGatt.getService(sDeviceInfoUUID[0]);
            if (srv == null) {
                return;
            }
            BluetoothGattCharacteristic chr = srv.getCharacteristic(sDeviceInfoUUID[1 + mIndex]);
            if (chr != null) {
                if (mBluetoothGatt.readCharacteristic(chr)) {
                    ++mIndex;//Next one
                }
            } else {
                ++mIndex;//Next one
            }
            if (1 + mIndex >= sDeviceInfoUUID.length) {
                mIndex = 0;
            } else {
                sJuggler.delayCall(mReadDeviceInfosDelayed, 50);
            }
        }
    };

    protected static Runnable mEnableBatteryLevelNotificationDelayed = new Runnable() {
        @Override
        public void run() {
            if (mBluetoothGatt != null) {
                BluetoothGattCharacteristic readCharB = sBatteryUUID.getReadCharacteristic(mBluetoothGatt);
                if (readCharB != null) {
                    mBluetoothGatt.setCharacteristicNotification(readCharB, mIsEnabled);
                    try {
                        BluetoothGattDescriptor descriptorB = readCharB.getDescriptors().get(0);
                        if (descriptorB != null) {
                            descriptorB.setValue(mIsEnabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                            if (!mBluetoothGatt.writeDescriptor(descriptorB)) {
                                sJuggler.delayCall(mEnableBatteryLevelNotificationDelayed, 50);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    protected static Runnable mEnableTemperatureNotificationDelayed = new Runnable() {
        @Override
        public void run() {
            if (mBluetoothGatt != null) {
                BluetoothGattCharacteristic readCharT = sTemperatureUUID.getReadCharacteristic(mBluetoothGatt);
                if (readCharT != null) {
                    mBluetoothGatt.setCharacteristicNotification(readCharT, mIsEnabled);
                    try {
                        BluetoothGattDescriptor descriptorT = readCharT.getDescriptors().get(0);
                        if (descriptorT != null) {
                            descriptorT.setValue(mIsEnabled ? BluetoothGattDescriptor.ENABLE_INDICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                            if (!mBluetoothGatt.writeDescriptor(descriptorT)) {
                                sJuggler.delayCall(mEnableTemperatureNotificationDelayed, 50);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    public static void enableTXNotification(boolean value) {
        // Check
        if (mBluetoothGatt == null) {
            return;
        }

        // Find
        BluetoothGattCharacteristic readChar = null;
        if (mUUIDsId == -1) {
            int i = 0;
            while (i < (sDefaultUUIDs.length - 1)) {
                readChar = sDefaultUUIDs[i].getReadCharacteristic(mBluetoothGatt);
                if (readChar != null) {
                    mUUIDsId = i;
                    break;
                }
                i++;
            }
        } else {
            readChar = sDefaultUUIDs[mUUIDsId].getReadCharacteristic(mBluetoothGatt);
        }
        if (readChar == null) {
            Log.e(TAG, "Read charateristic not found!MAC=" + mBluetoothGatt.getDevice().getAddress());
            return;
        }

        // Enable
        if (!value) {
            Log.e(TAG, "set Characteristic Notification is false!");
        }
        if (mBluetoothGatt.setCharacteristicNotification(readChar, mIsEnabled = value) == false) {
            Log.e(TAG, "set Characteristic Notification fail!");
        }
        //
        BluetoothGattDescriptor descriptor = readChar.getDescriptor(sDefaultUUIDs[mUUIDsId].descriptorNotification);

        if (descriptor != null) {
            descriptor.setValue(mIsEnabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            boolean status = mBluetoothGatt.writeDescriptor(descriptor);
            if (status == false) {
                Log.e(TAG, "write Descriptor fail!");
            }
        }
        // Device Information
        sJuggler.delayCall(mReadDeviceInfosDelayed, 500);
        // Battery Service
        sJuggler.delayCall(mEnableBatteryLevelNotificationDelayed, 2000);
        // Temperature Service
        sJuggler.delayCall(mEnableTemperatureNotificationDelayed, 2500);
    }


    protected Runnable mCalibrationDelayed = new Runnable() {
        @Override
        public void run() {
            BluetoothGattCharacteristic writeChar = null;
            writeChar = sOtherChannelUUIDs[CalibrationChannel].getWriteCharacteristic(mBluetoothGatt);
            if (writeChar == null) {
                Log.e(TAG, "sCalibrationChannel write charateristic not found!");
                return;
            }
            byte[] value = {(byte) 0xFE, 0x58, 0x69, 0x6D, 0x6D,
                    0x65, 0x72, 0x73, 0x65, 0x2E
                    ,


                    0x63, 0x6F, 0x6D, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00};
            writeChar.setValue(value);
            if (!mBluetoothGatt.writeCharacteristic(writeChar)) {
                Log.e(TAG, "sCalibrationChannel write charateristic fail!");
            }
        }
    };

    public boolean SetNotification(int channel, boolean enalbe) {

        // Check
        if (mBluetoothGatt == null) {
            return false;
        }
        if (channel >= sOtherChannelUUIDs.length) {
            Log.e(TAG, "[SetNotification]length is too long");
            return false;
        }
        // Find
        BluetoothGattCharacteristic readChar = null;
        readChar = sOtherChannelUUIDs[channel].getReadCharacteristic(mBluetoothGatt);

        if (readChar == null) {
            Log.e(TAG, "[SetNotification] Read charateristic not found!");
            return false;
        }

        if (mBluetoothGatt.setCharacteristicNotification(readChar, enalbe) == false) {
            Log.e(TAG, "[SetNotificationset] Characteristic Notification fail!");
        }
        //
        BluetoothGattDescriptor descriptor = readChar.getDescriptor(sOtherChannelUUIDs[channel].descriptorNotification);

        if (descriptor != null) {
            descriptor.setValue(enalbe ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            boolean status = mBluetoothGatt.writeDescriptor(descriptor);
            if (status == false) {
                Log.e(TAG, "[SetNotificationset]write Descriptor fail!");
            }

            if (enalbe && (channel == CalibrationChannel)) { //calibration channel
                sJuggler.delayCall(mCalibrationDelayed, 1000);
            }
        }


        return true;
    }

    public boolean writeRXCharacteristic(byte[] value) {
        // Check
        if (mBluetoothGatt == null) {
            Log.e(TAG, "mBluetoothGatt null!");
            return false;
        }

        // Find
        BluetoothGattCharacteristic writeChar = null;
        if (mUUIDsId == -1) {

        } else {
            writeChar = sDefaultUUIDs[mUUIDsId].getWriteCharacteristic(mBluetoothGatt);
        }
        if (writeChar == null) {
            Log.e(TAG, "write charateristic not found!");
            return false;
        }

        // Write
        writeChar.setValue(value);
        return mBluetoothGatt.writeCharacteristic(writeChar);
    }

    //#endregion Bluetooth

    //#region Properties

    public String getDeviceName() {
        if (this.mDevice == null) {
            return null;
        }
        if (this.mDevice.getName() == null) {
            return null;
        }
        return this.mDevice.getName();
    }

    public String getModelNumberString() {
        return mDeviceInfos[0];
    }

    public String getSerialNumberString() {
        return mDeviceInfos[1];
    }

    public String getFirmwareRevisionString() {
        Log.i(TAG, "XController Firmware Version:" + mDeviceInfos[2]);
        return mDeviceInfos[2];
    }

    public String getHardwareRevisionString() {
        Log.i(TAG, "XController Hardware Version:" + mDeviceInfos[3]);
        return mDeviceInfos[3];
    }

    public String getSoftwareRevisionString() {
        return mDeviceInfos[4];
    }

    public String getManufacturerNameString() {
        return mDeviceInfos[5];
    }


}

