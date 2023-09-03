package com.finall.cmt.entity;

import lombok.Data;


@Data
public class Message {

    int messageId;

    String fromId;

    String toId;

    String messageContent;

    int hasRead;

    String conversationId;


}
