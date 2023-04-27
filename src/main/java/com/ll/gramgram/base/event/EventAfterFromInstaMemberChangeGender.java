package com.ll.gramgram.base.event;

import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterFromInstaMemberChangeGender extends ApplicationEvent {
    private final InstaMember instaMember;
    private final String previousGender;

    public EventAfterFromInstaMemberChangeGender(Object source, InstaMember instaMember, String previousGender){
        super(source);
        this.instaMember = instaMember;
        this.previousGender = previousGender;
    }

}
