package ahang.connect.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ahang.connect.common.utils.ActivityUtils;
import ahang.connect.common.utils.BundleNames;
import ahang.connect.common.utils.database.TBMainConstants;
import ahang.connect.common.utils.database.TBTelConstants;
import ahang.connect.object.IdObj;
import ahang.connect.wechatcontacts.ContactInfoActivity;
import ahang.connect.wechatcontacts.R;


public class BigContactsAdapter extends BaseAdapter {
	private Context context;
	private Cursor cursor;
	private LinearLayout layout;
	PopupWindow telChoiceWindow = null;
	View telChoiceView = null;

	public BigContactsAdapter(Context context, Cursor cursor) {
		this.context = context;
		this.cursor = cursor;

	}

	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public Object getItem(int position) {
		return cursor.getPosition();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		layout = (LinearLayout) inflater.inflate(R.layout.cell_search_list, null);

		TextView name = (TextView) layout.findViewById(R.id.name);


		cursor.moveToPosition(position);

		final String cName = cursor.getString(cursor.getColumnIndex(TBMainConstants.NAME));
		final String id = cursor.getString(cursor.getColumnIndex(TBMainConstants.ID));

		//more details got when view
		//....

		name.setText(cName);


		name.setClickable(true);
		name.setFocusable(true);
		name.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onItemClick(Integer.parseInt(id));
			}
		});
		return layout;
	}

	private void onItemClick(int id) {
		IdObj obj = new IdObj(id);
		ActivityUtils.ActivitySkipWithObject(context, ContactInfoActivity.class, BundleNames.ID_OBJ, obj);
	}

	public void popPhoneSelector() {
		telChoiceWindow.update();
		telChoiceWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
		Animation anim1 = AnimationUtils.loadAnimation(context, R.anim.scale_center_enter);
		telChoiceView.findViewById(R.id.phone_choices).startAnimation(anim1);
	}

	private void initPopChoice(int telOrMsg, Cursor cursor) {
		telChoiceView = LayoutInflater.from(context).inflate(
				R.layout.pop_phone_choice, null, false);
		telChoiceWindow = new PopupWindow(telChoiceView,
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

		ColorDrawable dw = new ColorDrawable(0x00000000);
		telChoiceView.setBackgroundDrawable(dw);

		TextView telsTitle = (TextView) telChoiceView.findViewById(R.id.list_title);
		if (TelsAdapter.CHOICE_MSG == telOrMsg) telsTitle.setText("Message");
		else if (TelsAdapter.CHOICE_CALL == telOrMsg) telsTitle.setText("Call");

		ListView telList = (ListView) telChoiceView.findViewById(R.id.phone_list);
		List<String> tels = new ArrayList<>();
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			tels.add(cursor.getString(cursor.getColumnIndex(TBTelConstants.TEL)));
		}
		cursor.close();
		TelsAdapter telsAdapter = new TelsAdapter(context, telOrMsg, telChoiceWindow, tels);
		telList.setAdapter(telsAdapter);

		telChoiceView.setFocusable(true);
		telChoiceView.setFocusableInTouchMode(true);
		telChoiceView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				if (arg1 == KeyEvent.KEYCODE_BACK) {
					telChoiceWindow.dismiss();
					return true;
				}
				return false;
			}
		});
		telChoiceView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				telChoiceWindow.dismiss();
			}

		});

		telChoiceWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {

			}

		});

	}
}
