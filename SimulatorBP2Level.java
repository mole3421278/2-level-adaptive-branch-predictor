
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import javafx.application.*;
import static javafx.application.Application.*;
import javafx.collections.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;
import javafx.stage.*;

/**
 *
 * @author elmo
 */
public class SimulatorBP2Level extends Application {
    static int a; //numero di bit per l'indirizzo dell'istruzione
    static int p; //numero di bit di BIA usato per selezionare una delle tabelle PHT relativa ad un branch o set di branch
    static int m; //numero di bit di BIA usato per selezionare il BHSR nel BHT relativo ad un branch o set di branch
                    //il BHSR così selezionato viene usato per indicizzare la tabella PHT selezionata con p
                        //si ottengono a questo modo i j bit dello stato corrente della FSM
    static int k; //numero di bit del BHSR
    static int j; //numero di bit della FSM
    static List<Integer> BIA;
    static HashMap<Integer,Integer> BTB; //si suppone si utilizzi un BTB di grandezza infinita
    static List<Integer> realBTA;
    static BHSR[] BHT;
    static FSM[][] PHT;
    static int mValue;
    static int pValue;
    static int countRightDec = 0;
    static float accuracy;
    TableView<DataTableRowSim> tab;
    TableColumn<DataTableRowSim, String> Branchcol, BIAcol, RealBTAcol, BTBcol, BTBhitcol, BTBBIAcol, BTBBTAcol, BHTcol, BHSRcol, PHTcol, PHTnumbercol, PHTindexcol, FSMcol, DirPredcol, updatedBHSRcol, updatedFSMcol;
    static ObservableList<DataTableRowSim> tabObservableList;
    Button b;
    Label l, l2, l3, l4;
    static String log = "", log2 = "", log3 = "", log4 = "";
    static int scorriBIA = 0;
    static String hitmiss = "", BTBBIA = "", BTBBTA = "", oldBHSR = "", oldBHSRconv = "", oldFSM = "", oldPrev = "", nFSM = "";
    static boolean aggiornato = true;// lo uso per sapere se sto facendo la previsione o l'aggiornamento delle strutture dati
    static boolean termineSimulazione = false;
    @Override
    public void start(Stage primaryStage) {
        createTable();
        VBox root = new VBox();
        VBox vbox = new VBox();
        HBox hbox = new HBox();
        vbox.setAlignment(Pos.CENTER);
        b = new Button("Click Here To Continue The Simulation");
        l = new Label();
        l.setMinWidth(250);//fatto per distanziare ogni label
        l.setAlignment(Pos.CENTER);
        l2 = new Label();
        l2.setMinWidth(250);
        l2.setAlignment(Pos.CENTER);
        l3 = new Label();
        l3.setMinWidth(250);
        l3.setAlignment(Pos.CENTER);
        l4 = new Label();
        l4.setMinWidth(250);
        l4.setAlignment(Pos.CENTER);
        inizializza();
        b.setOnAction((event) -> {
            if(termineSimulazione == true){
                l3.setText("");
                log2 = "The simulation is finished with accuracy: " + accuracy + "%.";
                System.out.println(log2);
                l2.setText(log2);
                return;
            }
            procedura();
            if(aggiornato == false){
                l2.setText(log2);
                l3.setText(log3);
                l4.setText(log4);
                tabObservableList.add(new DataTableRowSim(Integer.toString(BIA.get(scorriBIA)),"",hitmiss,BTBBIA,BTBBTA,
                BHT[mValue].stampa(),Integer.toString(pValue),Integer.toString(BHT[mValue].convertiInDecimale()),
                        PHT[pValue][BHT[mValue].convertiInDecimale()].stampaStatoAttualeStringa(),
                PHT[pValue][BHT[mValue].convertiInDecimale()].previsioneDir(),"",""));
                oldBHSR = BHT[mValue].stampa();
                oldBHSRconv = Integer.toString(BHT[mValue].convertiInDecimale());
                oldFSM = PHT[pValue][BHT[mValue].convertiInDecimale()].stampaStatoAttualeStringa();
                oldPrev = PHT[pValue][BHT[mValue].convertiInDecimale()].previsioneDir();
            }
            else{
                l3.setText(log3);
                l4.setText(log4);
                tabObservableList.remove(tabObservableList.size()-1);
                tabObservableList.add(new DataTableRowSim(Integer.toString(BIA.get(scorriBIA-1)),
                        Integer.toString(realBTA.get(scorriBIA-1)),hitmiss,BTBBIA,BTBBTA,
                oldBHSR,Integer.toString(pValue),oldBHSRconv, oldFSM,oldPrev,BHT[mValue].stampa(),nFSM));
                accuracy = (float)(countRightDec*100/scorriBIA);
                l.setText(log + " \naccuracy: " + accuracy + "%");
            }
            tab.setItems(tabObservableList);
});
        l.setText(log);//Ogni volta viene aggiornato con ciò che viene mandato in output
        hbox.getChildren().addAll(l,l2,l3,l4);
        vbox.getChildren().add(b);
        Group group = new Group();
        group.getChildren().add(tab);
        root.getChildren().addAll(group, vbox, hbox);

        Scene scene = new Scene(root, 1070, 650);
        
        primaryStage.setTitle("Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    void procedura(){
        if(scorriBIA<BIA.size()){
            if(aggiornato == true)//se è aggiornato vuol dire che posso fare una nuova previsione
                effettuaPrevisione(BIA.get(scorriBIA));//mi rende aggiornato false
            else{
                aggiorna(BIA.get(scorriBIA),realBTA.get(scorriBIA));//mi rende aggiornato true
                scorriBIA++;
            }
        }
        else
            termineSimulazione = true;
    }
    void createTable(){
        tabObservableList = FXCollections.observableArrayList();
        tab = new TableView<DataTableRowSim>();
        tab.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tab.setPrefWidth(1060);
        tab.setPrefHeight(400);
        Branchcol = new TableColumn<DataTableRowSim, String>("new branch");
        BIAcol = new TableColumn<DataTableRowSim, String>("BIA");
        BIAcol.setCellValueFactory(new PropertyValueFactory<>("BIA"));
        RealBTAcol = new TableColumn<DataTableRowSim, String>("Real-BTA");
        RealBTAcol.setCellValueFactory(new PropertyValueFactory<>("realBTA"));
        Branchcol.getColumns().addAll(BIAcol, RealBTAcol);
        BTBcol = new TableColumn<DataTableRowSim, String>("BTB");
        BTBhitcol = new TableColumn<DataTableRowSim, String>("Hit/Miss");
        BTBhitcol.setCellValueFactory(new PropertyValueFactory<>("hitmissBTB"));
        BTBBIAcol = new TableColumn<DataTableRowSim, String>("BTB BIA");
        BTBBIAcol.setCellValueFactory(new PropertyValueFactory<>("BTBBIA"));
        BTBBTAcol = new TableColumn<DataTableRowSim, String>("BTB BTA");
        BTBBTAcol.setCellValueFactory(new PropertyValueFactory<>("BTBBTA"));
        BTBcol.getColumns().addAll(BTBhitcol, BTBBIAcol, BTBBTAcol);
        BHTcol = new TableColumn<DataTableRowSim, String>("BHT");
        BHSRcol = new TableColumn<DataTableRowSim, String>("BHSR");
        BHSRcol.setCellValueFactory(new PropertyValueFactory<>("BHSR"));
        BHTcol.getColumns().add(BHSRcol);
        PHTcol = new TableColumn<DataTableRowSim, String>("PHT");
        PHTnumbercol = new TableColumn<DataTableRowSim, String>("number");
        PHTnumbercol.setCellValueFactory(new PropertyValueFactory<>("numberPHT"));
        PHTindexcol = new TableColumn<DataTableRowSim, String>("index");
        PHTindexcol.setCellValueFactory(new PropertyValueFactory<>("indexPHT"));
        FSMcol = new TableColumn<DataTableRowSim, String>("FSM");//è il contenuto del PHT
        FSMcol.setCellValueFactory(new PropertyValueFactory<>("FSM"));
        PHTcol.getColumns().addAll(PHTnumbercol, PHTindexcol, FSMcol);
        DirPredcol = new TableColumn<DataTableRowSim, String>("DirPred");
        DirPredcol.setCellValueFactory(new PropertyValueFactory<>("DirPred"));
        updatedBHSRcol = new TableColumn<DataTableRowSim, String>("new BHSR");
        updatedBHSRcol.setCellValueFactory(new PropertyValueFactory<>("newBHSR"));
        updatedFSMcol = new TableColumn<DataTableRowSim, String>("new FSM");
        updatedFSMcol.setCellValueFactory(new PropertyValueFactory<>("newFSM"));
        tab.getColumns().addAll(Branchcol, BTBcol, BHTcol, PHTcol, DirPredcol, updatedBHSRcol, updatedFSMcol);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        
    }
    static void effettuaPrevisione(int bia){
        aggiornato = false;
        verificaPresenzaInBTB(bia);
        mValue = 0;//indice per BHT
        mValue = (bia/(a/8))%(int)(Math.pow(2, m));//con bia/(a/8) tolgo i primi due bit in architetture a 32 bit e i primi tre bit in architetture a 64 bit
        pValue = 0;//seleziona la tabella PHT
        pValue = (bia/(a/8))%(int)(Math.pow(2,p));
        String s = PHT[pValue][BHT[mValue].convertiInDecimale()].previsioneDir();
        log2 = "";
        log3 = "";
        log2 += " BIA: " + bia + "\n\n ";
        log2 += " BHSR: " + BHT[mValue].stampa() + "\n ";
        log2 += " Current State FSM: " + PHT[pValue][BHT[mValue].convertiInDecimale()].stampaStatoAttuale() + "\n ";
        log2 += " Prediction: " + s + "\n ";
        System.out.println(log2);
    }
    static void verificaPresenzaInBTB(int bia){
        if(!BTB.containsKey(bia)){//se non ho bia in BTB non posso saltare un ciclo per la predizione
            hitmiss = "MISS";
            BTBBIA = "-";
            BTBBTA = "-";
        }
        else{
            hitmiss = "HIT";
            BTBBIA = Integer.toString(bia);
            BTBBTA = Integer.toString(BTB.get(bia));
        }
    }
    static void aggiorna(int bia, int bta){
        aggiornato = true;
        log3 += " real BTA: " + bta + "\n\n ";
        aggiornaBTB(bia, bta);
        mValue = 0;//indice per BHT
        mValue = (bia/(a/8))%(int)(Math.pow(2, m));
        pValue = 0;//seleziona la tabella PHT
        pValue = (bia/(a/8))%(int)(Math.pow(2,p));
        int realDirTaken = 0;
        if(bta==(bia+(a/8))){//ottengo che sommo 4 per architetture a 32 bit e 8 per quelle a 64 bit
            realDirTaken = 0;
            log3 += " Real Direction: N" + "\n ";
        }
        else{
            realDirTaken = 1;//1 è Taken, 0 è NotTaken
            log3 += " Real Direction: T" + "\n ";
        }
        if((realDirTaken == 1) && (PHT[pValue][BHT[mValue].convertiInDecimale()].previsioneDir().equals("T"))||
                (realDirTaken == 0) && (PHT[pValue][BHT[mValue].convertiInDecimale()].previsioneDir().equals("N")))
            countRightDec++;
        PHT[pValue][BHT[mValue].convertiInDecimale()].aggiornaStatoFSM(realDirTaken);
        nFSM = Integer.toString(PHT[pValue][BHT[mValue].convertiInDecimale()].stampaStatoAttuale());
        log3 += " New State FSM: " + nFSM + "\n ";
        System.out.println(log3);
        BHT[mValue].leftShift(realDirTaken);
    }
    static void aggiornaBTB(int bia, int bta){
        if((!BTB.containsKey(bia))&&(bta!=(bia+(a/8))))//non ho ancora messo BIA nel BTB e BTA è diverso dall'istruzione successiva a BIA
            BTB.put(bia, bta);
        log4 = " BTB" + "\n\n ";
        for(Integer key : BTB.keySet()){
            log4 += " " + key + " " + BTB.get(key) + "\n ";
        }
        System.out.println(log4);
    }
    static void inizializza(){
        BIA = new ArrayList<>();
        BTB = new HashMap<>();
        realBTA = new ArrayList<>();
        elaboraConfigFile();
        elaboraBranchFile();
        if(verificaCorrettezzaDatiInseriti()==false)
            return;
        int sizeBHT = (int)Math.pow(2, m);
        BHT = new BHSR[sizeBHT];
        for(int i=0;i<sizeBHT;i++){
            BHT[i] = new BHSR(k);
        }
        int setPHT = (int)Math.pow(2, p);
        int sizePHT = (int)Math.pow(2,k);
        PHT = new FSM[setPHT][sizePHT];
        for(int i=0;i<setPHT;i++){
            for(int j2=0;j2<sizePHT;j2++){
                PHT[i][j2] = new FSM(j);
            }
        }
    }
    static boolean verificaCorrettezzaDatiInseriti(){
        if(p<0||m<0||k<0||j<0){
            log = "ERROR: p, m, k, j CAN'T ASSUME NEGATIVE VALUES";
            System.err.println(log);
            return false;
        }
        if(a!=32&&a!=64){
            log = "ERROR: THE LENGTH FOR ADDRESS INSERTED IS FORBIDDEN (only accepted 32 or 64)";
            System.err.println(log);
            return false;
        }
        if(p>m){
            log = "ERROR: p CAN'T BE GREATER THAN m";
            System.err.println(log);
            return false;
        }
        for(int i=0;i<BIA.size();i++){//verifico se per qualche BIA ho più di due possibili target (INAMMISSIBILE)
            if(realBTA.get(i)!=(BIA.get(i)+(a/8))){
                for(int u=i;u<realBTA.size();u++){
                    if((Objects.equals(BIA.get(i), BIA.get(u)))&&(!realBTA.get(u).equals(BIA.get(u)+(a/8)))
                            &&(!Objects.equals(realBTA.get(i), realBTA.get(u)))){
                        log = "ERROR: EACH BIA CAN'T HAVE MORE THAN TWO TARGETS";
                        System.err.println(log);
                        return false;
                    }
                }
            }   
        }
        return true;
    }
    static void elaboraBranchFile(){
         List<String> l = leggiFile("branch");
         String bia ="", bta = "";
         for(String s : l){
             if(s.matches("^[0-9].*$")){//stringa che inizia con un numero--> è il BIA seguito dal BTA
                 bia = s.substring(0, s.indexOf(" "));
                 BIA.add(Integer.parseInt(bia));
                 bta = s.substring(s.indexOf(" ")+1);
                 realBTA.add(Integer.parseInt(bta));
             }
         }
    }
    static void elaboraConfigFile(){
         List<String> l = leggiFile("config");
         for(String s : l){
             if(s.startsWith("a")){
                 s = s.replace("a", "").replace(" ", "");
                 a = Integer.parseInt(s);
                 log += " PARAMETERS \n\n bits for address: " + s + "\n";
                 System.out.println();
             }
             if(s.startsWith("p")){
                 s = s.replace("p", "").replace(" ", "");
                 p = Integer.parseInt(s);
                 log += " bits to select PHT: " + s + "\n";
                 System.out.println();
             }
             if(s.startsWith("m")){
                 s = s.replace("m", "").replace(" ", "");
                 m = Integer.parseInt(s);
                 log += " bits to select BHSR: " + s + "\n";
                 System.out.println();
             }
             if(s.startsWith("k")){
                 s = s.replace("k", "").replace(" ", "");
                 k = Integer.parseInt(s);
                 log += " bits for each BHSR: " + s + "\n";
                 System.out.println();
             }
             if(s.startsWith("j")){
                 s = s.replace("j", "").replace(" ", "");
                 j = Integer.parseInt(s);
                 log += " bits for FSM: " + s + "\n";
                 System.out.println(log);
             }
         }
        
    }
    static List<String> leggiFile(String nomeFile){
        List<String> lines = Collections.emptyList(); 
        try
        { 
            lines = Files.readAllLines(Paths.get(nomeFile + ".txt"), StandardCharsets.UTF_8); 
        } 
        catch (IOException e) 
        { 
            e.printStackTrace(); 
        } 
        return lines;
    }
    
}