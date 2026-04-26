package uk.ac.plymouth.comp2005maternity.service;

import org.junit.jupiter.api.Test;
import uk.ac.plymouth.comp2005maternity.client.HospitalApiClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaternityServiceTest {

    static class FakeHospitalApiClient extends HospitalApiClient {
        @Override
        public String getAdmissions() {
            return """
                    [
                      {"id":1,"admissionDate":"2020-11-28T16:45:00","dischargeDate":"2020-11-28T23:56:00","patientID":2},
                      {"id":2,"admissionDate":"2020-12-01T10:00:00","dischargeDate":"2020-12-01T12:00:00","patientID":2},
                      {"id":3,"admissionDate":"2020-12-02T10:00:00","dischargeDate":"2020-12-02T12:00:00","patientID":3}
                    ]
                    """;
        }

        @Override
        public String getRoomAllocations() {
            return """
                    [
                      {"id":1,"admissionID":1,"roomID":4,"timeIn":"2020-11-28T16:45:00","timeOut":"2020-11-28T18:00:00"},
                      {"id":2,"admissionID":2,"roomID":2,"timeIn":"2020-12-01T10:00:00","timeOut":"2020-12-01T11:00:00"},
                      {"id":3,"admissionID":2,"roomID":4,"timeIn":"2020-12-01T11:00:00","timeOut":"2020-12-01T12:00:00"},
                      {"id":4,"admissionID":3,"roomID":1,"timeIn":"2020-12-02T10:00:00","timeOut":"2020-12-02T12:00:00"}
                    ]
                    """;
        }
    }

    @Test
    void getRoomsByPatientReturnsCorrectRooms() {
        MaternityService service = new MaternityService(new FakeHospitalApiClient());

        List<Integer> rooms = service.getRoomsByPatient("2");

        assertEquals(List.of(2, 4), rooms);
    }

    @Test
    void getRoomsByPatientReturnsEmptyListWhenPatientHasNoAdmissions() {
        MaternityService service = new MaternityService(new FakeHospitalApiClient());

        List<Integer> rooms = service.getRoomsByPatient("999");

        assertEquals(List.of(), rooms);
    }

    @Test
    void getRoomsByPatientRemovesDuplicateRooms() {
        HospitalApiClient fakeClient = new FakeHospitalApiClient() {
            @Override
            public String getRoomAllocations() {
                return """
                    [
                      {"id":1,"admissionID":1,"roomID":4,"timeIn":"2020-11-28T16:45:00","timeOut":"2020-11-28T18:00:00"},
                      {"id":2,"admissionID":1,"roomID":4,"timeIn":"2020-11-28T18:00:00","timeOut":"2020-11-28T19:00:00"},
                      {"id":3,"admissionID":2,"roomID":2,"timeIn":"2020-12-01T10:00:00","timeOut":"2020-12-01T11:00:00"}
                    ]
                    """;
            }
        };

        MaternityService service = new MaternityService(fakeClient);

        List<Integer> rooms = service.getRoomsByPatient("2");

        assertEquals(List.of(2, 4), rooms);
    }

    @Test
    void getPatientsByRoomLast7DaysReturnsCorrectPatients() {
        HospitalApiClient fakeClient = new FakeHospitalApiClient() {
            @Override
            public String getRoomAllocations() {
                return """
                    [
                      {"id":1,"admissionID":1,"roomID":2,"timeIn":"2030-11-28T16:45:00","timeOut":"2030-11-28T18:00:00"},
                      {"id":2,"admissionID":2,"roomID":2,"timeIn":"2030-12-01T10:00:00","timeOut":"2030-12-01T11:00:00"},
                      {"id":3,"admissionID":3,"roomID":4,"timeIn":"2030-12-02T10:00:00","timeOut":"2030-12-02T12:00:00"}
                    ]
                    """;
            }
        };

        MaternityService service = new MaternityService(fakeClient);

        List<Integer> patients = service.getPatientsByRoomLast7Days("2");

        assertEquals(List.of(2), patients);
    }

    @Test
    void getPatientsByRoomLast7DaysReturnsEmptyListForOldAllocations() {
        HospitalApiClient fakeClient = new FakeHospitalApiClient() {
            @Override
            public String getRoomAllocations() {
                return """
                    [
                      {"id":1,"admissionID":1,"roomID":2,"timeIn":"2020-11-28T16:45:00","timeOut":"2020-11-28T18:00:00"},
                      {"id":2,"admissionID":2,"roomID":2,"timeIn":"2020-12-01T10:00:00","timeOut":"2020-12-01T11:00:00"}
                    ]
                    """;
            }
        };

        MaternityService service = new MaternityService(fakeClient);

        List<Integer> patients = service.getPatientsByRoomLast7Days("2");

        assertEquals(List.of(), patients);
    }

    @Test
    void getLeastUsedRoomReturnsCorrectRoom() {
        HospitalApiClient fakeClient = new FakeHospitalApiClient() {
            @Override
            public String getRoomAllocations() {
                return """
                    [
                      {"id":1,"admissionID":1,"roomID":1,"timeIn":"2030-11-28T16:45:00","timeOut":"2030-11-28T18:00:00"},
                      {"id":2,"admissionID":2,"roomID":1,"timeIn":"2030-12-01T10:00:00","timeOut":"2030-12-01T11:00:00"},
                      {"id":3,"admissionID":3,"roomID":2,"timeIn":"2030-12-02T10:00:00","timeOut":"2030-12-02T12:00:00"}
                    ]
                    """;
            }
        };

        MaternityService service = new MaternityService(fakeClient);

        Integer leastUsed = service.getLeastUsedRoom();

        assertEquals(2, leastUsed);
    }


}