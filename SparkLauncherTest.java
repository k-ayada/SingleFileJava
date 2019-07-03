package com.core;

/*
 The Spark code is already avaliable in testspark.jar and the main function in the class com.core.SparkRunner
*/
import java.io.FileInputStream;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.spark.launcher.SparkLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparkLauncherTest {

    final static Logger theLogger = LoggerFactory.getLogger(SparkLauncherTest.class);

    public static void main(final String[] args) {
        try {
            final Properties properties = new Properties();
            properties.load(new FileInputStream(args[0]));

            final String[] hadoopTableNames = properties.getProperty("hadoop.table.name")
                    .split(",");

            int i = 0;
            final String[] objects = properties.getProperty("objects").split(",");
            for (final String object : objects) {
                String hdfsObjectType = object;
                final String objectToFolderMap = properties.getProperty("objectToFolderMap");
                if (objectToFolderMap.contains(hdfsObjectType)) {
                    final StringTokenizer tokens = new StringTokenizer(objectToFolderMap, ",");
                    while (tokens.hasMoreTokens()) {
                        final String token = tokens.nextToken();
                        if (token.contains(hdfsObjectType)) {
                            final String[] strs = token.split(":");
                            hdfsObjectType = strs[1];
                            break;
                        }
                    }
                }

                final String hdfsDir = properties.getProperty("hdfsDir") + hdfsObjectType
                        + "_delta" + "/";
                final String hdfsArchiveDir = properties.getProperty("hdfsDir") + hdfsObjectType
                        + "_delta_archive" + "/";
                final Process spark = new SparkLauncher()
                .setSparkHome("/opt/cloudera/parcels/CDH-5.8.2-1.cdh5.8.2.p0.3/lib/spark")
                .setAppResource("testspark.jar").setMainClass("com.core.SparkRunner")
                .setMaster("yarn-client").setConf(SparkLauncher.DRIVER_MEMORY, "4g")
                .setConf(SparkLauncher.EXECUTOR_MEMORY, "16g")
                .setConf(SparkLauncher.EXECUTOR_CORES, "3")
                .setConf("spark.executor.instances", "10")
                .addAppArgs(hadoopTableNames[i], hdfsDir, hdfsArchiveDir)
                .addJar("/home/hdadmin/bin/csv-serde-0.9.1.jar").launch();
                final InputStreamReaderRunnable inputStreamReaderRunnable = new InputStreamReaderRunnable(
                        spark.getInputStream(), "input");
                final Thread inputThread = new Thread(inputStreamReaderRunnable,
                        "LogStreamReader input");
                inputThread.start();

                final InputStreamReaderRunnable errorStreamReaderRunnable = new InputStreamReaderRunnable(
                        spark.getErrorStream(), "error");
                final Thread errorThread = new Thread(errorStreamReaderRunnable,
                        "LogStreamReader error");
                errorThread.start();
                final int exitCode = spark.waitFor();
                theLogger.debug("exitCode = " + exitCode);
                i++;
            }
        } catch (final Exception e) {
            theLogger.error("Error in spark delta load ", e);
        }

    }
}
