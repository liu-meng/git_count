package com.lm.controller;

import com.lm.service.IGitService;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GitController
{
    @Autowired
    @Qualifier("gitStat")
    IGitService gitService;

    @RequestMapping({"/"})
    public String sayHello()
    {
        return "redirect:stat/git_count.html";
    }

    @RequestMapping({"/git"})
    public String index()
    {
        return "redirect:stat/git_count.html";
    }

    @RequestMapping({"/git/stat"})
    @ResponseBody
    public String statGit(HttpServletRequest request, @RequestParam(value="method", required=true) String method, @RequestParam(value="startDate", required=false) String startDate, @RequestParam(value="endDate", required=false) String endDate, @RequestParam(value="name", required=false) String name, @RequestParam(value="gitRoot", required=true) String gitRoot)
    {
        JSONObject result = new JSONObject();
        String msg = null;
        int success = 0;
        try
        {
            success = 1;
            result.put("data", this.gitService.stat(method, startDate, endDate, name, gitRoot));
        }
        catch (Exception e)
        {
            msg = e.getMessage();
            result.put("msg", msg);
            e.printStackTrace();
        }
        result.put("success", Integer.valueOf(success));

        return result.toString();
    }
}
