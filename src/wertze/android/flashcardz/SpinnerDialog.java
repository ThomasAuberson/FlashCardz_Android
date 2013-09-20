package wertze.android.flashcardz;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class SpinnerDialog extends Activity{
	
	private Button ok, cancel;
	private TextView textView;
	private Spinner spinner;
	
	public static boolean accept = false;
	public static String input = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spinnerdialog); // Set layout to spinnerdialog.xml
		
		Bundle b = getIntent().getExtras(); // Extract dialog text from intent
		String msg = b.getString("Message"); 
		
		textView = (TextView)findViewById(R.id.message); // Set dialog text
		textView.setText(msg);
		
		ArrayList<String> options = b.getStringArrayList("Options");		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, options);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinner = (Spinner) findViewById(R.id.spinner);
		spinner.setAdapter(adapter);
		
		ok = (Button)findViewById(R.id.accept_button);
		ok.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				input = spinner.getSelectedItem().toString();
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
