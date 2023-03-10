package io.conduktor.demos.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class ConsumerDemo {
    private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class.getSimpleName());

    public static void main(String[] args) {

        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "my-second-application";
        String topic = "demo_java";

        //creating consumer configs
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        //the best partition assigment strategy
        properties.setProperty(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());

        //create consumer
        KafkaConsumer<String,String>consumer = new KafkaConsumer<String, String>(properties);


        //get a reference to the current thread
        final Thread mainThread = Thread.currentThread();

        //adding the shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run(){
                log.info("Detected a shutdown, lets exit calling consumer.wakeup()...");
                consumer.wakeup();

                //join the main thread to allow the execution of the code in the main thread
                try{
                    mainThread.join();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        });

        try{
            //suscribe consumer to our topics
            consumer.subscribe(List.of(topic));

            //poll for new data

            while (true){
                ConsumerRecords<String,String> records =
                        consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String,String> record : records){
                    log.info("Key: "+record.key()
                            +"\n Value: "+record.value()
                            +"\nPartition: "+record.partition()
                            +"\nOffset: "+record.offset());
                }
            }
        }catch (WakeupException e){
            log.info("Wake up exception");
        }catch (Exception e){
            log.error("Unexpected exception");
        }finally {
            consumer.close(); //this also commit the offsets if need be
            log.info("The consumer is now gracefully closed");
        }



    }
}
