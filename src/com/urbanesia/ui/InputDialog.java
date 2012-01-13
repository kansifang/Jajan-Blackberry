package com.urbanesia.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.DialogFieldManager;

public class InputDialog extends Dialog {
	private EditField inputField;

	public InputDialog(String choices[], int values[], String msg) {
		super(
				msg, 
				choices,
				values,
				Dialog.OK, 
				Bitmap.getPredefinedBitmap(Bitmap.QUESTION), 
				Dialog.GLOBAL_STATUS
		);
		
		inputField = new EditField("", "", 50, EditField.EDITABLE);
		net.rim.device.api.ui.Manager delegate = getDelegate();
		if(delegate instanceof DialogFieldManager) {
			DialogFieldManager dfm = (DialogFieldManager)delegate;
			net.rim.device.api.ui.Manager manager = dfm.getCustomManager();
			if(manager != null){
				manager.insert(inputField, 0);
			}
		}
	}
	
	public String getInputText() {
		return inputField.getText();
	}
	
	protected boolean keyChar(char character, int status, int time) {
		if(character == Characters.ENTER) {
			this.close();
			return true;
		}
		return super.keyChar(character, status, time);
	}

}
