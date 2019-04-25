package com.lm.common;

import java.util.Date;

public class Util
{
    public static int calcDifDays(Date end, Date start)
    {
        long gap = end.getTime() - start.getTime();
        return (int)Math.floor(gap / 86400000L);
    }
}
