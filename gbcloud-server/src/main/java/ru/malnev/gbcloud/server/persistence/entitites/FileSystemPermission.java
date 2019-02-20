package ru.malnev.gbcloud.server.persistence.entitites;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class FileSystemPermission extends AbstractEntity
{
    @ManyToOne
    private User user;

    @ManyToOne
    private FileSystemEntity object;

    private boolean canRead;

    private boolean canWrite;
}
