package com.infogravity.timesheet.domain;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Timesheet.
 */
@Entity
@Table(name = "timesheet")
public class Timesheet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 3)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "day")
    private String day;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "time_in")
    private ZonedDateTime timeIn;

    @Column(name = "time_out")
    private ZonedDateTime timeOut;

    @Column(name = "work_hours")
    private String workHours;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public Timesheet firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Timesheet lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDay() {
        return day;
    }

    public Timesheet day(String day) {
        this.day = day;
        return this;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public LocalDate getDate() {
        return date;
    }

    public Timesheet date(LocalDate date) {
        this.date = date;
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ZonedDateTime getTimeIn() {
        return timeIn;
    }

    public Timesheet timeIn(ZonedDateTime timeIn) {
        this.timeIn = timeIn;
        return this;
    }

    public void setTimeIn(ZonedDateTime timeIn) {
        this.timeIn = timeIn;
    }

    public ZonedDateTime getTimeOut() {
        return timeOut;
    }

    public Timesheet timeOut(ZonedDateTime timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    public void setTimeOut(ZonedDateTime timeOut) {
        this.timeOut = timeOut;
    }

    public String getWorkHours() {
        return workHours;
    }

    public Timesheet workHours(String workHours) {
        this.workHours = workHours;
        return this;
    }

    public void setWorkHours(String workHours) {
        this.workHours = workHours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Timesheet timesheet = (Timesheet) o;
        if(timesheet.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, timesheet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Timesheet{" +
            "id=" + id +
            ", firstName='" + firstName + "'" +
            ", lastName='" + lastName + "'" +
            ", day='" + day + "'" +
            ", date='" + date + "'" +
            ", timeIn='" + timeIn + "'" +
            ", timeOut='" + timeOut + "'" +
            ", workHours='" + workHours + "'" +
            '}';
    }
}
