package org.example.test;

import org.example.data.airport.controllers.EmployeeDAO;
import org.example.data.airport.model13.Employee;
import org.example.data.airport.model13.Person;
import org.junit.Test;

import static org.junit.Assert.*;

public class EmployeeDAOTests {
    @Test
    public void testReadAll() {
        assertEquals(5, EmployeeDAO.getAllEmployees().size());
    }

    @Test
    public void testCreateDelete() {
        Person.bumpPID(EmployeeDAO.getAllEmployees().size()+1);
        Employee e = new Employee("Ryan", 6000);
        assertTrue(EmployeeDAO.addEmployee(e));
        assertEquals(6, EmployeeDAO.getAllEmployees().size());

        assertTrue(EmployeeDAO.deleteEmployee(e.getSSNR()));
        assertEquals(5, EmployeeDAO.getAllEmployees().size());
    }

    @Test
    public void testFind() {
        assertNotNull(EmployeeDAO.findEmployee(5));
    }

    @Test
    public void testUpdate() {
        Employee e = EmployeeDAO.findEmployee(5);
        e.setSalary(0);
        assertTrue(EmployeeDAO.updateEmployee(e));
        assertEquals(0, EmployeeDAO.findEmployee(e.getSSNR()).getSalary());
    }
}
