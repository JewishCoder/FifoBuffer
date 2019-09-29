package com.fifobuffer.services;

import com.fifobuffer.IDisposable;
import com.fifobuffer.producers.IProducer;

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
	private Supplier<IProducer<T>> Factory;
	private ArrayList<IProducer<T>> Producers;


	public ProducerService(
			int count,
			long frequencyNanos,
			long lifeTimeNanos,
			T produceData,
			Supplier<IProducer<T>> producerFactory){
		this.Count = count;
		this.Frequency = frequencyNanos;
		this.Factory = producerFactory;
		this.LifeTime = lifeTimeNanos;
		this.Producers = new ArrayList<IProducer<T>>();
		this.ProduceData=produceData;
	}

	public void startProducing(){
		Producers.clear();
		for(var i=0; i < Count; i++){
			Producers.add(Factory.get());
		}
		Trigger = new Timer("ProducerTrigger");
		Trigger.schedule(new TimerCallBack(this),0, Frequency);

		var timeOfDeath= new Date().toInstant().plus(LifeTime, ChronoUnit.NANOS);
		TimerToDeath = new Timer("DestroyTrigger");
		TimerToDeath.schedule(new TimerTask(){
			@Override
			public void run(){
				try{
					close();
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		},Date.from(timeOfDeath));
	}

	@Override
	public boolean getIsDisposed(){
		return IsDisposed;
	}

	@Override
	public void close() throws Exception{
		if(IsDisposed) return;

		Trigger.cancel();
		TimerToDeath.cancel();
		Trigger = null;
		TimerToDeath = null;
	}
}
