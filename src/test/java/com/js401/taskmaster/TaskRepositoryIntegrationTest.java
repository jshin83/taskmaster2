package com.js401.taskmaster;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TaskmasterApplication.class)
@WebAppConfiguration
@ActiveProfiles("local")
public class TaskRepositoryIntegrationTest {

    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    TaskRepository repository;

    private static final String EXPECTED_DESCRIPTION = "20";
    private static final String EXPECTED_TITLE = "hello world";

    @Before
    public void setup() throws Exception {
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(Task.class);

        tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        dynamoDBMapper.batchDelete((List<Task>)repository.findAll());
    }

    @Test
    public void readWriteTestCase() {
        Task dave = new Task(EXPECTED_TITLE, EXPECTED_DESCRIPTION);
        System.out.println(dave.toString());
        repository.save(dave);

        List<Task> result = (List<Task>) repository.findAll();

        assertTrue("Not empty", result.size() > 0);
        assertEquals("Contains task with expected title", EXPECTED_TITLE, result.get(0).getTitle());
        assertEquals("Contains task with expected description", EXPECTED_DESCRIPTION, result.get(0).getDescription());
        assertEquals("Contains task with expected status", "available", result.get(0).getStatus());
    }
}
