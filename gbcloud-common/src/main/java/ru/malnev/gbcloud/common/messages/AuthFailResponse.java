package ru.malnev.gbcloud.common.messages;

import lombok.Getter;
import lombok.Setter;

public class AuthFailResponse extends ServerErrorResponse
{
    @Getter
    @Setter
    private String reason;
}
