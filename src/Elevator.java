import com.oocourse.TimableOutput;
import com.oocourse.elevator3.PersonRequest;

public class Elevator extends Thread {
    private int floor;
    //0 stall 1:up -1:down
    private int state;
    
    public Elevator(int floor,int state) {
        this.floor = floor;
        this.state = state;
    }
    
    public int floor() {
        return this.floor;
    }
    
    public int state() {
        return this.state;
    }
    
    public void addFloor(int i) {
        this.floor = this.floor + i;
    }
    
    public void setState(int i) {
        this.state = i;
    }
    
    public void openDoor() {
        try {
            TimableOutput.println(
                    String.format("OPEN-%d", this.floor));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void closeDoor() {
        try {
            TimableOutput.println(
                    String.format("CLOSE-%d", this.floor));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void arrival() {
        TimableOutput.println(
                String.format("ARRIVE-%d", this.floor));
    }
    
    public void printOut(PersonRequest request) {
        TimableOutput.println(
                String.format("OUT-%d-%d",request.getPersonId(),this.floor));
    }
    
    public void printIn(PersonRequest request) {
        TimableOutput.println(
                String.format("IN-%d-%d",request.getPersonId(),this.floor));
    }
    
}
