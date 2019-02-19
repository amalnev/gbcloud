package ru.malnev.gbcloud.common.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthMessage extends AbstractMessage
{
    private String login;

    private String passwordHash;
}
