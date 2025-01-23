package org.example.data.airport.model13;

public class Employee extends Person {
    protected long ssnr;
    private long salary;

    public Employee(String name, long salary) {
        super.initProps(name);
        this.salary = salary;
        this.ssnr = (long) (Math.random()*9000. + 1000.);
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }

    public long getSSNR() {return this.ssnr;}
}
