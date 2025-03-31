package org.example.data.airport.controllers;

import org.example.ctrl.ConnectionFactory;
import org.example.data.airport.model13.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class EmployeeDAO {
    private static final PreparedStatement createS, readAllS, readOneS, updateS, deleteS;
    static {
        Connection con = ConnectionFactory.getInstance().getConnection();
        try {
            createS = con.prepareStatement("INSERT INTO EMPLOYEES (ssnr, name, salary) VALUES (?,?,?)");
            readAllS = con.prepareStatement("SELECT * FROM EMPLOYEES");
            readOneS = con.prepareStatement("SELECT * FROM EMPLOYEES WHERE ssnr = ?");
            updateS = con.prepareStatement("UPDATE EMPLOYEES SET name = ?, salary = ? WHERE ssnr = ?");
            deleteS = con.prepareStatement("DELETE FROM EMPLOYEES WHERE ssnr = ?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * queries database for all employees
     * @return Set of Java representations of employee entries
     */
    public static Set<Employee> getAllEmployees() {
        Set<Employee> emps = null;
        try {
            emps = new HashSet<>();
            ResultSet databaseRes = readAllS.executeQuery();
            while (databaseRes.next()) {
                emps.add(Employee.fromQuery(databaseRes));
            }
        } catch (SQLException e) {
            System.err.println("While querying (all): " + e);
        }

        return emps;
    }

    /**
     * deletes an employee
     * @param ssnr ssnr of employee to be deleted
     * @return
     */
    public static boolean deleteEmployee(long ssnr) {
        boolean ret = false;
        try {
            deleteS.setLong(1, ssnr);
            deleteS.executeUpdate();
            ret = true;
        } catch (SQLException e) {
            System.err.println("While deleting: " + e);
        }
        return ret;
    }

    /**
     * inserts an employee into the database
     * @param e employee to add
     * @return true on success
     */
    public static boolean addEmployee(Employee e) {
        boolean ret = false;
        try {
            createS.setLong(1, e.getSSNR());
            createS.setString(2, e.getName());
            createS.setInt(3, e.getSalary());
            createS.executeUpdate();
            ret = true;
        } catch (SQLException ex) {
            System.err.println("While inserting: " + ex);
        }
        return ret;
    }

    /**
     * updates the employee entry on the database with the same SSNR
     * @param e employee object whose values will be written to the new entry
     * @return true on success
     */
    public static boolean updateEmployee(Employee e) {
        boolean ret = false;
        try {
            updateS.setString(1, e.getName());
            updateS.setInt(2, e.getSalary());
            updateS.setLong(3, e.getSSNR());
            updateS.executeUpdate();
            ret = true;
        } catch (SQLException ex) {
            System.err.println("While updating: " + ex);
        }
        return ret;
    }

    /**
     * queries database for employee with specified ssnr
     * @param ssnr ssnr of employee
     * @return Java repr of entry or null on error
     */
    public static Employee findEmployee(long ssnr) {
        Employee ret = null;
        try {
            readOneS.setLong(1, ssnr);
            ResultSet rs = readOneS.executeQuery();
            if (rs.next()) {
                ret = Employee.fromQuery(rs);
            }
        } catch (SQLException ex) {
            System.err.println("While querying (single): " + ex);
        }
        return ret;
    }
}
