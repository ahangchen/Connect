package ahang.connect.common.utils.database;

import android.database.Cursor;

import java.util.ArrayList;


/**
 * Created by Administrator on 2015/4/8.
 */
public class CursorUtils {
	public static void cursorToStringArray(Cursor c, ArrayList<String> arrayList, String columnName) {
		int columnIndex = c.getColumnIndex(columnName);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			arrayList.add(c.getString(columnIndex));
		}

	}

	public static void closeExistsCursor(Cursor c) {
		if (c != null) c.close();
	}
}
