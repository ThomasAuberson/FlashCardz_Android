package wertze.android.flashcardz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	// FIELDS
	private ArrayList<Card> cardSet; // Complete properly ordered Card Set
	private ArrayList<String> cardSets; // A list of currently saved card sets
										// arranged from oldest to most recent
	private int num; // Currently selected card number
	private ArrayList<Card> currentCardSet; // Currently navigated card set inc.
											// filters and randomizing
	private String cardSetName = "DEFAULT";
	private boolean answer; // Whether or not display is set to display the
							// answer to the flash card or not
	private boolean askAnswers = false;
	private boolean frontPage = true;
	private String currentCategory = ""; // Most recently chosen category. Will
											// default when you create new cards

	// Filtering
	private HashSet<String> categories = new HashSet<String>();
	private HashSet<String> selectCategories = new HashSet<String>();
	private boolean priorityOnly = false;

	// Options Menu
	private String[] menuOptions;
	public final int PRIORITIZE = 0;
	public final int MODIFY = 1;
	public final int DELETE = 2;
	public final int FLIP = 3;
	public final int SHUFFLE = 4;
	public final int UNSHUFFLE = 5;
	public final int ORDER_ABC = 6;
	public final int COUNT = 7;
	public final int FILTERS = 8;
	public final int IMPORT_SET = 9;
	public static final int CANCEL = 10;
	public static final int NUM_OPTIONS = 11;

	// GUI Elements
	private Button prev, add, next, skip;
	private TextView display;

	// Dialog Request Codes
	public final int ADD_CARD = 1;
	public final int NEW_CARD_SET_NAME = 2;
	public final int DELETE_CARD_SET = 3;
	public final int OPEN_CARD_SET = 4;
	public final int IMPORT_CARD_SET = 5;
	public final int OPTIONS_MENU = 6;
	public final int CHANGE_CARD = 7;
	public final int IMPORT_CARD_SET_2 = 8;
	public final int FILTER_MENU = 9;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialize GUI Elements
		prev = (Button) findViewById(R.id.bPrev);
		add = (Button) findViewById(R.id.bAdd);
		next = (Button) findViewById(R.id.bNext);
		skip = (Button) findViewById(R.id.bSkip);
		display = (TextView) findViewById(R.id.tvDisplay);

		// Initialize Click Listeners
		display.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				optionsMenu();
			}
		});
		prev.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				prevCard();
			}
		});
		add.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// addCard();
				addCardDialog(getString(R.string.add_card_text), currentCategory);
			}
		});
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (frontPage)
					firstCard();
				else if (answer)
					nextCard();
				else
					answer();
			}
		});
		skip.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				nextCard();
			}
		});

		initializeOptionsMenu();

		loadFrontPage();

		// Load References
		loadReferences();
		if (!cardSets.isEmpty()) {
			loadCardSet(cardSets.get(0));
		} else
			cardSet = new ArrayList<Card>();
		currentCardSet = new ArrayList<Card>(cardSet);
	}

	public void loadReferences() {
		try {
			cardSets = new ArrayList<String>();
			InputStream inputStream = openFileInput("setlists.txt");

			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader scan = new BufferedReader(inputStreamReader);

				String s;

				if ((s = scan.readLine()) != null) {
					String[] vals = s.split("<");

					for (int i = 0; i < vals.length; i += 1) {
						cardSets.add(vals[i]);
					}
				}
				scan.close();
			}
		} catch (FileNotFoundException e) {
			cardSets = new ArrayList<String>();
		} catch (IOException e) {
			messageDialog(getString(R.string.refs_warning));
		}
	}

	// MENU METHODS
	public void newCardSet(String x) {
		if (x == null)
			return;
		else
			cardSetName = x;
		cardSet = new ArrayList<Card>();
		currentCardSet = new ArrayList<Card>(cardSet);
		categories = new HashSet<String>();
		selectCategories = new HashSet<String>();
		saveCardSet(cardSetName, false);
		loadFrontPage();
	}

	public void deleteCurrentCardSet() {
		cardSets.remove(cardSetName);
		newCardSet("DEFAULT");
	}

	public void importCardSet(String x) {
		try {
			cardSet = new ArrayList<Card>();
			String[] temp = x.split("/");
			String setName = temp[(temp.length - 1)];
			categories = new HashSet<String>();
			selectCategories = new HashSet<String>();

			File file = new File(x);
			FileInputStream inputStream = new FileInputStream(file);

			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader scan = new BufferedReader(inputStreamReader);

				int n = 0;
				String s;
				while ((s = scan.readLine()) != null) {
					String t = s;
					String q = scan.readLine();
					String a = scan.readLine();
					cardSet.add(new Card(q, a, n, t));
					if (!categories.contains(t)) {
						categories.add(t);
						selectCategories.add(t);
					}
					n++;
				}
				scan.close();
				cardSetName = setName;
				currentCardSet = new ArrayList<Card>(cardSet);
				inputDialog(getString(R.string.import_success), "", IMPORT_CARD_SET_2);
			}
		} catch (FileNotFoundException e) {
			messageDialog(getString(R.string.import_fail_2));
		} catch (IOException e) {
			messageDialog(getString(R.string.import_fail));
		}
	}

	public void loadCardSet(String x) {
		try {
			cardSet = new ArrayList<Card>();
			InputStream inputStream = openFileInput(x + ".sav");
			categories = new HashSet<String>();
			selectCategories = new HashSet<String>();

			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader scan = new BufferedReader(inputStreamReader);

				int n = 0;
				String s;

				if ((s = scan.readLine()) != null) {
					String[] vals = s.split("<");

					for (int i = 0; i < vals.length; i += 3) {
						String t = vals[i];

						String[] types = t.split(">");
						t = types[0];

						String q = vals[i + 1];
						String a = vals[i + 2];
						Card card = new Card(q, a, n, t);
						if (types.length > 1) {
							if (types[1].equals("!"))
								card.setPriority(true);
							if (types[1].equals("0"))
								card.setObsolete(true);
							if (types[1].equals("!0")) {
								card.setPriority(true);
								card.setObsolete(true);
							}
						}
						cardSet.add(card);
						if (!categories.contains(t)) {
							categories.add(t);
							selectCategories.add(t);
						}
						n++;
					}
				}
				cardSetName = x;
				currentCardSet = new ArrayList<Card>(cardSet);
				scan.close();
			}
		} catch (IOException e) {
			messageDialog(getString(R.string.load_fail));
		}
	}

	public void saveCardSet(String x, boolean prompt) {
		try {
			OutputStreamWriter osw = new OutputStreamWriter(openFileOutput(x + ".sav", Context.MODE_PRIVATE));

			for (int n = 0; n < cardSet.size(); n++) {
				String type = cardSet.get(n).getT();
				if (cardSet.get(n).getPriority() && cardSet.get(n).getObsolete())
					type = cardSet.get(n).getT() + ">!0";
				else if (cardSet.get(n).getObsolete())
					type = cardSet.get(n).getT() + ">0";
				else if (cardSet.get(n).getPriority())
					type = cardSet.get(n).getT() + ">!";
				osw.write((type + "<"));
				osw.write((cardSet.get(n).getQ() + "<"));
				osw.write((cardSet.get(n).getA() + "<"));
			}
			if (cardSets.contains(x))
				cardSets.remove(x);
			cardSets.add(0, x);
			osw.flush();
			osw.close();

			osw = new OutputStreamWriter(openFileOutput("setlists.txt", Context.MODE_PRIVATE));
			for (int n = 0; n < cardSets.size(); n++) {
				osw.write((cardSets.get(n) + "<"));
			}
			osw.flush();
			osw.close();
			if (prompt)
				messageDialog(getString(R.string.save_success_1) + " " + x + " " + getString(R.string.save_success_2));
		} catch (IOException e) {
			messageDialog(getString(R.string.save_fail));
		}
	}

	// DISPLAY METHODS
	public void refreshDisplay() {
		if (frontPage) {
			display.setTextSize(getResources().getDimension(R.dimen.title_text_size));
			display.setText(R.string.title_page);
		} else {
			display.setTextSize(getResources().getDimension(R.dimen.text_size));
			String q = currentCardSet.get(num).getQ();
			String a = currentCardSet.get(num).getA();
			if (askAnswers) {
				q = currentCardSet.get(num).getA();
				a = currentCardSet.get(num).getQ();
			}
			if (answer) { // If display is set to answer mode the question and
							// answer of the card will be displayed and the
							// centre button will be set to next card
				next.setText(R.string.next_sub_button);
				display.setText("Q) " + q + "\n\nA) " + a + " ");
			} else { // If display is not set to answer mode only question of
						// card will be displayed and centre button will be set
						// to answer which displays the answer
				display.setText("Q) " + q + " ");
				next.setText(R.string.answer_sub_button);
			}
		}
		// updateCategories();
	}

	public void loadFrontPage() {
		num = 0;
		frontPage = true;
		next.setText(R.string.first_sub_button);
		prev.setEnabled(false);
		skip.setEnabled(false);
		refreshDisplay();
	}

	// DIALOGS
	public void messageDialog(String msg) {
		Intent intent = new Intent("wertze.android.flashcardz.MESSAGEDIALOG");
		Bundle b = new Bundle();
		b.putString("Message", msg); // Message ID
		intent.putExtras(b);
		startActivity(intent);
	}

	public void promptDialog(String msg, int requestCode) {
		Intent intent = new Intent("wertze.android.flashcardz.PROMPTDIALOG");
		Bundle b = new Bundle();
		b.putString("Message", msg); // Message ID
		intent.putExtras(b);
		startActivityForResult(intent, requestCode);
		// RETURN BOOLEAN INPUT
	}

	public void inputDialog(String msg, String def, int requestCode) {
		Intent intent = new Intent("wertze.android.flashcardz.INPUTDIALOG");
		Bundle b = new Bundle();
		b.putString("Message", msg); // Message ID
		b.putString("Default", def); // Default Input ID
		intent.putExtras(b);
		startActivityForResult(intent, requestCode);
		// RETURN INPUT
	}

	public void spinnerDialog(String msg, ArrayList<String> options, int requestCode) {
		Intent intent = new Intent("wertze.android.flashcardz.SPINNERDIALOG");
		Bundle b = new Bundle();
		b.putString("Message", msg); // Message ID
		b.putStringArrayList("Options", options); // Options Array ID
		intent.putExtras(b);
		startActivityForResult(intent, requestCode);
		// RETURN INPUT
	}

	public void addCardDialog(String msg, String def) { // Return Card
		Intent intent = new Intent("wertze.android.flashcardz.ADDCARDDIALOG");
		Bundle b = new Bundle();
		b.putString("Message", msg); // Message ID
		b.putString("Default", def); // Default Card Category ID
		intent.putExtras(b);
		startActivityForResult(intent, ADD_CARD);
		// RETURN INPUT
	}

	public void changeCardDialog(String msg, Card c) { // Return Card
		Intent intent = new Intent("wertze.android.flashcardz.CHANGECARDDIALOG");
		Bundle b = new Bundle();
		b.putString("Message", msg); // Message ID
		b.putString("DefaultQ", c.getQ()); // Default Q ID
		b.putString("DefaultA", c.getA()); // Default A ID
		b.putString("DefaultCat", c.getT()); // Default Cat ID
		intent.putExtras(b);
		startActivityForResult(intent, CHANGE_CARD);
		// RETURN INPUT
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case ADD_CARD:
			if (AddCardDialog.accept) {
				addCard(AddCardDialog.inputQ, AddCardDialog.inputA, AddCardDialog.inputCat);
			}
			break;
		case NEW_CARD_SET_NAME:
			if (InputDialog.accept)
				newCardSet(InputDialog.input);
			break;
		case DELETE_CARD_SET:
			if (PromptDialog.accept)
				deleteCurrentCardSet();
			break;
		case OPEN_CARD_SET:
			if (SpinnerDialog.accept) {
				loadCardSet(SpinnerDialog.input);
				loadFrontPage();
			}
			break;
		case IMPORT_CARD_SET:
			if (InputDialog.accept) {
				String s = InputDialog.input;
				importCardSet(s);
			}
			break;
		case IMPORT_CARD_SET_2:
			String s = "DEFAULT";
			if (InputDialog.accept)
				s = InputDialog.input;
			cardSetName = s;
			saveCardSet(s, false);
			loadFrontPage();
			break;
		case OPTIONS_MENU:
			optionsMenuOptionSelected(ListMenu.inputIndex);
			break;
		case FILTER_MENU:
			if (FilterMenu.selectCategories.remove(getString(R.string.priority))) {
				priorityOnly = true;
			} else
				priorityOnly = false;
			selectCategories = new HashSet<String>(FilterMenu.selectCategories);
			if (selectCategories.contains(getString(R.string.none))) {
				selectCategories.remove(getString(R.string.none));
				selectCategories.add("");
			}
			updateFilters();
			break;
		case CHANGE_CARD:
			if (ChangeCardDialog.accept)
				modifyCurrentCard(ChangeCardDialog.inputQ, ChangeCardDialog.inputA, ChangeCardDialog.inputCat);
		}
	}

	// BUTTON METHODS
	public void firstCard() {
		if (currentCardSet == null || currentCardSet.isEmpty())
			return;
		prev.setEnabled(true);
		skip.setEnabled(true);
		num = 0;
		answer = false;
		frontPage = false;
		refreshDisplay();
	}

	public void prevCard() {
		num--;
		if (num < 0)
			num = (currentCardSet.size() - 1);
		if (num == currentCardSet.size())
			num = 0;
		answer = false;
		refreshDisplay();
	}

	public void addCard(String q, String a, String cat) {
		currentCategory = cat;
		int n = cardSet.size();
		Card card = new Card(q, a, n, cat);
		cardSet.add(card);
		if (!categories.contains(cat)) {
			categories.add(cat);
			selectCategories.add(cat);
		}
		if (selectCategories.contains(cat)) {
			currentCardSet.add(card);
		}
		saveCardSet(cardSetName, false);
	}

	public void nextCard() {
		num++;
		if (num == currentCardSet.size())
			num = 0;
		answer = false;
		refreshDisplay();
	}

	public void answer() {
		answer = true;
		refreshDisplay();
	}

	// OPTIONS MENU
	public void initializeOptionsMenu() {
		menuOptions = new String[NUM_OPTIONS];
		menuOptions[FLIP] = getString(R.string.flip);
		menuOptions[SHUFFLE] = getString(R.string.shuffle);
		menuOptions[UNSHUFFLE] = getString(R.string.unshuffle);
		menuOptions[ORDER_ABC] = getString(R.string.order_abc);
		menuOptions[MODIFY] = getString(R.string.modify);
		menuOptions[DELETE] = getString(R.string.delete);
		menuOptions[COUNT] = getString(R.string.count);
		menuOptions[FILTERS] = getString(R.string.filters);
		menuOptions[IMPORT_SET] = getString(R.string.import_set);
		menuOptions[PRIORITIZE] = getString(R.string.prioritize);
		menuOptions[CANCEL] = getString(R.string.cancel);
	}

	public void loadOptionsMenu() {
		if (currentCardSet.size() != 0) {
			if (currentCardSet.get(num).getPriority()) {
				menuOptions[PRIORITIZE] = getString(R.string.deprioritize);
				return;
			}
		}
		menuOptions[PRIORITIZE] = getString(R.string.prioritize);
	}

	public void optionsMenu() {
		loadOptionsMenu();
		Intent intent = new Intent("wertze.android.flashcardz.LISTMENU");
		Bundle b = new Bundle();
		b.putStringArray("Options", menuOptions);
		intent.putExtras(b);
		startActivityForResult(intent, OPTIONS_MENU);
	}

	public void optionsMenuOptionSelected(int index) {
		switch (index) {
		case FLIP: // "Flip Questions/Answers"
			flipQandA();
			return;
		case SHUFFLE: // "Shuffle"
			shuffle();
			return;
		case UNSHUFFLE: // "Unshuffle"
			unshuffle();
			return;
		case ORDER_ABC: // "Order Alphabetically"
			orderAlphabetically();
			return;
		case MODIFY: // "Modify Card"
			if (frontPage)
				return;
			changeCardDialog(getString(R.string.change_card_text), currentCardSet.get(num));
			return;
		case DELETE: // "Delete Card"
			if (frontPage)
				return;
			deleteCurrentCard();
			return;
		case COUNT: // "Card Count"
			displayCardCount();
			return;
		case FILTERS: // "Filter Categories"
			filterMenu();
			return;
		case CANCEL: // "Cancel"
			return;
		case IMPORT_SET: // "Import Card Set"
			inputDialog(getString(R.string.import_card_text), "", IMPORT_CARD_SET);
			return;
		case PRIORITIZE: // "(De)Prioritze Current Card"
			if (frontPage)
				return;
			prioritizeCurrentCard();
			return;
		}
	}

	public void prioritizeCurrentCard() {
		currentCardSet.get(num).flipPriority();
		saveCardSet(cardSetName, false);
		if (priorityOnly)
			updateFilters();
	}

	public void modifyCurrentCard(String q, String a, String cat) {
		currentCardSet.get(num).setQ(q);
		currentCardSet.get(num).setA(a);
		currentCardSet.get(num).setT(cat);

		saveCardSet(cardSetName, false);
		refreshDisplay();
	}

	public void deleteCurrentCard() {
		cardSet.remove(currentCardSet.get(num));
		currentCardSet.remove(currentCardSet.get(num));
		num--;
		if (num < 0)
			num = 0;
		refreshDisplay();
		saveCardSet(cardSetName, false);
	}

	public void displayCardCount() {
		String s = getString(R.string.current_cards_num1) + ":";
		s = s + "\n" + getString(R.string.current_cards_num2) + ": " + currentCardSet.size();
		s = s + "\n" + getString(R.string.current_cards_num3) + ": " + cardSet.size();
		messageDialog(s);
	}

	public void shuffle() {
		Collections.shuffle(currentCardSet);
		loadFrontPage();
	}

	public void unshuffle() {
		selectCategories = new HashSet<String>(categories);
		currentCardSet = new ArrayList<Card>(cardSet);
		loadFrontPage();
	}

	public void orderAlphabetically() {
		// Bubble Sort cards alphabetically (lexicographically)
		for (int k = 0; k < currentCardSet.size(); k++) {
			for (int i = 0; i < (currentCardSet.size() - 1); i++) {
				if (currentCardSet.get(i).compareCards(askAnswers, currentCardSet.get(i + 1))) {
					Card c = currentCardSet.get(i);
					currentCardSet.set(i, currentCardSet.get(i + 1));
					currentCardSet.set((i + 1), c);
				}
			}
		}
		loadFrontPage();
	}

	public void flipQandA() {
		askAnswers = !askAnswers;
		refreshDisplay();
	}

	public void updateFilters() {
		currentCardSet = new ArrayList<Card>();
		for (int i = 0; i < cardSet.size(); i++) {
			Card card = cardSet.get(i);
			if (selectCategories.contains(card.getT())) {
				if (card.getPriority() || !priorityOnly)
					currentCardSet.add(card);
			}
		}
		loadFrontPage();
	}

	public void filterMenu() {
		Intent intent = new Intent("wertze.android.flashcardz.FILTERMENU");
		Bundle b = new Bundle();
		ArrayList<String> options = new ArrayList<String>();
		options.add(getString(R.string.done));
		options.add(getString(R.string.priority));
		for (String s : categories) {
			if (s.equals(""))
				s = getString(R.string.none);
			options.add(s);
		}
		b.putStringArrayList("Options", options);
		intent.putExtras(b);
		startActivityForResult(intent, FILTER_MENU);
	}

	// MENU
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_set:
			inputDialog(getString(R.string.new_card_set_name), "", NEW_CARD_SET_NAME);
			// ==> newCardSet(inputDialogResult);
			return true;
		case R.id.open_set:
			spinnerDialog(getString(R.string.open_card_set), cardSets, OPEN_CARD_SET);
			// ==> loadCardSet(spinnerDialogResult);
			return true;
		case R.id.delete_set:
			promptDialog(getString(R.string.delete_card_set), DELETE_CARD_SET);
			// ==> saveCardSet(inputDialogResult);
			return true;
		case R.id.options:
			optionsMenu();
			return true;
		}
		return false;
	}
}
