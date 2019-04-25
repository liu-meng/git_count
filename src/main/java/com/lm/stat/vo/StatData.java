package com.lm.stat.vo;

public class StatData
        implements Comparable<StatData> {
    private String name;
    private long add;
    private long del;
    private long file;
    private String first;
    private String last;
    private long[] adds;
    private long[] dels;

    public StatData() {
    }

    public StatData(String name, long add, long del, long file, String first, String last, long[] adds, long[] dels) {
        this.name = name;
        this.add = add;
        this.del = del;
        this.file = file;
        this.first = first;
        this.last = last;
        this.adds = adds;
        this.dels = dels;
    }

    public StatData addData(StatData sd) {
        for (int i = 0; i < this.adds.length; i++) {
            this.adds[i] += sd.adds[i];
            this.dels[i] += sd.dels[i];
        }
        return new StatData(this.name, this.add + sd.add, this.del + sd.del, this.file + sd.file, this.first, sd.last, this.adds, this.dels);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAdd() {
        return this.add;
    }

    public void setAdd(long add) {
        this.add = add;
    }

    public long getDel() {
        return this.del;
    }

    public void setDel(long del) {
        this.del = del;
    }

    public long getFile() {
        return this.file;
    }

    public void setFile(long file) {
        this.file = file;
    }

    public String getFirst() {
        return this.first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return this.last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public long[] getAdds() {
        return this.adds;
    }

    public void setAdds(long[] adds) {
        this.adds = adds;
    }

    public long[] getDels() {
        return this.dels;
    }

    public void setDels(long[] dels) {
        this.dels = dels;
    }

    public void initDetail(int ind) {
        this.adds = new long[ind];
        this.dels = new long[ind];
    }

    public void setDetail(int ind, long add, long del) {
        this.adds[ind] = add;
        this.dels[ind] = del;
    }

    public String toString() {
        return "name:" + this.name + "; add:" + this.add + "; del:" + this.del + "; files:" + this.file + "; first:" + this.first + "; last:" + this.last;
    }

    public int compareTo(StatData o) {
        return this.add < o.add ? 1 : -1;
    }
}
