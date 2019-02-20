package ru.malnev.gbcloud.server.persistence.entitites;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
public class User extends NamedEntity
{
    @Getter
    @Setter
    private String passwordHash;

    @Getter
    @Setter
    private String homeDirectory;
}
