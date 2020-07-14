import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.PersonRequest;

public class InputHandle extends Thread {
    private Scheduler scheduler;
    
    public InputHandle(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
    
    public void run() {
        try {
            ElevatorInput elevatorInput = new ElevatorInput(System.in);
            while (true) {
                PersonRequest request = elevatorInput.nextPersonRequest();
                if (request == null) {
                    break;
                } else {
                    scheduler.put(request);
                }
            }
            elevatorInput.close();
            scheduler.setStall();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}