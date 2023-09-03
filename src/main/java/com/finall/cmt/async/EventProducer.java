package com.finall.cmt.async;

import com.finall.cmt.redis.CommonKey;
import com.finall.cmt.redis.JedisService;
import com.finall.cmt.redis.LikeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 这里相当于一个生产者，将封装好的EventModel事件，塞进队列中，等待消费者的消费
 */
@Service
public class EventProducer {

    @Autowired
    JedisService jedisService;

    /**
     * 将该事件推入到redis中的队列中
     *
     * @param eventModel
     */
    public boolean fireEvent(EventModel eventModel) {
        try {
            jedisService.lpush(LikeKey.LIKE_ASYNC_KEY, CommonKey.EVENT_LIKE_QUEUE, eventModel);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
