package com.ll.gramgram.base.event;

import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterModifyAttractiveType extends ApplicationEvent {
    private final LikeablePerson likeablePerson;
    private final int previousAttractiveTypeCode;
    public EventAfterModifyAttractiveType(Object source, LikeablePerson likeablePerson, int previousAttractiveTypeCode, int newAttractiveTypeCode){
        super(source);
        this.likeablePerson = likeablePerson;
        this.previousAttractiveTypeCode = previousAttractiveTypeCode;
    }
}