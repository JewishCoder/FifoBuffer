package com.fifobuffer.services;

import com.fifobuffer.IDisposable;
import com.fifobuffer.bufferReaders.IBufferReader;
import com.fifobuffer.consumers.SimpleConsumer;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class ConsumerService<T> implements IDisposable{
	class TimerCallBack extends TimerTask{

		private ConsumerService<T> Parent;

		public TimerCallBack(ConsumerService parent){
			this.Parent=parent;
		}

		@Override
		public void run(){
			for(var i = 0; i<Parent.Consumers.size(); i++){
				Parent.Consumers.get(i).consume(Parent.ConsumeData);
			}

		}
	}

	private int Count;
	private long Frequency;
	private IBufferReader ConsumeData;
	private Timer Trigger;
	private boolean IsDisposed;
	private Function<String,  SimpleConsumer> Factory;
	private ArrayList<SimpleConsumer> Consumers;


	public ConsumerService(
			int count,
			long frequencyMills,
			IBufferReader consumeData,
			Function<String, SimpleConsumer> consumeFactory){
		this.Count = count;
		this.Frequency = frequencyMills;
		this.Factory = consumeFactory;
		this.Consumers = new ArrayList<SimpleConsumer>();
		this.ConsumeData = consumeData;
	}

	public void startConsuming() throws Exception{
		Consumers.clear();
		for(var i=0; i < Count; i++){
			Consumers.add(Factory.apply(String.format("Consumer %d", i + 1)));
		}
		Trigger = new Timer("ProducerTrigger");
		Trigger.schedule(new TimerCallBack(this),0, Frequency);
	}

	public void closeInAbsenceData() throws InterruptedException{
		for(var i=0; i < Count; i++){
			Consumers.get(i).closeInAbsenceData();
		}
		while(true){
			var isAllClosed=0;
			for(var i=0;i<Count;i++){
				if(!Consumers.get(i).getState()){
					isAllClosed++;
				}
			}
			if(isAllClosed==Count){
				return;
			}
			TimeUnit.MILLISECONDS.sleep(Frequency);
		}

	}

	@Override
	public boolean getIsDisposed(){
		return IsDisposed;
	}

	@Override
	public void close() throws Exception{
		if(IsDisposed) return;

		Trigger.cancel();
		Trigger = null;
		for(var i = 0; i< Consumers.size(); i++){
			Consumers.get(i).close();
		}
	}
}
