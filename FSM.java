/**
 *
 * @author elmo
 */
public class FSM {
    int size;
    int statoAttuale;
    FSM(int j){
        size = (int)Math.pow(2, j);
        statoAttuale = 0;
    }
    void aggiornaStatoFSM(int dirTaken){//saturation counter
        if((dirTaken==1)&&(statoAttuale!=(size-1)))
                statoAttuale++;
        if((dirTaken==0)&&(statoAttuale!=0))
                statoAttuale--;
    }
    String previsioneDir(){
        if(statoAttuale <(size/2))
            return "N";
        else
            return "T";
    }
    int stampaStatoAttuale(){
        return statoAttuale;
    }
    String stampaStatoAttualeStringa(){
        return Integer.toString(statoAttuale);
    }
}
