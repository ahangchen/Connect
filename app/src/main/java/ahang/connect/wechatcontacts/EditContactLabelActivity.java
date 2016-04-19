package ahang.connect.wechatcontacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ahang.connect.common.utils.ActivityUtils;
import ahang.connect.common.utils.BundleNames;
import ahang.connect.common.utils.database.LabelTableUtils;
import ahang.connect.adapter.LabelsAdapter;
import ahang.connect.object.ActivityConstants;
import ahang.connect.object.IdObj;
import ahang.connect.object.LabelListObj;
import ahang.connect.object.LabelObj;
import ahang.connect.object.Signal;


public class EditContactLabelActivity extends Activity implements View.OnClickListener {

	TextView labelsBack;
	ListView labelList;
	TextView tvSure;

	LabelTableUtils labelTableUtils;

	IdObj contact;
	LabelListObj selectedLabels;


	int fromActivity;

	public int getFromActivity() {
		return fromActivity;
	}

	public void setFromActivity(int fromActivity) {
		this.fromActivity = fromActivity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_contact_label);

		init();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initList();
	}

	void initDatabase() {
		labelTableUtils = new LabelTableUtils(this);
	}

	void init() {
		intentCheck();
		initDatabase();
		findView();
		setListener();
	}

	void intentCheck() {
		//AddContact, ContactInfo
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		Signal signal = (Signal) bundle.getSerializable(Signal.NAME);
		setFromActivity(signal.getFrom());
		if (fromActivity == ActivityConstants.CONTACT_INFO_ACTIVITY) {
			contact = (IdObj) bundle.getSerializable(BundleNames.ID_OBJ);
		} else if (fromActivity == ActivityConstants.ADD_CONTACTS_ACTIVITY) {
			selectedLabels = new LabelListObj();
		}
	}

	void setListener() {

		labelsBack.setOnClickListener(this);
		tvSure.setOnClickListener(this);
	}

	void findView() {
		labelList = (ListView) findViewById(R.id.label_list);
		labelsBack = (TextView) findViewById(R.id.cancel);
//		existsLabels = (GridView) findViewById(R.id.exists_labels);
		tvSure = (TextView) findViewById(R.id.bt_sure);
//		if (fromActivity == ActivityConstants.CONTACT_INFO_ACTIVITY)
//			existsLabels.setVisibility(View.VISIBLE);
//		else
//			existsLabels.setVisibility(View.GONE);
	}

	void initList() {
		List<LabelObj> labels = labelTableUtils.selectAll();
		LabelsAdapter labelsAdapter = new LabelsAdapter(this, labels, getFromActivity(), selectedLabels);
		labelList.setAdapter(labelsAdapter);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.cancel:
				setEmptyResult();
				finish();
				break;
			case R.id.bt_sure:
				if (getFromActivity() == ActivityConstants.ADD_CONTACTS_ACTIVITY)
					ActivityUtils.setActivityResult
							(this, ActivityConstants.REQUEST_CODE_LABEL, BundleNames.LABEL_LIST,
									selectedLabels);
				//on edit contacts, change database in this activity,
				//so when back to edit contact activity, refresh database isn't needed;
				// but on new contacts, only change string list in this activity
				// so when back to add contact activity, refresh database is needed;
				finish();
				break;
		}
	}

//	protected void onPause() {
//		super.onPause();
//		//setEmptyResult();
////		CursorUtils.closeExistsCursor(cursorLabels);
////		CursorUtils.closeExistsCursor(cursorExistsLabels);
//		labelTableUtils.closeDataBase();
////		allTableUtils.closeDataBase();
//	}
//
//	protected void onStop() {
//		super.onStop();
//		//setEmptyResult();
////		CursorUtils.closeExistsCursor(cursorLabels);
////		CursorUtils.closeExistsCursor(cursorExistsLabels);
//		labelTableUtils.closeDataBase();
////		allTableUtils.closeDataBase();
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		//setEmptyResult();
////		CursorUtils.closeExistsCursor(cursorLabels);
////		CursorUtils.closeExistsCursor(cursorExistsLabels);
//		labelTableUtils.closeDataBase();
////		allTableUtils.closeDataBase();
//	}

	private void setEmptyResult() {
		selectedLabels.removeAllMember();
		if (getFromActivity() == ActivityConstants.ADD_CONTACTS_ACTIVITY)
			ActivityUtils.setActivityResult
					(this, ActivityConstants.REQUEST_CODE_LABEL, BundleNames.LABEL_LIST,
							selectedLabels);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			setEmptyResult();
			super.onKeyDown(keyCode, event);
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}
}