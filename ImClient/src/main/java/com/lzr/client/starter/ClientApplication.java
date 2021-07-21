package com.lzr.client.starter;

import com.lzr.client.client.CommandController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.lzr.client")
@SpringBootApplication
public class ClientApplication
{
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        ApplicationContext context =
                SpringApplication.run(ClientApplication.class, args);
        CommandController commandClient =
                context.getBean(CommandController.class);

        commandClient.initCommandMap();
        try
        {
            commandClient.startCommandThread();

        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }


}
