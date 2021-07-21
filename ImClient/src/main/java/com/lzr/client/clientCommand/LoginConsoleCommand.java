package com.lzr.client.clientCommand;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Scanner;

/**
 * LoginConsoleCommand
 *
 * Author: zirui liu
 * Date: 2021/7/21
 */
@Slf4j
@Data
@Service("LoginConsoleCommand")
public class LoginConsoleCommand implements BaseCommand
{
    public static final String KEY = "1";

    private String userName;
    private String password;

    @Override
    public void exec(Scanner scanner)
    {

        System.out.println("please enter login info：username@password ");
        String s = scanner.next();
        String[] array = s.split("@");

        userName = array[0];
        password = array[1];
        log.info("correct, id: {}, password{}", userName, password);
    }

    @Override
    public String getKey()
    {
        return KEY;
    }

    @Override
    public String getTip()
    {
        return "login";
    }

}
