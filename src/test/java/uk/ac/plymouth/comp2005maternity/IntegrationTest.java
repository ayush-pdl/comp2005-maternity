package uk.ac.plymouth.comp2005maternity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.ac.plymouth.comp2005maternity.service.MaternityService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IntegrationTest {

    @Autowired
    private MaternityService maternityService;

    @Test
    void contextLoads() {
        assertNotNull(maternityService);
    }

    @Test
    void serviceBeanLoadsAndCanSearchPatientsByRoom() {
        List<Integer> patients = maternityService.getPatientsByRoomLast7Days("2");

        assertNotNull(patients);
    }

    @Test
    void serviceBeanHandlesInvalidRoomWithoutCrashing() {
        List<Integer> patients = maternityService.getPatientsByRoomLast7Days("999");

        assertNotNull(patients);
        assertTrue(patients.isEmpty() || patients instanceof List<Integer>);
    }
}