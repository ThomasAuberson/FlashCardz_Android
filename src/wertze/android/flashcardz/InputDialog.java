package wertze.android.flashcardz;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class InputDialog extends Activity {
	
	private EditText inputField;
	private TextView textView;
	private Button ok,cancel;
	
	public static String input;
	public static boolean accept;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inputdialog); // Set layout to inputdialog.xml
		
		Bundle b = getIntent().getExtras(); // Extract dialog text from intent
		String msg = b.getString("Message"); 
		String def = b.getString("Default"); 
		
		textView = (TextView)findViewById(R.id.message); // Set dialog text
		textView.setText(msg);
		
		inputField = (EditText)findViewById(R.id.input); // Set input text field	
		inputField.setText(def);
		inputField.setOnEditorActionListener(new TextView.OnEditorActionListener(){
			public boolean onEditorAction(TextView tv, int i, KeyEvent kev) {
				
				// Return User Input Text
				input = tv.getText().toString();
				accept = true;
				finish(); // Close activity				
				return false; // ??
			}			
		});	
		
		ok = (Button)findViewById(R.id.accept_button);
		ok.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				input = inputField.getText().toString();
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
