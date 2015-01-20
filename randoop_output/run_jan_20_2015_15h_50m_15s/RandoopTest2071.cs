//LAST ACTION: CodeContractsTest.GenCounter.Void updateCount(Boolean)
//EXCEPTION: System.Diagnostics.Contracts.__ContractsRuntime.ContractException
using CodeContractsTest;
using System;
public class RandoopTest2071
{
  public static int Main()
  {
      //BEGIN TEST
      CodeContractsTest.GenCounter v0 =  new CodeContractsTest.GenCounter();
      ((CodeContractsTest.GenCounter)v0).resetCount() ;
      System.Int32 v2 = ((CodeContractsTest.GenCounter)v0).getCount() ;
      System.Boolean v3 = (System.Boolean)false;
      ((CodeContractsTest.GenCounter)v0).updateCount((System.Boolean)v3) ;
      System.Boolean v5 = (System.Boolean)true;
      ((CodeContractsTest.GenCounter)v0).updateCount((System.Boolean)v5) ;
      System.Int32 v7 = ((CodeContractsTest.GenCounter)v0).getCount() ;
      System.Boolean v8 = (System.Boolean)true;
      ((CodeContractsTest.GenCounter)v0).updateCount((System.Boolean)v8) ;
      System.Boolean v10 = (System.Boolean)true;
      ((CodeContractsTest.GenCounter)v0).updateCount((System.Boolean)v10) ;
      System.Boolean v12 = (System.Boolean)false;
      ((CodeContractsTest.GenCounter)v0).updateCount((System.Boolean)v12) ;
      System.Boolean v14 = (System.Boolean)false;
      ((CodeContractsTest.GenCounter)v0).updateCount((System.Boolean)v14) ;
      System.Boolean v16 = (System.Boolean)true;
      ((CodeContractsTest.GenCounter)v0).updateCount((System.Boolean)v16) ;
      //END TEST
      return 99;
    }
  }
}
