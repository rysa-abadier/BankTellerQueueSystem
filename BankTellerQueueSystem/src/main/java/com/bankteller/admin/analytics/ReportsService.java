package com.bankteller.admin.analytics;

import java.util.*;

public class ReportsService {

    public static double getAverageWaitTime() {
        return 6.8; // minutes
    }

    public static List<Object[]> getTellerEfficiency() {
        List<Object[]> data = new ArrayList<>();
        data.add(new Object[]{"Teller A", 35, "4.5 mins", "Excellent"});
        data.add(new Object[]{"Teller B", 28, "5.2 mins", "Good"});
        data.add(new Object[]{"Teller C", 22, "6.1 mins", "Average"});
        return data;
    }

    public static List<Object[]> getCustomerVolume(String period) {
        List<Object[]> data = new ArrayList<>();
        if (period.equals("daily")) {
            data.add(new Object[]{"July 17", 53});
            data.add(new Object[]{"July 18", 60});
            data.add(new Object[]{"July 19", 45});
        } else if (period.equals("weekly")) {
            data.add(new Object[]{"Week 1", 310});
            data.add(new Object[]{"Week 2", 280});
        } else if (period.equals("monthly")) {
            data.add(new Object[]{"June", 1230});
            data.add(new Object[]{"July", 890});
        }
        return data;
    }

    public static List<Object[]> getPeakHours() {
        List<Object[]> data = new ArrayList<>();
        data.add(new Object[]{"09:00 - 10:00", 25});
        data.add(new Object[]{"10:00 - 11:00", 18});
        data.add(new Object[]{"12:00 - 13:00", 40}); // peak
        data.add(new Object[]{"14:00 - 15:00", 19});
        data.add(new Object[]{"16:00 - 17:00", 30});
        return data;
    }
}
