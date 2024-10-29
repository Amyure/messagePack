package com.messagePack.model;

import com.messagePack.model.base.MessageBase;
import lombok.Getter;
import lombok.Setter;
import org.msgpack.annotation.MessagePackBeans;

@Getter
@Setter
@MessagePackBeans
public class AccountInfo implements MessageBase {

    private long accountId;
    
    public AccountInfo getMessage(Object o) {
        return (AccountInfo) o;
    }
}
