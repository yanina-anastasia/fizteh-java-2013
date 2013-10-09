package ru.fizteh.fivt.students.isItJavaOrSomething.Shell;

public final class SomethingIsWrong extends Exception {
    public SomethingIsWrong() { 
        super(); 
    }
    
    public SomethingIsWrong(String message) { 
        super(message); 
    }
    
    public SomethingIsWrong(String message, Throwable cause) { 
        super(message, cause); 
    }
    
    public SomethingIsWrong(Throwable cause) { 
        super(cause); 
    }
}
