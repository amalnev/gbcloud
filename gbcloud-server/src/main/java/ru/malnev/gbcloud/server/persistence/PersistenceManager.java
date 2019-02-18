package ru.malnev.gbcloud.server.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@ApplicationScoped
public class PersistenceManager
{
    private final EntityManagerFactory emFactory;

    private PersistenceManager()
    {
        emFactory = Persistence.createEntityManagerFactory("gbcloud-persistent-unit");
    }

    @Produces
    public EntityManager getEntityManager()
    {
        return emFactory.createEntityManager();
    }

    public void close()
    {
        emFactory.close();
    }
}
