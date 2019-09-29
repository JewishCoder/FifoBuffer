package com.fifobuffer;

import com.fifobuffer.bufferWriters.FileBufferWriter;
import com.fifobuffer.bufferWriters.IBufferWriter;
import com.fifobuffer.producers.SimpleProducer;
import com.fifobuffer.services.ProducerService;

public class Main {
    public static void main(String[] args) {

        try(var fileWriter = new FileBufferWriter("E:\\back\\buffer.txt"))
        {
            var producerService=new ProducerService<IBufferWriter>(
                    2,
                    1000,
                    5000,
                     fileWriter,
                     ()-> new SimpleProducer(""));
            producerService.startProducing();
           System.in.read();

        } catch(Exception e){
            e.printStackTrace();
        }

        System.out.println("Wait");
    }
}
