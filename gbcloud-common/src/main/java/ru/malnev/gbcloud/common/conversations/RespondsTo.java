package ru.malnev.gbcloud.common.conversations;

import ru.malnev.gbcloud.common.messages.IMessage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RespondsTo
{
    Class<? extends IMessage> value();
}
