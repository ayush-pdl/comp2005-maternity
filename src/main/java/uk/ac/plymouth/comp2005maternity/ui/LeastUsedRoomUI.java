package uk.ac.plymouth.comp2005maternity.ui;

import javax.swing.*;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class LeastUsedRoomUI {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Least Used Room Finder");

        JLabel title = new JLabel("Hospital Room Usage Checker", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel instructionLabel = new JLabel(
                "Click the button below to retrieve the least used maternity room.",
                SwingConstants.CENTER
        );
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton button = new JButton("Find Least Used Room");
        button.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel resultLabel = new JLabel("Result will appear here", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        button.addActionListener(e -> {
            resultLabel.setText("Loading...");

            try {
                URL url = new URL("http://localhost:8080/least-used-room");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                Scanner scanner = new Scanner(conn.getInputStream());
                String response = scanner.nextLine();

                resultLabel.setText("Least Used Room: " + response);

                scanner.close();

            } catch (Exception ex) {
                resultLabel.setText("Could not connect to API. Please start the backend first.");
            }
        });

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(title);
        panel.add(instructionLabel);
        panel.add(button);
        panel.add(resultLabel);

        frame.add(panel);
        frame.setSize(550, 280);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}