package com.messagePack.model;

import lombok.Getter;
import lombok.Setter;
import org.msgpack.annotation.MessagePackBeans;

@Getter
@Setter
@MessagePackBeans
public class AccountInfo {

    private long accountId;

}
