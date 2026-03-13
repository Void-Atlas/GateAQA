package ru.netology.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SQLHelper {

    private static final String url = "jdbc:mysql://localhost:3306/app";
    private static final String user = "app";
    private static final String password = "pass";

    public static String getPaymentStatus() {
        String status = "";

        String query = "SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1;";

        try (
                Connection conn = DriverManager.getConnection(url, user, password);
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(query)
        ) {
            if (rs.next()) {
                status = rs.getString("status");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }

    public static void cleanDatabase() {

        String deletePayments = "DELETE FROM payment_entity;";
        String deleteOrders = "DELETE FROM order_entity;";
        String deleteCredits = "DELETE FROM credit_request_entity;";

        try (
                Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement()
        ) {
            stmt.executeUpdate(deletePayments);
            stmt.executeUpdate(deleteOrders);
            stmt.executeUpdate(deleteCredits);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCreditStatus() {
        String status = "";

        String query = "SELECT status FROM credit_request_entity ORDER BY created DESC LIMIT 1;";

        try (
                Connection conn = DriverManager.getConnection(url, user, password);
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(query)
        ) {
            if (rs.next()) {
                status = rs.getString("status");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }
}