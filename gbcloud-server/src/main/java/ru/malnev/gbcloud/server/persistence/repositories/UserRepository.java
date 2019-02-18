package ru.malnev.gbcloud.server.persistence.repositories;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.malnev.gbcloud.server.persistence.entitites.User;

import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;

@Stateless
public class UserRepository extends AbstractRepository<User>
{
    @Override
    public @NotNull Class<User> getEntityClass()
    {
        return User.class;
    }

    @Nullable
    public User findByName(@NotNull String name)
    {
        return selectByUniqueValue("name", name);
    }
}
