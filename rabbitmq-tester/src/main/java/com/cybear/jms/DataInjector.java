package com.cybear.jms;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.stream.IntStream;

public class DataInjector {
	
	public static void main(String[] args) throws Exception {
		MessageStore.getInstance().init();
		FileInputStream fos = new FileInputStream("d:\\develop\\temp\\sys.data");
		byte [] data = fos.readAllBytes();
		fos.close();
		IntStream.range(1, 100).forEach(i -> {
			try {
				MessageStore.getInstance().publishMessage("QName1", data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});			
	}
}