package com.fonfon.noloss.ui.detail;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.fonfon.noloss.lib.BleService;
import com.fonfon.noloss.lib.CircleTransform;
import com.fonfon.noloss.lib.Device;
import com.fonfon.noloss.lib.SaveImageService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import io.realm.Realm;

public class DetailActivityViewModel implements OnMapReadyCallback {

    private static final int GALLERY_REQUEST = 2;
    private AppCompatActivity activity;
    public Device device;
    private DataListener dataListener;

    public ObservableField<String> name = new ObservableField<>("");
    public ObservableInt batteryLevel = new ObservableInt(0);
    public ObservableBoolean isAlarmed = new ObservableBoolean(false);
    public ObservableBoolean isConnected = new ObservableBoolean(false);

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String address = intent.getStringExtra(BleService.DEVICE_ADDRESS);
            byte battery = intent.getByteExtra(BleService.BATTERY_LEVEL, (byte) 0);
            switch (action) {
                case BleService.DEVICE_CONNECTED:
                    if (address.equals(device.getAddress())) {
                        isConnected.set(true);
                    }
                    break;
                case BleService.DEVICE_DISCONNECTED:
                    if (address.equals(device.getAddress())) {
                        isConnected.set(false);
                    }
                    break;
                case BleService.BATTERY_LEVEL_UPDATED:
                    batteryLevel.set(battery);
                    device.setBatteryLevel(battery);
                    break;
            }
        }
    };

    public DetailActivityViewModel(AppCompatActivity activity, String address, DataListener dataListener) {
        this.activity = activity;
        this.device = Realm.getDefaultInstance().where(Device.class).equalTo(Device.ADDRESS, address).findFirst();
        this.dataListener = dataListener;

        name.set(device.getName());
        batteryLevel.set(device.getBatteryLevel());
        isAlarmed.set(device.isAlarmed());
        isConnected.set(device.isConnected());
        dataListener.onImage(device.getBitmap());
        dataListener.onTitle(device.getName());
    }

    void resume() {
        IntentFilter intentFilter = new IntentFilter(BleService.DEVICE_CONNECTED);
        intentFilter.addAction(BleService.DEVICE_DISCONNECTED);
        intentFilter.addAction(BleService.BATTERY_LEVEL_UPDATED);
        activity.registerReceiver(receiver, intentFilter);
        activity.startService(
                new Intent(activity, BleService.class)
                        .setAction(BleService.CHECK_BATTERY)
        );
    }

    void pause() {
        activity.startService(
                new Intent(activity, BleService.class)
                        .setAction(BleService.STOP_SERVICE)
        );
        activity.unregisterReceiver(receiver);

        device.setBatteryLevel((byte) batteryLevel.get());
        device.setAlarmed(isAlarmed.get());
        device.setConnected(isConnected.get());
        Realm.getDefaultInstance().beginTransaction();
        device.setName(name.get());
        Realm.getDefaultInstance().commitTransaction();
    }

    public void imageClick(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        activity.startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST:
                    final Uri selectedImage = data.getData();
                    SaveImageService.start(activity, selectedImage, device.getAddress());
                    try {
                        final Bitmap bitmap = new CircleTransform()
                                .transform(
                                        MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selectedImage)
                                );
                        dataListener.onImage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(activity, "Не удалось загрузить изображение", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
    }

    public void alertClick(View view) {
        String action = device.isAlarmed() ? BleService.STOP_ALARM : BleService.START_ALARM;
        device.setAlarmed(!device.isAlarmed());
        isAlarmed.set(device.isAlarmed());
        activity.startService(
                new Intent(activity, BleService.class)
                        .setAction(action)
                        .putExtra(BleService.DEVICE_ADDRESS, device.getAddress())
        );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (device.getLatitude() != 0 && device.getLongitude() != 0) {
            LatLng latLng = new LatLng(device.getLatitude(), device.getLongitude());
            googleMap.addMarker(new MarkerOptions()
                    .title(device.getName())
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            );
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, googleMap.getMaxZoomLevel() - 5));
        }
    }

    public interface DataListener {
        void onImage(Bitmap image);

        void onTitle(String title);
    }
}