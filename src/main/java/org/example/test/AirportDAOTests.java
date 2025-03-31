package org.example.test;

import org.example.data.airport.controllers.AirportDAO;
import org.example.data.airport.model13.*;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class AirportDAOTests {
    @Test
    public void testReadAll() {
        assertEquals(10, AirportDAO.getAllPersons().size());
        assertEquals(4, AirportDAO.getAllPlanes().size());
        assertEquals(3, AirportDAO.getAllFlights().size());
        assertEquals(5, AirportDAO.getAllParticipatesRel().size());
    }

    @Test
    public void testCreate() {
        Person.bumpPID(AirportDAO.getAllPersons().size()+1);
        Plane.bumpPID(AirportDAO.getAllPlanes().size()+1);
        Employee e = new Stewardess("Ryan", 6000);
        assertTrue(AirportDAO.addPerson(e));

        Pilot p1 = new Pilot("Max", 9);
        assertTrue(AirportDAO.addPerson(p1));
        Pilot p2 = new Pilot("Lukas", 900);
        assertTrue(AirportDAO.addPerson(p2));

        Plane p = new CargoPlane("Airline 1", 3000);
        assertTrue(AirportDAO.addPlane(p));

        Flight f = new Flight(AirportDAO.getAllFlights().size()+1, LocalDateTime.of(2025, 03, 13, 23, 23),
                LocalDateTime.of(2025, 3, 14, 3, 23), p, "asdf", "asdf2", new Pilot[]{p1, p2});
        assertTrue(AirportDAO.addFlight(f));

        assertTrue(AirportDAO.addParticipatesRel(f, e));

        //assertTrue(EmployeeDAO.deleteEmployee(e.getSSNR()));
        //assertEquals(5, AirportDAO.getAllPersons().size());
    }

    @Test
    public void testFind() {
        assertNotNull(AirportDAO.findFlight(1));
    }
}
