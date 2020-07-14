import com.oocourse.elevator3.PersonRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Request {
    private PersonRequest personRequest;
    private String ele;
    private int change;
    private int anotheTo;
    private int used;
    
    private HashMap<Integer,ArrayList<String>> changeStop = new HashMap<>();
    
    private ArrayList<Integer> asFloor =
            new ArrayList<>(Arrays.
                    asList(-3,-2,-1,1,15,16,17,18,19,20));
    private ArrayList<Integer> bsFloor =
            new ArrayList<>(Arrays.
                    asList(-2,-1,1,2,4,5,6,7,8,9,10,11,12,13,14,15));
    private ArrayList<Integer> csFloor =
            new ArrayList<>(Arrays.
                    asList(1,3,5,7,9,11,13,15));
    
    public Request(PersonRequest personRequest) {
        this.personRequest = personRequest;
        this.ele = null;
        this.used = 0;
        setChange();
    }
    
    public int getChange() {
        return change;
    }
    
    public void setEle(String ele) {
        this.ele = ele;
        if (this.change == 1) {
            getStop();
            adjustDirection();
        }
    }
    
    private void setChange() {
        if ((asFloor.contains(personRequest.getFromFloor())
                && asFloor.contains(personRequest.getToFloor()))
                || (bsFloor.contains(personRequest.getFromFloor())
                && bsFloor.contains(personRequest.getToFloor()))
                || (csFloor.contains(personRequest.getFromFloor())
                && csFloor.contains(personRequest.getToFloor()))) {
            change = 0;
        } else {
            change = 1;
        }
    }
    
    private void getStop() {
        if (ele.equals("A")) {
            for (int i = 0; i < asFloor.size(); i++) {
                ArrayList<String> arrayList = new ArrayList<>();
                if (bsFloor.contains(personRequest.getToFloor())
                        && bsFloor.contains(asFloor.get(i))) {
                    arrayList.add("B");
                }
                if (csFloor.contains(personRequest.getToFloor())
                        && csFloor.contains(asFloor.get(i))) {
                    arrayList.add("C");
                }
                changeStop.put(asFloor.get(i),arrayList);
            }
        }
        if (ele.equals("B")) {
            for (int i = 0; i < bsFloor.size(); i++) {
                ArrayList<String> arrayList = new ArrayList<>();
                if (asFloor.contains(personRequest.getToFloor())
                        && asFloor.contains(bsFloor.get(i))) {
                    arrayList.add("A");
                }
                if (csFloor.contains(personRequest.getToFloor())
                        && csFloor.contains(bsFloor.get(i))) {
                    arrayList.add("C");
                }
                changeStop.put(bsFloor.get(i),arrayList);
            }
        }
        if (ele.equals("C")) {
            for (int i = 0; i < csFloor.size(); i++) {
                ArrayList<String> arrayList = new ArrayList<>();
                if (asFloor.contains(personRequest.getToFloor())
                        && asFloor.contains(csFloor.get(i))) {
                    arrayList.add("A");
                }
                if (bsFloor.contains(personRequest.getToFloor())
                        && bsFloor.contains(csFloor.get(i))) {
                    arrayList.add("B");
                }
                changeStop.put(csFloor.get(i),arrayList);
            }
        }
    }
    
    public PersonRequest getPersonRequest() {
        return personRequest;
    }
    
    private int adjustA() {
        int flag = 0;
        if (ele.equals("A")) {
            for (int i = asFloor.indexOf(personRequest.getFromFloor()) + 1;
                 i < asFloor.size(); i++) {
                if (changeStop.get(asFloor.get(i)) != null
                        && changeStop.get(asFloor.get(i)).size() != 0) {
                    flag = asFloor.get(i);
                    break;
                }
            }
        }
        return flag;
    }
    
    private void adjustDirection() {
        int flag = 0;
        if (ele.equals("A")) {
            flag = adjustA();
        } else if (ele.equals("B")) {
            for (int i = bsFloor.indexOf(personRequest.getFromFloor()) + 1;
                 i < bsFloor.size(); i++) {
                if (changeStop.get(bsFloor.get(i)) != null
                        && changeStop.get(bsFloor.get(i)).size() != 0) {
                    flag = bsFloor.get(i);
                    break;
                }
            }
        } else if (ele.equals("C")) {
            for (int i = csFloor.indexOf(personRequest.getFromFloor()) + 1;
                 i < csFloor.size(); i++) {
                if (changeStop.get(csFloor.get(i)) != null
                        && changeStop.get(csFloor.get(i)).size() != 0) {
                    flag = csFloor.get(i);
                    break;
                }
            }
        }
        if (personRequest.getFromFloor() < personRequest.getToFloor()
                && flag != 0) {
            anotheTo = flag;
            return;
        }
        if (ele.equals("A")) {
            for (int i = asFloor.indexOf(personRequest.getFromFloor()) - 1;
                 i >= 0; i--) {
                if (changeStop.get(asFloor.get(i)) != null
                        && changeStop.get(asFloor.get(i)).size() != 0) {
                    flag = asFloor.get(i);
                    break;
                }
            }
        } else if (ele.equals("B")) {
            for (int i = bsFloor.indexOf(personRequest.getFromFloor()) - 1;
                 i >= 0; i--) {
                if (changeStop.get(bsFloor.get(i)) != null
                        && changeStop.get(bsFloor.get(i)).size() != 0) {
                    flag = bsFloor.get(i);
                    break;
                }
            }
        } else if (ele.equals("C")) {
            for (int i = csFloor.indexOf(personRequest.getFromFloor()) - 1;
                 i >= 0; i--) {
                if (changeStop.get(csFloor.get(i)) != null
                        && changeStop.get(csFloor.get(i)).size() != 0) {
                    flag = csFloor.get(i);
                    break;
                }
            }
        }
        anotheTo = flag;
    }
    
    public int getAnotheTo() {
        return this.anotheTo;
    }
    
    public void setUsed() {
        this.used = 1;
    }
    
    public int getUsed() {
        return this.used;
    }
    
}
