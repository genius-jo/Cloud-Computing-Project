package cloudproject;

import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;

public class mycloudproject {

	/*
	 * Cloud Computing, Data Computing Laboratory Department of Computer Science
	 * Chungbuk National University
	 */
	
	static AmazonEC2 ec2;

	private static void init() throws Exception {
		/*
		 * The ProfileCredentialsProvider will return your [default] 
		 * credential profile by reading from the credentials file located at 
		 * (~/.aws/credentials).
		 */
		
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
					"Please make sure that your credentials file is at the correct " +
					"location (~/.aws/credentials), and is in valid format.",
					e);
		}
		ec2 = AmazonEC2ClientBuilder.standard()
				.withCredentials(credentialsProvider)
				.withRegion("ap-northeast-2") /* check the region at AWS console */																							 
				.build();
	}

	public static void main(String[] args) throws Exception {
		
		init();
		
		Scanner menu = new Scanner(System.in);
		Scanner id_string = new Scanner(System.in);
		int number = 0;
		
		while (true) {
			System.out.println(" ");
			System.out.println(" ");
			System.out.println("------------------------------------------------------------");
			System.out.println(" Amazon AWS Control Panel using SDK ");
			System.out.println(" ");
			System.out.println(" Cloud Computing, Computer Science Department ");
			System.out.println(" at Chungbuk National University ");
			System.out.println("------------------------------------------------------------");
			System.out.println(" 1. list instance     2. available zones ");
			System.out.println(" 3. start instance    4. available regions ");
			System.out.println(" 5. stop instance     6. create instance ");
			System.out.println(" 7. reboot instance   8. list images ");
			System.out.println("                      99. quit ");
			System.out.println("------------------------------------------------------------");
			
			System.out.print("Enter an integer: ");
			
			number = menu.nextInt();
			
			switch(number) {
			case 1:
                listInstances();
                break;

            case 2:
            	availableZones();
                break;

            case 3:
            	startInstance();
                break;

            case 4:
                break;

            case 5:
            	stopInstance();
                break;

            case 6:
                break;

            case 7:
                break;

            case 8:
                break;

            case 99:
                break;

			}
		}
	}

	//1. list instance
	public static void listInstances() {
		
		System.out.println("Listing instances....");
		boolean done = false;
		
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		
		while (!done) {
			DescribeInstancesResult response = ec2.describeInstances(request);
			
			for (Reservation reservation : response.getReservations()) {
				for(Instance instance : reservation.getInstances()) {
					System.out.printf(
					"[id] %s, " +
					"[AMI] %s, " +
					"[type] %s, " +
					"[state] %10s, " +
					"[monitoring state] %s",
					instance.getInstanceId(),
					instance.getImageId(),
					instance.getInstanceType(),
					instance.getState().getName(),
					instance.getMonitoring().getState());
				}
				System.out.println();
			}
			
			request.setNextToken(response.getNextToken());
			
			if (response.getNextToken() == null) {
				done = true;
			}
		}
	}
	
	//2. available zones
	public static void availableZones()	{
		
		System.out.println("Available zones...");
		
		DescribeAvailabilityZonesResult response = ec2.describeAvailabilityZones();
		
		for(AvailabilityZone zone : response.getAvailabilityZones()) {
			System.out.printf("[id] %s, " + "[region] %s,  " + "[zone] %s ", zone.getZoneId(), zone.getRegionName(), zone.getZoneName());
			System.out.println();
		}
		System.out.printf("You have access to %d Availability Zones.", response.getAvailabilityZones().size());
		System.out.println();
	}
	
	//3. start instance
	public static void startInstance() {
		System.out.print("Enter instance id : ");
		
		Scanner id_scan = new Scanner(System.in);
		String instance_id = id_scan.nextLine();
		
		System.out.printf("Starting ... %s", instance_id);
		System.out.println();
		
		//해당 인스턴스 id가 있는지 확인
		boolean exist = false;
		boolean done = false;
		DescribeInstancesRequest di_request = new DescribeInstancesRequest();
		while (!done) {
			DescribeInstancesResult di_response = ec2.describeInstances(di_request);
			
			for (Reservation reservation : di_response.getReservations()) {
				for(Instance instance : reservation.getInstances()) {
					
					//시작하려는 인스턴스 id와 id가 같을때
					if(instance.getInstanceId().contentEquals(instance_id) == true) {
						exist = true;
						break;
					}
				}
				
				if(exist == true)
					break;
			}
			
			if(exist == true)
				break;
			
			di_request.setNextToken(di_response.getNextToken());
			if (di_response.getNextToken() == null) {
				done = true;
			}
		}
		
		//해당 인스턴스 id가 없을때
		if(exist == false) {
			System.out.println("The instance id does not exist");
			return;
		}
		
		//해당 인스턴스 id가 있을때
		StartInstancesRequest si_request = new StartInstancesRequest();
		si_request.withInstanceIds(instance_id);
		ec2.startInstances(si_request);
		
		System.out.printf("Successfully started instance %s", instance_id);
	}

	//5. stop instance
	public static void stopInstance() {
		System.out.print("Enter instance id : ");
		
		Scanner id_scan = new Scanner(System.in);
		String instance_id = id_scan.nextLine();
		
		//해당 인스턴스 id가 있는지 확인
		boolean exist = false;
		boolean done = false;
		DescribeInstancesRequest di_request = new DescribeInstancesRequest();
		while (!done) {
			DescribeInstancesResult di_response = ec2.describeInstances(di_request);
			
			for (Reservation reservation : di_response.getReservations()) {
				for(Instance instance : reservation.getInstances()) {
					
					//시작하려는 인스턴스 id와 id가 같을때
					if(instance.getInstanceId().contentEquals(instance_id) == true) {
						exist = true;
						break;
					}
				}
				
				if(exist == true)
					break;
			}
			
			if(exist == true)
				break;
			
			di_request.setNextToken(di_response.getNextToken());
			if (di_response.getNextToken() == null) {
				done = true;
			}
		}
		
		//해당 인스턴스 id가 없을때
		if(exist == false) {
			System.out.println("The instance id does not exist");
			return;
		}
		
		//해당 인스턴스 id가 있을때
		StopInstancesRequest si_request = new StopInstancesRequest();
		si_request.withInstanceIds(instance_id);
		ec2.stopInstances(si_request);
		
		System.out.printf("Successfully stop instance %s", instance_id);
	}
	

}
