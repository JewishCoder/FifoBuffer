package com.fifobuffer.consumers;

import com.fifobuffer.StatisticsCollector;
import com.fifobuffer.bufferReaders.IBufferReader;
import com.fifobuffer.valueConverters.IValueConverter;
import com.fifobuffer.valueConverters.SimpleValueConverter;

public class SimpleConsumer implements IConsumer<IBufferReader>{

	class ThreadCallBack implements Runnable{

		public boolean CanConsume;
		public IBufferReader BufferReader;
		public boolean IsStarted;
		public boolean IsAutoClose;

		private Object SyncRoot;
		private String Name;
		private SimpleValueConverter Converter;
		private StatisticsCollector Collector;

		public ThreadCallBack(String name, StatisticsCollector collector){
			this.Name=name;
			this.SyncRoot=new Object();
			this.Converter=new SimpleValueConverter("#");
			this.Collector=collector;
		}

		@Override
		public void run(){
			while(IsStarted){
				synchronized(SyncRoot){
					if(CanConsume){
						if(BufferReader == null) return;

						var value = BufferReader.Read(36, Converter);
						if(value==null && IsAutoClose){
							IsStarted = false;
							CanConsume = false;
							break;
						}
						if(value != null && !value.isEmpty()){
							System.out.println(String.format("%s read value %s", Name, value));
							Collector.consumeIncrement();
						}
						CanConsume = false;
					}
				}
			}
		}
	}

	private String Name;
	private IValueConverter Converter;
	private Thread ThreadConsumer;
	private ThreadCallBack CallBack;
	private boolean IsDisposed;

	public SimpleConsumer(String name, StatisticsCollector collector){
		if(name==null || name.isEmpty()){
			throw new IllegalArgumentException();
		}

		this.CallBack = new ThreadCallBack(name,collector);
		this.CallBack.IsStarted = true;
		this.ThreadConsumer = new Thread(CallBack, name);
		this.ThreadConsumer.start();
	}

	public void closeInAbsenceData(){
		CallBack.IsAutoClose = true;
	}

	@Override
	public void consume(IBufferReader args){
		CallBack.BufferReader = args;
		CallBack.CanConsume = true;
	}

	public boolean getState(){
		return  CallBack.IsStarted;
	}

	@Override
	public boolean getIsDisposed(){
		return IsDisposed;
	}

	@Override
	public void close() throws Exception{
		if(IsDisposed) return;
		CallBack.IsStarted = false;
		CallBack.CanConsume = false;
		ThreadConsumer.interrupt();
		IsDisposed = true;
	}
}
