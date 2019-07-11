/* - http://www.javatips.net/blog/2011/09/call-jasper-report-ireport-from-java-application

You need to download

1.JDK 6
2.iReport-4.1.1 for designing the report
Following jar must be in classpath (Available from ireport installation directory)

1.commons-beanutils-1.8.2.jar
2.commons-collections-3.2.1.jar
3.commons-digester-1.7.jar
4.commons-logging-1.1.jar
5.groovy-all-1.7.5.jar
6.iText-2.1.7.jar
7.jasperreports-4.1.1.jar



 * User links:
 * http://jasperreports.sourceforge.net/tutorial/index.html
 * http://jasperreports.sourceforge.net/documentation.html
 * http://jasperreports.sourceforge.net/quick.how.to.html
 */
package com.mycompany.generator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

// import com.mycompany.helper.* ;
// import com.mycompany.dbi.*;

public class JasperReportSample {
 public static void main(String[] args) {
  HashMap hm = null;
  // System.out.println("Usage: ReportGenerator ....");

  try {
   System.out.println("Start ....");
   // Get jasper report
   String jrxmlFileName = "C:/reports/C1_report.jrxml";
   String jasperFileName = "C:/reports/C1_report.jasper";
   String pdfFileName = "C:/reports/C1_report.pdf";

   JasperCompileManager.compileReportToFile(jrxmlFileName, jasperFileName);
   
   // String dbUrl = props.getProperty("jdbc.url");
   String dbUrl = "jdbc:oracle:thin:@localhost:1521:mydbname";
   // String dbDriver = props.getProperty("jdbc.driver");
   String dbDriver = "oracle.jdbc.driver.OracleDriver";
   // String dbUname = props.getProperty("db.username");
   String dbUname = "mydb";
   // String dbPwd = props.getProperty("db.password");
   String dbPwd = "mydbpw";

   // Load the JDBC driver
   Class.forName(dbDriver);
   // Get the connection
   Connection conn = DriverManager
     .getConnection(dbUrl, dbUname, dbPwd);

   // Create arguments
   // Map params = new HashMap();
   hm = new HashMap();
   hm.put("ID", "123");
   hm.put("DATENAME", "April 2006");

   // Generate jasper print
   JasperPrint jprint = (JasperPrint)
                        JasperFillManager
						  .fillReport(jasperFileName, hm, conn);

   // Export pdf file
   JasperExportManager.exportReportToPdfFile(jprint, pdfFileName);
   
   System.out.println("Done exporting reports to pdf");
   
  } catch (Exception e) {
   System.out.print("Exceptiion" + e);
  }
 }
}

