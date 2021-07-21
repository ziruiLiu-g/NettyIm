package com.lzr.client.clientCommand;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Scanner;

/**
 * ClientCommandGet
 *
 * Author: zirui liu
 * Date: 2021/7/21
 */
@Data
@Service("ClientCommandGet")
public class ClientCommandGet implements BaseCommand
{

    public static final String KEY = "0";

    private String allCommandsShow;
    private String commandInput;

    @Override
    public void exec(Scanner scanner)
    {

        System.err.println("please enter command：");
        System.err.println(allCommandsShow);
        commandInput = scanner.next();


    }


    @Override
    public String getKey()
    {
        return KEY;
    }

    @Override
    public String getTip()
    {
        return "show all command";
    }

}
