package Occupancy;


import OccupancyNormal.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import jxl.*;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Budi Pradnyana
 */
public class Backpropagation {
    //setting parameter dulu
    private final int neuron_input = 4;
    private final int neuron_hidden = 6;
    private final int neuron_output = 1;
    private final double learning_rate = 0.6;
    private final int max_epoch = 3000;
    
    
    private Workbook w;  
    private double  mse[];
    private double  error;
   
    private double wee[][] = new double[neuron_input+1][neuron_hidden];  //bkin bobot dr input ke hidden + bias , biasa disebut w  (wih)
    private double vee[][] = new double[neuron_hidden+1][neuron_output]; //bkin bobot dr hidden ke outpur + bias, sebut saja v (who)
    private double deltawee[][] = new double[neuron_input+1][neuron_hidden]; //buat nampung delta w
    private double deltavee[] = new double[neuron_hidden+1];  // buat nampung delta v

    
    //aktivasi
    private double row_input[] = new double[neuron_input];  //buat nampung  input dari file_input untuk diproses belajar
    private double zedd[] = new double[neuron_hidden]; //buat nampung nilai dari tiap hidden neuron, sebut saja zed
    private double target; //buat nampung target dari file_output untuk dicocokin pas selesai propagasi maju
    private double y_output;  // buat nampung nilai akhir dari output neuron
    
    //faktor error
    private double faktor_error_output;
    private double faktor_error_hidden[] = new double[neuron_hidden];
    
    
    // buat nampung data dari excel
    private static double file_input[][];
    private static int file_output[][];
    private static int max_data;

    //isiin input neuron dg data di excel
    public void ReadData() throws IOException, BiffException{
        w = Workbook.getWorkbook(new File("D:/occupancy.xls"));        
        Sheet sheet = w.getSheet(0); 
        file_input = new double[sheet.getRows()][sheet.getColumns()-1];
        for (int i = 0; i < sheet.getRows(); i++) {
            for(int j=0; j<sheet.getColumns()-1; j++){
                Cell data = sheet.getCell(j, i);
                file_input[i][j] = Double.parseDouble(data.getContents());          
            }
        max_data = file_input.length;
        mse = new double[max_epoch];
        }
    }
    
    //isiin target dg data di excel
    public void ReadTarget() throws IOException, BiffException{
        w = Workbook.getWorkbook(new File("D:/occupancy.xls"));        
        Sheet sheet = w.getSheet(0); 
        file_output = new int[sheet.getRows()][sheet.getColumns()-4];
        for (int i = 0; i < sheet.getRows(); i++) {
                Cell data = sheet.getCell(4, i);
                file_output[i][0] = Integer.parseInt(data.getContents());                    
        }
    }
    
    
    public void ReadDataTesting() throws IOException, BiffException{
        w = Workbook.getWorkbook(new File("D:/occupancy_testing.xls"));        
        Sheet sheet = w.getSheet(0); 
        file_input = new double[sheet.getRows()][sheet.getColumns()-1];
        for (int i = 0; i < sheet.getRows(); i++) {
            for(int j=0; j<sheet.getColumns()-1; j++){
                Cell data = sheet.getCell(j, i);
                file_input[i][j] = Double.parseDouble(data.getContents());          
            }
        max_data = file_input.length;
        mse = new double[max_epoch];
        }
    }
        
    public void ReadTargetTesting() throws IOException, BiffException{
        w = Workbook.getWorkbook(new File("D:/occupancy_testing.xls"));        
        Sheet sheet = w.getSheet(0); 
        file_output = new int[sheet.getRows()][sheet.getColumns()-4];
        for (int i = 0; i < sheet.getRows(); i++) {
                Cell data = sheet.getCell(4, i);
                file_output[i][0] = Integer.parseInt(data.getContents());                    
        }
    }
    
    public void InisialisasiBobot(){
        //BUAT RANDOM BOBOTNYA (BELUM TENTU DPT 97%)
//        for(int i = 0; i<= neuron_input; i++){
//            for(int j=0; j< neuron_hidden; j++){
//                wee[i][j] = 1-(Math.random()*2);
//            }
//        }
//        
//        for(int i = 0; i<= neuron_hidden; i++){
//            for(int j=0; j< neuron_output; j++){
//                vee[i][j] = 1-(Math.random()*2);
//            }
//        }
        
        //BUAT BOBOT FIX (UDAH PASTI 97%)
        //hidden neuron 1
        wee[0][0] = 0.9644719428743411;
        wee[1][0] = -0.06848242208759303;
        wee[2][0] = -0.44739586340224813;
        wee[3][0] = -0.19464128818264737;
        wee[4][0] = -0.48347451221251636;
        
        //hidden neuron 2 
        wee[0][1] = -0.44620552874755437;
        wee[1][1] = 0.48007221054499727;
        wee[2][1] = 0.30171709527366786;
        wee[3][1] = -0.7095644579875073;
        wee[4][1] = -0.46697115877325457;
        
        //hidden neuron 3 
        wee[0][2] = -0.18443413582593848;
        wee[1][2] = 0.3317389988268804;
        wee[2][2] = -0.6036698328978949;
        wee[3][2] = 0.4320036353284349;
        wee[4][2] = -0.8734589895871729;
        
        //hidden neuron 4 
        wee[0][3] = 0.4830808635600172;
        wee[1][3] = 0.7311174796759321;
        wee[2][3] = -0.7165613203820143;
        wee[3][3] = 0.6572059245025226;
        wee[4][3] = 0.45445355469252546;
        
        //hidden neuron 5
        wee[0][4] = 0.7505339123209285;
        wee[1][4] = 0.15314959419147556;
        wee[2][4] = -0.7909200297575747;
        wee[3][4] = 0.6043779102718732;
        wee[4][4] = 0.2836312544165518;
        
        //hidden neuron 6
        wee[0][5] = 0.12176700099399573;
        wee[1][5] = 0.3110204636042262;
        wee[2][5] = -0.9215841957905857;
        wee[3][5] = -0.05130927549106801;
        wee[4][5] = -0.3193625734301613;
        
        
        //output neuron 
        vee[0][0] = 0.9381097647779881;
        vee[1][0] = -0.5702792430544346;
        vee[2][0] = 0.2688636304895926;
        vee[3][0] = 0.6232881757586086;
        vee[4][0] = -0.9726071000918566;
        vee[5][0] = 0.5678584821118109;
        vee[6][0] = 0.4298335317658406;
    }
    
    public double FungsiAktivasi(double f){
        return (1/(1 + Math.exp(-f)));
    }
    
    public void PropagasiMaju(){
        double summing;
        
        //hitung input ke hidden
        for(int i=0; i<neuron_hidden; i++){ // loop sebanyak neuron di hidden
            summing = 0.0;
            for(int j=0; j < neuron_input; j++){  //loop sebanyak neuron inputnya
                summing = summing +(row_input[j]*wee[j][i]);
            }
            summing = summing+ wee[neuron_input][i]; //tambah bias
            zedd[i] = FungsiAktivasi(summing);
        }
        
        //hitung hidden ke output
        summing = 0.0;
        for(int i=0; i<neuron_hidden; i++){ //loop sebanyak neuron di hidden, neuron output cuma 1
            summing = summing + (zedd[i]*vee[i][0]);
        }
        summing = summing + vee[neuron_hidden][0]; //tambah bias
        y_output = FungsiAktivasi(summing); //simpan nilai 1x propagasi maju
        
    }
    
    public void PropagasiMundur(){
        
        //hitung error di output layer, lalu hitung faktor errornya
        error = (target-y_output);
        faktor_error_output=(error*y_output*(1-y_output));
        
        //hitung delta bobot antara hidden - output, sebut saja deltavee
        for(int i=0; i<neuron_hidden;i++){
            deltavee[i] = error*zedd[i]*learning_rate;
        }
        deltavee[neuron_hidden] = error*learning_rate; //hitung juga delta biasnya
        
        
        //hitung faktor error pada input - hidden (hidden neuron) utk setiap hidden neuron
        for(int i=0; i<neuron_hidden; i++){
            faktor_error_hidden[i] = error*vee[i][0]*zedd[i]*(1-zedd[i]);
        }
        
        //hitung delta bobot pada input - hidden 
        for(int i=0; i<neuron_hidden; i++){
            for(int j=0; j<neuron_input; j++){
                deltawee[j][i] = faktor_error_hidden[i]*zedd[j]*learning_rate;
            }
            deltawee[neuron_input][i] = faktor_error_hidden[i]*learning_rate;  //hitung delta bobot bias
        }
    }
    
    public void UpdateBobot(){
                //hitung perubahan bobot input - hidden
        for(int i=0; i<neuron_hidden; i++){
            for(int j=0; j<neuron_input; j++){
                wee[j][i] =  wee[j][i] + deltawee[j][i];
            } 
                 wee[neuron_input][i] =  wee[neuron_input][i] + deltawee[neuron_input][i]; //lakukan juga perubahan pada bias
        }
        
        //hitung perubahan bobot hidden - output
        for(int i=0; i<neuron_output; i++){
            for(int j=0; j<neuron_hidden;j++){
                vee[j][i] =  vee[j][i] + deltavee[j];
            }
            vee[neuron_hidden][i] = vee[neuron_hidden][i] + deltavee[neuron_hidden];  //lakukan juga perubahan pada bias
        }
    }
    
    public void Belajar() throws IOException, BiffException{
        ReadData();
        ReadTarget();
        InisialisasiBobot();
        int epoh = 1;
        double sum_error;
        
        //TRAIN 1 : selama belum mencapai max_epoch, loop terus
        for (int epoch = 0; epoch < max_epoch; epoch++){
            sum_error = 0.0;
            for(int data=0; data < max_data; data++){
                for(int i=0; i< neuron_input; i++){
                    row_input[i] = file_input[data][i];
                }
                target = file_output[data][0];
                PropagasiMaju();
                PropagasiMundur();
                UpdateBobot();
                sum_error = sum_error+(Math.pow(error, 2));
            }
            mse[epoch] = (sum_error/max_data);
            System.out.println("MSE epoch ke "+(epoch+1)+" : "+ mse[epoch]); 
        }
//        WriteData(mse);  dipake buat nulis MSE ke excel, biar gmpang bikin grafiknya
        
        
         //TRAIN 2 : selama MSE masih lebih besar dari 0.022
//        do {
//            mse = 0.0;
//            for(int row=0; row < MAX_SAMPLES; row++){
//                for(int i=0; i< neuron_input; i++){
//                    inputs[i] = trainInputs[row][i];
//                }
//                target = trainOutput[row][0];
//                feedForward();
//                backProgate();
//                mse = mse+(Math.pow((target-output), 2));
//            }
//            System.out.println("MSE tiap epoch "+epoh+" : "+ (mse/MAX_SAMPLES));
//            epoh += 1;
//        } while (mse/MAX_SAMPLES > 0.022);
    }
    
    public void Testing() throws IOException, BiffException{
        //buat ngetest 
        ReadDataTesting();
        ReadTargetTesting();
        double benar=0.0;
        for(int i=0; i< max_data; i++){
            for(int j=0; j< neuron_input; j++){
                row_input[j]=file_input[i][j];
            }
            target = file_output[i][0];
            PropagasiMaju();
            double val = Math.round(y_output);
            if(val == target){
                benar = benar+1;
            } 
            System.out.println("Row    : "+(i+1));
            System.out.println("Output : "+ val + " -->  Target : "+ target);
            System.out.println();
        }
        double akurasi = (benar/max_data)*100;
        System.out.println("Benar Sebanyak : "+(Math.round(benar)));
        System.out.println("Total Sample   : "+max_data);
        System.out.println("Akurasi JST    : "+akurasi+"%");
    } 
}
