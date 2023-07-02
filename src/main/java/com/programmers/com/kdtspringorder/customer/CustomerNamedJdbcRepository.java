package com.programmers.com.kdtspringorder.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.*;

import static com.programmers.com.kdtspringorder.JdbcCustomerRepository.toUUID;

@Repository
public class CustomerNamedJdbcRepository implements CustomerRepository {

    private static final Logger logger = LoggerFactory.getLogger(CustomerNamedJdbcRepository.class);
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CustomerNamedJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Map<String, Object> toParamMap(Customer customer) {
        return new HashMap<>() {{
            put("customerId", customer.getCustomerID().toString().getBytes());
            put("name", customer.getName());
            put("email", customer.getEmail());
            put("createdAt", Timestamp.valueOf(customer.getCreatedAt()));
            put("lastLoginAt", customer.getLastLoginAt() != null ? Timestamp.valueOf(customer.getLastLoginAt()): null);
        }};
    }

    @Override
    public Customer insert(Customer customer) {
        var update = jdbcTemplate.update("INSERT INTO customers(customer_id, name, email, created_at) VALUES (UUID_TO_BIN(:customerId), ?, ?, ?)",
                toParamMap(customer));
        if (update != 1) {
            throw new RuntimeException("Nothing was iserted");
        }
        return customer;
    }

    @Override
    public Customer update(Customer customer) {
        var update = jdbcTemplate.update("UPDATE customers SET name = :name, email = :email, last_login_at = :lastLoginAt where customer_id = UUID_TO_BIN(:customerId)",
                toParamMap(customer));
        if (update != 1) {
            throw new RuntimeException("Nothing was updated");
        }
        return customer;
    }

    @Override
    public int count() {
        return jdbcTemplate.queryForObject("select count(*) from customers", Collections.emptyMap(), Integer.class);
    }

    @Override
    public List<Customer> findAll() {
        return jdbcTemplate.query("select * from customers", customerRowMapper);
    }

    private static final RowMapper<Customer> customerRowMapper = (resultSet, i) -> {
        var customerName = resultSet.getString("name");
        var email = resultSet.getString("email");
        var customerId = toUUID(resultSet.getBytes("customer_id"));
        var lastLoginAt = resultSet.getTimestamp("last_login_at") != null ?
                resultSet.getTimestamp("last_login_at").toLocalDateTime() : null;
        var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        return new Customer(customerId, customerName, email, lastLoginAt, createdAt);
    };

    @Override
    public Optional<Customer> findById(UUID customerId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject("select * from customers WHERE customer_id = UUID_TO_BIN(:customerId)",
                    Collections.singletonMap("customerId", customerId.toString().getBytes()),
                    customerRowMapper));
        } catch (EmptyResultDataAccessException e) {
            logger.error("Got empty result");
            return Optional.empty();
        }

    }

    @Override
    public Optional<Customer> findByName(String name) {
        try {
            return Optional.of(jdbcTemplate.queryForObject("select * from customers WHERE name = :name",
                    Collections.singletonMap("customerId", name),
                    customerRowMapper));
        } catch (EmptyResultDataAccessException e) {
            logger.error("Got empty result");
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        try {
            return Optional.of(jdbcTemplate.queryForObject("select * from customers WHERE email = :email",
                    Collections.singletonMap("email", email),
                    customerRowMapper));
        } catch (EmptyResultDataAccessException e) {
            logger.error("Got empty result");
            return Optional.empty();
        }
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.getJdbcTemplate().update("DELETE FROM customers");
    }
}
