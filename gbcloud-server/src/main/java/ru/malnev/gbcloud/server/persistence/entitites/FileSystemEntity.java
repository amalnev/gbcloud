package ru.malnev.gbcloud.server.persistence.entitites;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class FileSystemEntity extends NamedEntity
{
    @ManyToOne
    private FileSystemEntity parent;

    @ManyToOne
    private User owner;
}
