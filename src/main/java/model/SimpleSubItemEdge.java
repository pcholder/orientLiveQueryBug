package model;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.metadata.schema.OClass;

public class SimpleSubItemEdge {

    public final static String CLASS_NAME = SimpleSubItemEdge.class.getSimpleName();

    public static OClass createClassIfNotExists(ODatabaseSession db) {
        OClass edge = db.getClass(CLASS_NAME);
        if (edge == null) {
            return db.createEdgeClass(CLASS_NAME);
        }
        return edge;
    }

}
