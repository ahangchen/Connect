package ahang.connect.wechatcontacts;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ahang.connect.common.utils.ActivityUtils;
import ahang.connect.common.utils.BitmapUtils;
import ahang.connect.common.utils.BundleNames;
import ahang.connect.common.utils.Log;
import ahang.connect.common.utils.StringUtils;
import ahang.connect.common.utils.database.AllTableUtils;
import ahang.connect.common.utils.database.IDLabelTableUtils;
import ahang.connect.common.utils.database.LabelTableUtils;
import ahang.connect.common.utils.popup.PopConfirmUtils;
import ahang.connect.common.utils.popup.PopEditLabelUtils;
import ahang.connect.common.utils.popup.TodoOnResult;
import ahang.connect.adapter.ContactsAdapter;
import ahang.connect.adapter.MemEditAdapter;
import ahang.connect.object.ActivityConstants;
import ahang.connect.object.IdObj;
import ahang.connect.object.LabelObj;
import ahang.connect.object.LightIdObj;
import ahang.connect.object.Signal;


public class LabelDetailActivity extends Activity implements View.OnClickListener {
	final static String TAG = "labelDetail";
	LabelObj labelObj;
	boolean inEdit = false;

	AllTableUtils allTableUtils;
	LabelTableUtils labelTableUtils;
	IDLabelTableUtils idLabelTableUtils;
	List<LightIdObj> members;
	List<LightIdObj> contactList;

	TextView labelsBack;
	TextView editLabel;

	ImageView labelIcon;
	TextView labelName;
	TextView memberCount;

	LinearLayout addMemberLayout;
	Button addMember;

	ListView labelMembers;

	Button deleteLabel;

	PopEditLabelUtils popEditLabelUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_label_detail);
		init();

	}

	@Override
	protected void onResume() {
		super.onResume();
		checkState();
	}

	void init() {
		getLabelFromBundle();
		initDataBase();
		findView();
		initComponent();
	}

	void getLabelFromBundle() {
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			labelObj = (LabelObj) bundle.getSerializable(BundleNames.LABEL_OBJ);
		} else {
			Log.e(TAG, "Bundle null");
			finish();
		}
	}

	void initDataBase() {
		allTableUtils = new AllTableUtils(this);
		labelTableUtils = new LabelTableUtils(this);
		idLabelTableUtils = new IDLabelTableUtils(this);
	}

	void initComponent() {
		labelName.setText(labelObj.getLabelName());
		labelIcon.setBackgroundDrawable(null);
		if (labelObj.getIconPath().equals("")) {
			labelIcon.setBackgroundResource(R.drawable.label50);
		} else {
			labelIcon.setImageBitmap(BitmapUtils.decodeBitmapFromPath(labelObj.getIconPath()));
		}
		memberCount.setText(StringUtils.addBrackets(labelObj.getMemCount() + ""));
		popEditLabelUtils = new PopEditLabelUtils();
		popEditLabelUtils.initPopAddLabel(this, new TodoOnResult() {
			@Override
			public void doOnPosResult(String[] params) {
				//add label id
				if (!params[0].equals("")) {
					labelObj.setLabelName(params[0]);
					labelObj.setIconPath(params[1]);

					long state = labelTableUtils.updateAllWithLabel(labelObj, labelName.getText().toString());
					if (state < 0) Log.e("LabelsActivity", "update label failed");
					labelIcon.setImageBitmap(BitmapUtils.decodeBitmapFromPath(labelObj.getIconPath()));
					labelName.setText(labelObj.getLabelName());
					popEditLabelUtils.dismissWindow();
				} else {
					Toast.makeText(LabelDetailActivity.this, "标签名不能为空", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void doOnNegResult(String[] params) {

			}
		}, "编辑标签", R.id.label_detail_layout, labelObj);
		findViewById(R.id.title).setEnabled(true);
		setListener();

	}

	void checkState() {
		int viewState;
		if (inEdit) {
			initEditList();
			viewState = View.VISIBLE;
			editLabel.setText("Save");
		} else {
			saveEditResult();
			initViewList();
			viewState = View.GONE;
			editLabel.setText("Edit");
		}
		addMemberLayout.setVisibility(viewState);
		deleteLabel.setVisibility(viewState);

	}

	void setListener() {
		labelsBack.setOnClickListener(this);
		editLabel.setOnClickListener(this);
		deleteLabel.setOnClickListener(this);
		addMember.setOnClickListener(this);
		labelIcon.setOnClickListener(this);
		labelName.setOnClickListener(this);
		addMemberLayout.setOnClickListener(this);

	}

	void saveEditResult() {

	}

	void findView() {
		labelsBack = (TextView) findViewById(R.id.labels_back);
		editLabel = (TextView) findViewById(R.id.edit_label);
		labelIcon = (ImageView) findViewById(R.id.label_icon);
		labelName = (TextView) findViewById(R.id.label_name);
		memberCount = (TextView) findViewById(R.id.member_count);
		addMemberLayout = (LinearLayout) findViewById(R.id.add_member_layout);
		addMember = (Button) findViewById(R.id.add_member);
		labelMembers = (ListView) findViewById(R.id.member_list);
		deleteLabel = (Button) findViewById(R.id.delete_label);
		checkState();
	}

	void initViewList() {
		contactList = allTableUtils.selectLightIdObjOnLabel(labelObj.getLabelName());
		ContactsAdapter adapter = new ContactsAdapter(this, contactList);
		labelMembers.setAdapter(adapter);

	}

	void initEditList() {
		getLabelMemberCount();
		members = allTableUtils.selectLightIdObjOnLabel(labelObj.getLabelName());
		MemEditAdapter adapter = new MemEditAdapter(this, labelObj.getLabelName(), members);
		labelMembers.setAdapter(adapter);

	}

	private void getLabelMemberCount() {
		String count = labelTableUtils.selectMemCount(labelObj.getLabelName()) + "";
		memberCount.setText(StringUtils.addBrackets(count));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.labels_back:

				finish();
				break;
			case R.id.edit_label:
				inEdit = !inEdit;
				checkState();
				break;
			case R.id.add_member:
			case R.id.add_member_layout:

				ActivityUtils.startActivityWithObjectForResult
						(this, SearchActivity.class, Signal.NAME,
								new Signal(ActivityConstants.LABEL_DETAIL_ACTIVITY, ActivityConstants.SEARCH_ACTIVITY),
								ActivityConstants.RESULT_ADD_MEMBER);
				break;

			case R.id.delete_label:
				PopConfirmUtils popConfirmUtils = new PopConfirmUtils();
				popConfirmUtils.prepare(this, R.layout.pop_confirm);
				popConfirmUtils.initPopupWindow();
				popConfirmUtils.setTitle("Sure to delete?");
				popConfirmUtils.initTodo(new TodoOnResult() {
					@Override
					public void doOnPosResult(String[] params) {
						labelTableUtils.deleteWithLabel(labelObj.getLabelName());
						getLabelMemberCount();
						finish();
					}

					@Override
					public void doOnNegResult(String[] params) {

					}
				});
				popConfirmUtils.popWindowAtCenter(R.id.member_list, R.id.confirm_title);
				break;

			case R.id.label_icon:
			case R.id.label_name:
				popEditLabelUtils.popEditLabel();
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case ActivityConstants.RESULT_LOAD_IMAGE:
				popEditLabelUtils.handleResult(requestCode, resultCode, data);
				break;
			case ActivityConstants.RESULT_ADD_MEMBER:
				Bundle bundle = data.getExtras();
				int id = ((IdObj) bundle.getSerializable(BundleNames.ID_OBJ)).getId();
				if (id != 0) {
					long status = idLabelTableUtils.insertAll("" + id, labelObj.getLabelName());
					if (status < 0)
						Toast.makeText(this, "添加联系人标签失败，\n是否已经在标签中？", Toast.LENGTH_LONG).show();
					initEditList();
				}
				break;
		}

	}

//	protected void onPause() {
//		super.onPause();
////		CursorUtils.closeExistsCursor(cursorEdit);
////		CursorUtils.closeExistsCursor(cursorView);
//		allTableUtils.closeDataBase();
//
//	}
//
//	protected void onStop() {
//		super.onStop();
////		CursorUtils.closeExistsCursor(cursorEdit);
////		CursorUtils.closeExistsCursor(cursorView);
//		allTableUtils.closeDataBase();
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
////		CursorUtils.closeExistsCursor(cursorEdit);
////		CursorUtils.closeExistsCursor(cursorView);
//		allTableUtils.closeDataBase();
//	}

}
