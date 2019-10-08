package com.fifobuffer;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class StatisticsCollector implements IDisposable{

	class TimerCallBack extends TimerTask{

		private StatisticsCollector Collector;

		public TimerCallBack(StatisticsCollector collector){
			this.Collector=collector;
		}

		@Override
		public void run(){
			System.out.println("===============================================");
			System.out.println(String.format(
					"generated data: %s\nconsumed data: %s\ncollection time: %s seconds\nremaining time: %s secords",
					Collector.ProdusedCount,
					Collector.ConsumedCount,
					getWorkingTime(),
					getTimeToEnd()));
			System.out.println("===============================================");
		}
	}

	private long ProdusedCount;
	private long ConsumedCount;
	private Date Started;
	private long TimeToEnd;
	private Timer CollectorThread;
	private boolean IsDisposed;
	private TimerCallBack CallBack;

	public StatisticsCollector(long timeToEnd, long period){
		Started=new Date();
		TimeToEnd=timeToEnd;
		CallBack=new TimerCallBack(this);
		CollectorThread=new Timer();
		CollectorThread.schedule(CallBack, 0, period);
	}

	public synchronized void produceIncrement(){
		ProdusedCount++;
	}

	public synchronized void consumeIncrement(){
		ConsumedCount++;
	}

	public double getWorkingTime(){
		var now = new Date();
		var diff = now.getTime() - Started.getTime();

		return diff / 1000.0;
	}

	public double getTimeToEnd(){

		return Math.round(TimeToEnd / 1000.0 - getWorkingTime());
	}

	@Override
	public boolean getIsDisposed(){
		return IsDisposed;
	}

	@Override
	public void close() throws Exception{
		if(IsDisposed) return;

		CollectorThread.cancel();
		CollectorThread = null;
		CallBack.run();
		IsDisposed=true;
	}
}
