package ru.malnev.gbcloud.server.persistence.repositories;

import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.server.persistence.entitites.FileSystemEntity;

public class FileSystemRepository extends AbstractRepository<FileSystemEntity>
{
    @Override
    public @NotNull Class<FileSystemEntity> getEntityClass()
    {
        return FileSystemEntity.class;
    }
}
