package domain;

public class Entity <E> {

    private E id;

    public Entity(E id) {
        this.id = id;
    }

    public Entity() {

    }

    public E getId() {
        return id;
    }
    public void setId(E id) {
        this.id = id;
    }

}

