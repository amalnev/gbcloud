package ru.malnev.gbcloud.server.bootstrap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import ru.malnev.gbcloud.common.bootstrap.Bootstrap;
import ru.malnev.gbcloud.common.utils.Util;
import ru.malnev.gbcloud.server.config.ServerConfig;
import ru.malnev.gbcloud.server.persistence.entitites.User;
import ru.malnev.gbcloud.server.persistence.repositories.UserRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Inject
    @Getter(AccessLevel.PROTECTED)
    private ServerConfig config;

    @Override
    @SneakyThrows
    protected void init()
    {
        final BiConsumer<String, String> createIfNone = (login, password) ->
        {
            User user = userRepository.findByName(login);
            if (user == null)
            {
                user = new User();
                user.setName(login);
                user.setPasswordHash(Util.hash(password));
                userRepository.merge(user);
            }
        };

        createIfNone.accept(ROOT_USERNAME, ROOT_DEFAULT_PASSWORD);
        createIfNone.accept(USER_USERNAME, USER_DEFAULT_PASSWORD);

        final Path serverRootDir = Paths.get(config.getRootDirectory());
        if (!Files.exists(serverRootDir)) Files.createDirectories(serverRootDir);
        for (final User user : userRepository.select())
        {
            final Path userHomeDir = Paths.get(config.getRootDirectory(), user.getName());
            if (!Files.exists(userHomeDir)) Files.createDirectories(userHomeDir);
            user.setHomeDirectory(userHomeDir.toAbsolutePath().toString());
            userRepository.merge(user);
        }
    }
}
