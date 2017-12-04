package com.junliang.spring.listener;

import lombok.extern.slf4j.Slf4j;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

@Slf4j
public class EhcacheEventLoggerListener implements CacheEventListener<Object,Object> {

    @Override
    public void onEvent(CacheEvent<? extends Object, ? extends Object> event) {

        log.info("Event: " + event.getType() + " Key: " + event.getKey() + " old value: " + event.getOldValue()
                + " new value: " + event.getNewValue());

    }

}
