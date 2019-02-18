package ru.malnev.gbcloud.server.persistence.entitites;

import lombok.Getter;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@MappedSuperclass
public abstract class AbstractEntity
{
    @Id
    @Getter
    private String id = UUID.randomUUID().toString();
}
