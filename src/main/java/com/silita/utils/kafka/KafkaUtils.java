package com.silita.utils.kafka;

import com.silita.common.config.CustomizedPropertyConfigurer;
import com.silita.model.Document;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;
import java.util.Random;

@Component
public class KafkaUtils {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Producer producer;

    // 限制最大存储为16MB
    private final int maxBytes = 1024 * 1024 * 16;

    @PostConstruct
    public void KafkaUtils() {
        if (null == producer) {
            Properties props = new Properties();
            props.put("bootstrap.servers", CustomizedPropertyConfigurer.getContextProperty("kafka.hosts"));
            // “所有”设置将导致记录的完整提交阻塞，最慢的，但最持久的设置。
            props.put("acks", "all");
            // 如果请求失败，生产者也会自动重试，即使设置成0 the producer can automatically retry.
            props.put("retries", 0);
            // The producer maintains buffers of unsent records for each partition.
            props.put("batch.size", 1);
            // 默认立即发送，这里这是延时毫秒数
            props.put("linger.ms", 1);
            // 生产者缓冲大小，当缓冲区耗尽后，额外的发送调用将被阻塞。时间超过max.block.ms将抛出TimeoutException
            props.put("buffer.memory", 33554432);
            // 最大请求的字节数
            props.put("max.request.size", maxBytes);
            props.put("message.max.bytes", maxBytes);
            // 序列化
            props.put("key.serializer", StringSerializer.class.getName());
            props.put("value.serializer", MyObjectEncoder.class.getName());
            producer = new KafkaProducer<String, Document>(props);
        }
    }

    /**
     * 发送消息
     *
     * @param topic
     * @param document
     */
    public void send(String topic, int partition, String key, Document document) {
        try {
            ProducerRecord<String, Document> record = new ProducerRecord<String, Document>(topic, partition, key, document);
            producer.send(record);
        } catch (Exception e) {
            logger.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private Random partitionRandom = new Random();

    /**
     * 随机获得一个分区
     *
     * @return
     */
    public int randomPartition() {
        return partitionRandom.nextInt(4);
    }
}
