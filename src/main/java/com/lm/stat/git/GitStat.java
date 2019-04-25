package com.lm.stat.git;

import com.lm.service.IGitService;
import com.lm.stat.vo.StatData;
import com.lm.stat.vo.StatParam;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.SequenceInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
import org.springframework.stereotype.Service;

@Service
public class GitStat
        implements IGitService
{
    private Pattern pat = Pattern.compile("(\\d+)\\s(\\d+)\\s");

    public JSONArray stat(String method, String startDate, String endDate, String author, String gitRoot)
            throws Exception
    {
        Stack<String> result = multiCmdExec(method, startDate, endDate, author, gitRoot);
        StatParam statParam = calcDays(method, startDate, endDate);

        boolean start = true;
        long add = 0L;long del = 0L;long num = 0L;

        Map<String, StatData> stat = new HashMap();
        StatData sd = new StatData();
        while (!result.empty())
        {
            String line = (String)result.pop();

            Matcher m = this.pat.matcher(line);
            if (m.find())
            {
                MatchResult mr = m.toMatchResult();
                add += Long.parseLong(mr.group(1));
                del += Long.parseLong(mr.group(2));
                num += 1L;
            }
            else
            {
                String[] infos = line.split(";");
                String name = infos[0];
                String date = infos[1];

                sd.setName(name);
                sd.setAdd(add);
                sd.setDel(del);
                sd.setFile(num);
                sd.setFirst(date);
                sd.setLast(date);
                sd.initDetail(new Long(statParam.getDays()).intValue());
                sd.setDetail(statParam.calcDays(date), add, del);
                if (stat.containsKey(name))
                {
                    StatData msd = (StatData)stat.get(name);
                    stat.put(name, msd.addData(sd));
                }
                else
                {
                    stat.put(name, sd);
                }
                add = 0L;
                del = 0L;
                num = 0L;
                sd = new StatData();
            }
        }
        List<StatData> gsd = new ArrayList();
        for (Iterator<String> it = stat.keySet().iterator(); it.hasNext();)
        {
            String name = (String)it.next();
            StatData msd = (StatData)stat.get(name);
            gsd.add(msd);
        }
        Collections.sort(gsd);

        JSONArray sdArray = new JSONArray();
        JsonConfig config = new JsonConfig();
        config.setIgnoreDefaultExcludes(false);
        config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        config.setExcludes(new String[] { "days" });
        for (StatData msd : gsd)
        {
            JSONObject md = JSONObject.fromObject(msd);
            sdArray.add(md);
        }
        return sdArray;
    }

    private StatParam calcDays(String method, String startDate, String endDate)
            throws ParseException
    {
        StatParam sp = new StatParam();
        long days = 0L;
        Date startTime = null;
        Date lastTime = null;
        Calendar cur = Calendar.getInstance();
        switch (Integer.parseInt(method))
        {
            case 0:
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                startTime = sdf.parse(startDate);
                lastTime = sdf.parse(endDate);
                days = (lastTime.getTime() - startTime.getTime()) / 86400000L;
                days += 1L;
                break;
            case 1:
                lastTime = cur.getTime();
                cur.add(5, -1);
                startTime = cur.getTime();
                days = 1L;
                break;
            case 2:
                lastTime = cur.getTime();
                cur.add(5, -7);
                startTime = cur.getTime();
                days = 7L;
                break;
            case 3:
                Date today = new Date();
                lastTime = cur.getTime();
                cur.add(2, -1);
                startTime = cur.getTime();
                days = (lastTime.getTime() - startTime.getTime()) / 86400000L;
                break;
        }
        sp.setStartDate(startTime);
        sp.setEndDate(lastTime);
        sp.setDays(days);
        return sp;
    }

    private Stack<String> multiCmdExec(String method, String startDate, String endDate, String author, String gitRoot)
            throws IOException
    {
        Stack<String> s = new Stack();
        String since = null;
        String until = null;
        String committer = null;
        switch (Integer.parseInt(method))
        {
            case 0:
                since = startDate;
                until = endDate;
                break;
            case 1:
                since = "1.day.ago";
                break;
            case 2:
                since = "1.week.ago";
                break;
            case 3:
                since = "1.month.ago";
                break;
        }
        String cmd = "git log --pretty=format:\"%cn;%ad;%d\" --numstat --date=iso --since=" + since;

        cmd = cmd + ((until != null) && (!until.equals("")) ? " --until=" + until : "");

        System.out.println("cmd=>" + cmd);
        gitRoot = gitRoot.replaceAll("//", File.separator);
        gitRoot = gitRoot.replaceAll("\\\\", "\\" + File.separator);
        try
        {
            Process process = null;
            String os = System.getProperty("os.name");
            if (os.toLowerCase().startsWith("win")) {
                process = Runtime.getRuntime().exec("cmd", null, new File(gitRoot));
            } else {
                process = Runtime.getRuntime().exec("sh", null, new File(gitRoot));
            }
            SequenceInputStream sis = new SequenceInputStream(process.getInputStream(), process.getErrorStream());

            InputStreamReader isr = new InputStreamReader(sis, "utf-8");
            BufferedReader br = new BufferedReader(isr);

            OutputStreamWriter osw = new OutputStreamWriter(process.getOutputStream());
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(cmd);
            bw.newLine();
            bw.flush();
            bw.close();
            osw.close();

            String line = null;
            String mid = null;
            boolean r = false;
            while (null != (line = br.readLine())) {
                if (!line.startsWith("-"))
                {
                    Matcher m = this.pat.matcher(line);
                    if (m.find())
                    {
                        if ((mid != null) && (!r)) {
                            s.add(mid.replace(" +0800", ""));
                        }
                        s.add(line);
                        r = true;
                    }
                    else
                    {
                        if (r) {
                            r = false;
                        }
                        mid = line;
                    }
                }
            }
            process.destroy();
            br.close();
            isr.close();
            return s;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
