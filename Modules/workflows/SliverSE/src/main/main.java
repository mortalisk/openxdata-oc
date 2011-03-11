package main;

import main.client.BPELClient;
import main.server.BPELServerConstructor;

public class main {
	
	public static void main(String[] args) throws Exception {
		BPELServerConstructor bpel = new BPELServerConstructor(9001, "http://jbpm.org/examples/hello", "127.0.0.1", 9000,"hello.bpel");
		bpel.startServer();
		
		BPELClient client = new BPELClient("hellotest.xml", "127.0.0.1", 9001);
		
		//Starts the abosolete method in BPELClient
		//client.startClient();
		
		String[] results = client.queryBPEL("greeting");
		
		if(results != null){
			for(int i=0;i < results.length;i++){
				if(results[i] != null){
					System.out.print("Orginal string: " + results[i]);
					
					try {
						int number = Integer.valueOf(results[i]).intValue();
						number = number * 2;
						System.out.print(". But multiply it by 2 and get: " + number);
					}catch (Exception e) {
						System.out.println("This wasn't a number.");
						System.out.println("Error: " + e.getMessage());
					}
					
					System.out.println("");
				}
			}
		}else{
			System.out.println("No result!");
		}
		
	}
}
