package ahang.connect.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ahang.connect.common.utils.ActivityUtils;
import ahang.connect.common.utils.BundleNames;
import ahang.connect.common.utils.StringUtils;
import ahang.connect.object.ActivityConstants;
import ahang.connect.object.IdObj;
import ahang.connect.object.SearchObj;
import ahang.connect.wechatcontacts.ContactInfoActivity;
import ahang.connect.wechatcontacts.R;

public class SearchResultAdapter extends BaseAdapter {
	private Context context;
	private List<SearchObj> results;
	private String keyword;
	private int fromActivity;
	private LayoutInflater inflater;

	public SearchResultAdapter(Context context, List<SearchObj> results, String keyword, int fromActivity) {
		this.context = context;
		this.results = results;
		this.keyword = keyword.toLowerCase();
		this.fromActivity = fromActivity;
		inflater = LayoutInflater.from(context);
	}


	@Override
	public int getCount() {
		return results.size();
	}

	@Override
	public Object getItem(int position) {
		return results.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		TextView tvName;
		TextView searchDetail;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SearchObj result = results.get(position);
		final String id = result.getId();
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.cell_search_list, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) convertView.findViewById(R.id.name);
			holder.searchDetail = (TextView) convertView.findViewById(R.id.searched_detail);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}


//		String name = result.getName();
		String recoverName = StringUtils.recoverWordFromDB(result.getName());
		if (!StringUtils.containChinese(recoverName)) {

			holder.tvName.setText(StringUtils.simpleHighLight(keyword.replace(" ", ""), recoverName, "A4005B", "636362"));
		} else {
			if (!StringUtils.isNumber(keyword))
				holder.tvName.setText(StringUtils.simpleHighLightByPinyin(keyword, recoverName, "A4005B", "636362"));
			else
				holder.tvName.setText(StringUtils.simpleHighLight(keyword.replace(" ", ""), recoverName, "A4005B", "636362"));
		}
		holder.searchDetail.setText(getFirstMatchInfo(position, keyword.replace(" ", "")));
		//holder.searchDetail.setMovementMethod(ScrollingMovementMethod.getInstance());
		if (holder.searchDetail.getText().toString().equals(""))
			holder.searchDetail.setVisibility(View.GONE);

		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IdObj obj = new IdObj(Integer.parseInt(id));
				switch (fromActivity) {
					case ActivityConstants.MAIN_ACTIVITY:
						ActivityUtils.ActivitySkipWithObject(context, ContactInfoActivity.class, BundleNames.ID_OBJ, obj);
						break;
					case ActivityConstants.LABEL_DETAIL_ACTIVITY:
						ActivityUtils.setActivityResult(context, ActivityConstants.RESULT_ADD_MEMBER, BundleNames.ID_OBJ, obj);
						((Activity) context).finish();
						break;
					default:
						ActivityUtils.ActivitySkipWithObject(context, ContactInfoActivity.class, BundleNames.ID_OBJ, obj);
						break;
				}

			}
		});
		return convertView;
	}

	private Spanned getFirstMatchInfo(int position, String keyword) {
		SearchObj result = results.get(position);
		String matchInfo = "";
		if (StringUtils.isNumber(keyword)) {
			matchInfo = result.getTel();
			if (matchInfo != null)
				if (matchInfo.toLowerCase().contains(keyword)) {
					matchInfo = "Tel:" + matchInfo;
					return StringUtils.simpleHighLight(keyword, matchInfo, "A4005B", "636362");
				}
		}
		matchInfo = result.getLabel();
		if (matchInfo != null) {
			if (matchInfo.toLowerCase().contains(keyword)) {
				matchInfo = "Label:" + matchInfo;
				return StringUtils.simpleHighLight(keyword, matchInfo, "A4005B", "636362");
			}
		}
		matchInfo = result.getAddress();
		if (matchInfo.toLowerCase().contains(keyword)) {
//			if (matchInfo.length()>StringUtils.SHOW_LENGTH)
//				matchInfo = StringUtils.cutString(matchInfo,keyword,StringUtils.SHOW_LENGTH);
			matchInfo = "Address:" + matchInfo;
			return StringUtils.simpleHighLight(keyword, matchInfo, "A4005B", "636362");
		}
		matchInfo = result.getNote();
		if (matchInfo.toLowerCase().contains(keyword)) {
//			if (matchInfo.length()>StringUtils.SHOW_LENGTH)
//				matchInfo = StringUtils.cutString(matchInfo,keyword,StringUtils.SHOW_LENGTH);
			matchInfo = "Notes:" + matchInfo;
			return StringUtils.simpleHighLight(keyword, matchInfo, "A4005B", "636362");
		}

		return Html.fromHtml("");
	}
}
