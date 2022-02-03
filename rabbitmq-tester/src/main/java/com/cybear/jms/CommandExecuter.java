package com.cybear.jms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandExecuter {
	
	private static final Logger logger = LogManager.getLogger(CommandExecuter.class);	
	private static CommandExecuter instance = new CommandExecuter(1, 10);	
	int min = -1;
	int max = -1;
	
	private CommandExecuter(int max, int min) {
		this.min = min;
		this.max = max;
	}
	
	public static CommandExecuter getInstance()
	{
		return instance;
	}
	
	public void execute(String consumerTag, String commandName, String payload) {
		try {
			long randMillis = getRandomNumber() * 1000;
			long s = System.currentTimeMillis();
			logger.debug("CommandExceuter start exec: commandName {} consumerTag {} payload {}", commandName, consumerTag, payload);			
			Thread.sleep(randMillis);
			long e = System.currentTimeMillis();
			logger.debug("CommandExceuter Done exec: {} within {} millis", commandName, e - s);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public int getRandomNumber() {
	    return (int) ((Math.random() * (max - min)) + min);
	}

}
