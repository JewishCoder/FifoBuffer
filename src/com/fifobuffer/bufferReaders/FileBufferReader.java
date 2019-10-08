package com.fifobuffer.bufferReaders;

import com.fifobuffer.valueConverters.IValueConverter;

import java.io.*;
import java.nio.ByteBuffer;

public class FileBufferReader implements IBufferReader{

	private boolean IsDisposed;
	private RandomAccessFile FileStream;
	private String RemoveTemplateString;

	public FileBufferReader(String fileName) throws IOException{
		if(fileName == null || fileName.isEmpty()){
			throw new IllegalArgumentException(fileName);
		}
		var file = new File(fileName);
		if(!file.exists()){
			file.createNewFile();
		}
		if(!file.canRead() || !file.canWrite()){
			throw new IllegalArgumentException(fileName);
		}

		this.FileStream = new RandomAccessFile(fileName,"rw");
		this.RemoveTemplateString = "";
	}

	@Override
	public synchronized String Read(int bufferCount, IValueConverter converter){

		try{
			long readedBytes = 0;
			var currentReader = 0;
			do{
				var buffer = new byte[bufferCount];
				currentReader = FileStream.read(buffer);
				var value = converter.FromBytes(buffer);

				if(!value.isEmpty()){
					FileStream.seek(readedBytes);
					var length = value.length() + 1;
					if(RemoveTemplateString.length() != length){
						var builder=new StringBuilder(length);
						for(var i = 0; i < length; i++){
							builder.append("*");
						}
						RemoveTemplateString=builder.toString();
					}
					FileStream.writeBytes(RemoveTemplateString);
					FileStream.seek(readedBytes);
					FileStream.writeBytes(RemoveTemplateString.replace("*"," "));
					FileStream.seek(0);
					return value;
				}
				readedBytes += currentReader;
			}
			while(currentReader > 0);

			return null;
		}
		catch(Exception exc){
			exc.printStackTrace();
		}


		return "";
	}

	@Override
	public boolean getIsDisposed(){
		return IsDisposed;
	}

	@Override
	public void close() throws Exception{
		if(IsDisposed) return;
		FileStream.close();
		FileStream = null;
		IsDisposed=true;
	}
}
