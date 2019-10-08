package com.fifobuffer;

import com.fifobuffer.bufferReaders.FileBufferReader;
import com.fifobuffer.bufferReaders.IBufferReader;
import com.fifobuffer.bufferWriters.FileBufferWriter;
import com.fifobuffer.bufferWriters.IBufferWriter;
import com.fifobuffer.consumers.SimpleConsumer;
import com.fifobuffer.producers.SimpleProducer;
import com.fifobuffer.services.ConsumerService;
import com.fifobuffer.services.ProducerService;

import java.io.Console;
import java.io.DataInputStream;
import java.io.File;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        try{
            var in = new DataInputStream(System.in);


            System.out.print("Enter produce count");
            var produceCount = in.readInt();

            System.out.print("Enter produce life time in milliseconds");
            var lifeTime = in.readLong();

            System.out.print("Enter produce frequency in milliseconds");
            var frequency = in.readLong();

            System.out.print("Enter consumer count");
            var consumerCount = in.readInt();
            var guid = UUID.randomUUID();
            var file = File.createTempFile("bufferFile", guid.toString());

            try(var fileWriter = new FileBufferWriter(file.getPath())){
                try(var fileReader = new FileBufferReader(file.getPath())){

                    var producerService = new ProducerService<IBufferWriter>(
                            produceCount,
                            frequency,
                            lifeTime,
                            fileWriter,
                            (x) -> new SimpleProducer(x));
                    var consumeService = new ConsumerService<IBufferReader>(
                            consumerCount,
                            frequency,
                            fileReader,
                            (x) -> new SimpleConsumer(x));

                    consumeService.startConsuming();
                    producerService.startProducing();
                    consumeService.closeInAbsenceData();
                    System.out.println("Enter any key to end");
                    System.in.read();
                } catch(Exception e){
                    e.printStackTrace();
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
