package com.cybear.jms;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class TaskMessageQueue {
	
	private static final Logger logger = LogManager.getLogger(TaskMessageQueue.class);	
	private static final String EXCHANGE_NAME = "robosoc.scopes";	
	private static final String QUEUE_NAME = "queue_name";
	private static final String QUEUE_CONSUMER_TAG = "queue_consumer_tag";
	private static final String COMMAND_NAME = "command_name";
	private static final TaskMessageQueue instance = new TaskMessageQueue();
	private static final ConnectionFactory connectionFactory = new ConnectionFactory();
	private static Connection con = null;
	private static Channel channel = null;
	private List<Map<String, Object>> configList = null;	
	
	//jsonutils read from stream to read the config
	//extends the configurator interface
	
	
	public static TaskMessageQueue getInstance() {
    	return instance;
    }
	
	@SuppressWarnings("unchecked")
	public void init() throws Exception {    	  
    	InputStream is = ClassLoader.getSystemResourceAsStream("conf-mq.json");
    	ObjectMapper objectMapper = new ObjectMapper();
    	configList = objectMapper.readValue(is,List.class);    	
    	logger.debug("MessageStore init connection");
    	try { 
    		con = connectionFactory.newConnection();
    		channel = con.createChannel();    	
    		channel.exchangeDeclare(EXCHANGE_NAME, "topic"); 
    		configList.stream().forEach(this::initTopic);    		    			
    	} catch(Exception e) {
    		logger.error("error init message store", e);
    	}    	    		
    }    
    
    private void initConsumer(String consumerTag, String queueName, String commandName) throws IOException {    	
    	channel.basicConsume(queueName, true, consumerTag, new TaskConsumer(channel, commandName));
    	logger.debug("init consumer {} queueName {} CommandName {}", consumerTag, queueName, commandName);
    }
    
    public void publishMessage(String routingKey, String msg) throws IOException
    {
    	channel.basicPublish(EXCHANGE_NAME, routingKey, null, msg.getBytes("UTF-8"));
    }
    
    
    @SuppressWarnings("unchecked")
	private void initTopic(Map<String, Object> map)
    {    	
    	
    	String queueName = String.valueOf(map.get(QUEUE_NAME));
    	String consumerTag = String.valueOf(map.get(QUEUE_CONSUMER_TAG));
    	String commandName = String.valueOf(map.get(COMMAND_NAME));    	
		try {
			createQueue(queueName);
			initConsumer(consumerTag, queueName, commandName);
		} catch(IOException e) {
			logger.error("error in initConsumer part in MessageStore:initTopic", e);
		}    	
    }    
    
    private void createQueue(String queueName) throws IOException
    {    
		String q = channel.queueDeclare(queueName, true, false, false, null).getQueue();
		channel.queueBind(q, EXCHANGE_NAME, queueName);
		logger.debug("queueName: {}", queueName);    	
    }
}
