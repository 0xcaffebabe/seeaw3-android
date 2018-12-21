package wang.ismy.seeaw3.common;

import java.time.LocalDateTime;
import java.util.Date;

public class Log {

    private static boolean enable = true;

    public static void error(String message){
        if (enable){
            System.err.println(new Date() +"---"+message);
        }

    }

    public static void info(String message){
        if (enable){
            System.out.println(new Date()+"---"+message);
        }

    }

    public static void disable(){
        enable = false;
    }

    public static void enable(){
        enable = true;
    }


}
