package org.example.ctrl;

import java.util.Comparator;

public class Benchmark {
  static long size = 10000000, subsets = 1;
  public static class StrComp implements Comparator<String> {
    @Override
    public int compare(String s, String t1) {
      return s.compareTo(t1);
    }
  }

  private static ObjectStore<String> genSingleLayer() {
    ObjectStore<String> testOS = new ObjectStore<>("Strings");
    for (int i = 0; i < size; i++) {
      testOS.add(String.valueOf(i));
    }
    return testOS;
  }

  private static ObjectStore<String> genMultiple() {
    ObjectStore<String> testOS = new ObjectStore<>("Strings");
    for (int i = 0; i < subsets; i++) {
      ObjectStore<String> sub = new ObjectStore<>(testOS, "Sub" + i);
      for (int j = 0; j < size / subsets; j++) {
        sub.add(String.valueOf(j));
      }
    }
    return testOS;
  }

  public static void consumer(String s) {

  }
  public static void main(String[] args) {
    //System.out.println(testOS.showHierrachy());
    System.out.println("Starting benchmark...");
    ObjectStore<String> testOS = genMultiple();
    /*long startt = System.currentTimeMillis();
    int ret = testOS.select(new StrComp(), "9").size();
    System.out.println("Got " + (System.currentTimeMillis() - startt) + "ms");

    startt = System.currentTimeMillis();
    int ret2 = testOS.select(new StrComp(), "9").size();
    System.out.println("Got " + (System.currentTimeMillis() - startt) + "ms");

    assert ret == ret2;*/
    long startt = System.currentTimeMillis();
    testOS.stream().parallel().forEach(Benchmark::consumer);
    System.out.println("Got " + (System.currentTimeMillis() - startt) + "ms");
    startt = System.currentTimeMillis();
    testOS.streamList().stream().parallel().forEach(s -> s.forEach(Benchmark::consumer));
    System.out.println("Got " + (System.currentTimeMillis() - startt) + "ms");

    System.out.println("And singlethreaded:");
    startt = System.currentTimeMillis();
    testOS.stream().forEach(Benchmark::consumer);
    System.out.println("Got " + (System.currentTimeMillis() - startt) + "ms");
    startt = System.currentTimeMillis();
    testOS.streamList().stream().forEach(s -> s.forEach(Benchmark::consumer));
    System.out.println("Got " + (System.currentTimeMillis() - startt) + "ms");
  }
}
