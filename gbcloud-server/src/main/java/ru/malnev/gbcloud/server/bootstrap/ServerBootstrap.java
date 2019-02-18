package ru.malnev.gbcloud.server.bootstrap;

import ru.malnev.gbcloud.common.bootstrap.Bootstrap;
import ru.malnev.gbcloud.common.utils.PasswordUtil;
import ru.malnev.gbcloud.server.persistence.entitites.User;
import ru.malnev.gbcloud.server.persistence.repositories.UserRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

@ApplicationScoped
public class ServerBootstrap extends Bootstrap
{
    private static final String ROOT_USERNAME = "root";
    private static final String ROOT_DEFAULT_PASSWORD = "root";

    @Inject
    private UserRepository userRepository;

    @Override
    public void run()
    {
        User root = userRepository.findByName(ROOT_USERNAME);
        if(root == null)
        {
            root = new User();
            root.setName(ROOT_USERNAME);
            root.setPasswordHash(PasswordUtil.hash(ROOT_DEFAULT_PASSWORD));
            userRepository.merge(root);
        }

        System.out.println(root.getName());

        //super.run();
    }
}
