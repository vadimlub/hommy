package com.cybear.jms;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

public class RobosocCommandConsumer extends DefaultConsumer {

	Logger logger = LogManager.getLogger(RobosocCommandConsumer.class);
	int min = 1;
	int max = 5;
	int threadNumber = 5;
	
	final ExecutorService threadPool =  Executors.newFixedThreadPool(threadNumber);
	
	private String commandName = null;
	
	public RobosocCommandConsumer(Channel channel, String commandName) {
		super(channel);
		this.commandName = commandName;
	}
	
	@Override
    public void handleDelivery(String consumerTag,
                               Envelope envelope,
                               AMQP.BasicProperties properties,
                               byte[] body)
        throws IOException
    {
		String routingKey = envelope.getRoutingKey();        
        long deliveryTag = envelope.getDeliveryTag();                
        MessageCommand command = new MessageCommand(consumerTag, deliveryTag, routingKey, body);   
        //CommandExecuter.getInstance().execute(consumerTag, commandName, new String(body, "UTF-8"));
        //Future<Integer> res = execute(consumerTag, commandName, new String(body));
        execute(consumerTag, commandName, new String(body));
        /*
        try {
			logger.debug("CommandExceuter Done exec: {} within {} millis", commandName, res.get());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		
        
        	
    }	
	
	@Override
	public void handleShutdownSignal(String consumerTag,
			ShutdownSignalException sig) {
		// Called when a connection or a channel get closed
		super.handleShutdownSignal(consumerTag, sig);
	}
	
	@Override
	public void handleConsumeOk(String consumerTag) {		
		super.handleConsumeOk(consumerTag);
		logger.debug("Handle consume ok {}", consumerTag);
	}
	
	public Future<Integer> execute(String consumerTag, String commandName, String payload) {		
		return threadPool.submit(() -> {				
				long randMillis = getRandomNumber() * 1000;
				long s = System.currentTimeMillis();
				logger.debug("CommandExceuter start exec: commandName {} consumerTag {} payload {}", commandName, consumerTag, payload);			
				Thread.sleep(randMillis);
				long e = System.currentTimeMillis();	
				logger.debug("CommandExceuter Done exec: {} within {} millis", commandName, e - s);
				return Integer.valueOf((int)(e - s));				
		});	
	}
	
	public int getRandomNumber() {
	    return (int) ((Math.random() * (max - min)) + min);
	}
	
	class MessageCommand
	{
		String consumerTag = null;
		long deliveryTag = 0L;
		String routingKey = null;
		byte [] payload = null;	
		
		MessageCommand(String consTag, long deliverTag, String routeKey, byte [] load) {
			consumerTag = consTag;
			deliveryTag = deliverTag;
			routingKey = routeKey;
		}
	}

}
