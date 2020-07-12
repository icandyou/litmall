package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.domain.Payment;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;


/**
 * @author chei1
 */
@Service
public class RocketMQProvider {
    /**
     * 生产者的组名
     */
    @Value("${apache.rocketmq.producer.producerGroup}")
    private String producerGroup;
    @Value("Producer1")
    private String producerGroup1;

    /**
     * NameServer 地址
     */
    @Value("${apache.rocketmq.namesrvAddr}")
    private String namesAddr;
    // @PostConstruct
    // @PostContruct是spring框架的注解，在方法上加该注解会在项目启动的时候执行该方法，也可以理解为在spring容器初始化的时候执行该方法。
    public void defaultMQProducer(Payment payments) {
        //生产者的组名
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);

        //指定NameServer地址，多个地址以 ; 隔开
        producer.setNamesrvAddr(namesAddr);
        try {
            /**
             * Producer对象在使用之前必须要调用start初始化，初始化一次即可
             * 注意：切记不可以在每次发送消息时，都调用start方法
             */
            producer.start();

            //创建一个消息实例，包含 topic、tag 和 消息体
            //如下：topic 为 "TopicTest"，tag 为 "push"
            JSONObject json = (JSONObject) JSONObject.toJSON(payments);

            Message message = new Message("ADD", "push", json.toString().getBytes());

            System.out.println("json test:"+json);
            StopWatch stop = new StopWatch();

            SendResult result = producer.send(message);
            System.out.println("发送响应：MsgId:" + result.getMsgId() + "，发送状态:" + result.getSendStatus());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.shutdown();
        }
    }

    public void updateMQProducer(Payment payments) {
        //生产者的组名
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup1);

        //指定NameServer地址，多个地址以 ; 隔开
        producer.setNamesrvAddr(namesAddr);
        try {
            /**
             * Producer对象在使用之前必须要调用start初始化，初始化一次即可
             * 注意：切记不可以在每次发送消息时，都调用start方法
             */
            producer.start();

            //创建一个消息实例，包含 topic、tag 和 消息体
            //如下：topic 为 "TopicTest"，tag 为 "push"
            JSONObject json = (JSONObject) JSONObject.toJSON(payments);

            Message message = new Message("PUT", "update", json.toString().getBytes());

            System.out.println("json test:"+json);
            StopWatch stop = new StopWatch();
            stop.start();

            SendResult result = producer.send(message);
            System.out.println("发送响应：MsgId:" + result.getMsgId() + "，发送状态:" + result.getSendStatus());
            stop.stop();
            System.out.println("----------------发送消息耗时：" + stop.getTotalTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.shutdown();
        }
    }
}