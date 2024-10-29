package com.messagePack.model;

import lombok.Getter;
import lombok.Setter;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.MessagePackBeans;

import java.io.Serializable;

@Getter
@Setter
//@Message  //元素需要是public
@MessagePackBeans
public class MessageInfo implements Serializable {


    public String content;

}
