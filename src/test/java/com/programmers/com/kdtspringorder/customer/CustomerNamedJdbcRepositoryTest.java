package com.programmers.com.kdtspringorder.customer;

import com.wix.mysql.EmbeddedMysql;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.mockito.internal.util.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringJUnitConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerNamedJdbcRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(CustomerNamedJdbcRepositoryTest.class);


    @Configuration
    @ComponentScan(
            basePackages = {"com.programmers.com.kdtspringorder.customer"}
    )
    static class Config {

        @Bean
        public DataSource dataSource() {
            var dataSource = DataSourceBuilder.create()
                    .url("jdbc:mysql://192.168.0.2:3380/order_mgmt")
                    .username("username")
                    .password("password")
                    .type(HikariDataSource.class)
                    .build();
//            dataSource.setMaximumPoolSize(1000);
//            dataSource.setMinimumIdle(100);
            return dataSource;
        }

        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

        @Bean
        public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
            return new NamedParameterJdbcTemplate(jdbcTemplate);
        }

        @Bean
        public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean
        public TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager) {
            return new TransactionTemplate(platformTransactionManager);
        }


    }

    @Autowired
    CustomerNamedJdbcRepository customerNamedJdbcRepository;

    @Autowired
    DataSource dataSource;

    Customer newCustomer;

    EmbeddedMysql embeddedMysql;

    @BeforeAll
    void setup() {
        newCustomer = new Customer(UUID.randomUUID(), "test-use1r", "test11-user@gmail.com", LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
        customerNamedJdbcRepository.deleteAll();
//        var mysqlConfig = aMysqldConfig(v8_0_11)
//                .withCharset(UTF8)
//                .withPort(2215)
//                .withUser("test", "test1234!")
//                .withTimeZone("Asia/Seoul")
//                .build();
//
//        embeddedMysql = anEmbeddedMysql(mysqlConfig)
//                .addSchema("test-order_mgmt", classPathScript("schema.sql"))
//                .start();
    }

//    @AfterAll
//    void cleanup() {
//        embeddedMysql.stop();
//    }

    @Test
    @Order(1)
    public void testHikariConnectionPool() {
        assertThat(dataSource.getClass().getName(), is("com.zaxxer.hikari.HikariDataSource"));
    }

    @Test
    @Order(2)
    @DisplayName("고객을 추가할 수 있다.")
    public void testInsert() throws InterruptedException {
        try {
            customerNamedJdbcRepository.insert(newCustomer);
        } catch (BadSqlGrammarException e) {
            logger.error("Got BadSqlGrammarException error code -> {}", e.getSQLException().getErrorCode(), e);
        }

        var retrievedCustomer = customerNamedJdbcRepository.findById(newCustomer.getCustomerID());
        assertThat(retrievedCustomer.isEmpty(), is(false));
        assertThat(retrievedCustomer.get(), samePropertyValuesAs(newCustomer));
    }

    @Test
    @Order(3)
    @DisplayName("전체 고객을 조회할 수 있다.")
    public void testFindAll() throws InterruptedException {
        var customers = customerNamedJdbcRepository.findAll();
        assertThat(customers.isEmpty(), is(false));
//        Thread.sleep(100000);
    }

    @Test
    @Order(4)
    @DisplayName("이름으로 고객을 조회할 수 있다.")
    public void testFindByName() throws InterruptedException {
        var customer = customerNamedJdbcRepository.findByName(newCustomer.getName());
        assertThat(customer.isEmpty(), is(false));

        var unknown = customerNamedJdbcRepository.findByName("unknown-user");
        assertThat(unknown.isEmpty(), is(true));
    }

    @Test
    @Order(5)
    @DisplayName("이메일로 고객을 조회할 수 있다.")
    public void testFindByEmail() throws InterruptedException {
        var customer = customerNamedJdbcRepository.findByEmail(newCustomer.getEmail());
        assertThat(customer.isEmpty(), is(false));

        var unknown = customerNamedJdbcRepository.findByName("unknown-user@gmail.com");
        assertThat(unknown.isEmpty(), is(true));
    }

    @Test
    @Order(6)
    @DisplayName("고객을 수정할 수 있다.")
    public void testUpdate() throws InterruptedException {
        newCustomer.changeName("updated_user");
        customerNamedJdbcRepository.update(newCustomer);

        var all = customerNamedJdbcRepository.findAll();
        assertThat(all, hasSize(1));
        assertThat(all, everyItem(samePropertyValuesAs(newCustomer)));

        var retrievedCustomer = customerNamedJdbcRepository.findById(newCustomer.getCustomerID());
        assertThat(retrievedCustomer.isEmpty(), is(false));
        assertThat(retrievedCustomer.get(), samePropertyValuesAs(newCustomer));
    }

    @Test
    @Order(7)
    @DisplayName("트렌젝션 테스트")
    public void testTransaction() throws InterruptedException {
        var prevOne = customerNamedJdbcRepository.findById(newCustomer.getCustomerID());
        assertThat(prevOne.isEmpty(), is(false));
        var newOne = new Customer(UUID.randomUUID(), "a", "a@gmail.com", LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
        var insertedNewOne = customerNamedJdbcRepository.insert(newOne);
        try {
            customerNamedJdbcRepository.testTransaction(
                    new Customer(insertedNewOne.getCustomerID(),
                        "b", prevOne.get().getEmail(),
                        newOne.getCreatedAt()));
        } catch (DataAccessException e) {
            logger.error("Got error when testing transaction", e);
        }

        var maybeNewOne = customerNamedJdbcRepository.findById(insertedNewOne.getCustomerID());
        assertThat(maybeNewOne.isEmpty(), is(false));
        assertThat(maybeNewOne.get(), samePropertyValuesAs(newOne));

    }


}