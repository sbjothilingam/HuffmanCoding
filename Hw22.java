import java.io.File;
import java.io.PrintWriter;
import java.util.*;

/**
 *
 * @author Suresh Babu Jothilingam
 * Huffman coding and decoding
 */
/**
 * 
 * HuffMan Tree class to build Huffman tree
 */
class HuffManTree{
    HuffManTree left;
    HuffManTree right;
    int freq;
    char character;
    //int edge;
    HuffManTree(int freq){
        left=null;
        right=null;
        this.freq=freq;
    }
    public void setChar(char c){
        this.character=c;
    }
    public void setChild(HuffManTree left,HuffManTree right){
        this.left=left;
        this.right=right;
    }
    public char getChar(){
        return this.character;
    }
    public int getRoot(){
        return this.freq;
    }
    public boolean isLeftNull(){
        if(this.left==null)
            return true;
        else
            return false;
    }
    public boolean isRightNull(){
        if(this.right==null)
            return true;
        else
            return false;
    }
}
/**
 * 
 * Hw22 class performs HuffMan coding and decoding
 */
public class Hw22 {
    //to store the word count
    static HashMap<Character,Integer> wordCount=new HashMap<Character,Integer>();
    //to store the encoding of each character
    static HashMap<Character,String> encodedTable=new HashMap<Character,String>();
    //sorted Huffman encoded characters to build tree
    static ArrayList<HuffManTree> sorted=new ArrayList<HuffManTree>();
    static String fileName;static String fileNameEncoded;static String fileNameDecoded;
    
    //quick sort function to sort the encoded table created in Increasing order
    public static ArrayList<HuffManTree> quickSort(ArrayList<HuffManTree> table){
        ArrayList<HuffManTree> finalSortedTable=new ArrayList<HuffManTree>();
        if(table.size()>1){
            int pivot=table.size()/2;
            ArrayList<HuffManTree> left=new ArrayList<HuffManTree>();
            ArrayList<HuffManTree> right=new ArrayList<HuffManTree>();
            ArrayList<HuffManTree> middle=new ArrayList<HuffManTree>();
            for(int i=0;i<table.size();i++){
                if(table.get(i).getRoot()<table.get(pivot).getRoot()){
                    left.add(table.get(i));
                }
                else if(table.get(i).getRoot()>table.get(pivot).getRoot()){
                    right.add(table.get(i));
                }
                else{
                    middle.add(table.get(i));
                }
            }
            left=quickSort(left);
            for(int i=0;i<middle.size();i++){
                left.add(middle.get(i));
            }
            right=quickSort(right);
            for(int i=0;i<left.size();i++){
                finalSortedTable.add(left.get(i));
            }
            for(int i=0;i<right.size();i++){
                finalSortedTable.add(right.get(i));
            }
        }
        else{
            finalSortedTable.addAll(table);
        }
        return finalSortedTable;
    }
    
    //build HuffManTree function to build the huff man tree from the sorted encoded list
    public static void buildHuffManTree(){
        String str1="";String str0="";
        while(sorted.size()!=1){
            HuffManTree n1=sorted.get(0);
            HuffManTree n2=sorted.get(1);
            HuffManTree combined=new HuffManTree(n1.getRoot()+n2.getRoot());
            if(n1.getRoot()<=n2.getRoot()){
                combined.setChild(n1, n2);
            }else{
                combined.setChild(n2,n1);
            }
            sorted.remove(0);
            sorted.remove(0);
            sorted.add(combined);
            sorted=quickSort(sorted);
        }
    }
    
    //this function is to create encoded table for each character from the huffman tree
    public static void buildEncodedTable(HuffManTree tt, String str){
        if(encodedTable.size()!=wordCount.size()){
            if(tt.isLeftNull()&&tt.isRightNull()){
                encodedTable.put(tt.getChar(), str);
            }else if(tt.isLeftNull()&&!tt.isRightNull()){
                    encodedTable.put(tt.getChar(),str);
                    buildEncodedTable(tt.right, str+"1");
            }else if(tt.isRightNull()&&!tt.isLeftNull()){
                    encodedTable.put(tt.getChar(), str);
                    buildEncodedTable(tt.left, str+"0");
            }else if(!tt.isLeftNull() && !tt.isRightNull()){
                buildEncodedTable(tt.left, str+"0");
                buildEncodedTable(tt.right, str+"1");
            }
        }
    }
    
    //Encode function perform encoding of the file
    public static void encode(File f){
        //builds huffman tree and encoded table for the read file
        buildHuffManTree();
        buildEncodedTable(sorted.get(0),"");
        try{
            //write the encoded content to the file
            Scanner enc=new Scanner(f);
            PrintWriter write=new PrintWriter(fileNameEncoded);
            while(enc.hasNextLine()){
                String line=enc.nextLine();
                for(int i=0;i<line.length();i++){
                    write.print(encodedTable.get(line.charAt(i)));
                    System.out.print(encodedTable.get(line.charAt(i)));
                }
                write.println();
                System.out.println();
            }
            write.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    //This function reads the encoded file and decods the file
    public static void decode(File f){
        try{
            //read the encoded file and print the content to the decoded file
            Scanner scan=new Scanner(f);
            PrintWriter print=new PrintWriter(fileNameDecoded);
            while(scan.hasNextLine()){
                String line=scan.nextLine();
                HuffManTree mainTree=sorted.get(0);
                String outputLine="";
                for(int i=0;i<line.length();i++){
                    if(line.charAt(i)=='0'){
                        mainTree=mainTree.left;
                        if(mainTree.isLeftNull() || mainTree.isRightNull()){
                            outputLine+=Character.toString(mainTree.getChar());
                            mainTree=sorted.get(0);
                        }
                    }else if(line.charAt(i)=='1'){
                        mainTree=mainTree.right;
                        if(mainTree.isLeftNull() || mainTree.isRightNull()){
                            outputLine+=Character.toString(mainTree.getChar());
                            mainTree=sorted.get(0);
                        }
                    }
                }
                print.write(outputLine);
                print.write("\r\n");
                System.out.println(outputLine);
            }
            print.flush();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    //Copy function to copy the contents from the wordCount HashMap to Huffman tree object for encoding and decoding
    public static void copy(){
        Set keys=wordCount.keySet();
        Integer count;Character word;
        Iterator iterate=keys.iterator();
        while(iterate.hasNext()){
            word=(Character)iterate.next();
            count=(Integer)wordCount.get(word);
            HuffManTree h=new HuffManTree(count);
            h.setChar(word);
            sorted.add(h);
        }
        sorted=quickSort(sorted);
    }
    
    //prints the wordcount freqencty table
    public static void printTable(){
        for(int i=0;i<sorted.size();i++){
            System.out.println(sorted.get(i).getChar()+" - "+sorted.get(i).getRoot());
        }
    }
    
    //prints the table which has encoding for each character
    public static void printCodeTable(){
        Set keys=encodedTable.keySet();
        Character word;String en;
        Iterator iterate=keys.iterator();
        while(iterate.hasNext()){
            word=(Character)iterate.next();
            en=(String)encodedTable.get(word);
            System.out.println(word+" - "+en);
        }
    }
    
    //to compute the compression ratio
    public static void getCompressionRation(){
        Set keys=encodedTable.keySet();
        Character word;String en;
        Iterator iterate=keys.iterator();
        int total=0;
        while(iterate.hasNext()){
            word=(Character)iterate.next();
            en=(String)encodedTable.get(word);
            total+=en.length()*wordCount.get(word);
        }
        total=total/8;
        System.out.println("Compression ratio for this file is "+total+" bytes");
    }
    
    //Main function 
    public static void main(String args[]){
        try{
            while(true){
                Scanner input=new Scanner(System.in);
                System.out.println("Enter the file name to be encoded and decoded with path");
                fileName=input.nextLine();
                if(fileName.contains(File.separator)){
                    fileNameEncoded=fileName.substring(0,fileName.lastIndexOf(File.separator))+fileName.substring(fileName.lastIndexOf(File.separator),fileName.lastIndexOf("."))+"Encoded"+fileName.substring(fileName.lastIndexOf("."),(fileName.length()));
                    fileNameDecoded=fileName.substring(0,fileName.lastIndexOf(File.separator))+fileName.substring(fileName.lastIndexOf(File.separator),fileName.lastIndexOf("."))+"Decoded"+fileName.substring(fileName.lastIndexOf("."),(fileName.length()));
                }else{
                    fileNameEncoded=fileName.substring(0,fileName.lastIndexOf("."))+"Encoded"+fileName.substring(fileName.lastIndexOf("."),(fileName.length()));
                    fileNameDecoded=fileName.substring(0,fileName.lastIndexOf("."))+"Decoded"+fileName.substring(fileName.lastIndexOf("."),(fileName.length()));
                }
                Scanner scan=new Scanner(new File(fileName));
                while(scan.hasNextLine()){
                    String line=scan.nextLine();
                    for(int i=0;i<line.length();i++){
                        if(wordCount.containsKey(line.charAt(i))){
                            int incr=wordCount.get(line.charAt(i));
                            incr+=1;
                            wordCount.put(line.charAt(i), incr);
                        }
                        else{
                            wordCount.put(line.charAt(i), 1);
                        }
                    }
                }
                copy();
                System.out.println("Frequency Table");
                printTable();
                int choice=0;
                while(choice!=3){
                    System.out.println("1. Encode");
                    System.out.println("2. Decode");
                    System.out.println("3. Enter new file for encoding");
                    choice=input.nextInt();
                    if(choice==1){
                        long start=System.currentTimeMillis();
                        encode(new File(fileName));
                        long end=System.currentTimeMillis();
                        System.out.println("Code table");
                        printCodeTable();
                        System.out.println("Time take to encode the file and write it to a file "+(end-start));
                        System.out.println("Encoded file is in Path "+fileNameEncoded);
                        getCompressionRation();
                    }else if(choice==2){
                        long start=System.currentTimeMillis();
                        decode(new File(fileNameEncoded));
                        long end=System.currentTimeMillis();
                        System.out.println("Time taken to decode the file and write it to a file "+(end-start));
                        System.out.println("Decoded file is in Path "+fileNameDecoded);
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
