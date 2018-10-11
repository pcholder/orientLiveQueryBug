package model;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.OVertex;

public class SimpleItemEntity {

    public final static String CLASS_NAME = SimpleItemEntity.class.getSimpleName();
    public final static String NAME_PARAM = "name";

    private OVertex vertex;

    public static OClass createClassIfNotExists(ODatabaseSession db) {
        OClass itemClass = db.getClass(CLASS_NAME);
        if (itemClass == null) {
            itemClass = db.createVertexClass(CLASS_NAME);
            itemClass.createProperty(NAME_PARAM, OType.STRING);
        }
        return itemClass;
    }

    public SimpleItemEntity(ODatabaseSession session) {
        vertex = session.newVertex(CLASS_NAME);
    }

    private SimpleItemEntity(OVertex vertex) {
        this.vertex = vertex;
    }

    public static SimpleItemEntity wrap(OVertex vertex) {
        return new SimpleItemEntity(vertex);
    }

    public Object getIdentity() {
        return vertex.getIdentity();
    }

    public String getName() {
        return vertex.getProperty(NAME_PARAM);
    }

    public void setName(String name) {
        vertex.setProperty(NAME_PARAM, name);
    }
}