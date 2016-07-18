package meraschool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import meraschool.models.Location;

public class DbConnectorImpl implements DbConnector {

    private static final String LOCATIONS = "locations";
    private static final String VIEWS = "views";
    private static final String LINKS = "links";

    String dbFilePath;
    SqlJetDb db;

    public DbConnectorImpl(String dbpath) {
        dbFilePath = dbpath;
    }

    public int addView(Location location, Path image, int viewIdAfter) {
        try {
            prepareConnection(true);

            if (location.id == 0) {
                location.id = getMaxId(LOCATIONS, "id") + 1;
                ISqlJetTable locTable = db.getTable(LOCATIONS);
                locTable.insert(location.id, location.name);
            }

            int viewId = getMaxId(VIEWS, "id") + 1;
            ISqlJetTable viewTable = db.getTable(VIEWS);

            String filename = image.getFileName().toString();
            filename = filename.substring(filename.indexOf("."));
            String imageName = String.valueOf(viewId) + filename;
            Files.move(image, Paths.get(getImageDirectory(), imageName), StandardCopyOption.REPLACE_EXISTING);
            viewTable.insert(viewId, location.id, viewIdAfter, imageName);
            closeConnection();

            return viewId;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<Location> getLocationList() {
        try {
            prepareConnection(false);
            ISqlJetCursor cursor = db.getTable(LOCATIONS).open();
            List<Location> list = new ArrayList<Location>();
            do {
                list.add(new Location((int) cursor.getInteger("id"), cursor.getString("name")));
            } while (cursor.next());
            closeConnection();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Location>();
        }
    }

    public Location getLocationByViewId(int viewId) {
        Location loc = new Location(0, "tmpName");
        try {
            prepareConnection(false);
            ISqlJetCursor cursor = db.getTable(VIEWS).open();
            do {
                if (cursor.getInteger("id") == viewId) {
                    loc = new Location((int) cursor.getInteger("locid"), ""/**/);
                    break;
                }
            } while (cursor.next());
            if (loc.id != 0) {
                cursor = db.getTable(LOCATIONS).open();
                do {
                    if (cursor.getInteger("id") == loc.id) {
                        loc.name = cursor.getString("name");
                        break;
                    }
                } while (cursor.next());
            }
            closeConnection();
            return loc;
        } catch (Exception e) {
            e.printStackTrace();
            return loc;
        }
    }

    public int getFirstViewByLocation(Location location) {
        try {
            prepareConnection(false);
            ISqlJetCursor cursor = db.getTable(VIEWS).open();
            int viewId = 0;
            do {
                if (cursor.getInteger("locid") == location.id) {
                    if (cursor.getInteger("afterid") == 0) {
                        viewId = (int) cursor.getInteger("id");
                        break;
                    }
                }
            } while (cursor.next());
            closeConnection();
            return viewId;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Path getImagePathByViewId(int viewId) {
        try {
            prepareConnection(false);
            ISqlJetCursor cursor = db.getTable(VIEWS).open();
            Path image = null;
            do {
                if (cursor.getInteger("id") == viewId) {
                    image = Paths.get(getImageDirectory(), cursor.getString("image"));
                    break;
                }
            } while (cursor.next());
            closeConnection();
            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void removeLocation(Location location) {
        try {
            prepareConnection(true);
            ISqlJetCursor cursor = db.getTable(LOCATIONS).open();
            do {
                if (cursor.getInteger("id") == location.id) {
                    cursor.delete();
                }
            } while (cursor.next());
            cursor = db.getTable(VIEWS).open();
            List<Integer> viewIds = new ArrayList<Integer>();
            do {
                if (cursor.getInteger("locid") == location.id) {
                    Files.deleteIfExists(Paths.get(getImageDirectory(), cursor.getString("image")));
                    viewIds.add((int) cursor.getInteger("id"));
                    cursor.delete();
                }
            } while (cursor.next());
            cursor = db.getTable(LINKS).open();
            do {
                if (!cursor.eof()) {
                    int viewId = (int) cursor.getInteger("viewid");
                    for (Integer i : viewIds) {
                        if (i == viewId) {
                            cursor.delete();
                            break;
                        }
                    }
                }
            } while (cursor.next());
            closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeView(int viewId) {
        try {
            prepareConnection(true);
            ISqlJetCursor cursor = db.getTable(VIEWS).open();
            int locid = 0;
            do {
                if (!cursor.eof()) {
                    if (cursor.getInteger("id") == viewId) {
                        locid = (int) cursor.getInteger("locid");
                        cursor.delete();
                    }
                }
            } while (cursor.next());

            cursor = db.getTable(VIEWS).open();
            do {
                if (!cursor.eof()) {
                    if ((int) cursor.getInteger("locid") == locid) {
                        locid = 0;
                        break;
                    }
                }
            } while (cursor.next());
            if (locid > 0) {
                cursor = db.getTable(LOCATIONS).open(); // cursor.lookup ???
                do {
                    if (!cursor.eof()) {
                        if (cursor.getInteger("id") == locid) {
                            cursor.delete();
                            break;
                        }
                    }
                } while (cursor.next());
            }
            cursor = db.getTable(LINKS).open();
            do {
                if (!cursor.eof()) {
                    if (cursor.getInteger("viewid") == viewId) {
                        cursor.delete();
                    }
                }
            } while (cursor.next());
            closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // public void setImage(Embedded image, int viewId) {
    // try {
    // prepareConnection(false);
    // ISqlJetCursor cursor = db.getTable(VIEWS).open();
    // Path path = null;
    // do {
    // if (cursor.getInteger("id") == viewId) {
    // path = Paths.get(getImageDirectory() + "/" + cursor.getString("image"));
    // break;
    // }
    // } while (cursor.next());
    // closeConnection();
    // if (path == null) {
    // image.setSource(new ClassResource("/1.jpg", image.getApplication()));
    // ClassResource cr;
    //
    // } else {
    // BufferedImage bi = ImageIO.read(path.toFile());
    // int w = bi.getWidth();
    // int h = bi.getHeight();
    // int w1 = 600;
    // int h1 = (600 * h / w);
    // image.setWidth(w1 + "px");
    // image.setHeight(h1 + "px");
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    private int getMaxId(String tableName, String indexName) throws SqlJetException {
        ISqlJetTable table = db.getTable(tableName);
        ISqlJetCursor cursor = table.open().reverse();
        if (!cursor.eof()) {
            return (int) cursor.getInteger(indexName);
        } else {
            return 0;
        }
    }

    private void prepareConnection(boolean write) throws SqlJetException {
        File file = new File(dbFilePath);
        boolean createTables = !file.exists();
        db = SqlJetDb.open(file, true);
        if (createTables) {
            db.getOptions().setAutovacuum(true);
            db.beginTransaction(SqlJetTransactionMode.WRITE);
            db.getOptions().setUserVersion(1);
            db.createTable("CREATE TABLE locations (id INTEGER NOT NULL PRIMARY KEY, name TEXT NOT NULL)");
            db.createTable(
                    "CREATE TABLE views (id INTEGER NOT NULL PRIMARY KEY, locid INTEGER NOT NULL, afterid INTEGER, image TEXT NOT NULL)");
            db.createTable(
                    "CREATE TABLE links (id INTEGER NOT NULL PRIMARY KEY, viewid INTEGER NOT NULL, x REAL, y REAL, viewrefid INTEGER)");
            db.commit();
        }
        db.beginTransaction(write ? SqlJetTransactionMode.WRITE : SqlJetTransactionMode.READ_ONLY);
    }

    private void closeConnection() throws SqlJetException {
        db.commit();
        db.close();
    }

    private String getImageDirectory() {
        String dir = dbFilePath.substring(0, dbFilePath.lastIndexOf("/")) + "/images";
        Path dirPath = Paths.get(dir);
        if (!dirPath.toFile().exists()) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dir;
    }
}