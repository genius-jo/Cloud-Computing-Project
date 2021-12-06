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
import com.amazonaws.services.ec2.model.StopInstancesRequest;
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
			System.out.println(" 13. delete key pair ");
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
		
		System.out.printf("Starting ... %s", instance_id);
		System.out.println();
		
		//�ش� �ν��Ͻ� id�� �ִ��� Ȯ��
		boolean exist = false;
		boolean done = false;
		DescribeInstancesRequest di_request = new DescribeInstancesRequest();
		while (!done) {
			DescribeInstancesResult di_response = ec2.describeInstances(di_request);
			
			for (Reservation reservation : di_response.getReservations()) {
				for(Instance instance : reservation.getInstances()) {
					
					//�����Ϸ��� �ν��Ͻ� id�� id�� ������
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
		
		//�ش� �ν��Ͻ� id�� ������
		if(exist == false) {
			System.out.println("The instance id does not exist");
			return;
		}
		
		//�ش� �ν��Ͻ� id�� ������
		StartInstancesRequest si_request = new StartInstancesRequest();
		si_request.withInstanceIds(instance_id);
		ec2.startInstances(si_request);
		
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
		
		//�ش� �ν��Ͻ� id�� �ִ��� Ȯ��
		boolean exist = false;
		boolean done = false;
		DescribeInstancesRequest di_request = new DescribeInstancesRequest();
		while (!done) {
			DescribeInstancesResult di_response = ec2.describeInstances(di_request);
			
			for (Reservation reservation : di_response.getReservations()) {
				for(Instance instance : reservation.getInstances()) {
					
					//�����Ϸ��� �ν��Ͻ� id�� id�� ������
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
		
		//�ش� �ν��Ͻ� id�� ������
		if(exist == false) {
			System.out.println("The instance id does not exist");
			return;
		}
		
		//�ش� �ν��Ͻ� id�� ������
		StopInstancesRequest si_request = new StopInstancesRequest();
		si_request.withInstanceIds(instance_id);
		ec2.stopInstances(si_request);
		
		System.out.printf("Successfully stop instance %s", instance_id);
	}
	
	//6. create instance
	public static void createInstance() {
		System.out.print("Enter ami id : ");
		
		Scanner id_scan = new Scanner(System.in);
		String ami_id = id_scan.nextLine();
		
		//�ش� �̹���id�� �ִ��� Ȯ��
		DescribeImagesRequest di_request = new DescribeImagesRequest();
		di_request.withOwners("self");
		DescribeImagesResult di_response = ec2.describeImages(di_request);
		boolean exist = false;
		for(Image image : di_response.getImages()) {
			if(image.getImageId().contentEquals(ami_id) == true) {
				exist = true;
				break;
			}
		}
		
		//�ش� �̹����� ������
		if(exist == false) {
			System.out.println("The ami id does not exist");
			return;
		}
		
		//�ش� �̹����� ������
		RunInstancesRequest ri_request = new RunInstancesRequest();
		ri_request.withImageId(ami_id);
		ri_request.withInstanceType(InstanceType.T2Micro);
		ri_request.withMaxCount(1);
		ri_request.withMinCount(1);
		
		RunInstancesResult ri_response = ec2.runInstances(ri_request);
		System.out.printf("Successfully started EC2 instance %s " + "based on AMI %s", ri_response.getReservation().getInstances().get(0).getInstanceId(), ami_id);
		System.out.println();
	}
	
	//7. reboot instance
	public static void rebootInstance() {
		System.out.print("Enter instance id : ");
		
		Scanner id_scan = new Scanner(System.in);
		String instance_id = id_scan.nextLine();
		
		//�ش� �ν��Ͻ� id�� �ִ��� Ȯ��
		boolean exist = false;
		boolean done = false;
		DescribeInstancesRequest di_request = new DescribeInstancesRequest();
		while (!done) {
			DescribeInstancesResult di_response = ec2.describeInstances(di_request);
			
			for (Reservation reservation : di_response.getReservations()) {
				for(Instance instance : reservation.getInstances()) {
					
					//�����Ϸ��� �ν��Ͻ� id�� id�� ������
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
		
		//�ش� �ν��Ͻ� id�� ������
		if(exist == false) {
			System.out.println("The instance id does not exist");
			return;
		}
		
		//�ش� �ν��Ͻ� id�� ������
		System.out.printf("Rebooting... %s", instance_id);
		System.out.println();
		
		RebootInstancesRequest ri_request = new RebootInstancesRequest();
		ri_request.withInstanceIds(instance_id);
		
		RebootInstancesResult ri_response = ec2.rebootInstances(ri_request);
		
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
		
		//�ش� �ν��Ͻ� id�� �ִ��� Ȯ��
		boolean exist = false;
		boolean done = false;
		DescribeInstancesRequest di_request = new DescribeInstancesRequest();
		while (!done) {
			DescribeInstancesResult di_response = ec2.describeInstances(di_request);
			
			for (Reservation reservation : di_response.getReservations()) {
				for(Instance instance : reservation.getInstances()) {
					
					//�����Ϸ��� �ν��Ͻ� id�� id�� ������
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
		
		//�ش� �ν��Ͻ� id�� ������
		if(exist == false) {
			System.out.println("The instance id does not exist");
			return;
		}
		
		//�ش� �ν��Ͻ� id�� ������
		System.out.printf("Monitoring... %s", instance_id);
		System.out.println();
		
		MonitorInstancesRequest mi_request = new MonitorInstancesRequest();
		mi_request.withInstanceIds(instance_id);
		
		MonitorInstancesResult mi_response = ec2.monitorInstances(mi_request);
		
		System.out.printf("Successfully enabled monitoring for instance %s", instance_id);
		System.out.println();
	}
	
	//10. unmonitor instance
	public static void unmonitorInstance() {
		System.out.print("Enter instance id : ");
		
		Scanner id_scan = new Scanner(System.in);
		String instance_id = id_scan.nextLine();
		
		//�ش� �ν��Ͻ� id�� �ִ��� Ȯ��
		boolean exist = false;
		boolean done = false;
		DescribeInstancesRequest di_request = new DescribeInstancesRequest();
		while (!done) {
			DescribeInstancesResult di_response = ec2.describeInstances(di_request);
			
			for (Reservation reservation : di_response.getReservations()) {
				for(Instance instance : reservation.getInstances()) {
					
					//�����Ϸ��� �ν��Ͻ� id�� id�� ������
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
		
		//�ش� �ν��Ͻ� id�� ������
		if(exist == false) {
			System.out.println("The instance id does not exist");
			return;
		}
		
		//�ش� �ν��Ͻ� id�� ������
		System.out.printf("Unmonitoring... %s", instance_id);
		System.out.println();
		
		UnmonitorInstancesRequest umi_request = new UnmonitorInstancesRequest();
		umi_request.withInstanceIds(instance_id);
		
		UnmonitorInstancesResult umi_response = ec2.unmonitorInstances(umi_request);
		
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
		boolean exist = false;
		
		//�ش� Ű�� �̸��� �����ϴ��� Ȯ��
		DescribeKeyPairsResult dp_response = ec2.describeKeyPairs();
		
		for(KeyPairInfo key : dp_response.getKeyPairs()) {
			if(key.getKeyName().contentEquals(key_name) == true) {
				exist = true;
				break;
			}
		}
		
		//�ش� Ű�� �̸��� �̹� �����Ҷ�
		if(exist == true) {
			System.out.println( "The name of that key already exists");
			return;
		}
		
		//�ش� Ű�� �̸��� �������� ������
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
		boolean exist = false;
		
		//�ش� Ű�� �̸��� �����ϴ��� Ȯ��
		DescribeKeyPairsResult dp_response = ec2.describeKeyPairs();
		
		for(KeyPairInfo key : dp_response.getKeyPairs()) {
			if(key.getKeyName().contentEquals(key_name) == true) {
				exist = true;
				break;
			}
		}
		
		//�ش� Ű�� �̸��� �������� ������
		if(exist == false) {
			System.out.println( "The name of that key does not exist");
			return;
		}
		
		//�ش� Ű�� �̸��� �����Ҷ�		
		DeleteKeyPairRequest request = new DeleteKeyPairRequest();
		request.withKeyName(key_name);
		
		DeleteKeyPairResult response = ec2.deleteKeyPair(request);
		System.out.printf( "Successfully deleted key pair named %s", key_name);
	}
	

}
