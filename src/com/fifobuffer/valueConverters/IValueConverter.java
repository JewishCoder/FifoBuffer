package com.fifobuffer.valueConverters;

public interface IValueConverter{

	public byte[] ToBytes(String value);

	public String FromBytes(byte[] bytes);

}
