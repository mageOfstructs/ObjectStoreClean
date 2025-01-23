package org.example.data.airport.controllers;

import org.example.ctrl.Function;
import org.example.ctrl.ObjectStore;
import org.example.data.airport.model13.Plane;
import org.example.data.airport.model13.Plane;

import java.util.Comparator;
import java.util.Set;

/**
 * manager class for the abstract Plane object
 * this all-static impl generates a lot of duplication, yes. So, just rewrite it
 * in Rust :-)
 */
public enum PlaneManager {
  instance;
  private static ObjectStore<Plane> internal = new ObjectStore<>("Planes");
  private static final String FILENAME = "planes.obj";

  /**
   * creates a subset using the internal OS
   * @param asName subset name
   * @return subset
   * @param <T> type of the objects the subset will store (must obviously be a
   *     Plane subclass)
   */
  public <T extends Plane> ObjectStore<T> createSubset(String asName) {
    return new ObjectStore<>(internal, asName);
  }

  /**
   * add plane to given OS
   * @param osName ObjectStore name
   * @param p plane to be added
   * @return true if success, false otherwise
   */
  public boolean add(String osName, Plane p) { return internal.add(osName, p); }

  /**
   * remove plane from given OS
   * @param osName ObjectStore name
   * @param p plane to be added
   * @return index of removed obj, -1 if fail
   **/
  public int remove(String osName, Plane p) {
    return internal.remove(osName, p);
  }

  /**
   * populate internal OS using a persistent file
   * @return populated OS (kinda leaks the internal data, but sure)
   */
  public ObjectStore<Plane> load() {
    ObjectStore<Plane> ret = ObjectStore.read(FILENAME);
    if (ret != null)
      internal = ret;
    return internal;
  }

  /**
   * saves current OS state to a persistent file
   * @return true if success, false otherwise
   */
  public boolean save() { return internal.write(FILENAME); }

  /**
   * calls the function in f method on every elememt
   * @param f interface holding the function
   * @return number of objects modified
   */
  public int apply(String osName, Function f) {
    return internal.apply(osName, f);
  }

  /**
   * basic select
   * @param osName
   * @param cmp
   * @param p
   * @return
   */
  public Set<Plane> select(String osName, Comparator cmp, Plane p) {
    return internal.select(osName, cmp, p);
  }

  /**
   * select (depth search)
   * @param osName
   * @param cmp
   * @param p
   * @return
   */
  public Plane selectMostSpecific(String osName, Comparator<Plane> cmp, Plane p) {
    return internal.selectMostGeneral(osName, cmp, p);
  }

  /**
   * select (breadth search)
   * @param osName
   * @param cmp
   * @param p
   * @return
   */
  public Plane selectMostGeneral(String osName, Comparator<Plane> cmp, Plane p) {
    return internal.selectMostSpecific(osName, cmp, p);
  }

  public ObjectStore getInternal() {
    return internal;
  }
}
