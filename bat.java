

import java.util.Random;

public class bat {

	//****Public Variables****//
	int size;
	int distanse;
	double pulse, loudness;
	double fitness;
	int[] A = new int[100] ;
		
	bat()
	{
		
	}
	
	//**copy constructor**//
	
	bat(bat x)
	{
		for(int i=0;i<this.A.length;i++)
			this.A[i]=x.A[i];
		this.size=x.size;
		this.distanse=x.distanse;
		this.pulse=x.pulse;
		this.loudness=x.loudness;
		this.fitness=x.fitness;
	}
	
	//** Default constructor**//
	bat(int n, int starting, int[] data)
	{   
	    Random rnd = new Random();
	    pulse=0.001 + 0.1*rnd.nextDouble();
		size=n;
		for(int i=0;i<size;i++)
		{
			A[i]=data[i];
		}
		for(int i=0;i<size;i++)
		{
			if(A[i]==starting)
			{
				int tmp;
				tmp=A[0];
				A[0]=A[i];
				A[i]=tmp;
				break;
			}
		}
	}
}




