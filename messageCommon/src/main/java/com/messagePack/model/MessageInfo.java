package com.messagePack.model;

import lombok.Getter;
import lombok.Setter;
import org.msgpack.annotation.MessagePackBeans;

import java.io.Serializable;

@Getter
@Setter
@MessagePackBeans
public class MessageInfo implements Serializable {


    private String classPath;

    private byte[] object;
}
