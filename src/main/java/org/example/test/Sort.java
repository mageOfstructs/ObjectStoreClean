package org.example.test;

import org.example.ctrl.ObjectStore;
import org.example.data.airport.controllers.PersonManager;
import org.example.data.airport.model13.Employee;
import org.example.data.airport.model13.Person;
import org.example.data.airport.model13.Pilot;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

public class Sort {
    private final static Comparator<Employee> classComp = Comparator.comparing(e -> e.getClass().getName());

    private void testNameASCInternal(List<Employee> emps) {
        String lastName = null;
        for (Employee e : emps) {
            assertTrue(lastName == null || lastName.compareTo(e.getName()) <= 0);
            lastName = e.getName();
        }
    }
    @Test
    public void testNameASC() {
        List<Employee> emps = PersonManager.instance.load().getOs("Employees").sort(Comparator.comparing(Person::getName)).toList();
        testNameASCInternal(emps);
        for (Employee e : emps) {
            System.out.println(e.getName());
        }
    }

    @Test
    public void testNameAscSsnrDESC() {
        List<Employee> emps = PersonManager.instance.load().getOs("Employees")
                .sort(
                        Comparator.comparing(Employee::getName).thenComparing(Comparator.comparing(Employee::getSSNR).reversed()))
                .toList();
        String lastName = null;
        long lastSSNR = -1;
        for (Employee e : emps) {
            System.out.println(e.getName() + " " + e.getSSNR());
            assertTrue(lastName == null || lastName.compareTo(e.getName()) <= 0);
            assertTrue(lastSSNR == -1 || !lastName.equals(e.getName()) || lastSSNR - e.getSSNR() >= 0);
            lastName = e.getName();
            lastSSNR = e.getSSNR();
        }
    }

    @Test
    public void testClassAscNameDesc() {
        List<Employee> emps = PersonManager.instance.load().getOs("Employees").sort(classComp.thenComparing(Comparator.comparing(Employee::getName).reversed())).toList();
        String lastClass = null, lastName = null;
        for (Employee emp : emps) {
            assertTrue(lastClass == null || lastName.compareTo(emp.getClass().getName()) <= 0);
            assertTrue(lastName == null || !lastClass.equals(emp.getClass().getName()) || lastName.compareTo(emp.getName()) >= 0);
            lastClass = emp.getClass().getName();
            lastName = emp.getName();
        }
    }

    @Test
    public void testClassDescNameAscSsnrAsc() {
        List<Employee> emps = PersonManager.instance.load().getOs("Employees").sort(classComp.reversed().thenComparing(Employee::getName).thenComparing(Comparator.comparing(Employee::getSSNR).reversed())).toList();
        System.out.println(PersonManager.instance.showHierarchy());
        String lastClass = null, lastName = null;
        long lastSSNR = -1;
        for (Employee emp : emps) {
            System.out.println(emp.getClass().getName() + " " + emp.getName() + " " + emp.getSSNR());
            assertTrue(lastClass == null || lastClass.compareTo(emp.getClass().getName()) >= 0);
            assertTrue(lastName == null || !lastClass.equals(emp.getClass().getName()) || lastName.compareTo(emp.getName()) <= 0);
            assertTrue(lastSSNR == -1 || !lastName.equals(emp.getName()) || lastSSNR - emp.getSSNR() >= 0);
            lastClass = emp.getClass().getName();
            lastName = emp.getName();
            lastSSNR = emp.getSSNR();
        }
    }

    @Test
    public void testClassMembersCount() {
        Comparator<ObjectStore> countCmp = Comparator.comparing(ObjectStore::directSize);
        Stream<ObjectStore> sortedOSs = PersonManager.instance.load().getOs("Employees").streamOS().sorted(countCmp);
        sortedOSs.flatMap(ObjectStore::directStream).forEachOrdered(emp -> System.out.println(emp.getClass() + " " + ((Employee)emp).getName()));
        System.out.println(PersonManager.instance.showHierarchy());
    }

    @Test
    public void testPilotsNumOfFlightsDesc() {
        ObjectStore<Person> persons = PersonManager.instance.load();
        System.out.println(persons.showHierrachy());
        List<Pilot> pilots = persons.getOs("Pilots").sort(Comparator.comparing(Pilot::getNumberOfFlights).reversed()).toList();
        int lastNumOfFlights = -1;
        for (Pilot p : pilots) {
            System.out.println("Pilot " + p.getName() + ": " + p.getNumberOfFlights() + " flights");
            assertTrue(lastNumOfFlights == -1 || lastNumOfFlights - p.getNumberOfFlights() >= 0);
            lastNumOfFlights = p.getNumberOfFlights();
        }
    }
}
