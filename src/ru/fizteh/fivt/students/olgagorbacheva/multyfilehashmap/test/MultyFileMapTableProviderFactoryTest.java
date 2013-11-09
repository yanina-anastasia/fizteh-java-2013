package ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap.test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap.MultyFileMapTableProviderFactory;

public class MultyFileMapTableProviderFactoryTest {

      static MultyFileMapTableProviderFactory factory;
      
      @BeforeClass
      public static void setUpBeforeClass() throws Exception {
            factory = new MultyFileMapTableProviderFactory();
      }

      @Test (expected = IllegalArgumentException.class)
      public void test1() {
            factory.create("/dev");
      }
      
      @Test (expected = IllegalArgumentException.class)
      public void test2() {
            factory.create("/home/olga/ololo");
      }
      
      @Test (expected = IllegalArgumentException.class)
      public void test3() {
            factory.create(null);
      }
      
      @Test (expected = IllegalArgumentException.class)
      public void test4() {
            factory.create("");
      }
      
      @Test
      public void test5() {
            factory.create("/home/olga/Documents/java/DB/testing");
      }

}
