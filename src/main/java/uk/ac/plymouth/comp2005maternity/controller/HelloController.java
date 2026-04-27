package uk.ac.plymouth.comp2005maternity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.plymouth.comp2005maternity.service.MaternityService;

import java.util.List;


@RestController
public class HelloController {

    private final MaternityService service;

    public HelloController(MaternityService service) {
        this.service = service;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Backend is working!";
    }

    @GetMapping("/rooms/{patientId}")
    public List<Integer> getRooms(@PathVariable String patientId) {
        return service.getRoomsByPatient(patientId);
    }

    @GetMapping("/patients/room/{roomId}")
    public List<Integer> getPatientsByRoom(@PathVariable String roomId) {
        return service.getPatientsByRoomLast7Days(roomId);
    }

    @GetMapping("/least-used-room")
    public Integer getLeastUsedRoom() {
        return service.getLeastUsedRoom();
    }

    @GetMapping("/overloaded-staff")
    public List<Integer> getOverloadedStaff() {
        return service.getOverloadedStaff();
    }
}