package uk.ac.plymouth.comp2005maternity.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HospitalApiClient {

    private final RestTemplate restTemplate;

    private final String BASE_URL = "https://web.socem.plymouth.ac.uk/COMP2005/api";

    public HospitalApiClient() {
        this.restTemplate = new RestTemplate();
    }

    public String getPatients() {
        return restTemplate.getForObject(BASE_URL + "/Patients", String.class);
    }

    public String getAdmissions() {
        return restTemplate.getForObject(BASE_URL + "/Admissions", String.class);
    }

    public String getRoomAllocations() {
        return restTemplate.getForObject(BASE_URL + "/RoomAllocations", String.class);
    }

    public String getAllocations() {
        return restTemplate.getForObject(BASE_URL + "/Allocations", String.class);
    }
}