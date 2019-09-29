package com.fifobuffer.producers;

import java.sql.Timestamp;
import java.util.EventListener;
import java.util.Observer;


public interface IProducer<T> extends EventListener{

	public void produce(T args);

}
