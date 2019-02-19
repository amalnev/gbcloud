package ru.malnev.gbcloud.common.config;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.ParseException;

import java.util.ListIterator;

/**
 * This is a modification of Apache Commons BasicParser which allows it
 * to parse command lines with unknown options, ignoring them instead of raising an exception
 * upon encountering one.
 *
 * @author alexey.malnev
 */
public class IgnoringParser extends BasicParser
{
    @Override
    protected void processOption(final String arg, final ListIterator iter) throws ParseException
    {
        boolean hasOption = getOptions().hasOption(arg);
        if (hasOption)
        {
            super.processOption(arg, iter);
        }
    }
}
