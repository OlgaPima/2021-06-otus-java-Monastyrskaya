package ru.otus.demo;

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DbServiceClientImpl;

import java.util.ArrayList;
import java.util.List;

public class DbServiceDemo {

    private static final Logger log = LoggerFactory.getLogger(DbServiceDemo.class);

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();
        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);
        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);

        log.info("---------- Client 1 ----------");
        var client1 = new Client("dbServiceFirst");
        client1.setAddress(new Address("Королев", "Космонавтов", "1"));
        client1.setPhones(List.of(new Phone("8-496-333-22-11"), new Phone("8-900-999-88-77")));
        dbServiceClient.saveClient(client1);

        log.info("---------- Client 2 ----------");
        var client2 = new Client("dbServiceSecond");
        client2.setAddress(new Address("Королев", "Пионерская", "25"));
        client2.setPhones(List.of(new Phone("8-495-000-11-22"), new Phone("8-900-444-55-66")));
        var clientSecond = dbServiceClient.saveClient(client2);

        var clientSecondSelected = dbServiceClient.getClient(clientSecond.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecond.getId()));
        log.info("clientSecondSelected:{}", clientSecondSelected);

        var testLazyClient = new Client(clientSecondSelected.getId(), "dbServiceSecondUpdated");
        dbServiceClient.saveClient(testLazyClient);
        var clientUpdated = dbServiceClient.getClient(clientSecondSelected.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecondSelected.getId()));
        log.info("------------------------");
        log.info("clientUpdated:{}", clientUpdated);

        log.info("All clients --------------- ");
        dbServiceClient.findAll().forEach(client -> log.info("client:{}", client));
    }
}
