package io.conduktor.demos.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemo {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class.getSimpleName());

    public static void main(String[] args) {

        //create producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());

        //create the producer
        KafkaProducer<String,String> producer = new KafkaProducer<>(properties);


        //create a producer record
        String topic = "demo_java";
        String value = "hello kafka";
        String key = ""+Math.random()*100;


        ProducerRecord<String,String> producerRecord =
                new ProducerRecord<>(topic,key,value);


        //send data - asynchronous operation
        producer.send(producerRecord, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if(exception == null) log.info("The record was successfuly sent"
                        +"\nTopic:"+metadata.topic()
                        +"\nPartition:"+metadata.partition()
                        +"\nKey:"+producerRecord.key()
                        +"\nOffset:"+metadata.offset()
                        +"\nTimestamp:"+metadata.timestamp()
                );
            }
        });


        //flush and close the producer
        producer.flush();

        //flush and close producer;
        producer.close();
    }
}
