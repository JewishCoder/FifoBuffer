package com.fifobuffer.producers;

import com.fifobuffer.bufferWriters.IBufferWriter;

import java.util.Date;

public class SimpleProducer implements IProducer<IBufferWriter>{

	private String Name;
	private Thread T;

	public SimpleProducer(String name){
		Name = name;
		T=new Thread(new Runnable(){
			@Override
			public void run(){
			}
		});
	}

	@Override
	public void produce(IBufferWriter args){
		if(args == null) return;
		var currentDate = new Date().toString();
		var data = String.format("%s - %s |",Name, currentDate);

		args.write(data.getBytes());
	}
}
