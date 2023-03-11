package com.example.demo.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import java.io.IOException;
import java.util.Map;

@Configuration
@EnableJms
public class JmsConfig {

    @Value("${active-mq-username}")
    private String activeMqUsername;

    @Value("${active-mq-password}")
    private String activeMqPassword;

    @Autowired
    private AWSSecretsManager awsSecretsManager;

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL("tcp://localhost:61616");
        factory.setUserName(activeMqUsername);
        factory.setPassword(activeMqPassword);
        return factory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory){
        DefaultJmsListenerContainerFactory jmsListenerContainerFactory = new DefaultJmsListenerContainerFactory();

        jmsListenerContainerFactory.setConnectionFactory(connectionFactory);
        jmsListenerContainerFactory.setConcurrency("5-10");

        return  jmsListenerContainerFactory;
    }


    //Retrieve credentials from AWSSecretManager Client and set system properties
    @PostConstruct
    public void init() {
        String secretName = "local/activemq/demo";
        String region = "us-east-1";

        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secretName);
        GetSecretValueResult getSecretValueResult = awsSecretsManager.getSecretValue(getSecretValueRequest);

        if (getSecretValueResult.getSecretString() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String, String> secretsMap = objectMapper.readValue(getSecretValueResult.getSecretString(), Map.class);
                System.setProperty("active-mq-username", secretsMap.get("username"));
                System.setProperty("active-mq-password", secretsMap.get("password"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
