package CS3;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BusinessServer {
    public static void main(String[] args) {
        try {

            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Business Server is running and listening on port 12345...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private Socket clientSocket;
    private Connection dbConnection;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 注册MySQL数据库驱动程序
            dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/contact?characterEncoding=utf8&useSSL=false", "root", "123456");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            String action = in.readLine();

            if ("ADD_CONTACT".equals(action)) {
                String name = in.readLine();
                String address = in.readLine();
                String phone = in.readLine();
                addContact(name, address, phone, out);
            } else if ("UPDATE_CONTACT".equals(action)) {
                int contactId = Integer.parseInt(in.readLine());
                String name = in.readLine();
                String address = in.readLine();
                String phone = in.readLine();
                updateContact(contactId, name, address, phone, out);
            } else if ("DELETE_CONTACT".equals(action)) {
                int contactId = Integer.parseInt(in.readLine());
                deleteContact(contactId, out);
            } else if ("VIEW_CONTACTor".equals(action)) {
                viewContactor(out);
            } else {
                out.println("Invalid action.");
            }

            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addContact(String name, String address, String phone, PrintWriter out) {
        try {
            String query = "INSERT INTO contactor (name, address, phone) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, phone);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                out.println("Contact added successfully.");
            } else {
                out.println("Failed to add contact.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Failed to add contact.");
        }
    }

    private void updateContact(int contactId, String name, String address, String phone, PrintWriter out) {
        try {
            String query = "UPDATE contactor SET name=?, address=?, phone=? WHERE id=?";
            PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, phone);
            preparedStatement.setInt(4, contactId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                out.println("Contact updated successfully.");
            } else {
                out.println("Failed to update contact.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Failed to update contact.");
        }
    }

    private void deleteContact(int contactId, PrintWriter out) {
        try {
            String query = "DELETE FROM contactor WHERE id=?";
            PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
            preparedStatement.setInt(1, contactId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                out.println("Contact deleted successfully.");
            } else {
                out.println("Failed to delete contact.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Failed to delete contact.");
        }
    }

    private void viewContactor(PrintWriter out) {
        try {
            String query = "SELECT * FROM contactor";
            PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                out.println("ID: " + resultSet.getInt("id"));
                out.println("Name: " + resultSet.getString("name"));
                out.println("Address: " + resultSet.getString("address"));
                out.println("Phone: " + resultSet.getString("phone"));
                out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Failed to retrieve contactor.");
        }
    }
}