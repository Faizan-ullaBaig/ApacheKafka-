package demo.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;


public class KafkaProducerSync {

    private final static String TOPIC = "test-topic";
    private final static String BOOTSTRAP_SERVERS ="localhost:9095";

    private static Producer<Long, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
       // props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.ACKS_CONFIG, "all");   //min.insync.replicas = 2
        props.put(ProducerConfig.RETRIES_CONFIG,50000);
     //   props.put("retries",5000);
      //  props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG,1000);
        
        return new KafkaProducer<>(props);
    }


    static void runProducer(final int sendMessageCount) throws Exception {
        final Producer<Long, String> producer = createProducer();
        long time = System.currentTimeMillis();

        try {
         // index < time+sendMessageCount 
            for (long index = time;   ; index++) {
                final ProducerRecord<Long, String> record =
                        new ProducerRecord<>(TOPIC, index,"Message is " + index);

                RecordMetadata metadata = producer.send(record).get();

                long elapsedTime = System.currentTimeMillis() - time;
                System.out.printf("sent record(key=%s value=%s) " +
                                "meta(partition=%d, offset=%d) time=%d\n",
                        record.key(), record.value(), metadata.partition(),
                        metadata.offset(), elapsedTime);

            }
        } 
        
        catch (Exception e)
        {
        	e.printStackTrace();
        }
        
        finally {
            producer.flush();
            producer.close();
        }
    }




    public static void main(String... args)
                                throws Exception {
        for (int index = 0; index < 1; index++) {
            runProducer(10);
            Thread.sleep(30_000);
        }
    }
}














