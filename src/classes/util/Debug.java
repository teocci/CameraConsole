package classes.util;

/**
 * Created by sol on 2017-05-25.
 */
public class Debug {

    public static int DEBUG = 1;

    public static void log(String msg)
    {
        if ( DEBUG == 1 ) {
            System.out.println(msg);
        }
    }

    public static void log(Object msg)
    {
        if ( DEBUG == 1 ) {
            System.out.println(msg);
        }
    }

    public static void err(Object err)
    {
        if ( DEBUG == 1 ) {
            System.out.println(err);
        }
    }

    public static void err(Exception e) {
        if (DEBUG == 1) {
            e.printStackTrace();
        }
    }


}
