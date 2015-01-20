//LAST ACTION: CodeContractsTest.GenCounter.Void updateCount(Boolean)
//EXCEPTION: System.Diagnostics.Contracts.__ContractsRuntime.ContractException
using CodeContractsTest;
using System;
public class RandoopTest3364
{
  public static int Main()
  {
      //BEGIN TEST
      CodeContractsTest.GenCounter v0 =  new CodeContractsTest.GenCounter();
      ((CodeContractsTest.GenCounter)v0).resetCount() ;
      System.Boolean v2 = (System.Boolean)true;
      ((CodeContractsTest.GenCounter)v0).updateCount((System.Boolean)v2) ;
      System.Boolean v4 = (System.Boolean)true;
      ((CodeContractsTest.GenCounter)v0).updateCount((System.Boolean)v4) ;
      System.Boolean v6 = (System.Boolean)true;
      ((CodeContractsTest.GenCounter)v0).updateCount((System.Boolean)v6) ;
      System.Boolean v8 = (System.Boolean)false;
      ((CodeContractsTest.GenCounter)v0).updateCount((System.Boolean)v8) ;
      System.Int32 v10 = ((CodeContractsTest.GenCounter)v0).getCount() ;
      System.Int32 v11 = ((CodeContractsTest.GenCounter)v0).getCount() ;
      System.Boolean v12 = (System.Boolean)true;
      ((CodeContractsTest.GenCounter)v0).updateCount((System.Boolean)v12) ;
      //END TEST
      return 99;
    }
  }
}
