package meraschool;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import meraschool.models.LinkModel;
import meraschool.models.Location;

public class DbConnectorImpl implements DbConnector {

    private static final String LOCATIONS = "locations";
    private static final String VIEWS = "views";
    private static final String LINKS = "links";

    private static final String ID = "id";
    private static final String LOCID = "locid";
    private static final String ORDINAL = "ordr";
    private static final String IMAGE = "image";
    private static final String NAME = "name";
    private static final String VIEWID = "viewid";
    private static final String X = "x";
    private static final String Y = "y";
    private static final String VIEWREFID = "viewrefid";

    String dbFilePath;
    SqlJetDb db;

    public DbConnectorImpl(String dbpath) {
        dbFilePath = dbpath;
    }

    public int addView(Location location, Path image, int viewIdAfter) {
        try {
            prepareConnection(true);

            if (viewIdAfter > 0) {
                ISqlJetCursor vc = db.getTable(VIEWS).open();
                do {
                    if (!vc.eof() && vc.getInteger(ID) == viewIdAfter) {
                        location.id = (int) vc.getInteger(LOCID);
                        break;
                    }
                } while (vc.next());
            } else {
                if (location.id == 0) {
                    location.id = getMaxId(LOCATIONS) + 1;
                    db.getTable(LOCATIONS).insert(location.id, location.name);
                }
            }

            int viewId = getMaxId(VIEWS) + 1;

            String ext = image.getFileName().toString();
            ext = ext.substring(ext.lastIndexOf("."));
            String imageName = String.valueOf(viewId) + ext;
            final Path imagePath = Paths.get(getImageDirectory(), imageName);
            Files.move(image, imagePath, StandardCopyOption.REPLACE_EXISTING);

            ISqlJetCursor cursor = db.getTable(VIEWS).open();
            int ordinal = 1;
            do {
                if (!cursor.eof() && cursor.getInteger(ID) == viewIdAfter) {
                    ordinal = (int) cursor.getInteger(ORDINAL) + 1;
                    break;
                }
            } while (cursor.next());
            cursor = db.getTable(VIEWS).open();
            do {
                if (!cursor.eof() && cursor.getInteger(LOCID) == location.id && cursor.getInteger(ORDINAL) >= ordinal) {
                    cursor.update(cursor.getInteger(ID), cursor.getInteger(LOCID), cursor.getInteger(ORDINAL) + 1,
                            cursor.getString(IMAGE));
                }
            } while (cursor.next());

            db.getTable(VIEWS).insert(viewId, location.id, ordinal, imageName);
            closeConnection();

            new ImageCompressor(imagePath).join();

            return viewId;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int addLink(int viewId, double x, double y, int refViewId) {
        try {
            prepareConnection(true);
            int id = getMaxId(LINKS) + 1;
            db.getTable(LINKS).insert(id, viewId, x, y, refViewId);
            closeConnection();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void editLink(LinkModel lm) {
        try {
            prepareConnection(true);
            ISqlJetCursor cursor = db.getTable(LINKS).open();
            do {
                if (!cursor.eof()) {
                    if ((int) cursor.getInteger(ID) == lm.getId()) {
                        cursor.update(cursor.getInteger(ID), cursor.getInteger(VIEWID), cursor.getFloat(X),
                                cursor.getFloat(Y), lm.viewRefId);
                    }
                }
            } while (cursor.next());
            closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Location> getLocationList() {
        try {
            prepareConnection(false);
            ISqlJetCursor cursor = db.getTable(LOCATIONS).open();
            List<Location> list = new ArrayList<Location>();
            do {
                if (!cursor.eof()) {
                    list.add(new Location((int) cursor.getInteger(ID), cursor.getString(NAME)));
                }
            } while (cursor.next());
            closeConnection();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Location>();
        }
    }

    public Location getLocationByViewId(int viewId) {
        Location loc = null;
        try {
            prepareConnection(false);
            ISqlJetCursor cursor = db.getTable(VIEWS).open();
            do {
                if (!cursor.eof() && cursor.getInteger(ID) == viewId) {
                    loc = new Location((int) cursor.getInteger(LOCID), "");
                    break;
                }
            } while (cursor.next());
            if (loc != null && loc.id != 0) {
                cursor = db.getTable(LOCATIONS).open();
                do {
                    if (!cursor.eof() && cursor.getInteger(ID) == loc.id) {
                        loc.name = cursor.getString(NAME);
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
                if (!cursor.eof() && cursor.getInteger(LOCID) == location.id) {
                    if (cursor.getInteger(ORDINAL) == 1) {
                        viewId = (int) cursor.getInteger(ID);
                        break;
                    }
                    System.out.println(cursor.getInteger(ID) + " " + cursor.getInteger(LOCID) + " "
                            + cursor.getInteger(ORDINAL) + " " + cursor.getString(IMAGE));
                }
            } while (cursor.next());
            closeConnection();
            return viewId;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public enum Neighbor {
        LEFT, RIGHT
    }

    class ViewNode {
        public int viewId;
        public int ordinal;

        public ViewNode(int viewId, int ordinal) {
            this.viewId = viewId;
            this.ordinal = ordinal;
        }
    }

    public int getNeighborViewTo(int viewId, Neighbor n) {
        try {
            prepareConnection(false);

            ISqlJetCursor cursor = db.getTable(VIEWS).open();
            int locId = 0;
            ViewNode vn = null;
            do {
                if (!cursor.eof()) {
                    if (cursor.getInteger(ID) == viewId) {
                        locId = (int) cursor.getInteger(LOCID);
                        vn = new ViewNode(viewId, (int) cursor.getInteger(ORDINAL));
                        break;
                    }
                }
            } while (cursor.next());
            if (locId == 0) {
                return 0;
            }
            cursor = db.getTable(VIEWS).open();
            List<ViewNode> vnl = new ArrayList<ViewNode>();
            do {
                if (!cursor.eof()) {
                    if (cursor.getInteger(LOCID) == locId) {
                        vnl.add(new ViewNode((int) cursor.getInteger(ID), (int) cursor.getInteger(ORDINAL)));
                    }
                }
            } while (cursor.next());

            Collections.sort(vnl, new Comparator<ViewNode>() {
                public int compare(ViewNode vn1, ViewNode vn2) {
                    return vn1.ordinal > vn2.ordinal ? 1 : 0;
                }
            });

            int i;
            for (i = 0; i < vnl.size(); ++i) {
                if (vnl.get(i) == vn) {
                    break;
                }
            }

            closeConnection();

            return n == Neighbor.LEFT ? vnl.get((i - 1 + vnl.size()) % vnl.size()).viewId
                    : vnl.get((i + 1) % vnl.size()).viewId;
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
                if (!cursor.eof() && cursor.getInteger(ID) == viewId) {
                    image = Paths.get(getImageDirectory(), cursor.getString(IMAGE));
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

    public LinkModel getLinkById(int linkId) {
        LinkModel lm = null;
        if (linkId != 0) {
            try {
                prepareConnection(false);
                ISqlJetCursor cursor = db.getTable(LINKS).open();
                do {
                    if (!cursor.eof()) {
                        if ((int) cursor.getInteger(ID) == linkId) {
                            lm = new LinkModel(linkId, (int) cursor.getInteger(VIEWID), cursor.getFloat(X),
                                    cursor.getFloat(Y), (int) cursor.getInteger(VIEWREFID));
                        }
                    }
                } while (cursor.next());
                closeConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lm;
    }

    public List<LinkModel> getLinksByViewId(int viewId) {
        if (viewId == 0) {
            return null;
        }
        try {
            prepareConnection(false);
            ISqlJetCursor cursor = db.getTable(LINKS).open();
            List<LinkModel> links = null;
            do {
                if (!cursor.eof()) {
                    if ((int) cursor.getInteger(VIEWID) == viewId) {
                        if (links == null) {
                            links = new ArrayList<LinkModel>();
                        }
                        links.add(new LinkModel((int) cursor.getInteger(ID), (int) cursor.getInteger(VIEWID),
                                cursor.getFloat(X), cursor.getFloat(Y), (int) cursor.getInteger(VIEWREFID)));
                    }
                }
            } while (cursor.next());
            closeConnection();
            return links;
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
                if (!cursor.eof() && cursor.getInteger(ID) == location.id) {
                    cursor.delete();
                }
            } while (cursor.next());
            cursor = db.getTable(VIEWS).open();
            List<Integer> viewIds = new ArrayList<Integer>();
            do {
                if (!cursor.eof() && cursor.getInteger(LOCID) == location.id) {
                    Files.deleteIfExists(Paths.get(getImageDirectory(), cursor.getString(IMAGE)));
                    viewIds.add((int) cursor.getInteger(ID));
                    cursor.delete();
                }
            } while (cursor.next());
            cursor = db.getTable(LINKS).open();
            do {
                if (!cursor.eof()) {
                    int viewId = (int) cursor.getInteger(VIEWID);
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
                    if (cursor.getInteger(ID) == viewId) {
                        locid = (int) cursor.getInteger(LOCID);
                        cursor.delete();
                    }
                }
            } while (cursor.next());

            cursor = db.getTable(VIEWS).open();
            do {
                if (!cursor.eof()) {
                    if ((int) cursor.getInteger(LOCID) == locid) {
                        locid = 0;
                        break;
                    }
                }
            } while (cursor.next());
            if (locid > 0) {
                cursor = db.getTable(LOCATIONS).open(); // cursor.lookup ???
                do {
                    if (!cursor.eof()) {
                        if (cursor.getInteger(ID) == locid) {
                            cursor.delete();
                            break;
                        }
                    }
                } while (cursor.next());
            }
            cursor = db.getTable(LINKS).open();
            do {
                if (!cursor.eof()) {
                    if (cursor.getInteger(VIEWID) == viewId) {
                        cursor.delete();
                    }
                }
            } while (cursor.next());
            closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeLink(LinkModel link) {
        try {
            prepareConnection(true);
            ISqlJetCursor cursor = db.getTable(LINKS).open();
            do {
                if (!cursor.eof()) {
                    if ((int) cursor.getInteger(ID) == link.getId()) {
                        cursor.delete();
                    }
                }
            } while (cursor.next());
            closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getMaxId(String tableName) throws SqlJetException {
        ISqlJetCursor cursor = db.getTable(tableName).open().reverse();
        if (!cursor.eof()) {
            return (int) cursor.getInteger(ID);
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
            db.createTable(
                    "CREATE TABLE locations (" + ID + " INTEGER NOT NULL PRIMARY KEY, " + NAME + " TEXT NOT NULL)");
            db.createTable(
                    "CREATE TABLE views (" + ID + " INTEGER NOT NULL PRIMARY KEY, " + LOCID + " INTEGER NOT NULL, "
                            + ORDINAL + " INTEGER, " + IMAGE + " TEXT NOT NULL)");
            db.createTable(
                    "CREATE TABLE links (" + ID + " INTEGER NOT NULL PRIMARY KEY, " + VIEWID + " INTEGER NOT NULL, " + X
                            + " REAL, " + Y + " REAL, " + VIEWREFID + " INTEGER)");
            db.commit();
        }
        db.beginTransaction(write ? SqlJetTransactionMode.WRITE : SqlJetTransactionMode.READ_ONLY);
    }

    private void closeConnection() throws SqlJetException {
        db.commit();
        db.close();
    }

    private String getImageDirectory() {
        String dir = "images";
        int ind = dbFilePath.lastIndexOf("/");
        if (ind > 0) {
            dir = dbFilePath.substring(0, ind + 1) + dir;
        }
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

    class ImageCompressor extends Thread {
        private Path imagePath;

        public ImageCompressor(Path imagePath) {
            this.imagePath = imagePath;
            start();
        }

        public void run() {
            try {
                BufferedImage bi = ImageIO.read(imagePath.toFile());
                int scaleX = 600;
                int scaleY = 600 * bi.getHeight() / bi.getWidth();
                Image image = bi.getScaledInstance(scaleX, scaleY, Image.SCALE_SMOOTH);
                BufferedImage bi2;
                if (image instanceof BufferedImage) {
                    bi2 = (BufferedImage) image;
                } else {
                    bi2 = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
                    Graphics2D bGr = bi2.createGraphics();
                    bGr.drawImage(image, 0, 0, null);
                    bGr.dispose();
                }
                String ext = imagePath.toString().substring(imagePath.toString().lastIndexOf(".") + 1).toLowerCase();
                ImageIO.write(bi2, ext, imagePath.toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}