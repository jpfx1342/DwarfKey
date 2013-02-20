/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jpfx.dwarfkey.macro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * A macro is a collection of command groups, each of which represents a keypress,
 * converted to commands.
 * @author john
 */
public class Macro {
	public String name;
	public final ArrayList<Group> groups = new ArrayList<Group>();

	public Macro() { this(null); }
	public Macro(String name) {
		this.name = name;
	}
	
	public static String endGroup = "End of group";
	public static String endMacro = "End of macro";
	
	public static Macro readFromInputStream(InputStream is) throws IOException {
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader read = new BufferedReader(isr);
		String name = read.readLine();
		Macro macro = new Macro(name);
		
		//we keep looping until we reach the end of the file, marked by "End of macro"
		Group group = new Group();
		//we start with a group
		while (true) {
			//each keygroup is a list of commands, with the end marked by "End of group"
			String line = read.readLine();
			if (line == null) { System.err.print("Error: unexpected end of stream!"); break; }
			line = line.replaceAll("^\\s+|\\s+$", "");//trim leading/trailing whitespace
			
			if (line.isEmpty()) {
				//nothing happens.
				continue;
			} else if (line.equals(endGroup)) {
				//this group is done, add it, and make a new one.
				macro.groups.add(group);
				group = new Group();
				continue;
			} else if (line.equals(endMacro)) {
				//end of file.
				if (!group.commands.isEmpty())
					System.err.print("Warning: non-empty group before end of macro!");
				break;
			}
			//otherwise, this is a command.
			group.commands.add(line);
			
			//go again!
		}
		//we don't have to finish up any groups or anything, because the format
		//explicitly does that. (Having to finish up groups would be an error.)
		
		read.close();
		isr.close();
		
		return macro;
	}
	
	public void write(OutputStream os) throws IOException {
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter write = new BufferedWriter(osw);
		
		write.append(name);
		write.newLine();
		
		for (int i = 0; i < groups.size(); i++) {
			//dump a group, with 2 tabs
			Group grp = groups.get(i);
			
			for (int j = 0; j < grp.commands.size(); j++) {
				write.append('\t'); write.append('\t');
				write.append(grp.commands.get(j));
				write.newLine();
			}
			
			//end of group.
			write.append('\t');
			write.append(endGroup);
			write.newLine();
		}
		write.append(endMacro);
		write.newLine();
		
		write.close();
		osw.close();
	}

	public void validate() {
		for (int i = 0; i < groups.size(); i++) {
			if (groups.get(i).commands.isEmpty())
				groups.remove(i--);
		}
	}
}
