package com.finall.cmt.async;

import java.util.List;


public interface EventHandler {

    /**
     * 处理EventModel
     */
    void doHandler(EventModel eventModel);

    List<EventType> getSupportEventTypes();
}
