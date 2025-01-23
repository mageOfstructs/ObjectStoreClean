package org.example.data.airport;

import java.util.Comparator;
import java.util.Set;
import org.example.ctrl.ObjectStore;
import org.example.data.airport.controllers.PersonManager;
import org.example.data.airport.controllers.PlaneManager;
import org.example.data.airport.model13.*;

public class Main {
  public static void main(String[] args) {
    PlaneManager plmSingleton = PlaneManager.instance;
    ObjectStore<Plane> planes = plmSingleton.load();
    plmSingleton.createSubset("Cargo Planes");
    plmSingleton.add("Cargo Planes", new CargoPlane("Boeing", 4500));
    plmSingleton.add("Cargo Planes", new CargoPlane("Not Boeing", 4500));
    plmSingleton.add("Cargo Planes", new CargoPlane("Clyde", 4500));

    plmSingleton.createSubset("Military Planes");
    plmSingleton.add("Military Planes", new MilitaryPlane("US", "1x Java Legacy Database"));
    plmSingleton.add("Military Planes", new MilitaryPlane("EU", "1x Holy Rust Macro"));
    plmSingleton.add("Military Planes", new MilitaryPlane("EU", "1x Unholy C Macro"));

    plmSingleton.createSubset("Passenger Planes");
    plmSingleton.add("Passenger Planes", new PassengerPlane("Boeing"));
    plmSingleton.add("Passenger Planes", new PassengerPlane("Dutch"));
    plmSingleton.add("Passenger Planes", new PassengerPlane("Lumpi Industries"));
    plmSingleton.save();

    /*for (Object p : plmSingleton.getInternal().select(new PlaneBrandComparator(), new PassengerPlane("Boeing"))) {
      System.out.println(((Plane)p).getBrand());
    }*/

    PersonManager pmSingleton = PersonManager.instance;
    //pmSingleton.load();
    String employeeSubsetName = "Employees";
    ObjectStore<Employee> eOS = pmSingleton.createSubset(employeeSubsetName);
    Employee susi = new Employee("Susi", 2500);
    pmSingleton.add(employeeSubsetName, susi);
    pmSingleton.add(employeeSubsetName, new Employee("Mark", 1500));
    pmSingleton.add(employeeSubsetName, new Employee("Lukas", 5500));
    pmSingleton.add(employeeSubsetName, new Employee("Howard", 5500));
    pmSingleton.add(employeeSubsetName, new Employee("Peter", 5500));
    pmSingleton.add(employeeSubsetName, new Employee("Peter", 5501));

    new ObjectStore<Stewardess>(eOS, "Pilots");
    Flight testFlight = new Flight(); // just need it for sorting
    Pilot p = new Pilot("Peter", 5600);
    p.addFlight("Test", testFlight);
    p.addFlight("Test2", testFlight);
    pmSingleton.add("Pilots", p);
    p = new Pilot("Linus", 5300);
    p.addFlight("Test3", testFlight);
    pmSingleton.add("Pilots", p);
    pmSingleton.add("Pilots", new Pilot("Astrid", 5300));

    new ObjectStore<Stewardess>(eOS, "Stewardesses");
    pmSingleton.add("Stewardesses", new Stewardess("Paul", 3000));

    pmSingleton.createSubset("Passengers");
    pmSingleton.add("Passengers", new Passenger("Mike Schmidt", 9999));
    pmSingleton.add("Passengers", new Passenger("W. Afton", 99));

    System.out.println(pmSingleton.showHierarchy());
    pmSingleton.save();

    // give Susi a raise
    pmSingleton.apply("Employees", new RaiseSusisSalary());
    Set<Person> susis = pmSingleton.select("Employees", new EmployeeNameComparator(),
                                           new Employee("Susi", -1));
    assert susis.size() == 1;
    assert ((Employee)susis.iterator().next()).getSalary() == 5400;
  }
}
