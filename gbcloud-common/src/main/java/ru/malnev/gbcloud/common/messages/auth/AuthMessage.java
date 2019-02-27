package ru.malnev.gbcloud.common.messages.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.malnev.gbcloud.common.messages.AbstractMessage;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthMessage extends AbstractMessage
{
    private String login;

    private String passwordHash;
}
