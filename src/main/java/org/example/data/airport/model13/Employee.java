package org.example.data.airport.model13;

import org.example.data.airport.frontends.ITermPrintable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Employee extends Person {
  protected long ssnr = pid;
  private int salary;

  public Employee(String name, int salary) {
    super.initProps(name);
    this.salary = salary;
  }

  private Employee(String name, int salary, long ssnr) {
    super.initProps(name);
    this.salary = salary;
    this.ssnr = ssnr;
  }

  public int getSalary() { return salary; }

  public void setSalary(int salary) { this.salary = salary; }

  public long getSSNR() { return this.ssnr; }

  public static Employee fromQuery(ResultSet rs) throws SQLException {
    return new Employee(rs.getString("name"), rs.getInt("salary"),
                        rs.getInt("ssnr"));
  }

  @Override
  public String toPrintable() {
    return ITermPrintable.concat(String.valueOf(this.ssnr), String.valueOf(this.salary));
  }
}
