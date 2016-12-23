package com.infogravity.timesheet.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.infogravity.timesheet.domain.Timesheet;

import com.infogravity.timesheet.repository.TimesheetRepository;
import com.infogravity.timesheet.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Timesheet.
 */
@RestController
@RequestMapping("/api")
public class TimesheetResource {

    private final Logger log = LoggerFactory.getLogger(TimesheetResource.class);
        
    @Inject
    private TimesheetRepository timesheetRepository;

    /**
     * POST  /timesheets : Create a new timesheet.
     *
     * @param timesheet the timesheet to create
     * @return the ResponseEntity with status 201 (Created) and with body the new timesheet, or with status 400 (Bad Request) if the timesheet has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/timesheets")
    @Timed
    public ResponseEntity<Timesheet> createTimesheet(@Valid @RequestBody Timesheet timesheet) throws URISyntaxException {
        log.debug("REST request to save Timesheet : {}", timesheet);
        if (timesheet.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("timesheet", "idexists", "A new timesheet cannot already have an ID")).body(null);
        }
        Timesheet result = timesheetRepository.save(timesheet);
        return ResponseEntity.created(new URI("/api/timesheets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("timesheet", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /timesheets : Updates an existing timesheet.
     *
     * @param timesheet the timesheet to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated timesheet,
     * or with status 400 (Bad Request) if the timesheet is not valid,
     * or with status 500 (Internal Server Error) if the timesheet couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/timesheets")
    @Timed
    public ResponseEntity<Timesheet> updateTimesheet(@Valid @RequestBody Timesheet timesheet) throws URISyntaxException {
        log.debug("REST request to update Timesheet : {}", timesheet);
        if (timesheet.getId() == null) {
            return createTimesheet(timesheet);
        }
        Timesheet result = timesheetRepository.save(timesheet);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("timesheet", timesheet.getId().toString()))
            .body(result);
    }

    /**
     * GET  /timesheets : get all the timesheets.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of timesheets in body
     */
    @GetMapping("/timesheets")
    @Timed
    public List<Timesheet> getAllTimesheets() {
        log.debug("REST request to get all Timesheets");
        List<Timesheet> timesheets = timesheetRepository.findAll();
        return timesheets;
    }

    /**
     * GET  /timesheets/:id : get the "id" timesheet.
     *
     * @param id the id of the timesheet to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the timesheet, or with status 404 (Not Found)
     */
    @GetMapping("/timesheets/{id}")
    @Timed
    public ResponseEntity<Timesheet> getTimesheet(@PathVariable Long id) {
        log.debug("REST request to get Timesheet : {}", id);
        Timesheet timesheet = timesheetRepository.findOne(id);
        return Optional.ofNullable(timesheet)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /timesheets/:id : delete the "id" timesheet.
     *
     * @param id the id of the timesheet to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/timesheets/{id}")
    @Timed
    public ResponseEntity<Void> deleteTimesheet(@PathVariable Long id) {
        log.debug("REST request to delete Timesheet : {}", id);
        timesheetRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("timesheet", id.toString())).build();
    }

}
