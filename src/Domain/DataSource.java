package Domain;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;

/**
 * Created by Vai on 6/11/17.
 */
public class DataSource {

    public DataSource(){

    }

    public static Jongo connect(){
        DB db = new MongoClient("95.85.22.21", 27017).getDB("GSO-VAI");
        return new Jongo(db);
    }
}
