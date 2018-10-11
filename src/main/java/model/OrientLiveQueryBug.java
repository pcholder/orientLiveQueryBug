package model;

import com.orientechnologies.orient.core.db.*;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class OrientLiveQueryBug {

    private static Properties getProperties() throws IOException {
        Properties properties = new Properties();
        String file = Objects.requireNonNull(
                OrientLiveQueryBug.class.getClassLoader().getResource("db.properties")
        ).getFile();
        try(InputStream is = new FileInputStream(file);) {
            properties.load(is);
            return properties;
        }
    }

    private static void initDatabaseSchemes(ODatabaseSession session) {
        SimpleItemEntity.createClassIfNotExists(session);
        SimpleSubItemEdge.createClassIfNotExists(session);
    }

    private static void createSimpleEdge(ODatabaseSession session, OVertex from, OVertex to) {
        OEdge edge = session.newEdge(from, to, SimpleSubItemEdge.CLASS_NAME);
        edge.save();
    }

    public static void main(String [] args) throws Exception {
        Properties properties = getProperties();
        String dbName = properties.getProperty("db.name");
        String dbUser = properties.getProperty("db.user");
        String dbPass = properties.getProperty("db.password");

        OrientDB orient = new OrientDB("remote:localhost", OrientDBConfig.defaultConfig());
        ODatabasePool pool = new ODatabasePool(orient, dbName, dbUser, dbPass);

        ODatabaseSession liveSession = pool.acquire();
        initDatabaseSchemes(liveSession);
        ItemListener itemListener = new ItemListener();
        OLiveQueryMonitor live = liveSession.live("LIVE SELECT FROM " + SimpleItemEntity.CLASS_NAME, itemListener);
        System.out.println("finished");

        new Thread(() -> {
            try(ODatabaseSession session = pool.acquire();) {

//                session.begin();
                OVertex item1 = session.newVertex(SimpleItemEntity.CLASS_NAME);
                item1.setProperty("name", "item1");
                item1.save();

                OVertex item2 = session.newVertex(SimpleItemEntity.CLASS_NAME);
                item2.setProperty("name", "item2");
                item2.save();

                createSimpleEdge(session, item1, item2); // this line crashes subscriber on next insert

//                session.commit();
            }
        }).start();

        Thread.sleep(1000);
        new Thread(() -> {
            try(ODatabaseSession session = pool.acquire();) {

                // session.begin();
                OVertex itemVertex1 = session.newVertex(SimpleItemEntity.CLASS_NAME);
                itemVertex1.setProperty("name", "item3");
                itemVertex1.save();
                // session.commit();

                // session.begin();
                OVertex itemVertex2 = session.newVertex(SimpleItemEntity.CLASS_NAME);
                itemVertex2.setProperty("name", "item4");
                itemVertex2.save();

                createSimpleEdge(session, itemVertex1, itemVertex2); // next insert will crash subscriber

                // session.commit();
            }
        }).start();

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        liveSession.activateOnCurrentThread();
        live.unSubscribe();
        liveSession.close();
    }

}
