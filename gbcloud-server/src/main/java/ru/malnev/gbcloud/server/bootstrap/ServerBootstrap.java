package ru.malnev.gbcloud.server.bootstrap;

import ru.malnev.gbcloud.common.bootstrap.Bootstrap;
import ru.malnev.gbcloud.common.utils.PasswordUtil;
import ru.malnev.gbcloud.server.persistence.entitites.User;
import ru.malnev.gbcloud.server.persistence.repositories.UserRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import java.util.function.BiConsumer;

@ApplicationScoped
public class ServerBootstrap extends Bootstrap
{
    private static final String ROOT_USERNAME = "root";
    private static final String USER_USERNAME = "user";
    private static final String ROOT_DEFAULT_PASSWORD = "root";
    private static final String USER_DEFAULT_PASSWORD = "user";

    @Inject
    private UserRepository userRepository;

    @Override
    public void run()
    {
        final BiConsumer<String, String> createIfNone = (login, password) ->
        {
            User user = userRepository.findByName(login);
            if(user == null)
            {
                user = new User();
                user.setName(login);
                user.setPasswordHash(PasswordUtil.hash(password));
                userRepository.merge(user);
            }
        };

        createIfNone.accept(ROOT_USERNAME, ROOT_DEFAULT_PASSWORD);
        createIfNone.accept(USER_USERNAME, USER_DEFAULT_PASSWORD);

        super.run();
    }
}
