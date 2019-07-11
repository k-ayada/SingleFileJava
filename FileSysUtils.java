package org.util.fs;

import java.io.File;

import org.apache.hadoop.fs.Path;

public class FlSys {

	public static Path recreateDIR(String Path) {
		File dir = new File(Path);
		System.out.print("Dir : '" + Path + "'");
		if (dir.isDirectory()) {
			System.out.println("Exists. Just Cleaning it");
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				cleanNdelDIR(new File(dir, children[i]));
			} 
		} else {
			System.out.println(" Not Found. Creating it.");
			dir.mkdir();
		}
		return new Path(Path);

	}
	public static Path delNgetDIRpath(String Path) {
		cleanNdelDIR(new File(Path));
		return new Path(Path);
		
	}	
	public static void cleanNdelDIR(File Path) {

		if (Path.isDirectory()) {
			String[] children = Path.list();
			for (int i = 0; i < children.length; i++) {
				cleanNdelDIR(new File(Path, children[i]));
			}
		}
		Path.delete();
	}
}
