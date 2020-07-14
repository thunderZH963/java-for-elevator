import com.oocourse.TimableOutput;
import com.oocourse.elevator3.PersonRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import static java.lang.Math.abs;

public class MoreElevator extends Elevator {
    private String name;

    private Scheduler scheduler;
 
    private ArrayList<Integer> reachFloors;
   
    private int speed;
  
    private ArrayList<Request> takeQueueOut = new ArrayList<>();
 
    private Request mainRequest;
 
    private int capacity;

    private int person;
    private int exit;
    
    private ArrayList<Integer> asFloor =
            new ArrayList<>(Arrays
                    .asList(-3,-2,-1,1,15,16,17,18,19,20));
    private ArrayList<Integer> bsFloor =
            new ArrayList<>(Arrays
                    .asList(-2,-1,1,2,4,5,6,7,8,9,10,11,12,13,14,15));
    private ArrayList<Integer> csFloor =
            new ArrayList<>(Arrays
                    .asList(1,3,5,7,9,11,13,15));
    
    public MoreElevator(String name, Scheduler scheduler) {
        super(1,0);
        this.name = name;
        this.scheduler = scheduler;
        if (name.equals("A")) {
            reachFloors = asFloor;
            capacity = 6;
            speed = 400;
        } else if (name.equals("B")) {
            reachFloors = bsFloor;
            capacity = 8;
            speed = 500;
        } else {
            reachFloors = csFloor;
            capacity = 7;
            speed = 600;
        }
    }
    
    public void run() {
        while (true) {
            exit = 0;
            mainRequest = scheduler.get(name);
            exit = 1;
            mainRequest.setEle(name);
            if (mainRequest.getChange() == 0) {
                if ((mainRequest.getPersonRequest().getFromFloor()
                        - mainRequest.getPersonRequest().getToFloor()) < 0) {
                    setState(1);
                } else {
                    setState(-1);
                }
            } else if (mainRequest.getChange() == 1) {
                if ((mainRequest.getPersonRequest().getFromFloor()
                        - mainRequest.getAnotheTo()) < 0) {
                    setState(1);
                } else {
                    setState(-1);
                }
            }
            goTo(super.floor(),mainRequest.
                    getPersonRequest().getFromFloor(),mainRequest);
            //System.out.println(name+"end"+floor());
            setState(0);
            exit = 0;
            if (scheduler.noRun()
                    && scheduler.getInputQueue().size() == 0
                    && scheduler.getRequestQueue().size() == 0) {
                System.exit(0);
            }
        }
        
    }
    
    public int getExit() {
        return exit;
    }
    
    private void goTo(int fromIn, int toIn, Request mainRequest) {
        int from = fromIn;
        int to = toIn;
        if (from != floor()) {
            arrival();
        }
      
        to = checkInOut(from, to);
        for (int i = 0; i < abs(from - to); i++) {
            try {
                sleep(speed);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            i = floorAdd(from,to,i);
            
            if (floor() == to) {
               
                if (mainRequest.getUsed() == 0) {
                    from = to;
                    if (mainRequest.getChange() == 1) {
                        to = mainRequest.getAnotheTo();
                    } else {
                        to = mainRequest.getPersonRequest().getToFloor();
                    }
                } else {
                    
                    from = to;
                }
          
                i = -1;
             
                if (from == to && mainRequest.getUsed() == 1
                        && !(scheduler.isEmpty(name) == -1)) {
                    //System.out.println(name+"break");
                    arrival();
                    break;
                }
            }
            arrival();
            to = checkInOut(from,to);
        }
    }
    
    private int checkInOut(int from, int toIn) {
        int to = toIn;
        int temp;
        if (checkOut() == true) {   //out
            if ((temp = checkIn(true,from,to)) != 0) {
                to = temp;
            }
            closeDoor(); //close
        } else if ((temp = getIn(false, from, to)) != 0) {
            to = temp;
            if ((temp = checkIn(true,from,to)) != 0) {
                to = temp;
            }
            closeDoor();
        }
        return to;
    }
    
    private boolean checkOut() {
        int first = 0;
        for (int i = 0; i < takeQueueOut.size(); i++) {
            if ((takeQueueOut.get(i).getChange() == 0
                    && takeQueueOut.get(i).getPersonRequest().getToFloor()
                    == floor())
                    || (takeQueueOut.get(i).getChange() == 1
                    && takeQueueOut.get(i).getAnotheTo() == floor())) {
                if (first == 0) {
                    first = 1;
                    openDoor();
                }
                printOut(takeQueueOut.get(i).getPersonRequest());
                Request addRequest = null;
                if (takeQueueOut.get(i).getChange() == 1) {
                    addRequest = new Request(new PersonRequest(floor(),
                                    takeQueueOut.get(i).
                                            getPersonRequest().getToFloor(),
                                    takeQueueOut.get(i).
                                            getPersonRequest().getPersonId()));
                }
                takeQueueOut.remove(i);
                if (addRequest != null) {
                    scheduler.add(addRequest);
                }
                i--;
                person--;
            }
        }
        if (first == 1) {
            return true;
        }
        return false;
    }
    
    private int checkIn(boolean hasOpenIn,int from, int to) {
        boolean hasOpen = hasOpenIn;
        int maxFloor = to;
        int temp;
        int flag = 0;
        long lastDate = new Date().getTime();
        while (abs(new Date().getTime() - lastDate) < 400) {
            if ((temp = getIn(hasOpen, from, maxFloor)) != 0) {
                maxFloor = temp;
                flag = 1;
            }
            try {
                sleep((long)399.9);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (flag == 1) {
            return maxFloor;
        }
        return 0;
    }
    
    private int getIn(boolean hasOpenIn, int from, int to) {
        if (person == capacity) {
            return 0;
        }
        int first = 0;
        int maxFloor;
        boolean hasOpen = hasOpenIn;
        if ((maxFloor = checkMainIn(hasOpen, to)) != 0) {
            first = 1;
            hasOpen = true;
        }
        synchronized (scheduler.getRequestQueue()) {
            for (int i = 0; i < scheduler.getRequestQueue().size(); i++) {
                Request nowReq = scheduler.getRequestQueue().get(i);
                //System.out.println(nowReq.getPersonRequest().toString());
                if (nowReq.getPersonRequest().getFromFloor()
                        == floor() && person != capacity) {
                    if (ifTake(from, to, nowReq)) {
                        int nowTo;
                        if (nowReq.getChange() == 1) {
                            nowTo = nowReq.getAnotheTo();
                        } else {
                            nowTo = nowReq.getPersonRequest().getToFloor();
                        }
                        if (first == 0) {
                            if (hasOpen == false) {
                                openDoor();
                            }
                            first = 1;
                            maxFloor = to;
                        }
                        if ((super.state() == 1 && nowTo > to
                                && from <= to)) {
                            if (nowTo > maxFloor) {
                                maxFloor = nowTo;
                            }
                        }
                        if ((super.state() == -1 && nowTo < to
                                && from >= to)) {
                            if (nowTo < maxFloor) {
                                maxFloor = nowTo;
                            }
                        }
                        printIn(nowReq.getPersonRequest());
                        takeQueueOut.add(nowReq);
                        scheduler.remove(i);
                        i--;
                        person++;
                    }
                }
            }
            this.scheduler.getRequestQueue().notifyAll();
        }
        if (first == 1) {
            return maxFloor;
        }
        return 0;
    }
    
    private int checkMainIn(boolean hasOpen, int to) {
        int maxFloor = 0;
        if (mainRequest.getUsed() == 0 &&
                mainRequest.getPersonRequest().getFromFloor() == floor()) {
            if (hasOpen == false) {
                openDoor();
            }
            int nowTo;
            if (mainRequest.getChange() == 1) {
                nowTo = mainRequest.getAnotheTo();
            } else {
                nowTo = mainRequest.getPersonRequest().getToFloor();
            }
            maxFloor = to;
            if ((nowTo > to
                    && super.state() == 1)) {
                if (nowTo > maxFloor) {
                    maxFloor = nowTo;
                }
            }
            if ((nowTo < to
                    && super.state() == -1)) {
                if (nowTo < maxFloor) {
                    maxFloor = nowTo;
                }
            }
            printIn(mainRequest.getPersonRequest());
            takeQueueOut.add(mainRequest);
            mainRequest.setUsed();
            person++;
        }
        return maxFloor;
    }
    
    private boolean ifTake(int from, int to, Request request) {
        request.setEle(name);
        if (request.getChange() == 0
                && !reachFloors.
                contains(request.getPersonRequest().getToFloor())) {
            return false;
        }
        int reqTo;
        if (request.getChange() == 0) {
            reqTo = request.getPersonRequest().getToFloor();
        } else {
            reqTo = request.getAnotheTo();
        }
        boolean caseOne = ((request.getPersonRequest().getFromFloor()
                - reqTo) < 0
                && super.state() == 1 && to >= from);
        boolean caseTwo = ((request.getPersonRequest().getFromFloor()
                - reqTo) > 0
                && super.state() == -1 && to <= from);
        boolean caseThree = (state() == 1 && from - to > 0
                && (request.getPersonRequest().getFromFloor()
                - reqTo) > 0
                && reqTo >= to);
        boolean caseFour = (state() == -1 && from - to < 0
                && (request.getPersonRequest().getFromFloor()
                - reqTo) < 0
                && reqTo <= to);
        return caseOne | caseTwo | caseThree | caseFour;
        
    }
    
    private int floorAdd(int from, int to, int j) {
        int i = j;
        if (from > to) {
            addFloor(-1);
        } else {
            addFloor(1);
        }
        while (!reachFloors.contains(floor())) {
            if (floor() != 0) {
                arrival();
            } else {
                if (from > to) {
                    addFloor(-1);
                    i++;
                } else {
                    addFloor(1);
                    i++;
                }
                continue;
            }
            if (from > to) {
                i++;
                try {
                    sleep(speed);
                } catch (Exception e) {
                    System.out.println(e);
                }
                addFloor(-1);
            } else {
                i++;
                try {
                    sleep(speed);
                } catch (Exception e) {
                    System.out.println(e);
                }
                addFloor(1);
            }
        }
        
        return i;
    }
    
    public void openDoor() {
        try {
            TimableOutput.println(
                    String.format("OPEN-%d-%s", floor(), name));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void closeDoor() {
        try {
            TimableOutput.println(
                    String.format("CLOSE-%d-%s", floor(), name));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void arrival() {
        TimableOutput.println(
                String.format("ARRIVE-%d-%s", floor(), name));
    }
    
    public void printOut(PersonRequest request) {
        TimableOutput.println(
                String.format("OUT-%d-%d-%s",
                        request.getPersonId(),floor(), name));
    }
    
    public void printIn(PersonRequest request) {
        TimableOutput.println(
                String.format("IN-%d-%d-%s",
                        request.getPersonId(),floor(), name));
    }
    
}
