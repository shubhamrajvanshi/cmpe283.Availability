package vm_monitor;
import java.io.IOException;

import com.vmware.vim25.VirtualMachineRuntimeInfo;

		// TODO Auto-generated method stub
	
		
		//myvm.helloVM();
	
		public class pingThread implements Runnable
		{
			private Thread t;
			public String hostIp="";
			final MyVM myvm = new MyVM("T19-vm05-ubuntu32-P1-Medha");
		
			final MyHost myhost= new MyHost("t19-vHost01-cum4-lab1 _.133.71"); // create an instance of current host

			public void run()
			{
				//System.out.println("Thread name: "+ t.getName());
				
				myvm.helloVM();
				myhost.takeHostSnapshot();
			
				while(true)
				{
					try
					{
						
						hostIp= myvm.returnCurrentHost();
						System.out.println("host ip of this vm is : "+ hostIp);
						
						//myhost.revertHostSnapshot();
						
						boolean res=myvm.stateOfVM();
						if(res==false) //if VM powered off
						{
							System.out.println("VM is off. Checking for alarms..");
							boolean al = myvm.getalarm();
							if(al==true) // alarm set
							{
								System.out.println("User might have turned off VM."
										+ " Cannot ping. Exiting Thread");
								return ;
							}
							else //alarm not set
							{
								myvm.setalarm();
							}
						}
						else // VM power is on
						{
							System.out.println("VM is on. Trying to ping your VM");
						
							boolean result=myvm.pingMyVM();
							
							if(result == true) // ping success
							{						
								System.out.println("VM is responding..");
								myvm.createOneSnapshot();
							}
							else //ping fail
							{
									//if vm is on and ping fails
									//check if vhost is active
								
									//did user turn off vm suddenly ?
									boolean res1=myvm.stateOfVM();
									if(res1==true) // vm  on
									{	
			
										System.out.println("Virtual Machine is powered on. But ping failed. "
												+ "Hence attempting to ping vHost..");
										
										String host_name=myvm.returnCurrentHost();
										boolean host_result = myvm.pingHost(host_name);
										if(host_result == true) //vhost working
										{
											System.out.println("Ping working... Host is alive");
											myhost.takeHostSnapshot();
											
											System.out.println("Reverting VM to prev snapshot");
											boolean rev= myvm.revertToSnapshot();
											if(rev == true)// revert success
											{
												boolean on = myvm.stateOfVM();
												if(on==false)
												{
													myvm.powerOn();
													System.out
															.println("waiting for changes to take effect...");
													Thread.sleep(70000);
												}
												
												boolean ping = myvm.pingMyVM();
												if(ping==false)
												{
													System.out
															.println("Repair failed.. Something else is"
																	+ " wrong with the VM");
													return;
												}
													
											}
											else
											{
												System.out.println("No snapshots available to revert.");
											}
											  
										}
										else // vhost not working
										{
											boolean rev = myhost.revertHostSnapshot();
											if(rev == true)
											{
												boolean on = myhost.stateOfVhost();
												myhost.powerOn();
												System.out
														.println("waiting for changes to take effect...");
												Thread.sleep(70000);
												
											}
											boolean host_result1 = myvm.pingHost(host_name);
												
											if(host_result1==true)
											{
												boolean power = myvm.stateOfVM();
												System.out
														.println("Power on is : "+ power);
												System.out
														.println("please wait again.. changes taking place... ");
												if (power==false)
												{
													myvm.powerOn();
													
												}
												Thread.sleep(70000);
												myvm.pingMyVM();
											}
											else
											{
												System.out.println("Host not Live !! Should migrate to "
														+ "another live vHost now..");
												//look for another host
												String ip =myvm.lookForAnotherHost();
												System.out.println("IP of host : " + ip);
												if(ip.isEmpty())
												{
													ip = myvm.addNewHost();
												}
												myvm.migrateToNewHost( ip);
												myvm.pingMyVM();
											}
																					
											
										}
								
									}
									else
									{
										boolean poweroff= myvm.getAlarmTriggerStatus();
										if(poweroff)
											System.out.println("VM was turned off by user. Cannot ping anymore!");
										return;
									}
							}
						
						
						System.out.println("==========================================");
					}
					}
					catch(Exception e)
					{
						System.out.println(e.toString());
						e.printStackTrace();
					}
			
					try 
					{
						Thread.sleep(10000); // Check pinging after 10 Sec.
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				
				}
				
			}
			
			public void start(){
				t = new Thread (this);
				t.run();
			}
		}
	
