import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {


    public DataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("create table CurrencyType(id tinyint, type text)");
        database.execSQL("create table Expenditure(id int primary key, amount real, " +
                         "description text, currency tinyint,  foreign key(currency) " +
                         "references CurrencyType(id))");
        database.execSQL("create table Entry(id int primary key, amount real, " +
                         "description text, currency tinyint,  foreign key(currency) " +
                         "references currencyType(id))");
        database.execSQL("create table ReserveType(id tinyint primary key, type text)");
        database.execSQL("create table Reserve(id int primary key, type tinyint, amount real, " +
                         "entry int, foreign key(type) references ReserveType(id), " +
                         "foreign key(entry) references Entry(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {}
}
