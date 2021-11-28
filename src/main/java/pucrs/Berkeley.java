package pucrs;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Berkeley {
    public static List<Configuration> calculate(List<Configuration> times) {
        var localTime = LocalTime.now();
        var timesWithMaster = new ArrayList<>(times);
        timesWithMaster.add(new Configuration(0, localTime, LocalTime.parse("00:00")));

        var average = timesWithMaster.stream()
                .mapToLong(time -> localTime.toNanoOfDay() - time.getTime().toNanoOfDay())
                .average()
                .orElseThrow();

        times.stream().forEach(time -> {
            var difference = localTime.toNanoOfDay() - time.getTime().toNanoOfDay();
            var rtt = difference - time.getDelay().toNanoOfDay();
            var oneWayDelay = rtt / 2;
            if (time.getTime().isAfter(localTime))
                time.setTime(time.getTime().minusNanos(difference + oneWayDelay));
            else
                time.setTime(time.getTime().plusNanos(difference));
//            if (time.getTime().isAfter(localTime))
//                time.setTime(time.getTime().minusNanos((long) average + difference - time.getDelay().toNanoOfDay()));
//            else
//                time.setTime(time.getTime().plusNanos((long) average + difference - time.getDelay().toNanoOfDay()));
        });

        // RTT = localTime - times[i].getTime() - tdelay
        // one-way delay = RTT/2
        return times;
    }
}
