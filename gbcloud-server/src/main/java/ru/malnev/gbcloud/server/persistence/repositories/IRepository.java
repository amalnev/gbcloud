package ru.malnev.gbcloud.server.persistence.repositories;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.malnev.gbcloud.server.persistence.entitites.AbstractEntity;

import java.util.List;

public interface IRepository<T extends AbstractEntity>
{
    @Nullable List<T> select();

    @Nullable T select(@Nullable String id);

    void remove(@NotNull String id);

    void merge(@Nullable T entity);

    @NotNull T create();

    @NotNull Class<T> getEntityClass();
}
