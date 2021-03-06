package com.fonfon.noloss.db;

import android.location.Location;

import com.fonfon.geohash.GeoHash;
import com.fonfon.noloss.lib.Device;

import static com.fonfon.noloss.lib.Device.ZERO_GEOHASH;

public final class DeviceDB {

  private Long _id;
  private String address;
  private String name;
  private String geoHash;
  private String image;

  public DeviceDB() {
  }

  public DeviceDB(String address, String name, String defaultImage, Location location) {
    this._id = Device.doHash(address);
    this.address = address;
    this.name = name;
    geoHash = location == null ? ZERO_GEOHASH : GeoHash.fromLocation(location, GeoHash.MAX_CHARACTER_PRECISION).toString();
    this.image = defaultImage;
  }

  public DeviceDB(Device device) {
    this._id = device.get_id();
    this.address = device.getAddress();
    this.name = device.getName();
    this.geoHash = device.getGeoHash();
    this.image = device.getImage();
  }

  public Long get_id() {
    return _id;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getGeoHash() {
    return geoHash;
  }

  public void setGeoHash(String geoHash) {
    this.geoHash = geoHash;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }
}
