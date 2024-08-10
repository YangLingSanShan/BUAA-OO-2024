import java.util.HashMap;

public class ErrorCount {
    public static final ErrorCount counter = new ErrorCount();
    private int epiNum = 0;
    private HashMap<Integer, Integer> epi = new HashMap<>();
    private int erNum = 0;
    private HashMap<Integer, Integer> er = new HashMap<>();
    private int pinfNum = 0;
    private HashMap<Integer, Integer> pinf = new HashMap<>();
    private int rnfNum = 0;
    private HashMap<Integer, Integer> rnf = new HashMap<>();
    private int anfNum = 0;
    private HashMap<Integer, Integer> anf = new HashMap<>();
    private int etiNum = 0;
    private HashMap<Integer, Integer> eti = new HashMap<>();
    private int pnfNum = 0;
    private HashMap<Integer, Integer> pnf = new HashMap<>();
    private int tinfNum = 0;
    private HashMap<Integer, Integer> tinf = new HashMap<>();

    public int getEpiNum() {
        return epiNum;
    }

    public int getErNum() {
        return erNum;
    }

    public int getPinfNum() {
        return pinfNum;
    }

    public int getRnfNum() {
        return rnfNum;
    }

    public int getAnfNum() {
        return anfNum;
    }

    public int getEtiNum() {
        return etiNum;
    }

    public int getPnfNum() {
        return pnfNum;
    }

    public int getTinfNum() {
        return tinfNum;
    }

    public int getEpiIdTriggerTime(int id) {
        return epi.get(id);
    }

    public int getErIdTriggerTime(int id) {
        return er.get(id);
    }

    public int getPinfIdTriggerTime(int id) {
        return pinf.get(id);
    }

    public int getRnfIdTriggerTime(int id) {
        return rnf.get(id);
    }

    public int getAnfIdTriggerTime(int id) {
        return anf.get(id);
    }

    public int getEtiIdTriggerTime(int id) {
        return eti.get(id);
    }

    public int getPnfIdTriggerTime(int id) {
        return pnf.get(id);
    }

    public int getTinfIdTriggerTime(int id) {
        return tinf.get(id);
    }

    public void triggerEqualPersonIdException(int id) {
        epiNum++;
        if (epi.containsKey(id)) {
            epi.replace(id, epi.get(id) + 1);
        } else {
            epi.put(id, 1);
        }
    }

    public void triggerEqualRelationException(int id1, int id2) {
        erNum++;
        if (er.containsKey(id1)) {
            er.replace(id1, er.get(id1) + 1);
        } else {
            er.put(id1, 1);
        }
        if (id2 != id1) {
            if (er.get(id2) == null) {
                er.put(id2, 1);
            } else {
                er.replace(id2, er.get(id2) + 1);
            }
        }
    }

    public void triggerPersonIdNotFoundException(int id) {
        pinfNum++;
        if (pinf.containsKey(id)) {
            pinf.replace(id, pinf.get(id) + 1);
        } else {
            pinf.put(id, 1);
        }
    }

    public void triggerRelationNotFoundException(int id1, int id2) {
        rnfNum++;
        if (rnf.containsKey(id1)) {
            rnf.replace(id1, rnf.get(id1) + 1);
        } else {
            rnf.put(id1, 1);
        }
        //Here will not happen id1 == id2, or it will happen EqualPersonIdException.
        if (rnf.containsKey(id2)) {
            rnf.replace(id2, rnf.get(id2) + 1);
        } else {
            rnf.put(id2, 1);
        }
    }

    public void triggerAcquaintanceNotFoundException(int id) {
        anfNum++;
        if (anf.containsKey(id)) {
            anf.replace(id, anf.get(id) + 1);
        } else {
            anf.put(id, 1);
        }
    }

    public void triggerEqualTagIdException(int id) {
        etiNum++;
        if (eti.containsKey(id)) {
            eti.replace(id, eti.get(id) + 1);
        } else {
            eti.put(id, 1);
        }
    }

    public void triggerPathNotFoundException(int id1, int id2) {
        pnfNum++;
        if (pnf.containsKey(id1)) {
            pnf.replace(id1, pnf.get(id1) + 1);
        } else {
            pnf.put(id1, 1);
        }
        if (id2 != id1) {
            if (pnf.get(id2) == null) {
                pnf.put(id2, 1);
            } else {
                pnf.replace(id2, pnf.get(id2) + 1);
            }
        }
    }

    public void triggerTagIdNotFoundException(int id) {
        tinfNum++;
        if (tinf.containsKey(id)) {
            tinf.replace(id, tinf.get(id) + 1);
        } else {
            tinf.put(id, 1);
        }
    }
}
