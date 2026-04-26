package uk.ac.plymouth.comp2005maternity.ui;

import javax.swing.*;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class LeastUsedRoomUI {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Least Used Room Finder");

        JLabel title = new JLabel("Hospital Room Usage Checker");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JButton button = new JButton("Find Least Used Room");

        JLabel resultLabel = new JLabel("Press button to load result");

        button.addActionListener(e -> {
            try {
                URL url = new URL("http://localhost:8080/least-used-room");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                Scanner scanner = new Scanner(conn.getInputStream());

                String response = scanner.nextLine();

                resultLabel.setText("Least Used Room: " + response);

                scanner.close();

            } catch (Exception ex) {
                resultLabel.setText("Could not connect to API");
            }
        });

        frame.setLayout(new GridLayout(3,1));

        frame.add(title);
        frame.add(button);
        frame.add(resultLabel);

        frame.setSize(500,250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}