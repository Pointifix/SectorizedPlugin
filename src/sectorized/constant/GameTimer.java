package sectorized.constant;

import arc.struct.Seq;
import arc.util.Time;

public class GameTimer {
    private static final Seq<ScheduledTask> tasks = new Seq<>();

    public static void schedule(Runnable task, float delaySeconds) {
        tasks.add(new ScheduledTask(task, delaySeconds * 60f, 0, 1));
    }

    public static void schedule(Runnable task, float delaySeconds, float periodSeconds) {
        tasks.add(new ScheduledTask(task, delaySeconds * 60f, periodSeconds * 60f, -1));
    }

    public static void schedule(Runnable task, float delaySeconds, float periodSeconds, int repeatCount) {
        tasks.add(new ScheduledTask(task, delaySeconds * 60f, periodSeconds * 60f, repeatCount));
    }

    public static void update() {
        float delta = Time.delta;
        for (int i = tasks.size - 1; i >= 0; i--) {
            ScheduledTask t = tasks.get(i);
            t.delay -= delta;
            if (t.delay <= 0) {
                t.task.run();
                if (t.period > 0 && (t.repeatCount < 0 || t.repeatCount > 0)) {
                    if (t.repeatCount > 0) t.repeatCount--;
                    t.delay = t.period;
                } else {
                    tasks.remove(i);
                }
            }
        }
    }

    public static void clear() {
        tasks.clear();
    }

    private static class ScheduledTask {
        final Runnable task;
        float delay;
        final float period;
        int repeatCount;

        ScheduledTask(Runnable task, float delay, float period, int repeatCount) {
            this.task = task;
            this.delay = delay;
            this.period = period;
            this.repeatCount = repeatCount;
        }
    }
}
