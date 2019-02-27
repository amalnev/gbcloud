package ru.malnev.gbcloud.common.messages.pwd;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.malnev.gbcloud.common.messages.AbstractMessage;

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
