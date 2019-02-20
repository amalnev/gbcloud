package ru.malnev.gbcloud.server.persistence.repositories;

import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.malnev.gbcloud.server.persistence.entitites.AbstractEntity;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

public abstract class AbstractRepository<T extends AbstractEntity> implements IRepository<T>
{
    @Getter
    @Inject
    private EntityManager entityManager;

    protected @Nullable List<T> selectByValue(final @NotNull String columnName, final @Nullable Object value)
    {
        if (value == null) return null;
        final StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT e FROM ");
        queryBuilder.append(getEntityClass().getSimpleName());
        queryBuilder.append(" e WHERE e.");
        queryBuilder.append(columnName);
        queryBuilder.append(" = :value");
        final List<T> entities = entityManager
                .createQuery(queryBuilder.toString(), getEntityClass())
                .setParameter("value", value).getResultList();
        return entities;
    }

    protected @Nullable T selectByUniqueValue(final @NotNull String columnName, final @Nullable Object value)
    {
        final List<T> entities = selectByValue(columnName, value);
        if (entities == null) return null;
        if (entities.isEmpty()) return null;
        return entities.get(0);
    }

    @Override
    public @Nullable List<T> select()
    {
        final StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT e FROM ");
        queryBuilder.append(getEntityClass().getSimpleName());
        queryBuilder.append(" e");
        return entityManager.createQuery(queryBuilder.toString(), getEntityClass()).getResultList();
    }

    @Override
    public @Nullable T select(@Nullable String id)
    {
        return selectByUniqueValue("id", id);
    }

    @Override
    public void remove(@NotNull String id)
    {
        final StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("DELETE FROM ");
        queryBuilder.append(getEntityClass().getSimpleName());
        queryBuilder.append(" e WHERE e.id = :id");
        entityManager.getTransaction().begin();
        entityManager.createQuery(queryBuilder.toString()).setParameter("id", id).executeUpdate();
        entityManager.getTransaction().commit();
    }

    @Override
    public void merge(@Nullable T entity)
    {
        if (entity == null) return;
        entityManager.getTransaction().begin();
        entityManager.merge(entity);
        entityManager.getTransaction().commit();
    }

    @Override
    @NotNull
    @SneakyThrows
    public T create()
    {
        final T entity = getEntityClass().newInstance();
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
        return entity;
    }
}
