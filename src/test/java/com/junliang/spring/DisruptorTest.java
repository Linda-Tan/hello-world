package com.junliang.spring;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.junit.Test;
import sun.misc.Contended;

import java.util.concurrent.ThreadFactory;

/**
 * @author junliang
 * @date 2018/08/23
 */
public class DisruptorTest {

    @Test
    public void test() {

        // 生产者的线程工厂
        ThreadFactory threadFactory = new ThreadFactory() {
            int i = 0;

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "simpleThread" + String.valueOf(i++));
            }
        };

        // RingBuffer生产工厂,初始化RingBuffer的时候使用
        EventFactory<Element> factory = new EventFactory<Element>() {
            @Override
            public Element newInstance() {
                return new Element();
            }
        };


        // 处理Event的handler
        EventHandler<Element> handler = new EventHandler<Element>() {
            @Override
            public void onEvent(Element element, long sequence, boolean endOfBatch) throws InterruptedException {
                System.out.println("Element: " + Thread.currentThread().getName() + ": " + element.getValue() + ": " + sequence);
                // Thread.sleep(10000000);
            }
        };
        // 阻塞策略
        BlockingWaitStrategy strategy = new BlockingWaitStrategy();

        // 指定RingBuffer的大小

        int bufferSize = 8;

        // 创建disruptor，采用单生产者模式
        Disruptor<Element> disruptor = new Disruptor(factory, bufferSize, threadFactory, ProducerType.SINGLE, strategy);

        // 设置EventHandler
        disruptor.handleEventsWith(handler);
    // 启动disruptor的线程
        disruptor.start();
        for (int i = 0; i < 10; i++) {
            disruptor.publishEvent((element, sequence) -> {
                System.out.println("之前的数据" + element.getValue() + "当前的sequence" + sequence);
                element.setValue("我是第" + sequence + "个");
            });
        }

    }

    // 队列中的元素
    class Element {
        @Contended
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
