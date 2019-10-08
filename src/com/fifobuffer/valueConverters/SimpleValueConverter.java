package com.fifobuffer.valueConverters;

import com.fifobuffer.producers.SimpleProducer;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class SimpleValueConverter implements IValueConverter{

	private String Separator;
	private SimpleDateFormat DateConverter;

	public SimpleValueConverter(String separator){
		this.Separator = separator;
		this.DateConverter=new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
	}

	@Override
	public byte[] ToBytes(String value){
		if(value == null){
			return new byte[0];
		}
		var currentDate = new Date();
		var data = String.format("%s - %tF:%tT %s", value, currentDate, currentDate, Separator);

		return data.getBytes();
	}

	@Override
	public String FromBytes(byte[] bytes){
		if(bytes == null || bytes.length == 0) return "";
		var parsedValue = new String(bytes);
		var values = parsedValue.split(Separator);
		if(values.length == 0) return "";
		var date=new Date();
		var value=values[0];
		var beginDateIndex = value.indexOf("-");
		if(beginDateIndex==-1) return "";
		var dateString=value.substring(beginDateIndex + 1, value.length() - Separator.length() - 1);
		try{
			var parsedDate = DateConverter.parse(dateString);
			if(date.after(parsedDate)) return value;
		} catch(ParseException e){
			e.printStackTrace();
		}
		return "";
	}
}
