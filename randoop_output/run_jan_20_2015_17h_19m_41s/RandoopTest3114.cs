//LAST ACTION: CodeContractsTest.GenCounter.Void updateCount(Boolean)
//EXCEPTION: System.Diagnostics.Contracts.__ContractsRuntime.ContractException
using CodeContractsTest;
using System;
public class RandoopTest3114
{
  public static int Main()
  {
      //BEGIN TEST
      CodeContractsTest.GenCounter v0 =  new CodeContractsTest.GenCounter();
      System.Int32 v1 = ((CodeContractsTest.GenCounter)v0).getCount() ;
      System.Boolean v2 = (System.Boolean)false;
      ((CodeContractsTest.GenCounter)v0).updateCount((System.Boolean)v2) ;
      System.Int32 v4 = ((CodeContractsTest.GenCounter)v0).getCount() ;
      System.Boolean v5 = (System.Boolean)true;
      ((CodeContractsTest.GenCounter)v0).updateCount((System.Boolean)v5) ;
      System.Boolean v7 = (System.Boolean)true;
      ((CodeContractsTest.GenCounter)v0).updateCount((System.Boolean)v7) ;
      System.Boolean v9 = (System.Boolean)false;
      ((CodeContractsTest.GenCounter)v0).updateCount((System.Boolean)v9) ;
      System.Boolean v11 = (System.Boolean)true;
      ((CodeContractsTest.GenCounter)v0).updateCount((System.Boolean)v11) ;
      //END TEST
      return 99;
    }
  }
}
