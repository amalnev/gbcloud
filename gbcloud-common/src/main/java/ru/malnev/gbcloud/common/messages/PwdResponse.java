package ru.malnev.gbcloud.common.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class PwdResponse extends AbstractMessage
{
    @Getter
    @Setter
    private String currentDirectory;
}
