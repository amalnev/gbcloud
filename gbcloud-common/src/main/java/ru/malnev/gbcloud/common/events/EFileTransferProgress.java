package ru.malnev.gbcloud.common.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EFileTransferProgress
{
    private int percentComplete;

    private String fileName;
}
