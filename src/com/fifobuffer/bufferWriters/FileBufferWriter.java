package com.fifobuffer.bufferWriters;

import java.io.*;

public class FileBufferWriter implements IBufferWriter{

	private FileOutputStream StreamWriter;

	private String FileName;

	private boolean IsDisposed;

	public FileBufferWriter(String fileName) throws IOException{
		if(fileName == null || fileName.isEmpty()){
			throw new IllegalArgumentException(fileName);
		}
		var file = new File(fileName);
		if(!file.exists()){
			file.createNewFile();
		}
		if(!file.canWrite()){
			throw new IllegalArgumentException(fileName);
		}
		this.FileName = fileName;
		this.StreamWriter = new FileOutputStream(FileName, true);
	}

	@Override
	public synchronized void write(byte[] data){
		try{
			StreamWriter.write(data);
		} catch(IOException e){
			e.printStackTrace();
		}

	}

	@Override
	public boolean getIsDisposed(){
		return false;
	}

	@Override
	public void close() throws Exception{
		if(IsDisposed){
			return;
		}

		StreamWriter.close();
		IsDisposed=true;
	}
}
