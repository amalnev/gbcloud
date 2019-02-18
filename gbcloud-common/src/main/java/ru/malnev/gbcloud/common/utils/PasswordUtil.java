package ru.malnev.gbcloud.common.utils;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class PasswordUtil
{
    private static final String DEFAULT_HASH_ALGORITHM = "MD5";

    @NotNull
    @SneakyThrows
    public static String hash(final @NotNull String value)
    {
        final @NotNull MessageDigest digest = MessageDigest.getInstance(DEFAULT_HASH_ALGORITHM);
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        final byte[] hex = digest.digest(bytes);
        return DatatypeConverter.printHexBinary(hex);
    }
}
