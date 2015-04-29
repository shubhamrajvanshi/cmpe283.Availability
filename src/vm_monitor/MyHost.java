package vm_monitor;

import java.net.URL;
import java.rmi.RemoteException;

import CONFIG.SJSULAB;

import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.HostCapability;
import com.vmware.vim25.HostConnectSpec;
import com.vmware.vim25.HostHardwareInfo;
import com.vmware.vim25.HostSystemInfo;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class MyHost 
{
	private static String vmname ;
    private String currentHostIp;
    private Folder folder;
    private static ServiceInstance si ;
    private static VirtualMachine vm ;
    private static HostSystem hs;
    private static boolean flag=true;
    static String snapshotname;
    
    public MyHost()
    {
    	try
    	{
    		this.si=new ServiceInstance(new URL("https://130.65.133.70/sdk"),
					SJSULAB.getVmwareLogin(), SJSULAB.getVmwarePassword(), true);
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    }
    
    public MyHost(String host_name)
    {
    	try
    	{
    		//System.out.println("in constructor");
    		this.vmname= host_name;
    		this.si=new ServiceInstance(new URL("https://130.65.132.14/sdk"),
					SJSULAB.getVmwareLogin(), SJSULAB.getVmwarePassword(), true);
    		this.folder = si.getRootFolder();
    		this.vm=(VirtualMachine) new InventoryNavigator(folder).searchManagedEntity("VirtualMachine",
    			this.vmname);
    		//this.hs= (HostSystem) new InventoryNavigator(folder).searchManagedEntity("HostSystem", "130.65.133.71");
    		this.snapshotname="host2";
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    }
    
       
    public boolean revertHostSnapshot()
    {
    	System.out.println("in revert");
    	try
    	{
   
    		Task t1 = vm.getCurrentSnapShot().revertToSnapshot_Task(null);
    		vm.getCurrentSnapShot().toString();
    		if(t1.waitForTask()==t1.SUCCESS)
    			return true;
    		else
    			return false;
    	
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    	return false;
    }
    
    
    public void takeHostSnapshot()
    {
    	try
    	{
    		System.out.println("Please wait.. your vhost snapshot is being created....");
    		//System.out.println("current name : "+ snapshotname);
    		if(snapshotname.equalsIgnoreCase("host2"))
    		{
    			snapshotname="host1";
    			//System.out.println("came to if");
    		}
    		else 
    		{
    			snapshotname="host2";
    		}
    		//System.out.println("now name is : "+ snapshotname);
    		
    		Task t1 = vm.createSnapshot_Task(snapshotname, "my snap", false, false);
    		if(t1.waitForTask()==t1.SUCCESS)
    		{
    			System.out.println("Initial host snapshot created");
    			
       		}
    		else
    		{
    			System.out.println("Host Snapshot not yet created........................");
    		}
    		
    
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    }
    public boolean  stateOfVhost()
    {
    	boolean res=false;
    	
    	try{
    	
    	VirtualMachineRuntimeInfo vmri=vm.getRuntime();
    	String state=vmri.getPowerState().toString();
    	if(state.contains("poweredOn"))
    		res=true;
    	else res=false;
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
	
    return res;
    	
    }    
    public void powerOn() 
    {
        try 
        {
              // your code here
        	System.out.println("Powering on vHost '"+vm.getName() +"'. Please wait...");     
        	Task t=vm.powerOnVM_Task(null);
        	if(t.waitForTask()== Task.SUCCESS)
        	{
	        	System.out.println("vHost powered on.");
	        	System.out.println("====================================");
        	}
        	else
        		System.out.println("Power on failed / vHost already powered on...");
        } 
        catch ( Exception e ) 
        { 
        	System.out.println( e.toString() ) ;
        }
    }
}
