package com.programmers.com.kdtspringorder;

import com.programmers.com.kdtspringorder.customer.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JdbcCustomerRepository {
    private static final Logger logger = LoggerFactory.getLogger(JdbcCustomerRepository.class);
    private final String SELECT_SQL = "select * from customers WHERE name = ?";
    private final String SELECT_ALL_SQL = "select * from customers";
    private final String INSERT_SQL = "INSERT INTO customers(customer_id, name, email) VALUES (UUID_TO_BIN(?), ?, ?)";
    private final String UPDATE_BY_ID_SQL = "UPDATE customers SET name = ? WHERE customer_id = UUID_TO_BIN(?)";
    private final String DELETE_ALL_SQL = "DELETE FROM customers";

    public List<String> findNames(String name) {
//        var SELECT_SQL = "select * from customers WHERE name = '%s'".formatted(name);
        List<String> names = new ArrayList<>();


//        Connection connection = null;
//        Statement statement = null;
//        ResultSet resultSet = null;

        try (
                var connection = DriverManager.getConnection("jdbc:mysql://192.168.0.2:3380/order_mgmt?useSSL=false&serverTimezone=Asia/Seoul", "username", "password");
//            var statement = connection.createStatement();
                var statement = connection.prepareStatement(SELECT_SQL);

        ) {
            statement.setString(1, name);
            logger.info("statement -> {}", statement);
            try (
                    var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    var customerName = resultSet.getString("name");
                    var customerId = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
                    var createdAt = resultSet.getTime("created_at").toLocalTime();
                    logger.info("customer id -> {}, name -> {}, createdAt -> {}", customerId, customerName, createdAt);
                    names.add(customerName);
                }
            }
        } catch (SQLException throwables) {
            logger.error("Got error while closing connection", throwables);
//            throw throwables;
        }
//        finally {
//            try {
//                if (connection != null) connection.close();
//                if (statement != null) statement.close();
//                if (resultSet != null) resultSet.close();
//            } catch (SQLException exception) {
//                logger.error("Got error while closing connection", exception);
//            }
//        }

        return names;
    }

    public List<String> findAllName() {
        List<String> names = new ArrayList<>();

        try (
                var connection = DriverManager.getConnection("jdbc:mysql://192.168.0.2:3380/order_mgmt?useSSL=false&serverTimezone=Asia/Seoul", "username", "password");
                var statement = connection.prepareStatement(SELECT_ALL_SQL);

        ) {
            try (
                    var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    var customerName = resultSet.getString("name");
                    var customerId = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
                    var createdAt = resultSet.getTime("created_at").toLocalTime();
                    logger.info("customer id -> {}, name -> {}, createdAt -> {}", customerId, customerName, createdAt);
                    names.add(customerName);
                }
            }
        } catch (SQLException throwables) {
            logger.error("Got error while closing connection", throwables);
        }
        return names;
    }

    public List<UUID> findAllIds() {
        List<UUID> uuids = new ArrayList<>();

        try (
                var connection = DriverManager.getConnection("jdbc:mysql://192.168.0.2:3380/order_mgmt?useSSL=false&serverTimezone=Asia/Seoul", "username", "password");
                var statement = connection.prepareStatement(SELECT_ALL_SQL);

        ) {
            try (
                    var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    var customerName = resultSet.getString("name");
//                    var byteBuffer = ByteBuffer.wrap(resultSet.getBytes("customer_id"));
//                    var customerId = UUID.nameUUIDFromBytes();
                    var customerId = toUUID(resultSet.getBytes("customer_id"));
                    var createdAt = resultSet.getTime("created_at").toLocalTime();
                    logger.info("customer id -> {}, name -> {}, createdAt -> {}", customerId, customerName, createdAt);
                    uuids.add(customerId);
                }
            }
        } catch (SQLException throwables) {
            logger.error("Got error while closing connection", throwables);
        }
        return uuids;
    }

    public int insertCustomer(UUID customerId, String name, String email) {
        try (
                var connection = DriverManager.getConnection("jdbc:mysql://192.168.0.2:3380/order_mgmt?useSSL=false&serverTimezone=Asia/Seoul", "username", "password");
                var statement = connection.prepareStatement(INSERT_SQL);

        ) {
            statement.setBytes(1, customerId.toString().getBytes());
            statement.setString(2, name);
            statement.setString(3, email);
            return statement.executeUpdate();
        } catch (SQLException throwables) {
            logger.error("Got error while closing connection", throwables);
        }
        return 0;
    }

    public int updateCustomerName(UUID customerId, String name) {
        try (
                var connection = DriverManager.getConnection("jdbc:mysql://192.168.0.2:3380/order_mgmt?useSSL=false&serverTimezone=Asia/Seoul", "username", "password");
                var statement = connection.prepareStatement(UPDATE_BY_ID_SQL);

        ) {
            statement.setString(1, name);
            statement.setBytes(2, customerId.toString().getBytes());
            return statement.executeUpdate();
        } catch (SQLException throwables) {
            logger.error("Got error while closing connection", throwables);
        }
        return 0;
    }


    public int deleteAllCustomers() {
        try (
                var connection = DriverManager.getConnection("jdbc:mysql://192.168.0.2:3380/order_mgmt?useSSL=false&serverTimezone=Asia/Seoul", "username", "password");
                var statement = connection.prepareStatement(DELETE_ALL_SQL);

        ) {
            return statement.executeUpdate();
        } catch (SQLException throwables) {
            logger.error("Got error while closing connection", throwables);
        }
        return 0;
    }

    public void transactionTest(Customer customer) {
        String updateNameSql = "UPDATE customers SET name = ? WHERE customer_id = UUID_TO_BIN(?)";
        String updateEmailSql = "UPDATE customers SET email = ? WHERE customer_id = UUID_TO_BIN(?)";


        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.0.2:3380/order_mgmt?useSSL=false&serverTimezone=Asia/Seoul", "username", "password");
            try (
                    var updateNameStatement = connection.prepareStatement(updateNameSql);
                    var updateEmailStatement = connection.prepareStatement(updateEmailSql);
            ) {
                connection.setAutoCommit(false);
                updateNameStatement.setString(1, customer.getName());
                updateNameStatement.setBytes(2, customer.getCustomerID().toString().getBytes());
                updateNameStatement.execute();

                updateEmailStatement.setString(1, customer.getEmail());
                updateEmailStatement.setBytes(2, customer.getCustomerID().toString().getBytes());
                updateEmailStatement.executeUpdate();
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            if (connection != null) {
                try {
                    connection.rollback();
                    connection.close();
                } catch (SQLException throwable) {
                    logger.error("Got error while closing connection", throwable);
                    throw new RuntimeException(exception);
                }
            }
            logger.error("Got error while closing connection", exception);
            throw new RuntimeException(exception);
        }


    }

    public static UUID toUUID(byte[] bytes) {
        var byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }

    public static void main(String[] args) {
        var customerRepository = new JdbcCustomerRepository();

        customerRepository.transactionTest(new Customer(UUID.fromString("b0e55fcf-fdc3-401f-bba0-7bfaff36f0bc"), "updated-user", "new-user2@gmail.com", LocalDateTime.now()));
//        var count = customerRepository.deleteAllCustomers();
//        logger.info("deleted count -> {}", count);
//
//        var customerId = UUID.randomUUID();
//        logger.info("created customerId -> {}", customerId);
//        logger.info("created UUID Version -> {}", customerId.version());
//        customerRepository.insertCustomer(customerId, "new-user", "new-user@gmail.com");
//        customerRepository.insertCustomer(customerId, "new-user2", "new-user2@gmail.com");

//        customerRepository.findAllName().forEach(v -> logger.info("Found name : {}", v));

//        customerRepository.updateCustomerName(customer2, "updated-user2");
//        customerRepository.findAllIds().forEach(v -> logger.info("Found customerId : {} and version : {}", v, v.version()));
//
//        var names = new JdbcCustomerRepository().findNames("tester01' OR 'a'='a");
//        names.forEach(v -> logger.info("Found name : {}", v));
    }


}
