package ru.malnev.gbcloud.client.command;

import javax.inject.Inject;

@Keyword(Const.LPWD_COMMAND_KEYWORD)
public class LpwdCommand extends AbstractCommand
{
    @Inject
    private CLI cli;

    @Override
    public void run()
    {
        System.out.println(cli.pwd());
    }
}
