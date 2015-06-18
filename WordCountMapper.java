import java.io.IOException;

import java.util.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.FloatWritable;
public class WordCountMapper
  extends Mapper<LongWritable,Text,Text,DoubleWritable> {

  //private final static IntWritable one = new IntWritable(1);
  //private Text word = new Text();
  
	//THE GLOBAL VARIABLES//	

	public static int total_iter=30;
	public static int N=5;
	public static int initial_city=3;
	public static int[][] graph = new int[35][35];
	public static double[][] cost = new double[35][35];
	public static int pop_size=10;
	public static bat[] member = new bat[pop_size];
	public static int Fmin=2,Fmax=2;
	public Text word= new Text();
	public DoubleWritable fwritable=new DoubleWritable();

	//************************************************************//
	//************************************************************//
	//*******************THE MAIN FUNCTION************************//
	//************************************************************//
	//************************************************************//
  
  @Override
 public void map(LongWritable key, Text value , Context context)
      throws IOException , InterruptedException {
	  		int ii=0,jj=0,dummy=0;
	  		int[][] data = new int[50][N];
           String line = value.toString();
           StringTokenizer itr = new StringTokenizer(line, "\n");
   		while (itr.hasMoreTokens() && ii<50) {
		    String b1= itr.nextToken();
		    StringTokenizer b1itr = new StringTokenizer(b1);
		    jj=0;
		    dummy=0;
		    ii++;
		    while (b1itr.hasMoreTokens())
		    {	dummy=dummy+1;
		        String dummy1="";
		        for(int itrN=0;itrN<N;itrN++)
		    	{ dummy1=dummy1+b1itr.nextToken()+" ";
		    	  
		    	}
		        
		        fwritable.set(ii);
				 //word = new Text();
				 word.set(b1);
				 context.write(word,fwritable);
		    }
		    
				   	  
		}
   		
    } 
  }