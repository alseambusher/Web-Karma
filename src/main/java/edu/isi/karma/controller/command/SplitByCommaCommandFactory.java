/*******************************************************************************
 * Copyright 2012 University of Southern California
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This code was developed by the Information Integration Group as part 
 * of the Karma project at the Information Sciences Institute of the 
 * University of Southern California.  For more information, publications, 
 * and related projects, please see: http://www.isi.edu/integration
 ******************************************************************************/
package edu.isi.karma.controller.command;

import javax.servlet.http.HttpServletRequest;

import edu.isi.karma.view.VWorkspace;

public class SplitByCommaCommandFactory extends CommandFactory {
	private enum Arguments {
		vWorksheetId, hNodeId, delimiter
	}

	@Override
	public Command createCommand(HttpServletRequest request,
			VWorkspace vWorkspace) {
		String hNodeId = request.getParameter(Arguments.hNodeId.name());
		String vWorksheetId = request.getParameter(Arguments.vWorksheetId.name());
		String delimiter = request.getParameter(Arguments.delimiter.name());
		
		// Convert the delimiter into character primitive type
		char delimiterChar = ',';
		if(delimiter.equalsIgnoreCase("space"))
			delimiterChar = ' ';
		else if(delimiter.equalsIgnoreCase("tab"))
			delimiterChar = '\t';
		else {
			delimiterChar = new Character(delimiter.charAt(0));
		}
		
		return new SplitByCommaCommand(getNewId(vWorkspace), getWorksheetId(
				request, vWorkspace), hNodeId, vWorksheetId, delimiterChar);
	}
}
