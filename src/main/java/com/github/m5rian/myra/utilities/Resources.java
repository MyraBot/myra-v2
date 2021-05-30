package com.github.m5rian.myra.utilities;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class Resources {
    public String getCpuLoad() throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});

        if (list.isEmpty()) return String.valueOf(Double.NaN);

        Attribute att = (Attribute) list.get(0);
        Double value = (Double) att.getValue();

        // usually takes a couple of seconds before we get real values
        if (value == -1.0) return String.valueOf(Double.NaN);
        // returns a percentage value with 1 decimal point precision
        double v = (value * 1000) / 10.0;
        long round = Math.round(v);
        return round + "%";
    }

    public String getRAMUsage() {
        try {
            Runtime runtime = Runtime.getRuntime();
            long allocatedMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            return String.valueOf((allocatedMemory - freeMemory) / 1000 / 1000);
        } catch (Exception e) {
            return "There was an error while attempting to collect ram usage!";
        }
    }


    public String getRunningThreads() {
        return String.valueOf(Thread.activeCount());
    }
}
