package groovy;

import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.IOException;

/**
 * @author sajia
 *
 */
public class TestGroovyShell {
    // see if the number of loaded class keeps growing when
    // using GroovyShell.parse
    public static void test() {
        GroovyShell shell = new GroovyShell();
        String scriptText = "def mul(x, y) { x * y }\nprintln mul(5, 7)";
        
     //   while (true) {
            Script script = shell.parse(scriptText);
            Object result = script.run();
    //    }
    }
    
    public static void main(String[] args) {
        try {
            
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        test();
    }
}