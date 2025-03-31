package org.example.data.airport.model13;

import org.example.data.airport.controllers.AirportDAO;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Flight implements Serializable {
  private static int cur_fid = 0;
  private int fid;
  private LocalDateTime departure;
  private LocalDateTime arrival;
  private String depAirport;
  private String arrAirport;
  private Plane plane;
  private HashSet<Stewardess> stewardesses = new HashSet<Stewardess>();
  private HashSet<Passenger> passengers = new HashSet<>();
  private Pilot[] pilots;

  public Flight(int fid, LocalDateTime departure, LocalDateTime arrival, Plane p, String depAirport, String arrAirport, Pilot[] pilots) {
    this.fid = fid;
    this.departure = departure;
    this.arrAirport = arrAirport;
    this.depAirport = depAirport;
    this.arrival = arrival;
    this.pilots = pilots;
    this.plane = p;
  }
  public Flight(LocalDateTime departure, LocalDateTime arrival, Plane p, String depAirport, String arrAirport, Pilot[] pilots) {
    this(cur_fid++, departure, arrival, p, depAirport, arrAirport, pilots);
  }

  public boolean addStewardess(Stewardess s) { return stewardesses.add(s); }
  public static Flight fromQuery(ResultSet rs) throws SQLException {
    Plane p = AirportDAO.findPlane(rs.getInt(4));
    Pilot[] pilots = new Pilot[]{(Pilot) AirportDAO.findPerson(rs.getInt(7)), (Pilot)AirportDAO.findPerson(rs.getInt(8))};
    Flight ret = new Flight(rs.getInt("fid"), rs.getDate("departure").toLocalDate().atStartOfDay(), rs.getDate("arrival").toLocalDate().atStartOfDay(), p, rs.getString("airportDept"), rs.getString("airportArr"), pilots);
    for (int[] pair : AirportDAO.getAllParticipatesRel()) {
      if (pair[0] == ret.getFID()) {
        Person person = AirportDAO.findPerson(pair[1]);
        if (person instanceof Passenger) {
          ret.addPassenger((Passenger)person);
        } else ret.addStewardess((Stewardess) person);
      }
    }
    return ret;
  }

  public int getFID() {return fid;}
  public LocalDateTime getDeparture() { return departure; }
  public void setDeparture(LocalDateTime departure) {
    this.departure = departure;
  }
  public LocalDateTime getArrival() { return arrival; }
  public void setArrival(LocalDateTime arrival) { this.arrival = arrival; }
  public String getDepAirport() { return depAirport; }
  public void setDepAirport(String depAirport) {
    this.depAirport = depAirport;
  }
  public String getArrAirport() { return arrAirport; }
  public void setArrAirport(String arrAirport) {
    this.arrAirport = arrAirport;
  }
  public HashSet<Stewardess> getStewardesses() { return stewardesses; }
  public void setStewardesses(HashSet<Stewardess> stewardesses) {
    this.stewardesses = stewardesses;
  }
  public HashSet<Passenger> getPassengers() { return passengers; }
  public void setPassengers(HashSet<Passenger> passengers) {
    this.passengers = passengers;
  }
  public Pilot[] getPilots() { return pilots; }
  public void setPilots(Pilot[] pilots) { this.pilots = pilots; }

  public Plane getPlane() {
    return plane;
  }

  public void setPlane(Plane plane) {
    this.plane = plane;
  }

  public boolean addPassenger(Passenger p) {
    return this.passengers.add(p);
  }
}
