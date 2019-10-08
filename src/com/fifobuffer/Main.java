package com.fifobuffer;

import com.fifobuffer.bufferReaders.FileBufferReader;
import com.fifobuffer.bufferReaders.IBufferReader;
import com.fifobuffer.bufferWriters.FileBufferWriter;
import com.fifobuffer.bufferWriters.IBufferWriter;
import com.fifobuffer.consumers.SimpleConsumer;
import com.fifobuffer.producers.SimpleProducer;
import com.fifobuffer.services.ConsumerService;
import com.fifobuffer.services.ProducerService;

import java.io.*;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        try{
            var in = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Enter produce count: ");
            var produceCount = Integer.parseInt(in.readLine().trim());
            System.out.print("Enter produce life time in milliseconds: ");
            var lifeTime = Long.parseLong(in.readLine().trim());
            System.out.print("Enter produce frequency in milliseconds: ");
            var frequency = Long.parseLong(in.readLine().trim());
            System.out.print("Enter consumer count: ");
            var consumerCount = Integer.parseInt(in.readLine().trim());

            var guid = UUID.randomUUID();
            var file = File.createTempFile("bufferFile_", guid.toString());
            System.out.println(String.format("Buffer file created %s", file.getPath()));

            try(var statisticsCollector = new StatisticsCollector(lifeTime,frequency * 2)){
                try(var fileWriter = new FileBufferWriter(file.getPath())){
                    try(var fileReader = new FileBufferReader(file.getPath())){

                        var producerService = new ProducerService<IBufferWriter>(
                                produceCount,
                                frequency,
                                lifeTime,
                                fileWriter,
                                (x) -> new SimpleProducer(x, statisticsCollector));
                        var consumeService = new ConsumerService<IBufferReader>(
                                consumerCount,
                                frequency,
                                fileReader,
                                (x) -> new SimpleConsumer(x,statisticsCollector));

                        consumeService.startConsuming();
                        producerService.startProducing();
                        consumeService.closeInAbsenceData();
                    } catch(Exception e){
                        e.printStackTrace();
                    }
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
        System.out.println("Enter any key to end");
        try{
            System.in.read();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
