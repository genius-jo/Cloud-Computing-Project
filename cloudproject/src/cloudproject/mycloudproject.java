package cloudproject;

import java.util.Collection;
import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.amazonaws.services.ec2.model.DeleteKeyPairResult;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.MonitorInstancesRequest;
import com.amazonaws.services.ec2.model.MonitorInstancesResult;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.UnmonitorInstancesRequest;
import com.amazonaws.services.ec2.model.UnmonitorInstancesResult;

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
		boolean finish = false;
		
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
			System.out.println(" 9. monitor instance  10. unmonitor instance ");
			System.out.println(" 11. list key pair    12. create key pair ");
			System.out.println(" 13. delete key pair  99. quit  ");
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
            	availableRegions();
                break;

            case 5:
            	stopInstance();
                break;

            case 6:
            	createInstance();
                break;

            case 7:
            	rebootInstance();
                break;

            case 8:
            	listImages();
                break;
            
            case 9:
            	monitorInstance();
            	break;
            
            case 10:
            	unmonitorInstance();
            	break;
            
            case 11:
            	listKeyPair();
            	break;
            
            case 12:
            	createKeyPair();
            	break;
            	
            case 13:
            	deleteKeyPair();
            	break;
            	
            case 99:
            	finish = true;
                break;

			}
			
			if(finish == true) {
				System.out.println("quit");
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
		
		System.out.printf("Starting... %s", instance_id);
		System.out.println();
		
		//해당 인스턴스 id가 있는지 확인
		boolean exist = existInstance(instance_id);
		
		//해당 인스턴스 id가 없을때
		if(exist == false) {
			System.out.println("The instance id does not exist");
			return;
		}
		
		//해당 인스턴스 id가 있을때
		StartInstancesRequest request = new StartInstancesRequest();
		request.withInstanceIds(instance_id);
		
		StartInstancesResult response = ec2.startInstances(request);
		
		System.out.printf("Successfully started instance %s", instance_id);
	}
	
	//4. available regions
	public static void availableRegions() {
		
		System.out.println("Available regions...");
		
		DescribeRegionsResult response = ec2.describeRegions();
		
		for(Region region : response.getRegions()) {
			System.out.printf("[region] %s, " + "[endpoint] %s ", region.getRegionName(), region.getEndpoint());
			System.out.println();
		}
		
	}

	//5. stop instance
	public static void stopInstance() {
		
		System.out.print("Enter instance id : ");
		
		Scanner id_scan = new Scanner(System.in);
		String instance_id = id_scan.nextLine();
		
		//해당 인스턴스 id가 있는지 확인
		boolean exist = existInstance(instance_id);
		
		//해당 인스턴스 id가 없을때
		if(exist == false) {
			System.out.println("The instance id does not exist");
			return;
		}
		
		//해당 인스턴스 id가 있을때
		StopInstancesRequest request = new StopInstancesRequest();
		request.withInstanceIds(instance_id);
		
		StopInstancesResult response = ec2.stopInstances(request);
		
		System.out.printf("Successfully stop instance %s", instance_id);
	}
	
	//6. create instance
	public static void createInstance() {
		
		System.out.print("Enter ami id : ");
		
		Scanner id_scan = new Scanner(System.in);
		String ami_id = id_scan.nextLine();
		
		//해당 이미지id가 있는지 확인
		boolean exist = existImage(ami_id);
		
		//해당 이미지가 없을때
		if(exist == false) {
			System.out.println("The ami id does not exist");
			return;
		}
		
		//해당 이미지가 있을때
		RunInstancesRequest request = new RunInstancesRequest();
		request.withImageId(ami_id);
		request.withInstanceType(InstanceType.T2Micro);
		request.withMaxCount(1);
		request.withMinCount(1);
		
		RunInstancesResult response = ec2.runInstances(request);
		
		System.out.printf("Successfully started EC2 instance %s " + "based on AMI %s", response.getReservation().getInstances().get(0).getInstanceId(), ami_id);
		System.out.println();
	}
	
	//7. reboot instance
	public static void rebootInstance() {
		
		System.out.print("Enter instance id : ");
		
		Scanner id_scan = new Scanner(System.in);
		String instance_id = id_scan.nextLine();
		
		//해당 인스턴스 id가 있는지 확인
		boolean exist = existInstance(instance_id);
		
		//해당 인스턴스 id가 없을때
		if(exist == false) {
			System.out.println("The instance id does not exist");
			return;
		}
		
		//해당 인스턴스 id가 있을때
		System.out.printf("Rebooting... %s", instance_id);
		System.out.println();
		
		RebootInstancesRequest request = new RebootInstancesRequest();
		request.withInstanceIds(instance_id);
		
		RebootInstancesResult response = ec2.rebootInstances(request);
		
		System.out.printf("Successfully rebooted instance %s", instance_id);
		System.out.println();
	}
	
	//8. list images
	public static void listImages() {
		
		System.out.println("Listing images...");
		
		DescribeImagesRequest request = new DescribeImagesRequest();
		request.withOwners("self");
		
		DescribeImagesResult response = ec2.describeImages(request);

		for(Image image : response.getImages()) {
			System.out.printf("[ImageID] %s, " + "[Name] %s, " + "[Owner] %s ", image.getImageId(), image.getName(), image.getOwnerId());
			System.out.println();
		}
		
	}
	
	//9. monitor instance
	public static void monitorInstance() {
		
		System.out.print("Enter instance id : ");
		
		Scanner id_scan = new Scanner(System.in);
		String instance_id = id_scan.nextLine();
		
		//해당 인스턴스 id가 있는지 확인
		boolean exist = existInstance(instance_id);
		
		//해당 인스턴스 id가 없을때
		if(exist == false) {
			System.out.println("The instance id does not exist");
			return;
		}
		
		//해당 인스턴스 id가 있을때
		System.out.printf("Monitoring... %s", instance_id);
		System.out.println();
		
		MonitorInstancesRequest request = new MonitorInstancesRequest();
		request.withInstanceIds(instance_id);
		
		MonitorInstancesResult response = ec2.monitorInstances(request);
		
		System.out.printf("Successfully enabled monitoring for instance %s", instance_id);
		System.out.println();
	}
	
	//10. unmonitor instance
	public static void unmonitorInstance() {
		
		System.out.print("Enter instance id : ");
		
		Scanner id_scan = new Scanner(System.in);
		String instance_id = id_scan.nextLine();
		
		//해당 인스턴스 id가 있는지 확인
		boolean exist = existInstance(instance_id);
		
		//해당 인스턴스 id가 없을때
		if(exist == false) {
			System.out.println("The instance id does not exist");
			return;
		}
		
		//해당 인스턴스 id가 있을때
		System.out.printf("Unmonitoring... %s", instance_id);
		System.out.println();
		
		UnmonitorInstancesRequest request = new UnmonitorInstancesRequest();
		request.withInstanceIds(instance_id);
		
		UnmonitorInstancesResult response = ec2.unmonitorInstances(request);
		
		System.out.printf("Successfully disabled monitoring for instance %s", instance_id);
		System.out.println();
	}
	
	//11.list key pair
	public static void listKeyPair() {
		
		DescribeKeyPairsResult response = ec2.describeKeyPairs();
		
		for(KeyPairInfo key : response.getKeyPairs()) {
			System.out.printf( "[name] %s " + "[fingerprint] %s ", key.getKeyName(), key.getKeyFingerprint());
			System.out.println();
		}
	}
	
	//12. create key pair
	public static void createKeyPair() {
		System.out.print("Enter Key Name : ");
		
		Scanner name_scan = new Scanner(System.in);
		String key_name = name_scan.nextLine();
		boolean exist = existKey(key_name);
		
		//해당 키의 이름이 이미 존재할때
		if(exist == true) {
			System.out.println( "The name of that key already exists");
			return;
		}
		
		//해당 키의 이름이 존재하지 않을때
		CreateKeyPairRequest request = new CreateKeyPairRequest();
		request.withKeyName(key_name);
		
		CreateKeyPairResult response = ec2.createKeyPair(request);
		System.out.printf( "Successfully created key pair named %s", key_name);
	}
	
	//13. delete key pair
	public static void deleteKeyPair() {
		System.out.print("Enter Key Name : ");
		
		Scanner name_scan = new Scanner(System.in);
		String key_name = name_scan.nextLine();
		boolean exist = existKey(key_name);
		
		//해당 키의 이름이 존재하지 않을때
		if(exist == false) {
			System.out.println( "The name of that key does not exist");
			return;
		}
		
		//해당 키의 이름이 존재할때		
		DeleteKeyPairRequest request = new DeleteKeyPairRequest();
		request.withKeyName(key_name);
		
		DeleteKeyPairResult response = ec2.deleteKeyPair(request);
		System.out.printf( "Successfully deleted key pair named %s", key_name);
	}
	
	//해당 인스턴스 ID가 존재하는지 확인
	public static boolean existInstance(String instance_id) {
		
		boolean exist = false;
		boolean done = false;
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		while (!done) {
			DescribeInstancesResult response = ec2.describeInstances(request);
			
			for (Reservation reservation : response.getReservations()) {
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
			
			request.setNextToken(response.getNextToken());
			if (response.getNextToken() == null) {
				done = true;
			}
		}
		
		return exist;
	}
	
	//해당 이미지 ID가 존재하는지 확인
	public static boolean existImage(String ami_id) {
		//해당 이미지id가 있는지 확인
		DescribeImagesRequest request = new DescribeImagesRequest();
		request.withOwners("self");
		
		DescribeImagesResult response = ec2.describeImages(request);
		
		boolean exist = false;
		for(Image image : response.getImages()) {
			if(image.getImageId().contentEquals(ami_id) == true) {
				exist = true;
				break;
			}
		}
		
		return exist;
	}
	
	//해당 key의 이름이 존재하는지 확인
	public static boolean existKey(String key_name) {

		boolean exist = false;
		
		//해당 키의 이름이 존재하는지 확인
		DescribeKeyPairsResult response = ec2.describeKeyPairs();
		
		for(KeyPairInfo key : response.getKeyPairs()) {
			if(key.getKeyName().contentEquals(key_name) == true) {
				exist = true;
				break;
			}
		}
		
		return exist;
	}

}
