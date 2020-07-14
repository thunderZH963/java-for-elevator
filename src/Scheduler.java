import com.oocourse.elevator3.PersonRequest;
import java.util.ArrayList;
import java.util.Arrays;

public class Scheduler extends Thread {
    private int stall;
    private ArrayList<Request> inputQueue = new ArrayList<>();
    private ArrayList<Request> requestQueue = new ArrayList<>();
    private ArrayList<MoreElevator> elevatorPool = new ArrayList<>();
    private ArrayList<Integer> asFloor =
            new ArrayList<>(Arrays
                    .asList(-3,-2,-1,1,15,16,17,18,19,20));
    private ArrayList<Integer> bsFloor =
            new ArrayList<>(Arrays
                    .asList(-2,-1,1,2,4,5,6,7,8,9,10,11,12,13,14,15));
    private ArrayList<Integer> csFloor =
            new ArrayList<>(Arrays
                    .asList(1,3,5,7,9,11,13,15));
    
    public Scheduler() {
        elevatorPool.add(new MoreElevator("A",this));
        elevatorPool.add(new MoreElevator("B",this));
        elevatorPool.add(new MoreElevator("C", this));
    }
    
    public void run() {
        while (true) {
            upgrading();
        }
    }
    
    public void upgrading() {
        synchronized (inputQueue) {
            while (inputQueue.size() == 0) {
                try {
                    inputQueue.wait();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            synchronized (requestQueue) {
                Request request = inputQueue.get(0);
                inputQueue.remove(0);
                requestQueue.add(request);
                requestQueue.notifyAll();
                //if (inputQueue.size() == 0 && getStall() == 1) {
                //  break;
                //}
            }
            inputQueue.notifyAll();
        }
    }
    
    public void startPool() {
        for (int i = 0; i < elevatorPool.size(); i++) {
            elevatorPool.get(i).start();
            elevatorPool.get(i).setName("电梯");
        }
    }
    
    public Request get(String name) {
        synchronized (requestQueue) {
            while (requestQueue.isEmpty() || isEmpty(name) == -1) {
                try {
                    requestQueue.wait();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            int temp = isEmpty(name);
            Request request = requestQueue.get(temp);
            requestQueue.remove(temp);
            requestQueue.notifyAll();
            return request;
        }
    }
    
    public void put(PersonRequest request) {
        synchronized (inputQueue) {
            inputQueue.add(new Request(request));
            inputQueue.notifyAll();
        }
    }
    
    public void setStall() {
        this.stall = 1;
    }
    
    public int getStall() {
        return this.stall;
    }
    
    public ArrayList<Request> getInputQueue() {
        return this.inputQueue;
    }
    
    public int isEmpty(String name) {
        if (requestQueue.size() == 0) {
            return -1;
        }
        if (name.equals("A")) {
            for (int i = 0; i < requestQueue.size(); i++) {
                if (asFloor.contains(requestQueue.get(i).
                        getPersonRequest().getFromFloor())
                        && !(requestQueue.get(i).getChange() == 0
                        && !asFloor.contains(requestQueue.get(i).
                        getPersonRequest().getToFloor()))) {
                    return i;
                }
            }
            return -1;
        } else if (name.equals("B")) {
            for (int i = 0; i < requestQueue.size(); i++) {
                if (bsFloor.contains(requestQueue.get(i).
                        getPersonRequest().getFromFloor())
                        && !(requestQueue.get(i).getChange() == 0
                        && !bsFloor.contains(requestQueue.get(i).
                        getPersonRequest().getToFloor()))) {
                    return i;
                }
            }
            return -1;
        } else if (name.equals("C")) {
            for (int i = 0; i < requestQueue.size(); i++) {
                if (csFloor.contains(requestQueue.get(i).
                        getPersonRequest().getFromFloor())
                        && !(requestQueue.get(i).getChange() == 0
                        && !csFloor.contains(requestQueue.get(i).
                        getPersonRequest().getToFloor()))) {
                    return i;
                }
            }
            return -1;
        }
        return -1;
    }
    
    public ArrayList<Request> getRequestQueue() {
        return this.requestQueue;
    }
    
    public void remove(int i) {
        this.getRequestQueue().remove(i);
    }
    
    public void add(Request request) {
        requestQueue.add(request);
    }
    
    public boolean noRun() {
        if (getStall() == 1
                && elevatorPool.get(0).getExit() == 0
                && elevatorPool.get(1).getExit() == 0
                && elevatorPool.get(2).getExit() == 0) {
            return true;
        } else {
            return false;
        }
    }
    
}
