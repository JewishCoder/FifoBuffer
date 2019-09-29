package com.fifobuffer;

import com.fifobuffer.producers.IProducer;
import jdk.jfr.Event;

import javax.swing.event.EventListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Consumer;

public class Main {

	class Writer implements IDisposable{
		class TimerCallBack extends TimerTask{


			@Override
			public void run(){
				if(cancel()){
					return;

				}

			}
		}


		private ArrayList<IProducer> Producers;
		//Время жизни.
		private Timestamp LifeTime;
		//Частота записи в буффер.
		private Timestamp Frequency;

		private  boolean IsDisposed;

		private Timer Trigger;

		Writer(Collection<IProducer> producers, Timestamp lifeTime, Timestamp frequency){
			if(producers == null || lifeTime == null || frequency == null){
				throw new NullPointerException("arguments can't be null");
			}
			if(producers.size() == 0){
				throw new IllegalArgumentException("producers must be greater than zero");
			}

			this.Producers = new ArrayList<IProducer>(producers);
			this.LifeTime  = lifeTime;
			this.Frequency = frequency;
			this.Trigger = new Timer();
			this.Trigger.schedule(new TimerCallBack(),0,LifeTime.getTime());


		}

		private  void CallBack(IProducer p){};

		public void Start(){

		}

		@Override
		public boolean getIsDisposed(){
			return IsDisposed;
		}

		@Override
		public void close() throws Exception{

		}
	}

    public static void main(String[] args) {
        System.out.println("hello");
    }
}
