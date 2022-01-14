/**
 *
 * @author elmo
 */
public class BHSR {
    int size;
    int[] element;
    BHSR(int k){
        size = k;
        element = new int[k];
        for(int i=0;i<k;i++)
            element[i] = 0;
    }
    public void leftShift(int dirTaken){
        for(int i=size-1;i>0;i--)
            element[i] = element[i-1];
        element[0] = dirTaken;
    }
    public int convertiInDecimale(){
        int index = 0;
        for(int i=0;i<size;i++){
            index += element[i]*((int)(Math.pow(2, i)));
        }
        return index;
    }
    public String stampa(){
        String s = "";
        for(int i=size-1;i>=0;i--){
            s += Integer.toString(element[i]);
        }
        return s;
    }
}
