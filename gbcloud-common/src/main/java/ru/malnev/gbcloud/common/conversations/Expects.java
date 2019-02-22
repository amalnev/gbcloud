package ru.malnev.gbcloud.common.conversations;

import ru.malnev.gbcloud.common.messages.IMessage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Expects
{
    Class<? extends IMessage>[] value();
}
