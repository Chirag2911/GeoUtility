package com.killerrech.Utility;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import java.util.HashMap;

/** Customizing AutoCompleteTextView to return Place Description   
 *  corresponding to the selected item
 */
public class CustomAutoCompleteTextView extends AutoCompleteTextView {
	
	public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/** Returns the place description corresponding to the selected item */
	@Override
	protected CharSequence convertSelectionToString(Object selectedItem) {
		/** Each item in the autocompetetextview suggestion list is a hashmap object */
		HashMap<String, String> hm = (HashMap<String, String>) selectedItem;
		return hm.get("description");
	}

	@Override
	protected void performFiltering(CharSequence text, int keyCode) {
		// nothing, block the default auto complete behavior
//		String filterText = "";
//		super.performFiltering(filterText, keyCode);
	}
//	@Override
//	protected void replaceText(final CharSequence text) {
//		super.replaceText(text);
//	}
}
