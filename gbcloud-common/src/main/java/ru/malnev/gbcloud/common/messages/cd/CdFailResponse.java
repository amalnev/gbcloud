package ru.malnev.gbcloud.common.messages.cd;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.malnev.gbcloud.common.messages.ServerErrorResponse;

@NoArgsConstructor
@AllArgsConstructor
public class CdFailResponse extends ServerErrorResponse
{
    @Getter
    @Setter
    private String reason;
}
