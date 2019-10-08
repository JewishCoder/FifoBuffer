package com.fifobuffer.bufferReaders;

import com.fifobuffer.IDisposable;
import com.fifobuffer.valueConverters.IValueConverter;

public interface IBufferReader extends IDisposable{
	public String Read(int bufferCount, IValueConverter converter);
}
