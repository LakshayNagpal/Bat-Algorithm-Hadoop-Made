import java.io.IOException;
import java.util.*;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


public class WordCountReducer
  extends Reducer<Text , DoubleWritable, Text ,DoubleWritable> {
    
	//THE GLOBAL VARIABLES//	

	public static int total_iter=30;
	public static int N=5;
	public static int initial_city=3;
	public static int[][] graph = new int[35][35];
	public static double[][] cost = new double[35][35];
	public static int pop_size=10;
	public static bat[] member = new bat[pop_size];
	public static int Fmin=2,Fmax=2;

	//Function--> Print a Bat //	

	static void print_member(bat bt)
	{
		for (int i = 0; i < N; i++) 
		{
			System.out.printf("%d ",bt.A[i]);
		}
		System.out.println();		
		System.out.printf("Cost = %f , Distance = %d\n",bt.fitness, bt.distanse);		
	}

	//Function--> Print the Whole Population //	

	static void print_population()
	{
		for (int i = 0; i < member.length; i++) 
		{
			print_member(member[i]);
		}
		System.out.println("--------------------------------------------");		
	}

	//Function--> Calculate the fitness of one bat //	

	static void calculate_fitness_member(bat bt)
	{
	//	print_member(bt);
			Random rnd=new Random();
			int x=0;
			double value=0.0;
			for(int j=0;j<N-1;j++)
			{
				x=x+graph[bt.A[j]][bt.A[j+1]];
				value=value+100*rnd.nextDouble();
			}
			x=x+graph[bt.A[N-1]][bt.A[0]];
			value=value+cost[bt.A[N-1]][bt.A[0]];
			bt.fitness=value;
			bt.distanse=x;
	}

	//Function--> Calculate the fitness of Whole Population //	

	static void calculate_fitness_population()
	{
		for(int i=0;i<member.length;i++)
		{
			calculate_fitness_member(member[i]);
		}
	}

	//Function--> Return the index of best bat //	

	static int find_best()
	{
		int ans=0;
		double bst=-1;
		for(int i=0;i<member.length;i++)
		{
			if(member[i].fitness>bst)
			{
				bst=member[i].fitness;
				ans=i;
			}
		}
		return ans;
	}

	//Function--> Return the index of worst bat //	

	static int find_worst()
	{
		int ans=0;
		double bst=1000000;
		for(int i=0;i<member.length;i++)
		{
			if(member[i].fitness<bst)
			{
				bst=member[i].fitness;
				ans=i;
			}
		}
		return ans;
	}


	//Function--> To update population by velocity//	

	//Function--> Helper Function for UpdateByVelcity - To find the index of city in best bat//	

	static int index(int val,int[] A)
	{
		for(int i=0;i<A.length;i++)
		{
			if(A[i]==val)
				return i;
		}
		return -1;
	}

	static bat update_bat_velocity(int val,bat member,bat best)
	{
		bat temp=new bat(member);
		int chunk=N/val;
		int[] tmp=new int[chunk];
		int iter=1,cnt=0,curr_iter;
		while(iter<N)
		{
			curr_iter=iter;
			cnt=0;
			while(cnt<chunk && iter<N)
			{
				tmp[cnt++]=temp.A[iter]+(index(member.A[iter],best.A)*N);
				iter++;
			}
			Arrays.sort(tmp,0,cnt);
			iter=curr_iter;
			cnt=0;
			while(cnt<chunk && iter<N)
			{
				temp.A[iter]=tmp[cnt++]%N;
				if(temp.A[iter]==0)
					temp.A[iter]=N;
				iter++;
			}
		}
		calculate_fitness_member(temp);
		if(temp.fitness > member.fitness)
			member=temp;
		return member;
	}

	//Function--> To adjust i.e. to normalize an invalid bat//	
	
	static void adjustment(bat member)
	{
		int x=member.size,y=0;
		int[] B = new int[x+1];
		int[] ID = new int[x+1];
		for(int i=x-1;i>=0;i--)
		{
			if(B[member.A[i]]==0)
			{
				B[member.A[i]]=1;
			}
			else
			{
				ID[y++]=i;
			}			
		}
	    Random rnd = new Random();
	    for (int k = y - 1; k >= 0; k--)
	    {
	      int index = rnd.nextInt(k + 1);
	      int a = ID[index];
	      ID[index] = ID[k];
	      ID[k] = a;
	    }
	    y=0;
		for(int i=1;i<=x;i++)
		{
			if(B[i]==0)
			{
				member.A[ID[y++]]=i;
			}
		}
		calculate_fitness_member(member);
	}

	//Function--> Update Bat if fitness is better//	
	
	static bat update_bat_fitness(bat temp,bat member)
	{
		calculate_fitness_member(temp);
		if(temp.fitness > member.fitness)
			member=temp;
		return member;
	}

	//Function --> STEP 1 : Velocity impact on population
	
	static void update_population_by_velocity(bat best_bat)
	{
		Random rnd=new Random();
		int freq;
		for(int I=0;I<member.length;I++)
		{
			freq=Fmin+(rnd.nextInt(10000))%(Fmax-Fmin);
			if(freq<0)
				System.out.printf("Random Function Error\n");
			member[I] = update_bat_velocity(freq,member[I],best_bat);
			best_bat=update_bat_fitness(best_bat,member[I]);
		}												
	}

	//Function --> STEP 2 : Pulse Rate impact on population
	
	static void update_population_by_PulseRate(int iter)
	{
		Random rnd=new Random();
		int freq;
		for(int I=0;I<member.length;I++)
		{ 
			double curr_pul;
			curr_pul=member[I].pulse;
			curr_pul=curr_pul*(1-Math.exp(-100*iter));
			if(curr_pul < rnd.nextDouble())
			{
				freq=Fmin+(rnd.nextInt())%(Fmax-Fmin);
				while(freq>0)
				{
					bat dummy=new bat(member[I]);
					int x=rnd.nextInt(N-1)+1;
					int y=rnd.nextInt(N-1)+1;
					int tmpr;
					tmpr=dummy.A[x];
					dummy.A[x]=dummy.A[y];
					dummy.A[y]=tmpr;
					member[I] = update_bat_fitness(dummy,member[I]);
					freq--;
				}
			}
			else
			{
				freq=Fmin+(rnd.nextInt())%(Fmax-Fmin);
				while(freq>0)
				{
					bat dummy=new bat(member[I]);
					int x=rnd.nextInt(N-1)+1;
					int y=rnd.nextInt(N-1)+1;
					if(x>y)
					{
						int tmp;
						tmp=x;
						x=y;
						y=tmp;
					}
					int str=dummy.A[x];
					for(int k=x;k<y;k++)
					{
						dummy.A[k]=dummy.A[k+1];
					}
					dummy.A[y]=str;
					member[I] = update_bat_fitness(dummy,member[I]);
					freq--;
				}
			}
		}
	}

	//Function --> STEP 3 : Loudness impact on population
	
	static void update_population_by_Loudness()
	{
		int best,worst;
		Random rnd=new Random();
		for(int I=0;I<member.length;I++)
		{
			best = find_best();
			worst = find_worst();
			bat best_bat=member[best];
			bat worst_bat=member[worst];
			double curr_ld;
			curr_ld=(member[I].fitness-worst_bat.fitness)/(best_bat.fitness-worst_bat.fitness);
			if(curr_ld < rnd.nextDouble())
			{
				int lenth=rnd.nextInt(N/2);
				int x=rnd.nextInt(N/2);
				bat dummy = new bat(member[I]);
				for(int j=x;j<x+lenth;j++)
				{
					dummy.A[j]=best_bat.A[j];
				}
				adjustment(dummy);
				member[I] = update_bat_fitness(dummy,member[I]);					
			}
			else
			{
				int lenth=rnd.nextInt(N/2);
				int x=rnd.nextInt(N/2);
				bat dummy = new bat(member[I]);
				int ii=x,jj=x+lenth;
				while(ii<x+lenth)
				{
					dummy.A[ii]=best_bat.A[jj];
					ii++;jj--;
				}
				adjustment(dummy);
				member[I] = update_bat_fitness(dummy,member[I]);					
			}
		}
	}

	//Function(Virtual Population) --> STEP 1 : Swap//
	
	static void update_best_virtual_swap(bat best_bat, int best)
	{
		for(int J=0;J<pop_size;J++)
		{
			Random rnd=new Random();
			bat dummy=new bat(best_bat);
			int x=rnd.nextInt(N-1)+1;
			int y=rnd.nextInt(N-1)+1;
			int tmp;
			tmp=dummy.A[x];
			dummy.A[x]=dummy.A[y];
			dummy.A[y]=tmp;
			member[best] = update_bat_fitness(dummy,member[best]);
		}
	}

	//Function(Virtual Population) --> STEP 2 : Insert//
	
	static void update_best_virtual_insert(bat best_bat, int best)
	{
		Random rnd=new Random();
		for(int J=0;J<pop_size;J++)
		{
			bat dummy=new bat(member[best]);
			int x=rnd.nextInt(N-1)+1;
			int y=rnd.nextInt(N-1)+1;
			if(x>y)
			{
				int tmp;
				tmp=x;
				x=y;
				y=tmp;
			}
			int str=dummy.A[x];
			for(int k=x;k<y;k++)
			{
				dummy.A[k]=dummy.A[k+1];
			}
			dummy.A[y]=str;
			member[best] = update_bat_fitness(dummy,member[best]);
		}
	}

	//Function(Virtual Population) --> STEP 3 : Inverse//
	
	static void update_best_virtual_inverse(bat best_bat, int best)
	{
		for(int J=0;J<pop_size;J++)
		{
			Random rnd=new Random();
			bat dummy=new bat(member[best]);
			int x=rnd.nextInt(N-1)+1;
			int y=rnd.nextInt(N-1)+1;
			if(x>y)
			{
				int tmp;
				tmp=x;
				x=y;
				y=tmp;
			}
			int ii=x,jj=y;
			while(ii<jj)
			{
				int tmp;
				tmp=dummy.A[ii];
				dummy.A[ii]=dummy.A[jj];
				dummy.A[jj]=tmp;
				ii++;jj--;
			}
			member[best] = update_bat_fitness(dummy,member[best]);
		}
	}

	
	
	@Override
   public void reduce(Text key, Iterable<DoubleWritable> values,
               Context context)
      throws IOException , InterruptedException {
    
           bat contextBat=new bat();
           Text word =new Text();
    for(DoubleWritable value : values) {
  
    	String batCollect=key.toString();
    	
    	 int countBatN=0;
    	StringTokenizer b1itr = new StringTokenizer(batCollect);
		int[][] data = new int[pop_size][N];
		int ii=0,jj=0;
		while(b1itr.hasMoreTokens())
		{
			String h1=b1itr.nextToken();
			  data[ii][jj++]=Integer.parseInt(h1);
	                 if(jj==N)
	                 {
	                	 ii++;
	                	  jj=0;
	                	  if(ii==pop_size) break;
	                 }
		}
    	
		Random rnd=new Random();
//		System.out.printf("The Distance Matrix : \n");
		for(int i=1;i<=N;i++)			//Print Initial Table
		{
			for(int j=1;j<=N;j++)
			{
				graph[i][j]=87*(i*i+j*j+Math.abs(i-j)+i*j); //any random function
				cost[i][j]=100*rnd.nextDouble();
//				System.out.printf("%6d",graph[i][j]);
			}
//			System.out.printf("\n");			
		}
		
				Fmax=N-1;
				bat best_bat=new bat(N,initial_city,data[0]);
				for(int i=0;i<pop_size;i++)
				{
					member[i]=new bat(N,initial_city,data[i]);
				}
				
				calculate_fitness_population();
				int best = find_best();
				best_bat=member[best];
		
				//System.out.printf("The Initial Population : \n");
				//print_population();
		
	//****The Algorithm Starts Here****//
				
				for(int iter=1;iter<=total_iter;iter++)
				{
					//print_population();
		
	//****UPDATION OF PATHS BY Velocity****//
								
					update_population_by_velocity(best_bat);
					//print_population();
		
	//****UPDATION OF PATHS BY PULSE RATE****//
					
					update_population_by_PulseRate(iter);
					//print_population();
		
	//****UPDATION OF PATHS LOUDNESS****//
					
					update_population_by_Loudness();
					//print_population();
					
	//****FIND GLOBAL BEST****//
					
					calculate_fitness_population();
					best = find_best();
					best_bat=member[best];
					//System.out.printf("ITERATION NUMBER :%d \n",iter);
					//System.out.printf("The fittest BAT in real Population :\n");
					//print_member(best_bat);
					//System.out.printf("-----------------------------\n");
		
	//****VIRTUAL POPULATION****//
					
					//Step 1 : IMPROVE BEST BY SWAPPING//
					update_best_virtual_swap(best_bat,best);
					best_bat=member[best];
					
					//Step 2 : IMPROVE BEST BY INSERTION//
					update_best_virtual_insert(best_bat,best);
					best_bat=member[best];
		
					//Step 3 : IMPROVE BEST BY INVERSION//
					update_best_virtual_inverse(best_bat,best);
					best_bat=member[best];
		
					//System.out.printf("THE BEST SOLUTION APPLICATION OF BAT CHARACTERISTICS : \n",iter);			
					//print_member(best_bat);
					//System.out.printf("-----------------------------\n");
				}    	
    	
				
				contextBat=best_bat;
			    
    	/*	    
	    int dummy=0;
	    
	    while (b1itr.hasMoreTokens())
	    {	dummy=dummy+1;
	        String dummy1="";
	        for(int itrN=0;itrN<N;itrN++)
	    	{ dummy1=dummy1+b1itr.nextToken()+" ";
	    	  
	    	}
	       Text word=new Text();
	       word.set(dummy1);
	       IntWritable dummyS=new IntWritable(dummy);
     context.write(word,dummyS);
   }*/
  
    
 }
    
	String batAnswer="";
    for(int ibat=0;ibat<N;ibat++)
    {
    	batAnswer=batAnswer+contextBat.A[ibat]+" ";
    	
    }
    word.set(batAnswer);
    DoubleWritable dumd=new DoubleWritable(contextBat.fitness);
    context.write(word, dumd);
}
}