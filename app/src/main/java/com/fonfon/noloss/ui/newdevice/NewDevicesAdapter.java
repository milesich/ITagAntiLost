package com.fonfon.noloss.ui.newdevice;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fonfon.noloss.R;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.HashMap;
import java.util.Set;

final class NewDevicesAdapter extends RecyclerView.Adapter<NewDevicesAdapter.Holder> {

  private final HashMap<String, String> devices = new HashMap<>();
  private final NewDevicesAdapter.Listener listener;

  NewDevicesAdapter(NewDevicesAdapter.Listener listener) {
    this.listener = listener;
  }

  @Override
  public NewDevicesAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new NewDevicesAdapter.Holder(
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_device, parent, false)
    );
  }

  @Override
  public void onBindViewHolder(Holder holder, int position) {
    Set<String> keySet = devices.keySet();
    String[] addresses = keySet.toArray(new String[keySet.size()]);
    holder.textName.setText(devices.get(addresses[position]));
    holder.textAddress.setText(addresses[position]);
  }

  @Override
  public void onViewRecycled(Holder holder) {
    holder.itemView.setOnClickListener(null);
    super.onViewRecycled(holder);
  }

  @Override
  public int getItemCount() {
    return devices.size();
  }

  void add(String address, String name) {
    devices.put(address, name);
    notifyDataSetChanged();
  }

  void clear() {
    devices.clear();
    notifyDataSetChanged();
  }

  class Holder extends RecyclerView.ViewHolder {

    final TextView textName;
    final TextView textAddress;

    Holder(View view) {
      super(view);
      textName = (TextView) view.findViewById(R.id.text_name);
      textAddress = (TextView) view.findViewById(R.id.text_address);

      RxView.clicks(view)
          .map(o -> getAdapterPosition())
          .filter(p -> p != RecyclerView.NO_POSITION)
          .subscribe(p -> {
            Set<String> keySet = devices.keySet();
            String[] addresses = keySet.toArray(new String[keySet.size()]);
            if(addresses.length > 0) {
              listener.onDevice(addresses[p], devices.get(addresses[p]));
            }
          });
    }

  }

  interface Listener {
    void onDevice(String address, String name);
  }
}

