import com.oocourse.TimableOutput;

public class Main {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        Scheduler scheduler = new Scheduler();
        InputHandle inputHandle = new InputHandle(scheduler);
        inputHandle.start();
        inputHandle.setName("input");
        scheduler.startPool();
        scheduler.start();
        scheduler.setName("SCHE");
    }
}
