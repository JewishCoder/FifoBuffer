package com.fifobuffer.services;

import com.fifobuffer.IDisposable;
import com.fifobuffer.producers.IProducer;

import java.beans.Expression;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class ProducerService<T> implements IDisposable{

	class TimerCallBack extends TimerTask{

		private ProducerService<T> Parent;

		public TimerCallBack(ProducerService parent){
			this.Parent=parent;
		}

		@Override
		public void run(){
			for(var i=0;i<Parent.Producers.size();i++){
				Parent.Producers.get(i).produce(Parent.ProduceData);
			}

		}
	}

	private int Count;
	private long Frequency;
	private long LifeTime;
	private T ProduceData;
	private Timer Trigger;
	private Timer TimerToDeath;
	private boolean IsDisposed;
	private Function<String,IProducer<T>> Factory;
	private ArrayList<IProducer<T>> Producers;


	public ProducerService(
			int count,
			long frequencyMills,
			long lifeTimeMills,
			T produceData,
			Function<String,IProducer<T>> producerFactory){
		this.Count = count;
		this.Frequency = frequencyMills;
		this.Factory = producerFactory;
		this.LifeTime =  lifeTimeMills;
		this.Producers = new ArrayList<IProducer<T>>();
		this.ProduceData=produceData;
	}

	public void startProducing() throws Exception{
		Producers.clear();
		for(var i=0; i < Count; i++){
			Producers.add(Factory.apply(String.format("Producer %d", i + 1)));
		}
		Trigger = new Timer("ProducerTrigger");
		Trigger.schedule(new TimerCallBack(this),0, Frequency);

		Thread.sleep(LifeTime);

		close();
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
		for(var i=0;i<Producers.size();i++){
			Producers.get(i).close();
		}
	}
}
