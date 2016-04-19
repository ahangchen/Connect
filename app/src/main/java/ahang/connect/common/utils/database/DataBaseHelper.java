package ahang.connect.common.utils.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DataBaseHelper extends SQLiteOpenHelper {
	public final static int DBVersion = 4;
	public static boolean importData = false;
	private final static String DATABASE_NAME = "gofContacts";
	private final static String SQL_MAIN_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TBMainConstants.TABLE_NAME + " ("
//			+ TBMainConstants.ID
//			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TBMainConstants.NAME + " VARCHAR(8) NOT NULL,"
			+ TBMainConstants.L_PINYIN + " VARCHAR(24),"
			+ TBMainConstants.S_PINYIN + " VARCHAR(12),"
			// + TBMainConstants.TEL + " VARCHAR(15) NOT NULL,"
			// + TBMainConstants.LABEL + " INT,"
			+ TBMainConstants.ADDRESS + " TEXT,"
			+ TBMainConstants.NOTES + " TEXT)";
	private final static String SQL_ID_LABEL_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TBIDLabelConstants.TABLE_NAME + " ("
			+ TBIDLabelConstants.ID
			+ " INTEGER NOT NULL, "
			+ TBIDLabelConstants.LABEL + " VARCHAR(20) NOT NULL,constraint pk_t1 primary key ("
			+ TBIDLabelConstants.ID + "," + TBIDLabelConstants.LABEL
			+ "))";
	private final static String SQL_TEL_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TBTelConstants.TABLE_NAME + " ("
			+ TBTelConstants.ID
			+ " INTEGER NOT NULL, "
			+ TBTelConstants.TEL + " INTEGER NOT NULL,constraint pk_t2 primary key ("
			+ TBIDLabelConstants.ID + "," + TBTelConstants.TEL
			+ "))";
	private final static String SQL_LABEL_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TBLabelConstants.TABLE_NAME + " ("
			+ TBLabelConstants.LABEL + " VARCHAR(30) NOT NULL primary key,"
			+ TBLabelConstants.LABEL_ICON + " TEXT NOT NULL,"
			+ TBLabelConstants.MEMBER_COUNT + " INTEGER NOT NULL)";
	//    CREATE TRIGGER addmembercount after insert on id_label begin update label set count = count+1 where label.label = new.label;
	//    end;

	private final static String SQL_TRIGGER_ADD_MEMBER = "CREATE TRIGGER addMemberCount AFTER INSERT ON " +
			TBIDLabelConstants.TABLE_NAME +
			" BEGIN UPDATE " + TBLabelConstants.TABLE_NAME + " SET " + TBLabelConstants.MEMBER_COUNT + " = "
			+ TBLabelConstants.MEMBER_COUNT + "+1 WHERE " + TBLabelConstants.TABLE_NAME + "."
			+ TBLabelConstants.LABEL + " = NEW." + TBIDLabelConstants.LABEL + ";END";
	//CREATE TRIGGER removeMemberCount after delete on id_label begin update label set count = count-1 where label.label = old.label;end;
	private final static String SQL_TRIGGER_REMOVE_MEMBER =
			"CREATE TRIGGER removeMemberCount AFTER DELETE ON " +
					TBIDLabelConstants.TABLE_NAME + " BEGIN UPDATE "
					+ TBLabelConstants.TABLE_NAME + " SET " + TBLabelConstants.MEMBER_COUNT + " = "
					+ TBLabelConstants.MEMBER_COUNT + "-1 WHERE " + TBLabelConstants.TABLE_NAME + "."
					+ TBLabelConstants.LABEL + " = OLD." + TBIDLabelConstants.LABEL + ";END";
	//CREATE TRIGGER updateMemberCount after update on id_label begin
	// update label set count = (select count(label) from id_label where label = new.label) where label.label = old.label;
	// update label set count=(select count(label) from id_label where label = new.label) where label.label = new.label;end;
	private final static String SQL_TRIGGER_UPDATE_MEMBER = "CREATE TRIGGER updateMemberCount AFTER UPDATE ON " +
			TBIDLabelConstants.TABLE_NAME +
			" BEGIN UPDATE " + TBLabelConstants.TABLE_NAME + " SET " + TBLabelConstants.MEMBER_COUNT + " = "
			+ " (select count(" + TBIDLabelConstants.LABEL + ") from " + TBIDLabelConstants.TABLE_NAME
			+ " where " + TBIDLabelConstants.LABEL + " = old." + TBIDLabelConstants.LABEL + ")"
			+ " WHERE " + TBLabelConstants.TABLE_NAME + "."
			+ TBLabelConstants.LABEL + " = old." + TBIDLabelConstants.LABEL
			+ ";UPDATE "
			+ TBLabelConstants.TABLE_NAME + " SET " + TBLabelConstants.MEMBER_COUNT + " = "
			+ " (select count(" + TBIDLabelConstants.LABEL + ") from " + TBIDLabelConstants.TABLE_NAME
			+ " where " + TBIDLabelConstants.LABEL + " = new." + TBIDLabelConstants.LABEL + ")"
			+ " WHERE " + TBLabelConstants.TABLE_NAME + "."
			+ TBLabelConstants.LABEL + " = new." + TBIDLabelConstants.LABEL + ";END";

	//    CREATE TRIGGER updatelabelname after update of label on label begin update id_label set label = new.label where label = old.label;end;
	private final static String SQL_TRIGGER_UPDATE_LABEL = "CREATE TRIGGER updateLabelName AFTER UPDATE of " +
			TBLabelConstants.LABEL + " ON " +
			TBLabelConstants.TABLE_NAME +
			" BEGIN UPDATE " + TBIDLabelConstants.TABLE_NAME + " SET " + TBIDLabelConstants.LABEL + " = "
			+ "NEW." + TBIDLabelConstants.LABEL + " WHERE " + TBIDLabelConstants.LABEL + " = OLD."
			+ TBIDLabelConstants.LABEL + ";END";
	//    CREATE TRIGGER deleteLabel after delete on label begin delete from id_label where label = old.label;end;
	private final static String SQL_TRIGGER_REMOVE_LABEL = "CREATE TRIGGER deleteLabel AFTER delete on "
			+ TBLabelConstants.TABLE_NAME +
			" BEGIN delete from " + TBIDLabelConstants.TABLE_NAME
			+ " WHERE " + TBIDLabelConstants.LABEL + " = OLD."
			+ TBIDLabelConstants.LABEL + ";END";

	//create virtual table fts_contacts using fts4(_id,name,l_pinyin,s_pinyin,address,notes);

	//label表很小，没必要建virtual table
	private final static String SQL_CREATE_FTS_CONTACTS = "create virtual table "
			+ TBMainConstants.FTS_TABLE_NAME + " using fts4("
//			+ TBMainConstants.ID + ","
			+ TBMainConstants.NAME + "," + TBMainConstants.L_PINYIN + ","
			+ TBMainConstants.S_PINYIN + "," + TBMainConstants.ADDRESS + "," + TBMainConstants.NOTES + ")";
	//create trigger insert_fts_contacts after insert on contacts begin insert into fts_contacts
	// values (new._id,new.name,new.l_pinyin,new.s_pinyin,new.address,new.notes);end;
	//insert default record into id label tel
	private final static String SQL_TRIGGER_INSERT_FTS_CONTACTS =
			"create trigger " +
					TriggerConstants.INSERT_FTS_CONSTANTS +
					" after insert on " + TBMainConstants.TABLE_NAME
					+ " begin " +
					"insert into " + TBMainConstants.FTS_TABLE_NAME
					+ " values (" +
//					"new." + TBMainConstants.ID + "," +
					"new." + TBMainConstants.NAME
					+ ",new." + TBMainConstants.L_PINYIN + ",new." + TBMainConstants.S_PINYIN
					+ ",new." + TBMainConstants.ADDRESS + ",new." + TBMainConstants.NOTES + ");"
					+ "end;";
	// Don't use a trigger for updating the words table because of a bug
	// in FTS3.  The bug is such that the call to get the last inserted
	// row is incorrect.

	//create trigger update_fts_contacts after update on contacts begin update fts_contacts
	// set _id=new._id,name=new.name,l_pinyin=new.l_pinyin,s_pinyin=new.s_pinyin,address=new.address,
	// notes=new.notes where _id=old._id;end;
	private final static String SQL_TRIGGER_UPDATE_FTS_CONTACTS =
			"create trigger " +
					TriggerConstants.UPDATE_FTS_CONTACTS +
					" after update on " + TBMainConstants.TABLE_NAME
					+ " begin update " + TBMainConstants.FTS_TABLE_NAME + " set "
//					+ TBMainConstants.ID + " = new." + TBMainConstants.ID + ","
					+ TBMainConstants.NAME + " = new." + TBMainConstants.NAME + ","
					+ TBMainConstants.L_PINYIN + " = new." + TBMainConstants.L_PINYIN + ","
					+ TBMainConstants.S_PINYIN + " = new." + TBMainConstants.S_PINYIN + ","
					+ TBMainConstants.ADDRESS + " = new." + TBMainConstants.ADDRESS + ","
					+ TBMainConstants.NOTES + " = new." + TBMainConstants.NOTES + " where "
					+ TBMainConstants.ID + " = old." + TBMainConstants.ID + ";end";
	//create trigger delete_fts_contacts after delete on contacts begin delete from fts_contacts where _id=old._id;end;
	//delete from id_label,id_tel,
	private final static String SQL_TRIGGER_DELETE_FTS_CONTACTS =
			"create trigger " +
					TriggerConstants.DELETE_FTS_CONTACTS +
					" after delete on " + TBMainConstants.TABLE_NAME
					+ " begin " +
					"delete from " + TBMainConstants.FTS_TABLE_NAME + " where "
					+ TBMainConstants.ID + " = old." + TBMainConstants.ID + ";" +
					"delete from " + TBIDLabelConstants.TABLE_NAME + " where "
					+ TBIDLabelConstants.ID + " = old." + TBMainConstants.ID + ";" +
					"delete from " + TBTelConstants.TABLE_NAME + " where "
					+ TBTelConstants.ID + " = old." + TBMainConstants.ID + ";" +
					"end";
	//create virtual table fts_id_label using fts4(_id,label);
	private final static String SQL_CREATE_FTS_ID_LABEL = "create virtual table "
			+ TBIDLabelConstants.FTS_TABLE_NAME + " using fts4("
			+ TBIDLabelConstants.ID + "," + TBIDLabelConstants.LABEL + ")";
	//create trigger insert_fts_il after insert on id_label begin insert into fts_id_label values (new._id,new.label);end;
	private final static String SQL_TRIGGER_INSERT_FTS_IL = "create trigger " +
			TriggerConstants.INSERT_FTS_IL +
			" after insert on "
			+ TBIDLabelConstants.TABLE_NAME + " begin insert into " + TBIDLabelConstants.FTS_TABLE_NAME
			+ " values (new." + TBIDLabelConstants.ID + ",new." + TBIDLabelConstants.LABEL + ");end";
	//create trigger update_fts_il after update on id_label begin update fts_id_label set _id=new._id,label=new.label where label=old.label and _id=old._id;end;
	private final static String SQL_TRIGGER_UPDATE_FTS_IL =
			"create trigger " +
					TriggerConstants.UPDATE_FTS_IL +
					" after update on "
					+ TBIDLabelConstants.TABLE_NAME + " begin update " + TBIDLabelConstants.FTS_TABLE_NAME + " set "
					+ TBIDLabelConstants.ID + "=new." + TBIDLabelConstants.ID + ","
					+ TBIDLabelConstants.LABEL + " = new." + TBIDLabelConstants.LABEL + " where "
					+ TBIDLabelConstants.ID + " = old." + TBIDLabelConstants.ID + " and "
					+ TBIDLabelConstants.LABEL + " = old." + TBIDLabelConstants.LABEL + ";end";
	//create trigger delete_fts_il after delete on id_label begin delete from fts_id_label where _id = old._id and label=old.label;end;
	private final static String SQL_TRIGGER_DELETE_FTS_IL =
			"create trigger " +
					TriggerConstants.DELETE_FTS_IL +
					" after delete on "
					+ TBIDLabelConstants.TABLE_NAME + " begin delete from "
					+ TBIDLabelConstants.FTS_TABLE_NAME + " where "
					+ TBIDLabelConstants.ID + "=old." + TBIDLabelConstants.ID + " and "
					+ TBIDLabelConstants.LABEL + "=old." + TBIDLabelConstants.LABEL + ";end";

	//create virtual table fts_tel using fts4(_id,tel);
	private final static String SQL_CREATE_FTS_TEL =
			"create virtual table " + TBTelConstants.FTS_TABLE_NAME + " using fts4("
					+ TBTelConstants.ID + "," + TBTelConstants.TEL + ")";
	//create trigger insert_fts_tel after insert on tel begin insert into fts_tel values(new._id,new.label);end;
	private final static String SQL_TRIGGER_INSERT_FTS_TEL =
			"create trigger " +
					TriggerConstants.INSERT_FTS_TEL +
					" after insert on "
					+ TBTelConstants.TABLE_NAME + " begin insert into "
					+ TBTelConstants.FTS_TABLE_NAME + " values(new."
					+ TBTelConstants.ID + ",new." + TBTelConstants.TEL + ");end";
	//create trigger update_fts_tel after update on tel begin update fts_tel set _id=new._id,tel=new.tel where tel=old.tel and _id=old._id;end;
	private final static String SQL_TRIGGER_UPDATE_FTS_TEL =
			"create trigger " +
					TriggerConstants.UPDATE_FTS_TEL +
					" after update on "
					+ TBTelConstants.TABLE_NAME + " begin update " + TBTelConstants.FTS_TABLE_NAME + " set "
					+ TBTelConstants.ID + "=new." + TBTelConstants.ID + ","
					+ TBTelConstants.TEL + " = new." + TBTelConstants.TEL + " where "
					+ TBTelConstants.ID + " = old." + TBTelConstants.ID + " and "
					+ TBTelConstants.TEL + " = old." + TBTelConstants.TEL + ";end";
	//create trigger delete_fts_tel after delete on tel begin delete from fts_tel where _id = old._id and tel=old.tel;end;
	private final static String SQL_TRIGGER_DELETE_FTS_TEL =
			"create trigger " +
					TriggerConstants.DELETE_FTS_TEL +
					" after delete on "
					+ TBTelConstants.TABLE_NAME + " begin delete from "
					+ TBTelConstants.FTS_TABLE_NAME + " where "
					+ TBTelConstants.ID + "=old." + TBTelConstants.ID + " and "
					+ TBTelConstants.TEL + "=old." + TBTelConstants.TEL + ";end";

	//for inner join


	public DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DBVersion);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL(SQL_MAIN_TABLE_CREATE);
		db.execSQL(SQL_TEL_TABLE_CREATE);
		db.execSQL(SQL_LABEL_TABLE_CREATE);
		db.execSQL(SQL_ID_LABEL_TABLE_CREATE);

		db.execSQL(SQL_TRIGGER_ADD_MEMBER);
		db.execSQL(SQL_TRIGGER_REMOVE_MEMBER);
		db.execSQL(SQL_TRIGGER_UPDATE_MEMBER);
		db.execSQL(SQL_TRIGGER_UPDATE_LABEL);
		db.execSQL(SQL_TRIGGER_REMOVE_LABEL);

		db.execSQL(SQL_CREATE_FTS_CONTACTS);
		db.execSQL(SQL_TRIGGER_INSERT_FTS_CONTACTS);
		db.execSQL(SQL_TRIGGER_UPDATE_FTS_CONTACTS);
		db.execSQL(SQL_TRIGGER_DELETE_FTS_CONTACTS);

		db.execSQL(SQL_CREATE_FTS_ID_LABEL);
		db.execSQL(SQL_TRIGGER_INSERT_FTS_IL);
		db.execSQL(SQL_TRIGGER_UPDATE_FTS_IL);
		db.execSQL(SQL_TRIGGER_DELETE_FTS_IL);

		db.execSQL(SQL_CREATE_FTS_TEL);
		db.execSQL(SQL_TRIGGER_INSERT_FTS_TEL);
		db.execSQL(SQL_TRIGGER_UPDATE_FTS_TEL);
		db.execSQL(SQL_TRIGGER_DELETE_FTS_TEL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (newVersion) {
			case 2:
				db.execSQL("DROP TABLE IF EXISTS " + TBMainConstants.TABLE_NAME);
				db.execSQL("DROP TABLE IF EXISTS " + TBIDLabelConstants.TABLE_NAME);
				db.execSQL("DROP TABLE IF EXISTS " + TBTelConstants.TABLE_NAME);
				db.execSQL("DROP TABLE IF EXISTS " + TBLabelConstants.TABLE_NAME);
				break;
			case 3:
				db.execSQL("DROP TRIGGER IF EXISTS " + TriggerConstants.DELETE_FTS_CONTACTS);
				db.execSQL("DROP TRIGGER IF EXISTS " + TriggerConstants.DELETE_FTS_IL);
				db.execSQL("DROP TRIGGER IF EXISTS " + TriggerConstants.DELETE_FTS_TEL);
				db.execSQL("DROP TRIGGER IF EXISTS " + TriggerConstants.INSERT_FTS_CONSTANTS);
				db.execSQL("DROP TRIGGER IF EXISTS " + TriggerConstants.INSERT_FTS_IL);
				db.execSQL("DROP TRIGGER IF EXISTS " + TriggerConstants.INSERT_FTS_TEL);
				db.execSQL("DROP TRIGGER IF EXISTS " + TriggerConstants.UPDATE_FTS_CONTACTS);
				db.execSQL("DROP TRIGGER IF EXISTS " + TriggerConstants.UPDATE_FTS_IL);
				db.execSQL("DROP TRIGGER IF EXISTS " + TriggerConstants.UPDATE_FTS_TEL);
				break;
			case 4:
				db.execSQL("DROP TABLE IF EXISTS " + TBMainConstants.TABLE_NAME);
				db.execSQL("DROP TABLE IF EXISTS " + TBMainConstants.FTS_TABLE_NAME);
				db.execSQL("DROP TABLE IF EXISTS " + TBIDLabelConstants.TABLE_NAME);
				db.execSQL("DROP TABLE IF EXISTS " + TBIDLabelConstants.FTS_TABLE_NAME);
				db.execSQL("DROP TABLE IF EXISTS " + TBTelConstants.TABLE_NAME);
				db.execSQL("DROP TABLE IF EXISTS " + TBTelConstants.FTS_TABLE_NAME);
				db.execSQL("DROP TABLE IF EXISTS " + TBLabelConstants.TABLE_NAME);

				db.execSQL("DROP TRIGGER IF EXISTS " + TriggerConstants.DELETE_FTS_CONTACTS);
				db.execSQL("DROP TRIGGER IF EXISTS " + TriggerConstants.DELETE_FTS_IL);
				db.execSQL("DROP TRIGGER IF EXISTS " + TriggerConstants.DELETE_FTS_TEL);
				db.execSQL("DROP TRIGGER IF EXISTS " + TriggerConstants.INSERT_FTS_CONSTANTS);
				db.execSQL("DROP TRIGGER IF EXISTS " + TriggerConstants.INSERT_FTS_IL);
				db.execSQL("DROP TRIGGER IF EXISTS " + TriggerConstants.INSERT_FTS_TEL);
				db.execSQL("DROP TRIGGER IF EXISTS " + TriggerConstants.UPDATE_FTS_CONTACTS);
				db.execSQL("DROP TRIGGER IF EXISTS " + TriggerConstants.UPDATE_FTS_IL);
				db.execSQL("DROP TRIGGER IF EXISTS " + TriggerConstants.UPDATE_FTS_TEL);
		}

//        db.execSQL("DROP VIRTUAL TABLE IF EXISTS " + TBMainConstants.FTS_TABLE_NAME);
//        db.execSQL("DROP VIRTUAL TABLE IF EXISTS " + TBIDLabelConstants.FTS_TABLE_NAME);
//        db.execSQL("DROP VIRTUAL TABLE IF EXISTS " + TBTelConstants.FTS_TABLE_NAME);
		onCreate(db);
	}

}
