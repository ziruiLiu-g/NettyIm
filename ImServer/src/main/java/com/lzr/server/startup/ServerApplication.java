package com.lzr.server.startup;

import com.lzr.server.server.ChatServer;
import com.lzr.server.server.session.service.SessionManger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.lzr")
@SpringBootApplication
public class ServerApplication
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // init and start Spring env and Spring context
        ApplicationContext context =
                SpringApplication.run(ServerApplication.class, args);

        /**
         * build SessionManger
         */
        SessionManger sessionManger = context.getBean(SessionManger.class);
        SessionManger.setSingleInstance(sessionManger);

        /**
         * start server
         */
        ChatServer nettyServer = context.getBean(ChatServer.class);
        nettyServer.run();
    }

}
