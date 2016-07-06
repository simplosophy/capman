package io.capman.util;

/**
 * Created by flying on 7/5/16.
 */
public class StringUtils {
    public static boolean isEmpty(String str)
    {
        if( str == null || str.length() == 0 )
            return true;
        return false;
    }
}
