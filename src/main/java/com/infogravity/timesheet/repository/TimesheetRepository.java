package com.infogravity.timesheet.repository;

import com.infogravity.timesheet.domain.Timesheet;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Timesheet entity.
 */
@SuppressWarnings("unused")
public interface TimesheetRepository extends JpaRepository<Timesheet,Long> {

}
