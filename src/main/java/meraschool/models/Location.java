package meraschool.models;

public class Location {
    public int id;
    public String name;

    public Location(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return id + ": " + name;
    }
}
