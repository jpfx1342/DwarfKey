/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jpfx.dwarfkey;

import com.jpfx.dwarfkey.macro.Command;
import com.jpfx.dwarfkey.macro.Macro;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author john
 */
public class DwarfKey {
	public static String macroExt = ".mak";
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception {
		//find Dwarf Fortress directory
		File dfDir = new File(System.getProperty("user.home")+"/df_linux");
		if (!dfDir.isDirectory()) throw new IllegalStateException("Dwarf Fortress directory ("+dfDir+") invalid.");
		//find the macro directory, or create it if it doesn't exist
		File makDir = new File(dfDir, "data/init/macros");
		if (!makDir.isDirectory())
			if (!makDir.mkdirs())
				throw new IllegalStateException("Unable to create macro directory ("+makDir+").");
		
		//Dump the binding targets. A binding target is one tokens in a macro
		//that triggers some action in game. Dumping the binding targets lets us
		//easily find new ones.
		if (1==0) {
			System.out.println("Dumping bind targets:");
			
			File initDir = new File(dfDir, "data/init");
			//read the interface file.
			FileInputStream is = new FileInputStream(new File(initDir, "interface.txt"));
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader read = new BufferedReader(isr);
			
			ArrayList<String> targets = new ArrayList<String>();
			String line = read.readLine();
			while (line != null) {
				if (line.startsWith("[BIND:")) {
					int index1 = line.indexOf(':')+1;
					int index2 = line.indexOf(':', index1);
					String target = line.substring(index1, index2);
					targets.add(target);
				}
				
				line = read.readLine();
			}
			boolean printKnown = true;
			for (int i = 0; i < targets.size(); i++) {
				Command.CommandSubgroup group = Command.getSubgroup(targets.get(i));
				
				if (group == null || printKnown) {
					System.out.println(targets.get(i)+" ("+(group == null ? "UNKNOWN" : group.name()+":"+group.group)+")");
				}
			}
			System.exit(0);
		}
		
		//find a macro
		String macroName = "Make 2x2 Bedrooms";
		File macroPath = new File(makDir, macroName+macroExt);
		FileInputStream fis = new FileInputStream(macroPath);
		
		//read a macro
		Macro macro = Macro.readFromInputStream(fis);
		fis.close(); //close the stream, we're done with it
		
		System.out.println("Successfully loaded macro \""+macro.name+"\" ("+macro.groups.size()+" groups).");
		System.out.println("Applying filter..."); //this filters all commands in the file, basically optimizing it.
		Command.applyCommandFilter(macro, new Command.GroupCommandFilter(
			Command.CommandSubgroup.NAV_SELECT,
			Command.CommandSubgroup.NAV_CURSOR,
			Command.CommandSubgroup.NAV_MENU,
			Command.CommandSubgroup.FORT_MENU_DESIGNATE));
		macro.validate();
		
		//write the macro out to the main output
		System.out.println("Dumping macro:");
		macro.write(System.out); //this writes the macro out in the same format DF uses
		
		System.out.println("Writing macro...");
		FileOutputStream fos = new FileOutputStream(macroPath);
		macro.write(fos);
	}
}
