package com.fifobuffer.bufferWriters;

import java.io.*;

public class FileBufferWriter implements IBufferWriter{

	private FileOutputStream StreamWriter;
	private String FileName;
	private boolean IsDisposed;
	private byte[] LineSeparatorBytes;

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
		this.LineSeparatorBytes = System.lineSeparator().getBytes();
	}

	@Override
	public synchronized void write(byte[] data){
		try{
			StreamWriter.write(data);
			StreamWriter.write(LineSeparatorBytes);
			System.out.println("Data written to file");
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
