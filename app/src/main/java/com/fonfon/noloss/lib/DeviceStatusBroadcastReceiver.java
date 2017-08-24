package com.fonfon.noloss.lib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fonfon.noloss.BleService;

public class DeviceStatusBroadcastReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(final Context context, final Intent intent) {
    if (intent != null) {
      final String address = intent.getStringExtra(BleService.DEVICE_ADDRESS);
      final String action = intent.getAction();
      if (action != null && address != null) {
//        Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
//          @Override
//          public void execute(Realm realm) {
//            DeviceDB device = realm.where(DeviceDB.class).equalTo(DeviceDB.ADDRESS, address).findFirst();
//            if (device != null) {
//              switch (action) {
//                case BleService.DEVICE_BUTTON_CLICKED:
//                  context.startService(new Intent(context, LocationChangeService.class)
//                      .putExtra(BleService.DEVICE_ADDRESS, address)
//                  );
//                  break;
//                case BleService.DEVICE_DISCONNECTED:
//                  if (App.getInstance().getVisibleAddress() == null) {
//                    String statusText = context.getResources().getString(R.string.status_disconnected);
//                    Bitmap bitmap = BitmapUtils.stringToBitMap(device.getImage());
//
//                    ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
//                        .notify(device.doHash(),
//                            new NotificationCompat.Builder(context)
//                                .setLargeIcon(bitmap)
//                                .setSmallIcon(R.drawable.ic_find_key)
//                                .setContentTitle(context.getString(R.string.app_name))
//                                .setVibrate(new long[]{1000, 1000})
//                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
//                                .setContentText(device.getName() + " " + statusText)
//                                .build());
//                  }
//                  break;
//              }
//            }
//          }
//        });
      }
    }
  }

  void lol() {

  }
}
