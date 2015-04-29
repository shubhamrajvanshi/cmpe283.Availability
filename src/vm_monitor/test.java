package vm_monitor;

public class test 
{

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		final MyVM myvm = new MyVM("T19-vm05-ubuntu32-P1-Medha");
		//String s =myvm.snapshotToClone();
		//System.out.println("name : "+ s);
		System.out.println(myvm.stateOfVM());
	}

}
