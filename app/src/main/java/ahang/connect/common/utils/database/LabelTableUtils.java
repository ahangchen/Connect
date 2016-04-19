package ahang.connect.common.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ahang.connect.common.utils.StringUtils;
import ahang.connect.object.LabelObj;


public class LabelTableUtils {
	private static DataBaseHelper dataBaseHelper;
	SQLiteDatabase db;

	public LabelTableUtils(Context context) {
		dataBaseHelper = new DataBaseHelper(context);
		//insert several data
		/*for (int i=0;i<10;i++){
		    insertAll("Friend"+i,""+i,""+i,"10086",i%3,"","");
        }*/
	}

	//insert
	public long insertAll(String label, String iconPath) {
		closeDataBase();
		label = StringUtils.splitChineseSingly(label);
		ContentValues value = new ContentValues();
		value.put(TBLabelConstants.LABEL, label);
		value.put(TBLabelConstants.LABEL_ICON, iconPath);
		value.put(TBLabelConstants.MEMBER_COUNT, 0 + "");
		db = dataBaseHelper.getWritableDatabase();
		long status;
		try {
			status = db.insert(TBLabelConstants.TABLE_NAME, null, value);
		} finally {
			db.close();
		}
		return status;
	}

	public long insertAll(LabelObj labelObj) {
		closeDataBase();
		labelObj.setLabelName(StringUtils.splitChineseSingly(labelObj.getLabelName()));
		ContentValues value = new ContentValues();
		value.put(TBLabelConstants.LABEL, labelObj.getLabelName());
		value.put(TBLabelConstants.LABEL_ICON, labelObj.getIconPath());
		value.put(TBLabelConstants.MEMBER_COUNT, 0 + "");
		db = dataBaseHelper.getWritableDatabase();
		long status;
		try {
			status = db.insert(TBLabelConstants.TABLE_NAME, null, value);
		} finally {
			db.close();
		}

		return status;
	}

	//delete
	public int deleteWithLabel(String label) {
		closeDataBase();
		db = dataBaseHelper.getWritableDatabase();
		label = StringUtils.splitChineseSingly(label);
		int status;
		try {
			status = db.delete(TBLabelConstants.TABLE_NAME,
					TBLabelConstants.LABEL + "=?", new String[]{label});
		} finally {
			db.close();
		}

		return status;
	}

	public long updateAllWithLabel(LabelObj labelObj, String onLabel) {
		closeDataBase();
		db = dataBaseHelper.getWritableDatabase();
		onLabel = StringUtils.splitChineseSingly(onLabel);
		labelObj.setLabelName(StringUtils.splitChineseSingly(labelObj.getLabelName()));
		ContentValues value = new ContentValues();
		value.put(TBLabelConstants.LABEL, labelObj.getLabelName());
		value.put(TBLabelConstants.LABEL_ICON, labelObj.getIconPath());
		value.put(TBLabelConstants.MEMBER_COUNT, labelObj.getMemCount());
		labelObj.setLabelName(StringUtils.recoverWordFromDB(labelObj.getLabelName()));
		long status;
		try {
			status = db.update(TBLabelConstants.TABLE_NAME, value,
					TBLabelConstants.LABEL + "=?", new String[]{onLabel});
		} finally {
			db.close();
		}

		return status;
	}

	//update ALL
	public long updateAllWithLabel(String label, String iconPath, String memCount,
	                               String onLabel) {
		closeDataBase();
		db = dataBaseHelper.getWritableDatabase();
		label = StringUtils.splitChineseSingly(label);
		onLabel = StringUtils.splitChineseSingly(onLabel);
		ContentValues value = new ContentValues();
		value.put(TBLabelConstants.LABEL, label);
		value.put(TBLabelConstants.LABEL_ICON, iconPath);
		value.put(TBLabelConstants.MEMBER_COUNT, memCount);
		long status;
		try {
			status = db.update(TBLabelConstants.TABLE_NAME, value,
					TBLabelConstants.LABEL + "=?", new String[]{onLabel});
		} finally {
			db.close();
		}


		return status;
	}

	public long updateMemCountWithLabel(String memCount,
	                                    String onLabel) {
		closeDataBase();
		db = dataBaseHelper.getWritableDatabase();
		onLabel = StringUtils.splitChineseSingly(onLabel);
		ContentValues value = new ContentValues();
		value.put(TBLabelConstants.LABEL, memCount);
		long status;
		try {
			status = db.update(TBLabelConstants.TABLE_NAME, value,
					TBLabelConstants.LABEL + "=?", new String[]{onLabel});
		} finally {
			db.close();
		}


		return status;
	}

	public void addMemCountByName(int addNum, String byName) {
		closeDataBase();
		db = dataBaseHelper.getWritableDatabase();
		byName = StringUtils.splitChineseSingly(byName);
		db.execSQL("update " + TBLabelConstants.TABLE_NAME + " set " + TBLabelConstants.MEMBER_COUNT
				+ " = " + TBLabelConstants.MEMBER_COUNT + "+" + addNum +
				" where " + TBLabelConstants.LABEL + "= ?", new String[]{byName});
		db.close();
	}

	public void minusMemCountByName(int minusNum, String byName) {
		closeDataBase();
		db = dataBaseHelper.getWritableDatabase();
		byName = StringUtils.splitChineseSingly(byName);
		db.execSQL("update " + TBLabelConstants.TABLE_NAME + " set " + TBLabelConstants.MEMBER_COUNT
				+ " = " + TBLabelConstants.MEMBER_COUNT + "-" + minusNum
				+ " where " + TBLabelConstants.LABEL + "= ?", new String[]{byName});
		db.close();
	}

	public long updateIconWithLabel(String iconPath,
	                                String onLabel) {
		closeDataBase();
		db = dataBaseHelper.getWritableDatabase();
		onLabel = StringUtils.splitChineseSingly(onLabel);
		ContentValues value = new ContentValues();
		value.put(TBLabelConstants.LABEL, iconPath);
		long status;
		try {
			status = db.update(TBLabelConstants.TABLE_NAME, value,
					TBLabelConstants.LABEL + "=?", new String[]{onLabel});
		} finally {
			db.close();
		}


		return status;
	}

	public long updateLabelWithLabel(String label,
	                                 String onLabel) {
		closeDataBase();
		db = dataBaseHelper.getWritableDatabase();
		label = StringUtils.splitChineseSingly(label);
		onLabel = StringUtils.splitChineseSingly(onLabel);
		ContentValues value = new ContentValues();
		value.put(TBLabelConstants.LABEL, label);

		long status;
		try {
			status = db.update(TBLabelConstants.TABLE_NAME, value,
					TBLabelConstants.LABEL + "=?", new String[]{onLabel});
		} finally {
			db.close();
		}


		return status;
	}

	//TODO SUGGESTION: RETURN LIST FOR QUERY BUT NOT CURSOR,SO YOU CAN CLOSE THE CURSOR AND DB AT ONCE
	//query


	//query
	public Cursor selectIcon(String label) {
		closeDataBase();
		label = StringUtils.splitChineseSingly(label);
		db = dataBaseHelper.getReadableDatabase();
		Cursor c = db.query(TBLabelConstants.TABLE_NAME, new String[]{TBLabelConstants.LABEL_ICON},
				TBLabelConstants.LABEL + " = ?", new String[]{label}, null, null, null);
		//db.close();
		return c;
	}

	//query
	public int selectMemCount(String label) {
		closeDataBase();
		db = dataBaseHelper.getReadableDatabase();
		label = StringUtils.splitChineseSingly(label);
		Cursor cursorCount = db.query(TBLabelConstants.TABLE_NAME, new String[]{TBLabelConstants.MEMBER_COUNT},
				TBLabelConstants.LABEL + " = ?", new String[]{label}, null, null, null);
		if (cursorCount.getCount() == 0) {
			cursorCount.close();
			closeDataBase();
			return -1;
		}
		cursorCount.moveToPosition(0);
		int count = cursorCount.getInt(cursorCount.getColumnIndex(TBLabelConstants.MEMBER_COUNT));
		cursorCount.close();
		closeDataBase();
		//db.close();
		return count;
	}

	//query
	public LabelObj selectAllOnLabel(String label) {
		closeDataBase();
		db = dataBaseHelper.getReadableDatabase();
		Cursor cursorLabel;
		label = StringUtils.splitChineseSingly(label);
		cursorLabel = db.query(TBLabelConstants.TABLE_NAME, new String[]{TBLabelConstants.LABEL},
				TBLabelConstants.LABEL + " = ?", new String[]{label}, null, null, null);
		if (cursorLabel.getCount() == 0) {
			cursorLabel.close();
			closeDataBase();
			return new LabelObj("", "", -1);
		}
		cursorLabel.moveToPosition(0);
		LabelObj labelObj = new LabelObj(
				StringUtils.recoverWordFromDB(cursorLabel.getString(cursorLabel.getColumnIndex(TBLabelConstants.LABEL))),
				cursorLabel.getString(cursorLabel.getColumnIndex(TBLabelConstants.LABEL_ICON)),
				cursorLabel.getInt(cursorLabel.getColumnIndex(TBLabelConstants.MEMBER_COUNT))
		);
		cursorLabel.close();
		closeDataBase();
		//db.close();
		return labelObj;
	}

	//query
	public List<LabelObj> selectAll() {
		closeDataBase();
		db = dataBaseHelper.getReadableDatabase();
//		String name = dataBaseHelper.getDatabaseName();
		Cursor cursorLabels = db.query(TBLabelConstants.TABLE_NAME, null,
				null, null, null, null, null);
		List<LabelObj> labels = new ArrayList<>();
		for (int i = 0; i < cursorLabels.getCount(); i++) {
			cursorLabels.moveToPosition(i);
			labels.add(new LabelObj(
					StringUtils.recoverWordFromDB(cursorLabels.getString(cursorLabels.getColumnIndex(TBLabelConstants.LABEL))),
					cursorLabels.getString(cursorLabels.getColumnIndex(TBLabelConstants.LABEL_ICON)),
					cursorLabels.getInt(cursorLabels.getColumnIndex(TBLabelConstants.MEMBER_COUNT))
			));
		}
		cursorLabels.close();
		closeDataBase();
		//db.close();
		return labels;
	}

	public void closeDataBase() {
		if (db == null) return;
		if (db.isOpen()) db.close();
	}
}
