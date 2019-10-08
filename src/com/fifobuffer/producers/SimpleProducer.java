package com.fifobuffer.producers;

import com.fifobuffer.IDisposable;
import com.fifobuffer.StatisticsCollector;
import com.fifobuffer.bufferWriters.IBufferWriter;
import com.fifobuffer.valueConverters.SimpleValueConverter;

import java.util.Date;

public class SimpleProducer implements IProducer<IBufferWriter> {

	class ThreadCallBack implements Runnable{

		public boolean CanProduce;
		public IBufferWriter BufferWriter;
		public boolean IsStarted;

		private Object SyncRoot;
		private String Name;
		private SimpleValueConverter Converter;
		private StatisticsCollector Collector;

		public ThreadCallBack(String name,StatisticsCollector collector){
			this.SyncRoot = new Object();
			this.Name = name;
			this.Converter = new SimpleValueConverter("#");
			this.Collector = collector;
		}

		@Override
		public void run(){
			while(IsStarted){
			synchronized(SyncRoot){
					if(CanProduce){
						if(BufferWriter == null) return;

						BufferWriter.write(Converter.ToBytes(Name));
						Collector.produceIncrement();
						CanProduce = false;
						//System.out.println(String.format("%s write to file", Name));
					}
				}
			}
		}
	}

	private String Name;
	private Thread ThreadProducer;
	private ThreadCallBack AsyncProducer;
	private boolean IsDisposed;

	public SimpleProducer(String name, StatisticsCollector collector){
		this.Name = name;
		this.AsyncProducer = new ThreadCallBack(name, collector);
		this.AsyncProducer.IsStarted = true;
		this.ThreadProducer = new Thread(AsyncProducer, name);
		this.ThreadProducer.start();
	}

	@Override
	public void produce(IBufferWriter args){
		AsyncProducer.BufferWriter = args;
		AsyncProducer.CanProduce = true;
	}

	@Override
	public boolean getIsDisposed(){
		return IsDisposed;
	}

	@Override
	public void close() throws Exception{
		if(IsDisposed) return;
		AsyncProducer.CanProduce = false;
		AsyncProducer.IsStarted = false;
		ThreadProducer.interrupt();
		IsDisposed = true;
	}
}
