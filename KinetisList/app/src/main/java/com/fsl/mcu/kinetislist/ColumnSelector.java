package com.fsl.mcu.kinetislist;

import com.fsl.mcu.kinetislist.R;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.TransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ColumnSelector extends Activity {
    private static final String TAG = "ColumnSelector ListActivity";
	private String nowFamily;
	private String selectColumn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.column_selector);

		// Get the parameters from the previous activity.
        final Intent intent = getIntent();
        nowFamily = intent.getExtras().getString("Family");
        selectColumn = intent.getExtras().getString("Column");
	
		try {
			DeviceList.addTask(DBdefine.MSG.MSG_TASK_LIST_COLUMN_VALUES, null, true, nowFamily, selectColumn,null);
		} catch (InterruptedException e) {
			Toast.makeText(this, "Can not get data from the database.", Toast.LENGTH_LONG).show();
		//	e.printStackTrace();
		}

		final int nValues = DeviceList.valueList.size();
		/** This records the changes of each value. Used to compare what are changed when press "OK". */
		final boolean[] valueSelection = new boolean[nValues];
		/** This records the initial setting of each value. Used to compare what are changed when press "OK". */
		final boolean[] dbSelection = new boolean[nValues];
		final CheckBox[] checkBox = new CheckBox[nValues];
		final int BASEBOXID = getResources().getInteger(R.integer.ParamFilterBoxID);

		String columnName = intent.getExtras().getString("Name");
		TextView title = (TextView)findViewById(R.id.selector_title);
		title.setText("Items of \""+columnName+"\"");
		OnCheckedChangeListener checkBox_Listener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				int checkedId = buttonView.getId()-BASEBOXID;
				valueSelection[checkedId] = isChecked;
			}
		};
		
		TableLayout table = (TableLayout)findViewById(R.id.selector_table);
		final CheckBox[] checks = new CheckBox[nValues];
		CheckBox checkbox;
		String strText;
		DBdefine.ColumnValue cValue;
		for (int iBox=0; iBox < nValues; iBox++) {
			LayoutInflater inflater = LayoutInflater.from(this);
			LinearLayout llv = (LinearLayout)inflater.inflate(R.layout.selector_line, table, true);

			checkbox = (CheckBox)findViewById(R.id.line_text);
			checkbox.setId(BASEBOXID+iBox);
			cValue = DeviceList.valueList.get(iBox);
			strText = cValue.Text;
			checkbox.setTag(strText);
			if (strText.equals(String.valueOf("-1")))
				strText = "-";
			checkbox.setText(strText);
			checkbox.setOnCheckedChangeListener(checkBox_Listener);
			valueSelection[iBox] = dbSelection[iBox] = cValue.Visible;
			checkbox.setChecked(valueSelection[iBox]);
			
			checks[iBox] = checkbox;
		}
		
		Button btn_sel = (Button)findViewById(R.id.select_allitem);
		btn_sel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int ibox = 0; ibox < nValues; ibox++) {
					checks[ibox].setChecked(true);
				}
			}
		});
		
		Button btn_desell = (Button)findViewById(R.id.deselect_allitem);
		btn_desell.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int ibox = 0; ibox < nValues; ibox++) {
					checks[ibox].setChecked(false);
				}
			}
		});
		
		Button btn_cancel = (Button)findViewById(R.id.choose_item_cancel);
		btn_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		Button btn_done = (Button)findViewById(R.id.choose_item_ok);
		btn_done.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean refresh = evaluateChanges();
				if (refresh) {				
					try {
						DeviceList.addTask(DBdefine.MSG.MSG_TASK_CHANGE_COLUMN_VALUES, null, true, selectColumn, null, null);
					} catch (InterruptedException e) {
						Toast.makeText(ColumnSelector.this, "Can not change data from the database.", Toast.LENGTH_LONG).show();
						return;
					}
					ChartActivity.InvalideTable();
				}
				finish();
			}
			
			private boolean evaluateChanges() {
				boolean refresh = false;
				DBdefine.ColumnValue cv;
				for (int iBox=0; iBox < nValues; iBox++) {
					cv = DeviceList.valueList.get(iBox);
					if (valueSelection[iBox] != dbSelection[iBox]) {
						cv.Visible = valueSelection[iBox];	// The new state
						DeviceList.valueList.set(iBox, cv);	// Change it
						refresh = true;
					}
					else {
						cv.Text = null;
						DeviceList.valueList.set(iBox, cv);	// Dummy remove it
					}
				}
				return refresh;
			}
		});
	}
}
