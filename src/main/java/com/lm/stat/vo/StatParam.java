package com.lm.stat.vo;

import com.lm.common.Util;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StatParam
{
    private String method;
    private Date startDate;
    private Date endDate;
    private long days;

    public int calcDays(String sDate)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try
        {
            date = sdf.parse(sDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return Util.calcDifDays(date, this.startDate);
    }

    public String getMethod()
    {
        return this.method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public Date getStartDate()
    {
        return this.startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return this.endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public long getDays()
    {
        return this.days;
    }

    public void setDays(long days)
    {
        this.days = days;
    }
}
