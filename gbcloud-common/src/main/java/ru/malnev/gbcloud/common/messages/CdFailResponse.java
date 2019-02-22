package ru.malnev.gbcloud.common.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class CdFailResponse extends ServerErrorResponse
{
    @Getter
    @Setter
    private String reason;
}
