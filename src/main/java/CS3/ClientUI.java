package CS3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ClientUI {
    private JFrame frame;
    private JPanel inputPanel, resultPanel;
    private JTextField nameField, addressField, phoneField;
    private JButton addButton, updateButton, deleteButton, viewButton;
    private JTextArea resultArea;
    private Socket serverSocket;

    public ClientUI() {
        frame = new JFrame("Personal Contact Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

        inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2));

        nameField = new JTextField(20);
        nameField.setToolTipText("Enter Name");
        addressField = new JTextField(20);
        addressField.setToolTipText("Enter Address");
        phoneField = new JTextField(20);
        phoneField.setToolTipText("Enter Phone Number");

        addButton = new JButton("Add Contact");
        updateButton = new JButton("Update Contact");
        deleteButton = new JButton("Delete Contact");
        viewButton = new JButton("View Contacts");

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Address:"));
        inputPanel.add(addressField);
        inputPanel.add(new JLabel("Phone:"));
        inputPanel.add(phoneField);
        inputPanel.add(addButton);
        inputPanel.add(updateButton);
        inputPanel.add(deleteButton);
        inputPanel.add(viewButton);

        resultPanel = new JPanel();
        resultPanel.setLayout(new BorderLayout());
        resultArea = new JTextArea(10, 30);
        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(resultPanel, BorderLayout.CENTER);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendRequest("ADD_CONTACT");
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendRequest("UPDATE_CONTACT");
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendRequest("DELETE_CONTACT");
            }
        });

        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendRequest("VIEW_CONTACTor");
            }
        });

        try {
            serverSocket = new Socket("localhost", 12345); // 连接到业务逻辑服务器
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to connect to the server.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        frame.setVisible(true);
    }

    private void sendRequest(String action) {
        try {
            PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
            out.println(action);
            out.println(nameField.getText());
            out.println(addressField.getText());
            out.println(phoneField.getText());

            BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            String response;
            resultArea.setText(""); // 清空之前的结果
            while ((response = in.readLine()) != null) {
                resultArea.append(response + "\n");
            }

            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to send request to the server.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ClientUI();
            }
        });
    }

}