package experiment_builder.sample;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

public class ExampleLog4j{

   /* Get actual class name to be printed on */
   //static Logger log = Logger.getLogger(ExampleLog4j.class );
   
   private static final org.apache.logging.log4j.Logger log = LogManager.getLogger( ExampleLog4j.class );
   
   public static void main(String[] args) throws IOException,SQLException{
      log.trace("Hello this is a trace message");
      log.debug("Hello this is a debug message");
      log.info("Hello this is an info message");
      log.warn("Hello this is an warn message");
   }
}