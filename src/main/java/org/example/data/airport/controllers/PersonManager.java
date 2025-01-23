package org.example.data.airport.controllers;

import org.example.ctrl.Function;
import org.example.ctrl.ObjectStore;
import org.example.data.airport.model13.Person;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * manager class for the abstract Person object
 * this all-static impl generates a lot of duplication, yes. So, just rewrite it
 * in Rust :-)
 */
public enum PersonManager {
  instance;

  private static ObjectStore<Person> internal = new ObjectStore<>("Persons");
  private static final String FILENAME = "persons.obj";

  /**
   * creates a subset using the internal OS
   * @param asName subset name
   * @return subset
   * @param <T> type of the objects the subset will store (must obviously be a
   *     Person subclass)
   */
  public <T extends Person> ObjectStore<T> createSubset(String asName) {
    return new ObjectStore<>(internal, asName);
  }

  /**
   * add person to given OS
   * @param osName ObjectStore name
   * @param p person to be added
   * @return true if success, false otherwise
   */
  public boolean add(String osName, Person p) {
    return internal.add(osName, p);
  }

  /**
   * remove person from given OS
   * @param osName ObjectStore name
   * @param p person to be added
   * @return index of removed obj, -1 if fail
   **/
  public int remove(String osName, Person p) {
    return internal.remove(osName, p);
  }

  /**
   * populate internal OS using a persistent file
   * @return populated OS (kinda leaks the internal data, but sure)
   * warum warum warum is das im Manager
   */
  public ObjectStore<Person> load() {
    ObjectStore<Person> ret = ObjectStore.read(FILENAME);
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
  public Set<Person> select(String osName, Comparator cmp, Person p) {
    return internal.select(osName, cmp, p);
  }

  /**
   * select (depth search)
   * @param osName
   * @param cmp
   * @param p
   * @return
   */
  public Person selectMostSpecific(String osName, Comparator<Person> cmp, Person p) {
    return internal.selectMostGeneral(osName, cmp, p);
  }

  /**
   * select (breadth search)
   * @param osName
   * @param cmp
   * @param p
   * @return
   */
  public Person selectMostGeneral(String osName, Comparator<Person> cmp, Person p) {
    return internal.selectMostSpecific(osName, cmp, p);
  }

  public Stream<? extends Person> sort(Comparator<Person> cmp) {
    return internal.sort(cmp);
  }

  public Stream<? extends Person> sort(String osName, Comparator<? extends Person> cmp) {
    return internal.getOs(osName).sort(cmp);
  }

  public String showHierarchy() {
    return internal.showHierrachy();
  }

  public int size() {
    return internal.size();
  }
}
