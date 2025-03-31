drop table employee cascade constraints;

create table employee (
    ssnr number primary key,
    name varchar2(100),
    salary int
);

INSERT INTO employees (name, ssnr, salary) VALUES ('Alice Johnson', 1, 55000);
INSERT INTO employees (name, ssnr, salary) VALUES ('Bob Smith', 2, 60000);
INSERT INTO employees (name, ssnr, salary) VALUES ('Charlie Brown', 3, 45000);
INSERT INTO employees (name, ssnr, salary) VALUES ('Diana White', 4, 70000);
INSERT INTO employees (name, ssnr, salary) VALUES ('Eve Davis', 5, 48000);

SELECT * FROM employees WHERE ssnr = 5;

COMMIT;
