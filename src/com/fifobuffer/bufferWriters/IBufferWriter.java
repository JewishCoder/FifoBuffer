package com.fifobuffer.bufferWriters;

import com.fifobuffer.IDisposable;

public interface IBufferWriter extends IDisposable{

	public void write(byte[] data);
}
