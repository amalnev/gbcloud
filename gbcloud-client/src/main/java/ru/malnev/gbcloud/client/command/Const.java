package ru.malnev.gbcloud.client.command;

class Const
{
    //command keywords
    public static final String CLOSE_COMMAND_KEYWORD = "close";
    public static final String EXIT_COMMAND_KEYWORD = "exit";
    public static final String LCD_COMMAND_KEYWORD = "lcd";
    public static final String LLS_COMMAND_KEYWORD = "lls";
    public static final String LPWD_COMMAND_KEYWORD = "lpwd";
    public static final String OPEN_COMMAND_KEYWORD = "open";
    public static final String PING_COMMAND_KEYWORD = "ping";
    public static final String RCD_COMMAND_KEYWORD = "rcd";
    public static final String RLS_COMMAND_KEYWORD = "rls";
    public static final String RPWD_COMMAND_KEYWORD = "rpwd";
    public static final String HELP_COMMAND_KEYWORD = "help";
    public static final String PUT_COMMAND_KEYWORD = "put";
    public static final String GET_COMMAND_KEYWORD = "get";
    public static final String RMKDIR_COMMAND_KEYWORD = "rmkdir";
    public static final String RRM_COMMAND_KEYWORD = "rrm";

    //command argument names
    public static final String SERVER_ARGUMENT_NAME = "Host";
    public static final String LOGIN_ARGUMENT_NAME = "Login";
    public static final String PASSWORD_ARGUMENT_NAME = "Password";
    public final static String TARGET_DIRECTORY_ARGUMENT_NAME = "Target directory";
    public final static String TARGET_FILE_ARGUMENT_NAME = "Target file";
    public final static String TARGET_PATH_ARGUMENT_NAME = "Target path";

    //command descriptions
    public static final String CLOSE_COMMAND_DESCRIPTION = "Close connection to server.";
    public static final String EXIT_COMMAND_DESCRIPTION = "Quit the program.";
    public static final String LCD_COMMAND_DESCRIPTION = "Change the current local directory.";
    public static final String LLS_COMMAND_DESCRIPTION = "List the contents of the current local directory.";
    public static final String LPWD_COMMAND_DESCRIPTION = "Print the path to the current local directory";
    public static final String OPEN_COMMAND_DESCRIPTION = "Open connection to server and authenticate.";
    public static final String PING_COMMAND_DESCRIPTION = "Send an echo-packet to the connected server.";
    public static final String RCD_COMMAND_DESCRIPTION = "Change the current remote directory.";
    public static final String RLS_COMMAND_DESCRIPTION = "List the contents of the current remote directory.";
    public static final String RPWD_COMMAND_DESCRIPTION = "Print the path to the current remote directory";
    public static final String HELP_COMMAND_DESCRIPTION = "Display help";
    public static final String PUT_COMMAND_DESCRIPTION = "Upload a file to the server.";
    public static final String GET_COMMAND_DESCRIPTION = "Downloads a file from the server.";
    public static final String RMKDIR_COMMAND_DESCRIPTION = "Creates a directory on the server.";
    public static final String RRM_COMMAND_DESCRIPTION = "Deletes a file or directory on the server.";
}
