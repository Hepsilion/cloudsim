#include<stdio.h>
#include<mpi.h>
#include<unistd.h>
#include<stdlib.h>
#include<sys/time.h>
#include<time.h>

void func(int myid, long run_time, int sleep_time, long start_time, long exec_time){
	clock_t start, end;
	long i, num=exec_time*100;
	struct timeval cur_time;

	usleep(start_time*1000000);
	gettimeofday(&cur_time, NULL);	printf("process %d begins at time %ld.%ld\n", myid, cur_time.tv_sec, cur_time.tv_usec);
	for(i=0; i<num; i++){
		start=end=clock();
		while((end-start)<=run_time){
			end=clock();
		}
		usleep(sleep_time);
	}
	gettimeofday(&cur_time, NULL);	printf("process %d ends at time %ld.%ld\n", myid, cur_time.tv_sec, cur_time.tv_usec);
}

int main(int argc, char** argv) {
	int hid=atoi(argv[1]);

	int myid, numprocs, namelen;
	char processor_name[MPI_MAX_PROCESSOR_NAME];

	struct timeval proc_start_time, proc_end_time;
	gettimeofday(&proc_start_time, NULL);
	printf("Program now starts: %ld.%ld\n", proc_start_time.tv_sec, proc_start_time.tv_usec);

	MPI_Init(NULL, NULL);
	MPI_Comm_size(MPI_COMM_WORLD, &numprocs);
	MPI_Comm_rank(MPI_COMM_WORLD, &myid);
	MPI_Get_processor_name(processor_name, &namelen);
	printf("Process %d of %d is on %s\n", myid, numprocs, processor_name);

	if(hid==0){
		if(myid==0){
			func(myid, 11785, -1785, 882, 54512);	//cloudlet0
			usleep(53819000000);
		}else{
			func(myid, 10985, -985, 56625, 52588);	//cloudlet22
			usleep(0);
		}
	}else if(hid==1){
	}else if(hid==2){
		if(myid==0){
			func(myid, 10969, -969, 1725, 52188);	//cloudlet1
			usleep(58556000000);
		}else{
			func(myid, 12575, -2575, 58655, 53814);	//cloudlet23
			usleep(0);
		}
	}else if(hid==3){
	}else if(hid==4){
		if(myid==0){
			func(myid, 13294, -3294, 2749, 55790);	//cloudlet2
			usleep(19308000000);
		}else{
			func(myid, 12238, -2238, 24858, 52989);	//cloudlet15
			usleep(0);
		}
	}else if(hid==5){
	}else if(hid==6){
		if(myid==0){
			func(myid, 11491, -1491, 3605, 53512);	//cloudlet3
			usleep(54625000000);
		}else{
			func(myid, 10842, -842, 58886, 52856);	//cloudlet24
			usleep(0);
		}
	}else if(hid==7){
	}else if(hid==8){
		if(myid==0){
			func(myid, 12092, -2092, 4020, 56550);	//cloudlet4
			usleep(36969000000);
		}else{
			func(myid, 13483, -3483, 42788, 54751);	//cloudlet16
			usleep(0);
		}
	}else if(hid==9){
	}else if(hid==10){
		if(myid==0){
			func(myid, 13982, -3982, 5256, 51961);	//cloudlet5
			usleep(48988000000);
		}else{
			func(myid, 12693, -2693, 52683, 53522);	//cloudlet21
			usleep(0);
		}
	}else if(hid==11){
	}else if(hid==12){
		if(myid==0){
			func(myid, 11108, -1108, 5289, 57435);	//cloudlet6
			usleep(59440000000);
		}else{
			func(myid, 10860, -860, 68233, 53931);	//cloudlet26
			usleep(0);
		}
	}else if(hid==13){
	}else if(hid==14){
		if(myid==0){
			func(myid, 13054, -3054, 7265, 53652);	//cloudlet7
			usleep(39427000000);
		}else{
			func(myid, 12757, -2757, 43237, 57107);	//cloudlet17
			usleep(0);
		}
	}else if(hid==15){
	}else if(hid==16){
		if(myid==0){
			func(myid, 11330, -1330, 14903, 53309);	//cloudlet8
			usleep(56284000000);
		}else{
			func(myid, 13915, -3915, 68996, 55500);	//cloudlet27
			usleep(0);
		}
	}else if(hid==17){
	}else if(hid==18){
		if(myid==0){
			func(myid, 13660, -3660, 19929, 53005);	//cloudlet9
			usleep(33391000000);
		}else{
			func(myid, 12486, -2486, 51571, 54754);	//cloudlet20
			usleep(0);
		}
	}else if(hid==19){
	}else if(hid==20){
		if(myid==0){
			func(myid, 12118, -2118, 22355, 50594);	//cloudlet10
			usleep(55965000000);
		}else{
			func(myid, 13399, -3399, 72305, 56609);	//cloudlet28
			usleep(0);
		}
	}else if(hid==21){
	}else if(hid==22){
		if(myid==0){
			func(myid, 9780, 220, 22703, 53905);	//cloudlet11
			usleep(39980000000);
		}else{
			func(myid, 11302, -1302, 59308, 57280);	//cloudlet25
			usleep(0);
		}
	}else if(hid==23){
	}else if(hid==24){
		if(myid==0){
			func(myid, 12307, -2307, 23041, 52500);	//cloudlet12
			usleep(55630000000);
		}else{
			func(myid, 14368, -4368, 76191, 54980);	//cloudlet29
			usleep(0);
		}
	}else if(hid==25){
	}else if(hid==26){
		if(myid==0){
			func(myid, 13440, -3440, 23154, 52955);	//cloudlet13
			usleep(24298000000);
		}else{
			func(myid, 12652, -2652, 47394, 53013);	//cloudlet18
			usleep(0);
		}
	}else if(hid==27){
	}else if(hid==28){
		if(myid==0){
			func(myid, 13481, -3481, 24307, 52021);	//cloudlet14
			usleep(27230000000);
		}else{
			func(myid, 12307, -2307, 49871, 53687);	//cloudlet19
			usleep(0);
		}
	}else{
	}

	MPI_Finalize();
	gettimeofday(&proc_end_time, NULL);
	printf("Program now ends: %ld.%ld\n", proc_end_time.tv_sec, proc_end_time.tv_usec);

	return 0;
}