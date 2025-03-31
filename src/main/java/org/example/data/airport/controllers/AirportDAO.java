package org.example.data.airport.controllers;

import oracle.jdbc.proxy.annotation.Pre;
import org.example.ctrl.ConnectionFactory;
import org.example.data.airport.model13.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AirportDAO {
  private static final String[] TABLES = new String[] {"person", "plane", "flight", "participates"};
  private static final String[] ID_COLS = new String[] {"pid", "pid", "fid", null};
  private enum StatementType { CREATE, READA, READO, UPDATE, DELETE }
  private static final PreparedStatement[] personStatements = new PreparedStatement[5];
  private static final PreparedStatement[] planeStatements = new PreparedStatement[5];
  private static final PreparedStatement[] flightStatements = new PreparedStatement[5];
  private static final PreparedStatement[] participatesStatements = new PreparedStatement[5];
  private static final PreparedStatement[][] statements =
      new PreparedStatement[][] {personStatements, planeStatements,
                                 flightStatements, participatesStatements};
  static {
    Connection con = ConnectionFactory.getInstance().getConnection();
    try {
      for (int i = 0; i < TABLES.length; i++) {
          statements[i][StatementType.READA.ordinal()] =
              con.prepareStatement("SELECT * FROM " + TABLES[i]);
          if (ID_COLS[i] != null) {
            statements[i][StatementType.READO.ordinal()] =
                con.prepareStatement("SELECT * FROM " + TABLES[i] + " WHERE " + ID_COLS[i] + " = ?");
            statements[i][StatementType.DELETE.ordinal()] =
                con.prepareStatement("DELETE FROM " + TABLES[i] + " WHERE " + ID_COLS[i] + " = ?");
        }
      }
      personStatements[StatementType.CREATE.ordinal()] =
          con.prepareStatement("INSERT INTO person VALUES (personSeq.nextval,?,?,?,?)");
      personStatements[StatementType.UPDATE.ordinal()] =
          con.prepareStatement("UPDATE person SET salary = ?, name = "
                               + "?, bagWeight = ? WHERE pid = ?");

      planeStatements[StatementType.CREATE.ordinal()] =
          con.prepareStatement("INSERT INTO plane VALUES (planeSeq.nextval,?,?,?,?)");
      //planeStatements[StatementType.UPDATE.ordinal()] = con.prepareStatement("UPDATE Plane SET brand = ?, weight = ?, arsenal = ? WHERE pid = ?");

      flightStatements[StatementType.CREATE.ordinal()] =
          con.prepareStatement("INSERT INTO flight VALUES (flightSeq.nextval,?,?,?,?,?,?,?)");
      flightStatements[StatementType.UPDATE.ordinal()] = con.prepareStatement(
          "UPDATE flight SET departure = ?, arrival = ?, planeId = ?, "
          + "airportDept = ?, airportArr = ?, pilotPassportNr = ?, "
          + "copilotPassportNr = ?");
      participatesStatements[StatementType.READO.ordinal()] = con.prepareStatement("SELECT * FROM participates WHERE flightId = ? AND passengerPassport = ?");
      participatesStatements[StatementType.CREATE.ordinal()] = con.prepareStatement("INSERT INTO participates VALUES (?,?)");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * queries database for all persons
   * @return Set of Java representations of Person entries
   */
  public static Set<Person> getAllPersons() {
    Set<Person> persons = null;
    try {
      persons = new HashSet<>();
      ResultSet rs =
          personStatements[StatementType.READA.ordinal()].executeQuery();
      while (rs.next()) {
        persons.add(Person.fromQuery(rs));
      }
    } catch (SQLException e) {
      System.err.println("While querying (all): " + e);
    }

    return persons;
  }

  /**
   * deletes a person
   * @param ssnr ssnr of Person to be deleted
   * @return true if success
   */
  public static boolean deletePerson(long ssnr) {
    boolean ret = false;
    try {
      PreparedStatement deleteS =
          personStatements[StatementType.DELETE.ordinal()];
      deleteS.setLong(1, ssnr);
      ret = deleteS.executeUpdate() == 1;
    } catch (SQLException e) {
      System.err.println("While deleting: " + e);
    }
    return ret;
  }

  /**
   * inserts a Person into the database
   * @param p Person to add
   * @return true on success
   */
  public static boolean addPerson(Person p) {
    boolean ret = false;
    try {
      int pType = Person.getDiscriminationIndex(p);
      //personStatements[StatementType.CREATE.ordinal()].setLong(1, p.getPID());

      if (pType < 3)
        personStatements[StatementType.CREATE.ordinal()].setInt(
            1, ((Employee)p).getSalary());
      else
        personStatements[StatementType.CREATE.ordinal()].setNull(1,
                                                                 Types.NUMERIC);

      personStatements[StatementType.CREATE.ordinal()].setString(2,
                                                                 p.getName());
      personStatements[StatementType.CREATE.ordinal()].setInt(3, pType);

      if (pType == 3)
        personStatements[StatementType.CREATE.ordinal()].setInt(
            4, ((Passenger)p).getBagWeight());
      else
        personStatements[StatementType.CREATE.ordinal()].setNull(4,
                                                                 Types.NUMERIC);
      personStatements[StatementType.CREATE.ordinal()].executeUpdate();
      ret = true;
    } catch (SQLException ex) {
      System.err.println("While inserting: " + ex);
    }
    return ret;
  }

  /**
   * updates the Person entry on the database with the same SSNR
   * @param p Person object whose values will overwrite the entry
   * @return true on success
   */
  public static boolean updatePerson(Person p) {
    boolean ret = false;
    try {
      int pType = Person.getDiscriminationIndex(p);

      if (pType < 3)
        personStatements[StatementType.UPDATE.ordinal()].setInt(
            1, ((Employee)p).getSalary());
      else
        personStatements[StatementType.UPDATE.ordinal()].setNull(1,
                                                                 Types.NUMERIC);

      personStatements[StatementType.UPDATE.ordinal()].setString(2,
                                                                 p.getName());

      if (pType == 3)
        personStatements[StatementType.UPDATE.ordinal()].setInt(
            3, ((Passenger)p).getBagWeight());
      else
        personStatements[StatementType.UPDATE.ordinal()].setNull(3,
                                                                 Types.NUMERIC);

      personStatements[StatementType.UPDATE.ordinal()].setLong(4, p.getPID());

      personStatements[StatementType.UPDATE.ordinal()].executeUpdate();
      ret = true;
    } catch (SQLException ex) {
      System.err.println("While updating: " + ex);
    }
    return ret;
  }

  /**
   * queries database for Person with specified ssnr
   * @param ssnr ssnr of Person
   * @return Java repr of entry or null on error
   */
  public static Person findPerson(long ssnr) {
    Person ret = null;
    try {
      personStatements[StatementType.READO.ordinal()].setLong(1, ssnr);
      ResultSet rs =
          personStatements[StatementType.READO.ordinal()].executeQuery();
      if (rs.next()) {
        ret = Person.fromQuery(rs);
      }
    } catch (SQLException ex) {
      System.err.println("While querying (single): " + ex);
    }
    return ret;
  }

  /**
   * queries database for all Plane
   * @return Set of Java representations of Plane entries
   */
  public static Set<Plane> getAllPlanes() {
    Set<Plane> emps = null;
    try {
      emps = new HashSet<>();
      ResultSet databaseRes =
          planeStatements[StatementType.READA.ordinal()].executeQuery();
      while (databaseRes.next()) {
        emps.add(Plane.fromQuery(databaseRes));
      }
    } catch (SQLException e) {
      System.err.println("While querying (all): " + e);
    }

    return emps;
  }

  /**
   * deletes a Plane
   * @param ssnr ssnr of Plane to be deleted
   * @return true on success
   */
  public static boolean deletePlane(long ssnr) {
    boolean ret = false;
    try {
      planeStatements[StatementType.DELETE.ordinal()].setLong(1, ssnr);
      planeStatements[StatementType.DELETE.ordinal()].executeUpdate();
      ret = true;
    } catch (SQLException e) {
      System.err.println("While deleting: " + e);
    }
    return ret;
  }

  /**
   * inserts an Plane into the database
   * @param p Plane to add
   * @return true on success
   */
  public static boolean addPlane(Plane p) {
    boolean ret = false;
    try {
      int planeType = Plane.getDiscriminationIndex(p);
      PreparedStatement createS =
      planeStatements[StatementType.CREATE.ordinal()];

      //createS.setLong(1, p.getPid());
      createS.setString(1, p.getBrand());
      createS.setInt(2, planeType);
      //createS.setInt(4, -1);

      // weight cap
      if (planeType == 0)
        createS.setInt(4, ((CargoPlane)p).getWeightCapacity());
      else
        createS.setNull(4, Types.NUMERIC);
      if (planeType == 2)
        createS.setString(3, ((MilitaryPlane)p).getArsenal());
      else
        createS.setNull(3, Types.VARCHAR);
      ret = createS.executeUpdate() == 1;
    } catch (SQLException ex) {
      System.err.println("While inserting: " + ex);
    }
    return ret;
  }

  /**
   * updates the Plane entry on the database with the same SSNR
   * @param p Plane object whose values will be written to the new entry
   * @return true on success
   */
  public static boolean updatePlane(Plane p) {
    boolean ret = false;
    try {
      int planeType = Plane.getDiscriminationIndex(p);
      PreparedStatement s =
      planeStatements[StatementType.UPDATE.ordinal()];
      s.setString(1, p.getBrand());
      if (planeType == 0)
        s.setInt(2, ((CargoPlane)p).getWeightCapacity());
      else s.setNull(2, Types.NUMERIC);
      if (planeType == 2)
        s.setInt(3, ((CargoPlane)p).getWeightCapacity());
      else s.setNull(3, Types.NUMERIC);
      ret = s.executeUpdate() == 1;
    } catch (SQLException ex) {
      System.err.println("While updating: " + ex);
    }
    return ret;
  }

  /**
   * queries database for Plane with specified pid
   * @param pid pid of Plane
   * @return Java repr of entry or null on error
   */
  public static Plane findPlane(long pid) {
    Plane ret = null;
    try {
      planeStatements[StatementType.READO.ordinal()].setLong(1, pid);
      ResultSet rs =
          planeStatements[StatementType.READO.ordinal()].executeQuery();
      if (rs.next()) {
        ret = Plane.fromQuery(rs);
      }
    } catch (SQLException ex) {
      System.err.println("While querying (single): " + ex);
    }
    return ret;
  }

  /**
   * queries database for all flights
   * @return Set of Java representations of Flight entries
   */
  public static Set<Flight> getAllFlights() {
    Set<Flight> flights = null;
    try {
      flights = new HashSet<>();
      ResultSet rs =
          flightStatements[StatementType.READA.ordinal()].executeQuery();
      while (rs.next()) {
        flights.add(Flight.fromQuery(rs));
      }
    } catch (SQLException e) {
      System.err.println("While querying (all): " + e);
    }

    return flights;
  }

  /**
   * deletes a flight
   * @param ssnr ssnr of Person to be deleted
   * @return true on success
   */
  public static boolean deleteFlight(long ssnr) {
    boolean ret = false;
    try {
      flightStatements[StatementType.DELETE.ordinal()].setLong(1, ssnr);
      ret = flightStatements[StatementType.DELETE.ordinal()].executeUpdate() == 1;
    } catch (SQLException e) {
      System.err.println("While deleting: " + e);
    }
    return ret;
  }

  /**
   * inserts an Person into the database
   * @param f Person to add
   * @return true on success
   */
  public static boolean addFlight(Flight f) {
    boolean ret = false;
    try {
      PreparedStatement s = flightStatements[StatementType.CREATE.ordinal()];
      //s.setLong(1, f.getFID());
      s.setDate(1, Date.valueOf(f.getDeparture().toLocalDate()));
      s.setDate(2, Date.valueOf(f.getArrival().toLocalDate()));
      s.setLong(3, f.getPlane().getPid());
      s.setString(4, f.getDepAirport());
      s.setString(5, f.getArrAirport());
      s.setLong(6, f.getPilots()[0].getPID());
      s.setLong(7, f.getPilots()[1].getPID());
      ret = s.executeUpdate() == 1;
      for (Person p : f.getStewardesses()) {
        if (findPerson(p.getPID()) == null)
          addPerson(p);
        addParticipatesRel(f, p);
      }
      for (Person p : f.getPassengers()) {
        if (findPerson(p.getPID()) == null)
          addPerson(p);
        addParticipatesRel(f, p);
      }
    } catch (SQLException ex) {
      System.err.printf("PIDs: Plane=%d Pilot=%d Copilot=%d", f.getPlane().getPid(), f.getPilots()[0].getPID(), f.getPilots()[1].getPID());
      System.err.println("While inserting: " + ex);
    }
    return ret;
  }

  /**
   * updates the Person entry on the database with the same SSNR
   * @param e Person object whose values will be written to the new entry
   * @return true on success
   */
  /*
  public static boolean updateFlight(Flight e) {
    boolean ret = false;
    try {
      PreparedStatement s = flightStatements[StatementType.UPDATE.ordinal()];
      s.setDate(1, Date.valueOf(e.getDeparture().toLocalDate()));
      s.setDate(2, Date.valueOf(e.getArrival().toLocalDate()));
      s.setLong(3, e.getPlane().getPid());
      s.setString(4, e.getDepAirport());
      s.setString(5, e.getArrAirport());
      s.setLong(6, e.getPilots()[0].getPID());
      s.setLong(7, e.getPilots()[1].getPID());
      ret = s.executeUpdate() == 1;
    } catch (SQLException ex) {
      System.err.println("While updating: " + ex);
    }
    return ret;
  }*/

  /**
   * queries database for Person with specified fid
   * @param fid fid of Person
   * @return Java repr of entry or null on error
   */
  public static Flight findFlight(long fid) {
    Flight ret = null;
    try {
      flightStatements[StatementType.READO.ordinal()].setLong(1, fid);
      ResultSet rs =
          flightStatements[StatementType.READO.ordinal()].executeQuery();
      if (rs.next()) {
        ret = Flight.fromQuery(rs);
      }
    } catch (SQLException ex) {
      System.err.println("While querying (single): " + ex);
    }
    return ret;
  }

  public static boolean addParticipatesRel(Flight f, Person p) {
    boolean ret = false;
    try {
      PreparedStatement s = participatesStatements[StatementType.CREATE.ordinal()];
      s.setInt(1, f.getFID());
      s.setLong(2, p.getPID());
      ret = s.executeUpdate() == 1;
    } catch (SQLException e) {
      System.err.println(e);
    }
    return ret;
  }

  public static boolean hasParticipatesRel(Flight f, Person p) {
    boolean ret = false;
    try {
      PreparedStatement s = participatesStatements[StatementType.READO.ordinal()];
      s.setInt(1, f.getFID());
      s.setLong(2, p.getPID());
      int rows = 0;
      ResultSet rs = s.executeQuery();
      while (rs.next()) rows++;
      ret = rows == 1;
    } catch (SQLException e) {
      System.err.println(e);
    }
    return ret;
  }

  public static ArrayList<int[]> getAllParticipatesRel() {
    ArrayList<int[]> ret = null;
    try {
      PreparedStatement s = participatesStatements[StatementType.READA.ordinal()];
      ResultSet rs = s.executeQuery();
      ret = new ArrayList<>();
      while (rs.next()) {
        ret.add(new int[] {rs.getInt(1), rs.getInt(2)});
      }
    } catch (SQLException e) {
      System.err.println(e);
    }
    return ret;
  }
}
