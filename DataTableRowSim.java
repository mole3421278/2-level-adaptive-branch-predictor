
/**
 *
 * @author elmo
 */
public class DataTableRowSim {
    
        String BIA;
        String realBTA;
        String hitmissBTB;
        String BTBBIA;
        String BTBBTA;
        String BHSR; //portato a stringa per vedere ogni singolo bit
        String numberPHT;
        String indexPHT;
        String FSM;
        String DirPred;
        String newBHSR;
        String newFSM;
        DataTableRowSim(String BIA, String realBTA, String hitmissBTB, String BTBBIA, String BTBBTA, String BHSR,
                String numberPHT, String indexPHT, String FSM, String DirPred, String newBHSR, String newFSM){
            this.BIA = BIA;
            this.realBTA = realBTA;
            this.hitmissBTB = hitmissBTB;
            this.BTBBIA = BTBBIA;
            this.BTBBTA = BTBBTA;
            this.BHSR = BHSR;
            this.numberPHT = numberPHT;
            this.indexPHT = indexPHT;
            this.FSM = FSM;
            this.DirPred = DirPred;
            this.newBHSR = newBHSR;
            this.newFSM = newFSM;
        }//senza i seguenti apparentemente inutili getter la tabella non viene caricata. Inoltre, la parola che segue "get" deve essere maiuscola
        public String getBIA(){
            return BIA;
        }
        public String getRealBTA(){
            return realBTA;
        }
        public String getHitmissBTB(){
            return hitmissBTB;
        }
        public String getBTBBIA(){
            return BTBBIA;
        }
        public String getBTBBTA(){
            return BTBBTA;
        }
        public String getBHSR(){
            return BHSR;
        }
        public String getNumberPHT(){
            return numberPHT;
        }
        public String getIndexPHT(){
            return indexPHT;
        }
        public String getFSM(){
            return FSM;
        }
        public String getDirPred(){
            return DirPred;
        }
        public String getNewBHSR(){
            return newBHSR;
        }
        public String getNewFSM(){
            return newFSM;
        }
}
