package com.fifobuffer.consumers;

import com.fifobuffer.IDisposable;

import java.util.EventListener;

public interface IConsumer<T> extends EventListener, IDisposable{
	public void consume(T args);
}
