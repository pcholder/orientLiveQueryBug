package model;

import com.orientechnologies.common.exception.OException;
import com.orientechnologies.orient.core.db.OLiveQueryResultListener;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.sql.executor.OResult;

public class ItemListener implements OLiveQueryResultListener {
    @Override
    public void onCreate(ODatabaseDocument database, OResult data) {
        System.out.println("created");
    }

    @Override
    public void onUpdate(ODatabaseDocument database, OResult before, OResult after) {
        System.out.println("updated");
    }

    @Override
    public void onDelete(ODatabaseDocument database, OResult data) {
        System.out.println("deleted");
    }

    @Override
    public void onError(ODatabaseDocument database, OException exception) {
        System.err.println("error");
    }

    @Override
    public void onEnd(ODatabaseDocument database) {
        System.out.println("ended");
    }
}
