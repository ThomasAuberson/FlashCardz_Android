package wertze.android.flashcardz;

import java.util.ArrayList;
import java.util.HashSet;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FilterMenu extends ListActivity {

	private ArrayList<String> options;

	public static HashSet<String> selectCategories;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		selectCategories = new HashSet<String>();

		Bundle b = getIntent().getExtras();
		options = b.getStringArrayList("Options");

		setListAdapter(new ArrayAdapter<String>(FilterMenu.this, android.R.layout.simple_list_item_multiple_choice, options));
		this.getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (position == 0) {
			finish();
		} else {
			String filter = options.get(position);
			if (selectCategories.contains(filter)) {
				selectCategories.remove(filter);

			} else {
				selectCategories.add(filter);
			}
		}
	}
}
