package ru.malnev.gbcloud.common.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
//@NoArgsConstructor
public class PwdResponse extends AbstractMessage
{
    @Getter
    @Setter
    private String currentDirectory;

    private List<String> strings = new LinkedList<>();

    public PwdResponse()
    {
        strings.add("123");
    }
}
