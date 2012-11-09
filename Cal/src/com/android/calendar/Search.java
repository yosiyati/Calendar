package com.android.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Search extends Activity implements OnClickListener {
	
	// searchボタン
	private Button search = null;
	// 検索したい名前を入力するEditText
	private EditText editText = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		editText = (EditText)findViewById(R.id.editText);
		
		//検索ボタンにListenerを設定
		search = (Button)findViewById(R.id.SearchButton);
		search.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		SpannableStringBuilder ssb = (SpannableStringBuilder)editText.getText();
        String str = ssb.toString();
		
	}

}
