package uk.ac.plymouth.comp2005maternity.ui;

import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class PatientsInRoomUI {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Patients in Room Finder");

        JLabel title = new JLabel("Hospital Patient Room Checker", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel instruction = new JLabel(
                "Enter a room ID below, then click 'Find Patients' to search.",
                SwingConstants.CENTER
        );
        instruction.setFont(new Font("Arial", Font.PLAIN, 15));

        JLabel helperText = new JLabel("Example Room IDs: 1, 2, or 3", SwingConstants.CENTER);
        helperText.setFont(new Font("Arial", Font.ITALIC, 13));

        JLabel roomLabel = new JLabel("Room ID:");
        roomLabel.setFont(new Font("Arial", Font.BOLD, 15));

        JTextField roomInput = new JTextField();
        roomInput.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton searchButton = new JButton("Find Patients");
        searchButton.setFont(new Font("Arial", Font.BOLD, 16));

        JTextArea resultArea = new JTextArea("Enter a room ID and click Find Patients.");
        resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);

        searchButton.addActionListener(e -> {
            try {
                String roomId = roomInput.getText().trim();

                // Empty validation
                if (roomId.isEmpty()) {
                    resultArea.setText("Please enter a room ID before searching.");
                    return;
                }

                // Numeric validation
                if (!roomId.matches("\\d+")) {
                    resultArea.setText("Please enter a valid numeric room ID.");
                    return;
                }

                resultArea.setText("Searching...");

                String response = getString(roomId);

                if (response.equals("[]")) {
                    resultArea.setText("No patients found in room " + roomId + " within the last 7 days.");
                } else {
                    resultArea.setText("Patients found in room " + roomId + ":\n" + response);
                }

            } catch (Exception ex) {
                resultArea.setText("Could not connect to backend API. Please make sure the Spring Boot server is running.");
            }
        });

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.add(roomLabel, BorderLayout.WEST);
        inputPanel.add(roomInput, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        mainPanel.add(title);
        mainPanel.add(instruction);
        mainPanel.add(helperText);
        mainPanel.add(inputPanel);
        mainPanel.add(searchButton);
        mainPanel.add(new JScrollPane(resultArea));

        frame.add(mainPanel);
        frame.setSize(650, 430);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static @NonNull String getString(String roomId) throws IOException {
        URL url = new URL("http://localhost:8080/patients/room/" + roomId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        Scanner scanner = new Scanner(conn.getInputStream());
        StringBuilder responseBuilder = new StringBuilder();

        while (scanner.hasNextLine()) {
            responseBuilder.append(scanner.nextLine());
        }

        scanner.close();

        return responseBuilder.toString();
    }
}