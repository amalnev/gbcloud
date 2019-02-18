package ru.malnev.gbcloud.server.persistence.entitites;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class NamedEntity extends AbstractEntity
{
    @Getter
    @Setter
    @Column(nullable = false, unique = true)
    private String name;
}
