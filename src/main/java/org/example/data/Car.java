package org.example.data;

/**
 * A car in our universe of discourse represents a
 * typical POJO (plain old java object), which is
 * an ordinary Java class having a default constructor,
 * getter and setter methods on private data fields.
 */
public class Car {
  // to be defined
  private String manufacturer;
  private String licensePlate;
  private long fin = id++;

  private static int id = 0;

  public Car(String manufacturer, String licensePlate) {
    this.manufacturer = manufacturer;
    this.licensePlate = licensePlate;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  @Override
  public String toString() {
    return "Car{manufacturer=" + manufacturer + ",licensePlate=" + licensePlate + "," + fin + "}";
  }

  public long getFin() {
      return this.fin;
  }

  public void setFin(long fin) {
    this.fin = fin;
  }
}
