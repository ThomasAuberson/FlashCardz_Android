package wertze.android.flashcardz;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddCardDialog extends Activity {
	
	private EditText inputFieldQ, inputFieldA, inputFieldCat;
	private TextView textView;
	private Button ok,cancel;
	
	public static String inputQ = "";
	public static String inputA = "";
	public static String inputCat = "";
	public static boolean accept = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_dialog);
		
		Bundle b = getIntent().getExtras();
		String msg = b.getString("Message");
		String def = b.getString("Default");
		
		textView = (TextView)findViewById(R.id.message);
		textView.setText(msg);
		inputFieldQ = (EditText)findViewById(R.id.input_q);			
		inputFieldA = (EditText)findViewById(R.id.input_a);
		
		inputFieldCat = (EditText)findViewById(R.id.input_cat);
		inputFieldCat.setText(def);
//		inputFieldCat.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//			public boolean onEditorAction(TextView tv, int actionId, KeyEvent kev) {
//				inputQ = inputFieldQ.getText().toString();
//				inputA = inputFieldA.getText().toString();
//				inputCat = inputFieldCat.getText().toString();
//				accept = true;
//				finish();
//				return false;
//			}
//		});
		
		ok = (Button)findViewById(R.id.accept_button);
		ok.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				inputQ = inputFieldQ.getText().toString();
				inputA = inputFieldA.getText().toString();
				inputCat = inputFieldCat.getText().toString();
				accept = true;
				finish();				
			}			
		});
		
		cancel = (Button)findViewById(R.id.cancel_button);
		cancel.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				accept = false;
				finish();				
			}			
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
