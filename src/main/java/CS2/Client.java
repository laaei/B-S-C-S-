package CS2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Client {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/contact?characterEncoding=utf8&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";
    private static final String CONTACTS_TABLE = "contactor";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 注册MySQL数据库驱动程序
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("1. 查看联系人");
                System.out.println("2. 添加联系人");
                System.out.println("3. 修改联系人");
                System.out.println("4. 删除联系人");
                System.out.println("5. 退出");
                System.out.print("输入选项： ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        viewContacts(connection);
                        break;
                    case 2:
                        addContact(connection, scanner);
                        break;
                    case 3:
                        updateContact(connection, scanner);
                        break;
                    case 4:
                        deleteContact(connection, scanner);
                        break;
                    case 5:
                        System.out.println("退出中...");
                        connection.close();
                        scanner.close();
                        System.exit(0);
                    default:
                        System.out.println("无效输入...");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("MySQL JDBC驱动程序未找到。");
        }
    }

    private static void viewContacts(Connection connection) {
        try {
            String query = "SELECT * FROM " + CONTACTS_TABLE;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("id"));
                System.out.println("Name: " + resultSet.getString("name"));
                System.out.println("Address: " + resultSet.getString("address"));
                System.out.println("Phone: " + resultSet.getString("phone"));
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addContact(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter Name: ");
            String name = scanner.next();
            System.out.print("Enter Address: ");
            String address = scanner.next();
            System.out.print("Enter Phone: ");
            String phone = scanner.next();

            String query = "INSERT INTO " + CONTACTS_TABLE + " (name, address, phone) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, phone);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Contact added successfully.");
            } else {
                System.out.println("Failed to add contact.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateContact(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter the ID of the contact you want to update: ");
            int contactId = scanner.nextInt();

            // 检查联系人是否存在
            if (!contactExists(connection, contactId)) {
                System.out.println("Contact not found.");
                return;
            }

            System.out.print("Enter updated Name: ");
            String name = scanner.next();
            System.out.print("Enter updated Address: ");
            String address = scanner.next();
            System.out.print("Enter updated Phone: ");
            String phone = scanner.next();

            String query = "UPDATE " + CONTACTS_TABLE + " SET name=?, address=?, phone=? WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, phone);
            preparedStatement.setInt(4, contactId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Contact updated successfully.");
            } else {
                System.out.println("Failed to update contact.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteContact(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter the ID of the contact you want to delete: ");
            int contactId = scanner.nextInt();

            // 检查联系人是否存在
            if (!contactExists(connection, contactId)) {
                System.out.println("Contact not found.");
                return;
            }

            String query = "DELETE FROM " + CONTACTS_TABLE + " WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, contactId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Contact deleted successfully.");
            } else {
                System.out.println("Failed to delete contact.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean contactExists(Connection connection, int contactId) {
        try {
            String query = "SELECT * FROM " + CONTACTS_TABLE + " WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, contactId);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next(); // 如果存在，返回 true
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
