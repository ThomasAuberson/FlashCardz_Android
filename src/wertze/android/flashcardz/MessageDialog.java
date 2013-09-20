package wertze.android.flashcardz;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MessageDialog extends Activity {
	
	private Button button;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messagedialog);
		
		Bundle b = getIntent().getExtras();
		String msg = b.getString("Message");
		
		textView = (TextView)findViewById(R.id.message);
		textView.setText(msg);
		button = (Button)findViewById(R.id.accept_button);		
		button.setOnClickListener(new TextView.OnClickListener(){
			public void onClick(View arg0) {
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
