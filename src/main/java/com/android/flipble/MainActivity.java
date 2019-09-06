package com.android.flipble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.flipble.logcat.Slog;
import com.android.flipble.util.BlePackage;
import com.android.flipble.util.DataUtils;
import com.android.flipble.util.HexUtil;
import com.android.flipble.util.Jugger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = MainActivity.class.getSimpleName();

    protected static int mUUIDsId = -1;
    public Jugger sJuggler = new Jugger(this);
    protected boolean mIsEnabled = true;
    protected byte[] mBufferRead = new byte[100];

    public static int getUUIDsId() {
        return mUUIDsId;
    }

    private Button open_BLE;
    private Button searchDevice;
    private TextView tvBtConnectStatus;
    private Button btnChangeTo4yuan;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private BluetoothDevice bluetoothDevice;
    public static BluetoothGatt mBluetoothGatt;//中央使用和处理数据
    public BluetoothGattCharacteristic mNotifyCharacteristic;

    private boolean mScanning;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    tvBtConnectStatus.setText("蓝牙已连接");
                    break;
                case 2:
                    tvBtConnectStatus.setText("蓝牙已断开");
                    break;
                    default:
                        break;
            }
        }
    };

    private UUID_Group[] sDefaultUUIDs = new UUID_Group[]{
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

    UUID_Group sBatteryUUID = new UUID_Group(
            "0000180f-0000-1000-8000-00805f9b34fb",
            "00002A19-0000-1000-8000-00805f9b34fb",
            "00000000-0000-1000-8000-00805f9b34fb",
            "00002902-0000-1000-8000-00805f9b34fb"
    );

    UUID_Group sTemperatureUUID = new UUID_Group(
            "00001809-0000-1000-8000-00805f9b34fb",
            "00002A1C-0000-1000-8000-00805f9b34fb",
            "00000000-0000-1000-8000-00805f9b34fb",
            "00002902-0000-1000-8000-00805f9b34fb"
    );

    UUID[] sDeviceInfoUUID = {
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


    public final int CalibrationChannel = 0;
    UUID_Group[] sOtherChannelUUIDs = new UUID_Group[]{
            //IMU Calibration
            new UUID_Group(
                    "0000f010-0000-1000-8000-00805f9b34fb",
                    "0000f011-0000-1000-8000-00805f9b34fb",
                    "0000f012-0000-1000-8000-00805f9b34fb",
                    "00002902-0000-1000-8000-00805f9b34fb"
            )
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        supportBluetooth();
        initView();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//蓝牙状态改变的广播
        filter.addAction(BluetoothDevice.ACTION_FOUND);//找到设备的广播
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//搜索完成的广播
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);//开始扫描的广播
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//状态改变
        registerReceiver(receiver, filter);
    }

    private void initView() {
        open_BLE = (Button) this.findViewById(R.id.open_bluetooth);
        tvBtConnectStatus= (TextView) this.findViewById(R.id.bt_connect_status);
        btnChangeTo4yuan= (Button) this.findViewById(R.id.switch_to_4yuan);
        btnChangeTo4yuan.setOnClickListener(this);
        open_BLE.setOnClickListener(this);

        searchDevice = (Button) this.findViewById(R.id.search_bluetooth);
        searchDevice.setOnClickListener(this);

        if (bluetoothEnable()) {
            open_BLE.setText("关闭蓝牙");
        } else {
            open_BLE.setText("打开蓝牙");
        }

    }

    // 判断是否支持蓝牙
    private boolean supportBluetooth() {
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            return false;
        }
        return true;
    }

    // 判断蓝牙是否开启
    private boolean bluetoothEnable() {
        return bluetoothAdapter.isEnabled();
    }

    private void searchBluetoothDevice() {
        Log.i(TAG,"search bluetooth device.");
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_bluetooth:
                if (supportBluetooth()) {
                    if (bluetoothEnable()) {
                        Toast.makeText(this, "关闭蓝牙中...", Toast.LENGTH_LONG).show();
                        bluetoothAdapter.disable();
                        open_BLE.setText("打开蓝牙");
                    } else {
                        Toast.makeText(this, "关闭开启中...", Toast.LENGTH_LONG).show();
                        bluetoothAdapter.enable();
                        open_BLE.setText("关闭蓝牙");
                    }
                } else {
                    Toast.makeText(this, "设备不支持蓝牙", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.search_bluetooth:
                Log.i(TAG, "click search bluetooth device.");
                searchBluetoothDevice();
                break;
            case R.id.switch_to_4yuan:
                writeData();
                break;
                default:
                    break;
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i(TAG,"device name:" + device.getName() + " ,address:" + device.getAddress() + " BondState:" + device.getBondState());
                if (device.getName() != null && device.getName().contains(BlePackage.targetDeviceName)) {
                    int bondState = device.getBondState();
                    Log.i(TAG,"已搜索到指定的" + BlePackage.targetDeviceName + "设备，开始自动去配对" + " bondState:" + bondState);
                    try {
                        if (bondState == BluetoothDevice.BOND_NONE) {
                            Method createBondMethod = device.getClass().getMethod("createBond");
                            createBondMethod.invoke(device);
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bluetoothDevice = bluetoothAdapter.getRemoteDevice(device.getAddress());
                                if (bluetoothDevice == null) {
                                    Slog.e("liu", "bluetoothDevice is null! error!");
                                    return;
                                }
                                mBluetoothGatt = bluetoothDevice.connectGatt(context, true, mGattCallback);
                            }
                        }, 3000);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                Log.i(TAG,"receiver ACTION_STATE_CHANGED");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i(TAG, "receiver discovery finished");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.i(TAG, "receiver discovery started");
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                Log.i(TAG, "receiver bind state change....");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        onClose();
    }

    protected BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i(TAG, "**** onConnectionStateChange ****");
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG,"**** STATE_CONNECTED ****");
                handler.sendEmptyMessage(1);
                boolean result = mBluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "**** STATE_DISCONNECTED ****");
                handler.sendEmptyMessage(2);
                onClose();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG,"**** onServicesDiscovered ****");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG,"**** GATT_SUCCESS ****");
                enableTXNotification(mIsEnabled);
            }
        }
        private static final String HEX = "0123456789abcdef";
        public String bytes2hex(byte[] bytes)
        {
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes)
            {
                // 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
                sb.append(HEX.charAt((b >> 4) & 0x0f));
                // 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
                sb.append(HEX.charAt(b & 0x0f));
            }
            return sb.toString();
        }

        /**
         * Callback triggered as a result of a remote characteristic notification.
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            UUID uuid = characteristic.getUuid();
            Log.i(TAG, "**** onCharacteristicChanged **** + uuid:" + uuid);
            if (uuid.compareTo(sDefaultUUIDs[mUUIDsId].characteristicRead) == 0) {
                //Slog.d("liu", "mUUIDsId is " + mUUIDsId);
                mBufferRead = characteristic.getValue();
                Log.e(TAG,"uuid=="+uuid+"，bytes2hex(mBufferRead)==="+bytes2hex(mBufferRead));
                DataUtils.getInstance().handleData(bytes2hex(mBufferRead));
                /**
                 * 0100025e4105664f06e27c00000e000000000000
                 * 01000267d5056db406f26d00000f000000000000
                 * 0100026f5505730d06fed2000010000000000000
                 * 0100027551057700070883000011000000000000
                 */
            } else if (uuid.compareTo(sBatteryUUID.characteristicRead) == 0) {
                Slog.d("liu", "sBatteryUUID");
                mBufferRead = characteristic.getValue();

            } else if (uuid.compareTo(sTemperatureUUID.characteristicRead) == 0) {
                Slog.d("liu", "sTemperatureUUID");
                mBufferRead = characteristic.getValue();

            } else if (uuid.compareTo(sOtherChannelUUIDs[CalibrationChannel].characteristicRead) == 0) {
                Slog.d("liu", "sOtherChannelUUIDs");
                mBufferRead = characteristic.getValue();
            }
        }

        /**
         * Callback reporting the result of a characteristic read operation.
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "**** onCharacteristicRead ****" + " ,status is " + status);
            UUID uuid = characteristic.getUuid();
            Log.i(TAG,"uuid value is " + uuid);
            int index = -1;
            for (int i = 0; i < sDeviceInfoUUID.length - 1; ++i) {
                if (uuid.compareTo(sDeviceInfoUUID[1 + i]) == 0) {
                    index = i;
                    break;
                }
            }
            Log.i(TAG,"index value is " + index);
            if (index != -1) {
                String ascii = new String(characteristic.getValue(), Charset.forName("ascii"));
                Log.i(TAG,"ascii is " + ascii);
            }
        }
        /**
         * 写操作的回调
         * */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.e(TAG,"onCharacteristicWrite()  status="+status+",value="+HexUtil.encodeHexStr(characteristic.getValue()));
        }
    };

    public void enableTXNotification(boolean value) {
        // Check
        if (mBluetoothGatt == null) {
            return;
        }

        // Find
        BluetoothGattCharacteristic readChar = null;
        Log.i(TAG,"init mUUIDsId value is " + mUUIDsId);
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
            Log.e("liu", "Read charateristic not found!MAC=" + mBluetoothGatt.getDevice().getAddress());
            return;
        }

        // Enable
        if (!value) {
            Log.e("liu", "set Characteristic Notification is false!");
        }
        if (mBluetoothGatt.setCharacteristicNotification(readChar, mIsEnabled = value) == false) {
            Log.e("liu", "set Characteristic Notification fail!");
        }

        BluetoothGattDescriptor descriptor = readChar.getDescriptor(sDefaultUUIDs[mUUIDsId].descriptorNotification);

        if (descriptor != null) {
            descriptor.setValue(mIsEnabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            boolean status = mBluetoothGatt.writeDescriptor(descriptor);
            if (status == false) {
                Log.e("liu", "write Descriptor fail!");
            }
        }


        // Device Information
        getDeviceInfo();
        // Device Information
        //sJuggler.delayCall(mReadDeviceInfosDelayed, 500);
        // Battery Service
        //sJuggler.delayCall(mEnableBatteryLevelNotificationDelayed, 500);
        // Temperature Service
        sJuggler.delayCall(mEnableTemperatureNotificationDelayed, 1000);//500
    }

    private void getDeviceInfo() {
        int mIndex = 0;
        BluetoothGattService srv = mBluetoothGatt.getService(sDeviceInfoUUID[0]);
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
            sJuggler.delayCall(mReadDeviceInfosDelayed, 1000);//50
        }
    }

    protected Runnable mReadDeviceInfosDelayed = new Runnable() {

        protected int mIndex = 0;

        @Override
        public void run() {
            if (mBluetoothGatt == null) {
                return;
            }
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
                sJuggler.delayCall(mReadDeviceInfosDelayed, 1000);//50
            }
        }
    };

    protected Runnable mEnableBatteryLevelNotificationDelayed = new Runnable() {
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

    protected Runnable mEnableTemperatureNotificationDelayed = new Runnable() {
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

    private void onClose() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
        }
        mBluetoothGatt = null;
    }

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
                Log.e(TAG, "getWriteCharacteristic characteristicWrite==="+characteristicWrite);
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
    private void writeData(){
       // BluetoothGattService service=mBluetoothGatt.getService(sDefaultUUIDs[1].getWriteCharacteristic());
        //BluetoothGattCharacteristic charaWrite=service.getCharacteristic(write_UUID_chara);
        BluetoothGattCharacteristic charaWrite=sDefaultUUIDs[1].getWriteCharacteristic(mBluetoothGatt);
        byte[] data = new byte[0];
        String content="0x20";//etWriteContent.getText().toString();
        if (!TextUtils.isEmpty(content)){
            data=HexUtil.hexStringToBytes(content);
        }
        Log.e(TAG, "writeData: length="+data.length);
        charaWrite.setValue(data);
        mBluetoothGatt.writeCharacteristic(charaWrite);
//        if (data.length>20){//数据大于个字节 分批次写入
//            Log.e(TAG, "writeData: length="+data.length);
//            int num=0;
//            if (data.length%20!=0){
//                num=data.length/20+1;
//            }else{
//                num=data.length/20;
//            }
//            for (int i=0;i<num;i++){
//                byte[] tempArr;
//                if (i==num-1){
//                    tempArr=new byte[data.length-i*20];
//                    System.arraycopy(data,i*20,tempArr,0,data.length-i*20);
//                }else{
//                    tempArr=new byte[20];
//                    System.arraycopy(data,i*20,tempArr,0,20);
//                }
//                charaWrite.setValue(tempArr);
//                mBluetoothGatt.writeCharacteristic(charaWrite);
//            }
//        }else{
//            Log.e(TAG, "write Characteristic...");
//
//        }
    }
}
