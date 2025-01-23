package org.example.data.airport;

import org.example.data.airport.model13.Employee;

import java.util.Comparator;

public class SalaryComparator implements Comparator<Employee> {

    @Override
    public int compare(Employee employee, Employee t1) {
        return (int)(employee.getSalary()-t1.getSalary());
    }
}
