package com.lzr.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * imnode pojo
 *
 * Author: zirui liu
 * Date: 2021/7/19
 */
@Data
public class ImNode implements Comparable<ImNode>, Serializable {
    private static final long serialVersionUID = 4606297492065786802L;

    //worker id, gen by zk
    private long id;

    //Netty connect num
    private Integer balance = 0;

    //Netty IP
    private String host="127.0.0.1";

    //Netty port
    private Integer port=8081;

    public ImNode()
    {
    }

    public ImNode(String host, Integer port)
    {
        this.host = host;
        this.port = port;
    }

    @Override
    public String toString()
    {
        return "ImNode{" +
                "id='" + id + '\'' +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                ",balance=" + balance +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImNode node = (ImNode) o;
        return Objects.equals(host, node.host) &&
                Objects.equals(port, node.port);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, host, port);
    }

    @Override
    public int compareTo(ImNode o) {
        return 0;
    }

    public void incrementBalance()
    {
        balance++;
    }

    public void decrementBalance()
    {
        balance--;
    }
}
