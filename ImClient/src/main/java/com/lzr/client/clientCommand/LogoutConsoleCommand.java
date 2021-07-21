package com.lzr.client.clientCommand;

import org.springframework.stereotype.Service;

import java.util.Scanner;

/**
 * LogoutConsoleCommand
 *
 * Author: zirui liu
 * Date: 2021/7/21
 */
@Service("LogoutConsoleCommand")
public class LogoutConsoleCommand implements BaseCommand
{
    public static final String KEY = "10";

    @Override
    public void exec(Scanner scanner)
    {

    }


    @Override
    public String getKey()
    {
        return KEY;
    }

    @Override
    public String getTip()
    {
        return "exit";
    }

}
