package org.example.ctrl;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An object store manages a collection of objects
 * beeing compatible to a base type. An array
 * represents for example a sequence of elements having a
 * base type, ie array of int, array of String, etc.
 */
public class ObjectStore<T> implements Serializable {

  private HashSet<T> internal = new HashSet<>();
  private String canonicalName;

  private HashSet<ObjectStore> subsets = new HashSet<>();

  /**
   * Creates an object store for objects beeing
   * compatible with the given base type.
   */
  public ObjectStore(String canonName) { canonicalName = canonName; }

  public ObjectStore(ObjectStore superset, String canonName) {
    this(canonName);
    superset.addSubset(this);
  }

  private <S extends T> void addSubset(ObjectStore<S> subset) {
    subsets.add(subset);
  }

  /**
   * Adds o to this object store when o is compatible
   * and returns true when o could be added or false
   * otherwise.
   *
   * @param o ...
   * @return ...
   */
  public boolean add(T o) {
    return internal.add(o);
    // return this.baseType.isInstance(o) && internal.add(hashKey++, o);
  }

  /**
   * adds o to the OS with the name osName
   **/
  public boolean add(String osName, T o) {
    if (canonicalName.equals(osName)) {
      return add(o);
    } else {
      return subsets.stream().filter(sub -> sub.add(osName, o)).findAny().isPresent();
      /*for (ObjectStore os : this.subsets) {
        if (os.add(osName, o)) {
          return true;
        }
      }
      return false;*/
    }
  }

  /**
   * unpacks the collection and adds each element to this ObjectStore instance
   * @param col collection holding the elements to be added
   * @return
   */
  public boolean addAll(Collection<T> col) { return internal.addAll(col); }

  /**
   * Returns the number of elements within this
   * object store.
   *
   * @return
   */
  public int size() {
    //return internal.size() + subsets.stream().reduce(0, (os1, os2) -> os1.size() + os2.size());
    return (int) stream().count();
  }

  public int directSize() {
    return internal.size();
  }

  /**
   * @return the base type of this object store.
   */
  /*public T getBaseType() {
    return T;
  }*/

  /**
   * Removes all objects from this object store
   * which are equal to the given Object o which
   * has to be compatible to this object stores
   * base type. Return -1 when o is not compatible.
   *
   * @param o
   * @return
   */
  public int remove(T o) {
    int ret = -1;
    if (internal.remove(o)) {
      ret = 0;
    }
    return ret;
  }

  public ObjectStore getOs(String osName) {
    ObjectStore ret = null;
    if (canonicalName.equals(osName)) ret = this;
    if (ret == null) {
      Optional<ObjectStore> maybeOS = subsets.stream().filter(sub -> sub.canonicalName.equals(osName)).findAny();
      ret = maybeOS.isPresent() ? maybeOS.get() : null;
      /*Iterator<ObjectStore> it = subsets.iterator();
      while (it.hasNext() && ret == null) {
        ObjectStore os = it.next().getOs(osName);
        if (os != null) {
          ret = os;
        }
      }*/
    }
    return ret;
  }

  public int remove(String osName, T o) {
    if (canonicalName.equals(osName)) {
      return remove(o);
    } else {
      Optional<Integer> maybeInt = subsets.stream().map(sub -> sub.remove(osName, o)).filter(remRet -> remRet != -1).findAny();
      return maybeInt.orElse(-1);
      /*for (ObjectStore os : this.subsets) {
        int curRet = os.remove(osName, o);
        if (curRet != -1) {
          return curRet;
        }
      }
      return -1;*/
    }
  }

  /**
   * Returns true when the given object store os
   * is equal to this object store.
   *
   * @param os
   * @return
   */
  public boolean equal(ObjectStore os) {
    return this.internal.equals(os.internal);
  }

  private String makeLine(boolean isFirst) {
    return String.format("+--- %s (%d elements, %d explicit)\n", canonicalName,
                         size(), internal.size());
  }

  private void genIndent(StringBuilder ret, int tabs) {
    for (int i = 0; i < tabs; i++) {
      ret.append("|\t");
    }
  }

  private String showHierrachy(int tabs, boolean isFirst) {
    StringBuilder ret = new StringBuilder();
    genIndent(ret, tabs);
    ret.append(makeLine(isFirst));
    isFirst = true;

    for (ObjectStore os : subsets) {
      ret.append(os.showHierrachy(tabs + 1, isFirst));
      isFirst = false;
    }
    return ret.toString();
  }

  /**
   * generates hierarchy representation of this store and all of its subsets
   * @return a human-readable representation
   */
  public String showHierrachy() { return showHierrachy(0, true); }

  /**
   * calls function f for every object in this store (implicit and explicit
   * members)
   * @param f functional interface
   * @return number of times the function has modified an object
   */
  public int apply(Function<T> f) {
    return (int) stream().map(o -> f.apply(o)).filter(res -> res != null).count();
    /*int ret = 0;
    for (T el : internal) {
      if (f.apply(el) != null) {
        ret++;
      }
    }
    for (ObjectStore os : subsets) {
      ret += os.apply(f);
    }
    return ret;*/
  }

  /**
   * calls function f for every object in the store with the name osName (implicit and explicit
   * members)
   * @param f functional interface
   * @return number of times the function has modified an object
   */
  public int apply(String osName, Function<T> f) {
    ObjectStore os = getOs(osName);
    int ret = -1;
    if (os != null) ret = os.apply(f);
    return ret;
  }

  /**
   * helper enum for Comparator
   * members correspond to compareTo return values
   */
  public enum CompRes {
    LESS,
    EQUALS,
    GREATER;

    public boolean equalsCompareToResult(int i) {
      switch (this) {
      case LESS:
        return i < 0;
      case EQUALS:
        return i == 0;
      case GREATER:
        return i > 0;
      default:
        throw new IllegalStateException("BUG: unreachable");
      }
    }
  }

  public Set<T> select2(Comparator<T> cmp, T obj) {
    return this.getStream()
            .filter(o -> cmp.compare(o, obj) == 0)
            .collect(Collectors.toSet());
  }

  public Stream<T> getStream() {
    return Stream.concat(
            internal.stream(),
            subsets.stream().flatMap(ObjectStore::getStream)
    );
  }

  /**
   * returns a set of all objects, where cmp returns res
   * @param cmp Comparator object
   * @param obj object to compare against
   * @param res expected compare result
   * @return set of all objects, where cmp returns res
   */
  public Set<T> select(Comparator<T> cmp, T obj, CompRes res) {
    return (Set<T>) Stream.concat(
            internal.stream()
                    .filter(o -> res.equalsCompareToResult(cmp.compare(o, obj))),
            subsets.stream()
                    .flatMap(sub -> sub.select(cmp, obj, res).stream()))
            .collect(Collectors.toSet());
    /*Set<T> ret = new HashSet<>();

    for (T el : internal) {
      if (res.equalsCompareToResult(cmp.compare(el, obj))) {
        ret.add(el);
      }
    }
    for (ObjectStore os : this.subsets) {
      ret.addAll(os.select(cmp, obj, res));
    }
    return ret;*/
  }

  /**
   * returns a set of all objects from the store with the name 'osName', where cmp returns res
   * @param osName name of OS
   * @param cmp Comparator object
   * @param obj object to compare against
   * @param res expected compare result
   * @return set of all objects, where cmp returns res
   */
  public Set<T> select(String osName, Comparator<T> cmp, T obj, CompRes res) {
    Set<T> ret = null;
    ObjectStore os = getOs(osName);
    if (os != null) ret = os.select(cmp, obj, res);
    return ret;
  }

  /**
   * shortcut for select
   * @param osName name of OS
   * @param cmp
   * @param obj
   * @return all objects where cmp.compare(cmp, obj) == 0
   */
  public Set<T> select(String osName, Comparator<T> cmp, T obj) {
    return select(osName, cmp, obj, CompRes.EQUALS);
  }

  /**
   * shortcut for select
   * @param cmp
   * @param obj
   * @return all objects where cmp.compare(cmp, obj) == 0
   */
  public Set<T> select(Comparator<T> cmp, T obj) {
    return select(cmp, obj, CompRes.EQUALS);
  }

  private void addNElements(Comparator<T> cmp, T obj, int count, Set<T> ret) {
    internal.stream().takeWhile(o -> ret.size() < count).forEach(o -> ret.add(o));
    /*Iterator<T> it = internal.iterator();
    while (it.hasNext() && ret.size() < count) {
      T el = it.next();
      if (cmp.compare(el, obj) == 0) {
        ret.add(el);
      }
    }*/
  }

  /**
   * internal implementation for selectMostSpecific
   * This function receives an additional output pointer 'ret', which will be
   * used to store results
   * @param cmp
   * @param obj
   * @param count
   * @param ret output pointer
   */
  /*private void selectMostSpecific(Comparator<T> cmp, T obj, int count,
                                  Set<T> ret) {
    if (ret.size() < count) {
      Iterator<ObjectStore> subsetIt = subsets.iterator();
      while (subsetIt.hasNext() && ret.size() < count) {
        subsetIt.next().selectMostSpecific(cmp, obj, count, ret);
      }
      addNElements(cmp, obj, count, ret);
    }
  }*/

  /**
   * searches for the most specific objects, that match the criteria
   * depth search
   * @param cmp comparator
   * @param obj object to compare against
   * @param count number of elements to return
   * @return count elements, where cmp.compare(el, obj) == 0
   */
  public Set<T> selectMostSpecific(Comparator<T> cmp, T obj, int count) {
    return (Set<T>) subsets.stream().flatMap(sub -> sub.selectMostSpecific(cmp, obj, count).stream()).limit(count).collect(Collectors.toSet());
    //Set<T> ret = new HashSet<>(count);
    //selectMostSpecific(cmp, obj, count, ret);
    //return ret;
  }

  /**
   * searches for the most specific object, that match the criteria
   * depth search
   * @param cmp comparator
   * @param obj object to compare against
   * @return the first element found, where cmp.compare(el, obj) == 0
   */
  public T selectMostSpecific(Comparator<T> cmp, T obj) {
    Set<T> retSet = selectMostSpecific(cmp, obj, 1);
    T ret = null;
    if (retSet.size() == 1) {
      ret = retSet.iterator().next();
    }
    return ret;
  }

  /**
   * searches for the most specific object in OS named 'osName', that match the criteria
   * depth search
   * @param cmp comparator
   * @param obj object to compare against
   * @return the first element found, where cmp.compare(el, obj) == 0
   */
  public T selectMostSpecific(String osName, Comparator<T> cmp, T obj) {
    ObjectStore<T> os = getOs(osName);
    T ret = null;
    if (os != null) ret = os.selectMostSpecific(cmp, obj);
    return ret;
  }

  /**
   * internal impl for selectMostGeneral, receives an additional parameter
   * 'ret', which is an output pointer pointing to the only HashSet which holds
   * the matched objects depth search
   * @param cmp comparator
   * @param obj object to compare against
   * @param count number of elements to return
   * @param ret ouput pointer
   * @return count elements, where cmp.compare(el, obj) == 0
   */
  private void selectMostGeneral(Comparator<T> cmp, T obj, int count,
                                 Set<T> ret) {
    addNElements(cmp, obj, count, ret);
    if (count > ret.size()) {
      subsets.stream().takeWhile(sub -> ret.size() < count).forEach(sub -> sub.selectMostGeneral(cmp, obj, count, ret));
      /*Iterator<ObjectStore> itSubsets = subsets.iterator();
      while (itSubsets.hasNext() && ret.size() < count) {
        itSubsets.next().selectMostGeneral(cmp, obj, count, ret);
      }*/
    }
  }

  /**
   * searches for the most general objects, that match the criteria
   * depth search
   * @param cmp comparator
   * @param obj object to compare against
   * @param count number of elements to return
   * @return count elements, where cmp.compare(el, obj) == 0
   */
  public Set<T> selectMostGeneral(Comparator<T> cmp, T obj, int count) {
    Set<T> ret = internal.stream().filter(o -> cmp.compare(o, obj) == 0).collect(Collectors.toSet());
    subsets.stream().forEach(sub -> {
      ret.addAll(sub.selectMostGeneral(cmp, obj, count));
    });
    //selectMostGeneral(cmp, obj, count, ret);
    return ret;
  }

  /**
   * searches for the most general objects, that match the criteria
   * depth search
   * @param cmp comparator
   * @param obj object to compare against
   * @return elements, where cmp.compare(el, obj) == 0
   */
  public T selectMostGeneral(String osName, Comparator<T> cmp, T obj) {
    ObjectStore<T> os = getOs(osName);
    T ret = null;
    if (os != null) ret = os.selectMostGeneral(cmp, obj);
    return ret;
  }

  /**
   * searches for the most specific objects, that match the criteria
   * breadth search
   * @param cmp comparator
   * @param obj object to compare against
   * @return first element, where cmp.compare(el, obj) == 0
   */
  public T selectMostGeneral(Comparator<T> cmp, T obj) {
    Set<T> retSet = selectMostGeneral(cmp, obj, 1);
    T ret = null;
    if (retSet.size() == 1) {
      ret = retSet.iterator().next();
    }
    return ret;
  }

  private void addSelectedMembers(Comparator<T> cmp, T obj,
                                  ObjectStore<T> ret) {
    for (T el : internal) {
      if (cmp.compare(el, obj) == 0) {
        ret.add(el);
      }
    }
  }
  private void selectPreserving(Comparator<T> cmp, T obj,
                                ObjectStore<T> parent) {
    ObjectStore<T> ret = new ObjectStore<>(parent, this.canonicalName);
    internal.stream().filter(el -> cmp.compare(el, obj) == 0).forEach(el -> ret.add(el));
    subsets.stream().forEach(sub -> sub.selectPreserving(cmp, obj, ret));
    /*addSelectedMembers(cmp, obj, ret);
    for (ObjectStore subset : subsets) {
      subset.selectPreserving(cmp, obj, ret);
    }*/
  }
  public ObjectStore<T> selectPreserving(Comparator<T> cmp, T obj) {
    ObjectStore<T> ret = new ObjectStore<>(this.canonicalName);
    internal.stream().filter(el -> cmp.compare(el, obj) == 0).forEach(el -> ret.add(el));
    subsets.stream().forEach(sub -> sub.selectPreserving(cmp, obj, ret));
    /*addSelectedMembers(cmp, obj, ret);
    for (ObjectStore subset : subsets) {
      subset.selectPreserving(cmp, obj, ret);
    }*/
    return ret;
  }

  /**
   * @return this object store elements in form of an array.
   */
  public Object[] toArray() {
    return stream().toArray();
    /*Object[] ret = new Object[size()];
    int i = 0;
    for (Object o : internal) {
      ret[i] = o;
      i++;
    }
    for (ObjectStore os : subsets) {
      Object[] arr = os.toArray();
      for (int j = 0; j < arr.length; j++) {
        ret[i++] = arr[j];
      }
    }
    return ret;*/
  }

  /**
   * generic function for deserializing a serialized ObjectStore
   * all errors will be printed to stderr
   * @param filename file to read
   * @return deserialized ObjectStore, null if error
   * @param <T> type of ObjectStore contents
   */
  public static <T> ObjectStore<T> read(String filename) {
    ObjectStore<T> ret = null;
    try {
      ObjectInputStream ois =
          new ObjectInputStream(new FileInputStream(filename));
      ret = (ObjectStore<T>)ois.readObject(); // trust me bro
      ois.close();
      System.out.println("loaded " + ret.size() + " " + ret.canonicalName);
    } catch (IOException | ClassNotFoundException e) {
      System.err.println("Error: " + e);
    }
    return ret;
  }

  /**
   * serialize an ObjectStore and store it in filename
   * @param filename file to write OS to
   * @return true if success
   */
  public boolean write(String filename) {
    try {
      ObjectOutputStream oos =
          new ObjectOutputStream(new FileOutputStream(filename));
      System.out.println("writing " + size() + " " + canonicalName);
      oos.writeObject(this);
      oos.close();
      return true;
    } catch (IOException e) {
      System.err.println("Error: " + e);
      return false;
    }
  }

  public Stream<? extends T> sort(Comparator<T> cmp) {
    return stream().sorted(cmp);
  }

  public Stream<? extends T> stream() {
    return Stream.concat(internal.stream(), subsets.stream().parallel().flatMap(ObjectStore::stream));
  }
  private void streamList(List<Stream<T>> ret) {
    ret.add(internal.stream());
    subsets.forEach(sub -> sub.streamList(ret));
  }
  public List<Stream<T>> streamList() {
    List<Stream<T>> ret = new LinkedList<>();
    streamList(ret);
    return ret;
  }

  public Object[] toArrayDirect() {
    return internal.toArray();
    /*Object[] ret = new Object[directSize()];
    int i = 0;
    for (Object o : internal) {
      ret[i] = o;
      i++;
    }
    return ret;*/
  }
  
  public Stream<ObjectStore> streamOS() {
    return Stream.concat(Stream.of(this), subsets.stream());
  }
}
