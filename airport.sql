DROP TABLE person CASCADE CONSTRAINTS;
DROP TABLE plane CASCADE CONSTRAINTS;
DROP TABLE flight CASCADE CONSTRAINTS;
DROP TABLE participates CASCADE CONSTRAINTS;
DROP SEQUENCE personSeq;
DROP SEQUENCE planeSeq;
DROP SEQUENCE flightSeq;

CREATE SEQUENCE personSeq
	MINVALUE 0
	START WITH 1
	INCREMENT BY 1;
    
CREATE SEQUENCE planeSeq
	MINVALUE 0
	START WITH 1
	INCREMENT BY 1;

CREATE SEQUENCE flightSeq
	MINVALUE 0
	START WITH 1
	INCREMENT BY 1;

CREATE TABLE person (
  pid number PRIMARY KEY,
  salary number,
  name varchar2 (100),
  persType number,
  bagWeight number
);

CREATE TABLE plane (
  pid number PRIMARY KEY,
  brand varchar2 (100),
  planeType number,
  -- weight INT,
  arsenal varchar2 (100),
  weightCapacity INT
);

CREATE TABLE flight (
  fid INT PRIMARY KEY,
  departure DATE,
  arrival DATE,
  planeId REFERENCES plane (pid),
  airportDept varchar2(100),
  airportArr varchar2(100),
  pilotPassportNr REFERENCES person(pid),
  copilotPassportNr REFERENCES person(pid)
);

CREATE TABLE participates (
  flightId REFERENCES flight (fid),
  passengerPassport REFERENCES person(pid), 
  CONSTRAINT pk_has PRIMARY KEY (flightId, passengerPassport)
);

-- Insert data into the person table
INSERT INTO person (pid, salary, name, persType, bagWeight) VALUES (personSeq.nextval, 50000, 'John Doe', 1, 20);
INSERT INTO person (pid, salary, name, persType, bagWeight) VALUES (personSeq.nextval, 60000, 'Jane Smith', 1, 25);
INSERT INTO person (pid, salary, name, persType, bagWeight) VALUES (personSeq.nextval, 70000, 'Alice Johnson', 2, 15);
INSERT INTO person (pid, salary, name, persType, bagWeight) VALUES (personSeq.nextval, 80000, 'Bob Brown', 2, 30);
INSERT INTO person (pid, salary, name, persType, bagWeight) VALUES (personSeq.nextval, 90000, 'Charlie Davis', 1, 10);
INSERT INTO person (pid, salary, name, persType, bagWeight) VALUES (personSeq.nextval, 50000, 'John Dash', 1, 20);
INSERT INTO person (pid, salary, name, persType, bagWeight) VALUES (personSeq.nextval, 60000, 'Smith Jane', 1, 25);
INSERT INTO person (pid, salary, name, persType, bagWeight) VALUES (personSeq.nextval, 70000, 'Alice Johnson', 3, 15);
INSERT INTO person (pid, salary, name, persType, bagWeight) VALUES (personSeq.nextval, 80000, 'Bob Brown', 2, 30);
INSERT INTO person (pid, salary, name, persType, bagWeight) VALUES (personSeq.nextval, 80000, 'John Brown', 3, 30);


-- Insert data into the plane table
INSERT INTO plane (pid, brand, planeType, arsenal, weightCapacity) VALUES (planeSeq.nextval, 'Boeing', 1, NULL, NULL);
INSERT INTO plane (pid, brand, planeType, arsenal, weightCapacity) VALUES (planeSeq.nextval, 'Airbus', 2, '1x C macro', NULL);
INSERT INTO plane (pid, brand, planeType, arsenal, weightCapacity) VALUES (planeSeq.nextval, 'Cessna', 0, NULL, 4000);
INSERT INTO plane (pid, brand, planeType, arsenal, weightCapacity) VALUES (planeSeq.nextval, 'Embraer', 1, NULL, NULL);

-- Insert data into the flight table
INSERT INTO flight (fid, departure, arrival, planeId, airportDept, airportArr, pilotPassportNr, copilotPassportNr) VALUES (flightSeq.nextval, TO_DATE('2023-10-01 10:00', 'YYYY-MM-DD HH24:MI'), TO_DATE('2023-10-01 12:00', 'YYYY-MM-DD HH24:MI'), 1, 'JFK', 'LAX', 1, 2);
INSERT INTO flight (fid, departure, arrival, planeId, airportDept, airportArr, pilotPassportNr, copilotPassportNr) VALUES (flightSeq.nextval, TO_DATE('2023-10-02 14:00', 'YYYY-MM-DD HH24:MI'), TO_DATE('2023-10-02 16:00', 'YYYY-MM-DD HH24:MI'), 2, 'LAX', 'ORD', 6, 7);
INSERT INTO flight (fid, departure, arrival, planeId, airportDept, airportArr, pilotPassportNr, copilotPassportNr) VALUES (flightSeq.nextval, TO_DATE('2023-10-03 09:00', 'YYYY-MM-DD HH24:MI'), TO_DATE('2023-10-03 11:00', 'YYYY-MM-DD HH24:MI'), 3, 'ORD', 'MIA', 1, 6);

-- Insert data into the participates table
INSERT INTO participates (flightId, passengerPassport) VALUES (1, 8);
INSERT INTO participates (flightId, passengerPassport) VALUES (1, 9);
INSERT INTO participates (flightId, passengerPassport) VALUES (2, 3);
INSERT INTO participates (flightId, passengerPassport) VALUES (2, 4);
INSERT INTO participates (flightId, passengerPassport) VALUES (3, 10);

COMMIT;
