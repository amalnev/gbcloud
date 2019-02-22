package ru.malnev.gbcloud.client.command;

import javax.inject.Qualifier;
import java.lang.annotation.*;

@Qualifier
@Inherited
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Command
{
}
