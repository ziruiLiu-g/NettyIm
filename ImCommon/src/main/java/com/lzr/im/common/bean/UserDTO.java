package com.lzr.im.common.bean;

import com.lzr.im.common.bean.msg.ProtoMsg;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class UserDTO
{

    String userId;
    String userName;
    String devId;
    String token;
    String nickName = "nickName";
    PLATTYPE platform = PLATTYPE.WINDOWS;

    // windows,mac,android, ios, web , other
    public enum PLATTYPE
    {
        WINDOWS, MAC, ANDROID, IOS, WEB, OTHER;
    }

    private String sessionId;


    public void setPlatform(int platform)
    {
        PLATTYPE[] values = PLATTYPE.values();
        for (int i = 0; i < values.length; i++)
        {
            if (values[i].ordinal() == platform)
            {
                this.platform = values[i];
            }
        }

    }


    @Override
    public String toString()
    {
        return "User{" +
                "uid='" + userId + '\'' +
                ", devId='" + devId + '\'' +
                ", token='" + token + '\'' +
                ", nickName='" + nickName + '\'' +
                ", platform=" + platform +
                '}';
    }

    public static UserDTO fromMsg(ProtoMsg.LoginRequest info)
    {
        UserDTO user = new UserDTO();
        user.userId = new String(info.getUid());
        user.devId = new String(info.getDeviceId());
        user.token = new String(info.getToken());
        user.setPlatform(info.getPlatform());
        log.info("login...: {}", user.toString());
        return user;

    }

}
