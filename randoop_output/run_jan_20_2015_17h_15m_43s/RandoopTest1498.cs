//LAST ACTION: CodeContractsTest.GenCounter.Void updateCount(Boolean)
//EXCEPTION: System.Diagnostics.Contracts.__ContractsRuntime.ContractException
using CodeContractsTest;
using System;
public class RandoopTest1498
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
      System.Boolean v7 = (System.Boolean)true;
      ((CodeContractsTest.GenCounter)v0).updateCount((System.Boolean)v7) ;
      System.Int32 v9 = ((CodeContractsTest.GenCounter)v0).getCount() ;
      System.Int32 v10 = ((CodeContractsTest.GenCounter)v0).getCount() ;
      System.Int32 v11 = ((CodeContractsTest.GenCounter)v0).getCount() ;
      System.Boolean v12 = (System.Boolean)true;
      ((CodeContractsTest.GenCounter)v0).updateCount((System.Boolean)v12) ;
      System.Boolean v14 = (System.Boolean)true;
      ((CodeContractsTest.GenCounter)v0).updateCount((System.Boolean)v14) ;
      //END TEST
      return 99;
    }
  }
}
