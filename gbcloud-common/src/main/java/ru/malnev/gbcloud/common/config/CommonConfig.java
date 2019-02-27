package ru.malnev.gbcloud.common.config;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import java.util.Iterator;

/**
 * This class is a container of options common to both client- and server-side applications
 *
 * @author alexey.malnev
 */
@ApplicationScoped
public class CommonConfig extends OptionRepository
{
    private static final String SERVER_PORT_OPTION_NAME = "ServerPort";
    private static final String DIAG_MODE_OPTION_NAME = "DiagMode";
    private static final String LOGGING_OPTION_NAME = "Logging";

    private static final int DEFAULT_SERVER_PORT = 9999;
    private static final boolean DEFAULT_DIAG_MODE = false;
    private static final boolean DEFAULT_LOGGING = false;

    public CommonConfig()
    {
        addAllowedOption(new IntegerOption(SERVER_PORT_OPTION_NAME, DEFAULT_SERVER_PORT));
        addAllowedOption(new BooleanOption(DIAG_MODE_OPTION_NAME, DEFAULT_DIAG_MODE));
        addAllowedOption(new BooleanOption(LOGGING_OPTION_NAME, DEFAULT_LOGGING));
    }

    protected String getStringOption(final @NotNull String name)
    {
        return ((StringOption) get(name)).getValue();
    }

    protected int getIntegerOption(final @NotNull String name)
    {
        return ((IntegerOption) get(name)).getValue();
    }

    protected boolean getBooleanOption(final @NotNull String name)
    {
        return ((BooleanOption) get(name)).getValue();
    }

    protected void setIntegerOption(final @NotNull String name, final int value, final int defaultValue)
    {
        final IntegerOption option = new IntegerOption();
        option.setName(name);
        option.setValue(value);
        option.setDefaultValue(defaultValue);

        put(name, option);
    }

    protected void setStringOption(final @NotNull String name, final @NotNull String value, final @NotNull String defaultValue)
    {
        final StringOption option = new StringOption();
        option.setName(name);
        option.setValue(value);
        option.setDefaultValue(defaultValue);

        put(name, option);
    }

    protected void setBooleanOption(final @NotNull String name, final boolean value, final boolean defaultValue)
    {
        final BooleanOption option = new BooleanOption();
        option.setName(name);
        option.setValue(value);
        option.setDefaultValue(defaultValue);

        put(name, option);
    }

    public int getServerPort()
    {
        return getIntegerOption(SERVER_PORT_OPTION_NAME);
    }

    public void setServerPort(final int serverPort)
    {
        setIntegerOption(SERVER_PORT_OPTION_NAME, serverPort, DEFAULT_SERVER_PORT);
    }

    public boolean isInDiagMode()
    {
        return getBooleanOption(DIAG_MODE_OPTION_NAME);
    }

    public void setDiagMode(final boolean value)
    {
        setBooleanOption(DIAG_MODE_OPTION_NAME, value, DEFAULT_DIAG_MODE);
    }

    public boolean isLoggingEnabled()
    {
        return getBooleanOption(LOGGING_OPTION_NAME);
    }

    public void setLoggingEnabled(final boolean value)
    {
        setBooleanOption(LOGGING_OPTION_NAME, value, DEFAULT_LOGGING);
    }

    /**
     * This method parses program command line and initializes program options
     * with corresponding values
     *
     * @param args Command line passed to psvm.
     * @throws ParseException
     */
    public void parseCommandLine(final @NotNull String[] args) throws ParseException
    {
        final Options options = new Options();
        for (Iterator<IOption> iter = iterator(); iter.hasNext(); )
        {
            final IOption option = iter.next();
            options.addOption(option.getName(), !(option instanceof BooleanOption), null);
        }

        final IgnoringParser parser = new IgnoringParser();
        final CommandLine cmd = parser.parse(options, args, false);
        for (org.apache.commons.cli.Option cmdOption : cmd.getOptions())
        {
            final IOption storedOption = get(cmdOption.getOpt());
            if (storedOption instanceof BooleanOption)
            {
                final BooleanOption booleanOption = (BooleanOption) storedOption;
                setBooleanOption(booleanOption.getName(), true, booleanOption.getDefaultValue());
            }
            else if (storedOption instanceof StringOption)
            {
                final StringOption stringOption = (StringOption) storedOption;
                setStringOption(stringOption.getName(), cmdOption.getValue(), stringOption.getDefaultValue());
            }
            else if (storedOption instanceof IntegerOption)
            {
                final IntegerOption integerOption = (IntegerOption) storedOption;
                setIntegerOption(integerOption.getName(), Integer.parseInt(cmdOption.getValue()), integerOption.getDefaultValue());
            }
        }

        for (Iterator<IOption> iter = iterator(); iter.hasNext(); )
        {
            final IOption option = iter.next();
            if (option instanceof BooleanOption)
            {
                final BooleanOption booleanOption = (BooleanOption) option;
                if (!cmd.hasOption(option.getName()))
                {
                    setBooleanOption(option.getName(), false, booleanOption.getDefaultValue());
                }
            }
        }
    }
}
