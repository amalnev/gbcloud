package ru.malnev.gbcloud.common.utils;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.DatatypeConverter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.zip.CRC32;

public class Util
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

    public static long calculateCheckSum(final @NotNull String filePath) throws IOException
    {
        final InputStream inputStream = new FileInputStream(filePath);
        final CRC32 crc = new CRC32();
        int bytesRead;
        byte[] byteBuffer = new byte[4096];
        while ((bytesRead = inputStream.read(byteBuffer)) != -1)
        {
            crc.update(byteBuffer, 0, bytesRead);
        }

        return crc.getValue();
    }
}
