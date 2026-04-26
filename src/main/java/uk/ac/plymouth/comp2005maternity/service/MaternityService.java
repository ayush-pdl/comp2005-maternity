package uk.ac.plymouth.comp2005maternity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.ac.plymouth.comp2005maternity.client.HospitalApiClient;
import uk.ac.plymouth.comp2005maternity.model.Admission;
import uk.ac.plymouth.comp2005maternity.model.RoomAllocation;
import uk.ac.plymouth.comp2005maternity.model.Allocation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Service
public class MaternityService {

    private final HospitalApiClient apiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MaternityService(HospitalApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public List<Integer> getRoomsByPatient(String patientId) {
        try {
            Admission[] admissions = objectMapper.readValue(
                    apiClient.getAdmissions(), Admission[].class);

            RoomAllocation[] roomAllocations = objectMapper.readValue(
                    apiClient.getRoomAllocations(), RoomAllocation[].class);

            List<Integer> patientAdmissions = new ArrayList<>();
            Set<Integer> rooms = new LinkedHashSet<>();

            for (Admission a : admissions) {
                if (String.valueOf(a.patientID).equals(patientId)) {
                    patientAdmissions.add(a.id);
                }
            }

            for (RoomAllocation r : roomAllocations) {
                if (patientAdmissions.contains(r.admissionID)) {
                    rooms.add(r.roomID);
                }
            }

            List<Integer> result = new ArrayList<>(rooms);
            Collections.sort(result);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Integer> getPatientsByRoomLast7Days(String roomId) {
        try {
            Admission[] admissions = objectMapper.readValue(
                    apiClient.getAdmissions(), Admission[].class);

            RoomAllocation[] roomAllocations = objectMapper.readValue(
                    apiClient.getRoomAllocations(), RoomAllocation[].class);

            List<Integer> matchingAdmissionIds = new ArrayList<>();
            Set<Integer> patients = new LinkedHashSet<>();

            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

            for (RoomAllocation r : roomAllocations) {
                if (String.valueOf(r.roomID).equals(roomId) && r.timeIn != null) {
                    LocalDateTime timeIn = LocalDateTime.parse(r.timeIn);
                    if (timeIn.isAfter(sevenDaysAgo)) {
                        matchingAdmissionIds.add(r.admissionID);
                    }
                }
            }

            for (Admission a : admissions) {
                if (matchingAdmissionIds.contains(a.id)) {
                    patients.add(a.patientID);
                }
            }

            List<Integer> result = new ArrayList<>(patients);
            Collections.sort(result);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public Integer getLeastUsedRoom() {
        try {
            RoomAllocation[] roomAllocations = objectMapper.readValue(
                    apiClient.getRoomAllocations(), RoomAllocation[].class);

            java.util.Map<Integer, Integer> roomUsage = new java.util.HashMap<>();

            for (RoomAllocation r : roomAllocations) {
                roomUsage.put(r.roomID,
                        roomUsage.getOrDefault(r.roomID, 0) + 1);
            }

            Integer leastUsedRoom = null;
            int minUsage = Integer.MAX_VALUE;

            for (java.util.Map.Entry<Integer, Integer> entry : roomUsage.entrySet()) {
                if (entry.getValue() < minUsage) {
                    minUsage = entry.getValue();
                    leastUsedRoom = entry.getKey();
                }
            }

            return leastUsedRoom;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public List<Integer> getOverloadedStaff() {
        try {
            Admission[] admissions = objectMapper.readValue(
                    apiClient.getAdmissions(), Admission[].class);

            Allocation[] allocations = objectMapper.readValue(
                    apiClient.getAllocations(), Allocation[].class);

            Map<Integer, Integer> admissionToPatient = new HashMap<>();
            for (Admission a : admissions) {
                admissionToPatient.put(a.id, a.patientID);
            }

            Map<Integer, HashSet<Integer>> employeePatients = new HashMap<>();

            for (Allocation allocation : allocations) {
                Integer patientId = admissionToPatient.get(allocation.admissionID);
                if (patientId != null) {
                    employeePatients
                            .computeIfAbsent(allocation.employeeID, k -> new HashSet<>())
                            .add(patientId);
                }
            }

            List<Integer> overloaded = new ArrayList<>();

            for (Map.Entry<Integer, HashSet<Integer>> entry : employeePatients.entrySet()) {
                if (entry.getValue().size() >= 3) {
                    overloaded.add(entry.getKey());
                }
            }

            Collections.sort(overloaded);
            return overloaded;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}