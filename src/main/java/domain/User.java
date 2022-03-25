package domain;

import java.util.Objects;

public class User extends Entity<Long> {
    private String firstName;
    private String lastName;
    private String password;

    public User(Long id, String firstName, String lastName) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = "/";
    }

    public User(Long id, String firstName, String lastName, String password){
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    public User(String firstName, String lastName) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = "/";
    }

    public User(String firstName, String lastName, String password){
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    /**
     * @return String
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName String
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return String
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName String
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    /**
     * overrides to string method from Object
     * @return user parameters as string
     */
    @Override
    public String toString() {
        return "id = " + getId() + '\n' +
                "firstName = " + firstName + "; " +
                "lastName = " + lastName + "; " +
                '\n';
    }


    /**
     *  overrides equals method from Object
     * @param o Object
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getId().equals(that.getId());
    }

    /**
     *  overrides hashCode method from Object
     * @return int hash code, based on first and last name
     */
    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getId());
    }
}