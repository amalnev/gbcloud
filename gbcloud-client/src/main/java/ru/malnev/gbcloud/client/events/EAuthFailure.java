package ru.malnev.gbcloud.client.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class EAuthFailure
{
    @Getter
    @Setter
    private String reason;
}
