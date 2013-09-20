package wertze.android.flashcardz;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListMenu extends ListActivity {

	private String[] options;

	public static int inputIndex;
	public static boolean accept;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle b = getIntent().getExtras();
		options = b.getStringArray("Options");
		inputIndex = MainActivity.CANCEL;

		setListAdapter(new ArrayAdapter<String>(ListMenu.this, android.R.layout.simple_list_item_1 , options));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		inputIndex = position;
		accept = true;
		finish();
	}
}

//simple_list_item_multiple_choice