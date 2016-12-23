package com.infogravity.timesheet.web.rest;

import com.infogravity.timesheet.TimesheetApp;

import com.infogravity.timesheet.domain.Timesheet;
import com.infogravity.timesheet.repository.TimesheetRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TimesheetResource REST controller.
 *
 * @see TimesheetResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TimesheetApp.class)
public class TimesheetResourceIntTest {

    private static final String DEFAULT_FIRST_NAME = "AAA";
    private static final String UPDATED_FIRST_NAME = "BBB";

    private static final String DEFAULT_LAST_NAME = "AAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBB";

    private static final String DEFAULT_DAY = "AAAAA";
    private static final String UPDATED_DAY = "BBBBB";

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final ZonedDateTime DEFAULT_TIME_IN = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_TIME_IN = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_TIME_IN_STR = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(DEFAULT_TIME_IN);

    private static final ZonedDateTime DEFAULT_TIME_OUT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_TIME_OUT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_TIME_OUT_STR = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(DEFAULT_TIME_OUT);

    private static final String DEFAULT_WORK_HOURS = "AAAAA";
    private static final String UPDATED_WORK_HOURS = "BBBBB";

    @Inject
    private TimesheetRepository timesheetRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restTimesheetMockMvc;

    private Timesheet timesheet;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TimesheetResource timesheetResource = new TimesheetResource();
        ReflectionTestUtils.setField(timesheetResource, "timesheetRepository", timesheetRepository);
        this.restTimesheetMockMvc = MockMvcBuilders.standaloneSetup(timesheetResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Timesheet createEntity(EntityManager em) {
        Timesheet timesheet = new Timesheet()
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .day(DEFAULT_DAY)
                .date(DEFAULT_DATE)
                .timeIn(DEFAULT_TIME_IN)
                .timeOut(DEFAULT_TIME_OUT)
                .workHours(DEFAULT_WORK_HOURS);
        return timesheet;
    }

    @Before
    public void initTest() {
        timesheet = createEntity(em);
    }

    @Test
    @Transactional
    public void createTimesheet() throws Exception {
        int databaseSizeBeforeCreate = timesheetRepository.findAll().size();

        // Create the Timesheet

        restTimesheetMockMvc.perform(post("/api/timesheets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timesheet)))
                .andExpect(status().isCreated());

        // Validate the Timesheet in the database
        List<Timesheet> timesheets = timesheetRepository.findAll();
        assertThat(timesheets).hasSize(databaseSizeBeforeCreate + 1);
        Timesheet testTimesheet = timesheets.get(timesheets.size() - 1);
        assertThat(testTimesheet.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testTimesheet.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testTimesheet.getDay()).isEqualTo(DEFAULT_DAY);
        assertThat(testTimesheet.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testTimesheet.getTimeIn()).isEqualTo(DEFAULT_TIME_IN);
        assertThat(testTimesheet.getTimeOut()).isEqualTo(DEFAULT_TIME_OUT);
        assertThat(testTimesheet.getWorkHours()).isEqualTo(DEFAULT_WORK_HOURS);
    }

    @Test
    @Transactional
    public void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = timesheetRepository.findAll().size();
        // set the field null
        timesheet.setFirstName(null);

        // Create the Timesheet, which fails.

        restTimesheetMockMvc.perform(post("/api/timesheets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timesheet)))
                .andExpect(status().isBadRequest());

        List<Timesheet> timesheets = timesheetRepository.findAll();
        assertThat(timesheets).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTimesheets() throws Exception {
        // Initialize the database
        timesheetRepository.saveAndFlush(timesheet);

        // Get all the timesheets
        restTimesheetMockMvc.perform(get("/api/timesheets?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(timesheet.getId().intValue())))
                .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME.toString())))
                .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME.toString())))
                .andExpect(jsonPath("$.[*].day").value(hasItem(DEFAULT_DAY.toString())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
                .andExpect(jsonPath("$.[*].timeIn").value(hasItem(DEFAULT_TIME_IN_STR)))
                .andExpect(jsonPath("$.[*].timeOut").value(hasItem(DEFAULT_TIME_OUT_STR)))
                .andExpect(jsonPath("$.[*].workHours").value(hasItem(DEFAULT_WORK_HOURS.toString())));
    }

    @Test
    @Transactional
    public void getTimesheet() throws Exception {
        // Initialize the database
        timesheetRepository.saveAndFlush(timesheet);

        // Get the timesheet
        restTimesheetMockMvc.perform(get("/api/timesheets/{id}", timesheet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(timesheet.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME.toString()))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME.toString()))
            .andExpect(jsonPath("$.day").value(DEFAULT_DAY.toString()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.timeIn").value(DEFAULT_TIME_IN_STR))
            .andExpect(jsonPath("$.timeOut").value(DEFAULT_TIME_OUT_STR))
            .andExpect(jsonPath("$.workHours").value(DEFAULT_WORK_HOURS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTimesheet() throws Exception {
        // Get the timesheet
        restTimesheetMockMvc.perform(get("/api/timesheets/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTimesheet() throws Exception {
        // Initialize the database
        timesheetRepository.saveAndFlush(timesheet);
        int databaseSizeBeforeUpdate = timesheetRepository.findAll().size();

        // Update the timesheet
        Timesheet updatedTimesheet = timesheetRepository.findOne(timesheet.getId());
        updatedTimesheet
                .firstName(UPDATED_FIRST_NAME)
                .lastName(UPDATED_LAST_NAME)
                .day(UPDATED_DAY)
                .date(UPDATED_DATE)
                .timeIn(UPDATED_TIME_IN)
                .timeOut(UPDATED_TIME_OUT)
                .workHours(UPDATED_WORK_HOURS);

        restTimesheetMockMvc.perform(put("/api/timesheets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTimesheet)))
                .andExpect(status().isOk());

        // Validate the Timesheet in the database
        List<Timesheet> timesheets = timesheetRepository.findAll();
        assertThat(timesheets).hasSize(databaseSizeBeforeUpdate);
        Timesheet testTimesheet = timesheets.get(timesheets.size() - 1);
        assertThat(testTimesheet.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testTimesheet.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testTimesheet.getDay()).isEqualTo(UPDATED_DAY);
        assertThat(testTimesheet.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testTimesheet.getTimeIn()).isEqualTo(UPDATED_TIME_IN);
        assertThat(testTimesheet.getTimeOut()).isEqualTo(UPDATED_TIME_OUT);
        assertThat(testTimesheet.getWorkHours()).isEqualTo(UPDATED_WORK_HOURS);
    }

    @Test
    @Transactional
    public void deleteTimesheet() throws Exception {
        // Initialize the database
        timesheetRepository.saveAndFlush(timesheet);
        int databaseSizeBeforeDelete = timesheetRepository.findAll().size();

        // Get the timesheet
        restTimesheetMockMvc.perform(delete("/api/timesheets/{id}", timesheet.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Timesheet> timesheets = timesheetRepository.findAll();
        assertThat(timesheets).hasSize(databaseSizeBeforeDelete - 1);
    }
}
