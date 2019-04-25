package com.lm.service;

import net.sf.json.JSONArray;

public abstract interface IGitService
{
    public abstract JSONArray stat(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
            throws Exception;
}
