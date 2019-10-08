package com.fifobuffer.producers;

import com.fifobuffer.IDisposable;

import java.sql.Timestamp;
import java.util.EventListener;
import java.util.Observer;


public interface IProducer<T> extends EventListener, IDisposable{

	public void produce(T args);

}
