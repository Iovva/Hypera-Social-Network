package service;

import domain.*;
import exceptions.ValidatorException;
import javafx.scene.image.Image;
import repository.database.EventDbRepo;
import repository.database.FriendshipDbRepo;
import repository.database.MessageDbRepo;
import repository.database.UserDbRepo;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Service {

    private final UserDbRepo repoUser;
    private final FriendshipDbRepo repoFriendship;
    private final MessageDbRepo repoMessage;
    private final EventDbRepo repoEvent;

    /**
     * constructor
     * @param repoUser UserRepoFile
     * @param repoFriendship FriendshipRepoFile
     */
    public Service(UserDbRepo repoUser, FriendshipDbRepo repoFriendship, MessageDbRepo repoMessage, EventDbRepo repoEvent) {
        this.repoUser = repoUser;
        this.repoFriendship = repoFriendship;
        this.repoMessage = repoMessage;
        this.repoEvent = repoEvent;
    }

    public User addUser(User user) {
        return repoUser.save(user);
    }

    /**
     * removes an user to the repo
     * @param id long
     * @return User, if removed, null otherwise
     */
    public User removeUser(long id){
        /*
        repoFile
        if (repoUser.findOne(id) != null)
            repoFriendship.removeUserInFriendship(repoUser.findOne(id));
        return repoUser.delete(id);
        */
        return  repoUser.delete(id);
    }

    public User updateUser(long id, String firstName, String lastName, String password){
        User user = new User(id, firstName, lastName, password);
        return repoUser.update(user);
    }

    /**
     * returns every user
     * @return Iterable
     */
    public Iterable<User> getUsers(){
        return repoUser.findAll();
    }

    public User findUser(long id){
        return repoUser.findOne(id);
    }

    public User findUserByName(String firstName, String lastName){
        return repoUser.findOneByName(firstName, lastName);
    }

    public Boolean loginUser(User user){
        return repoUser.loginUser(user);
    }

    public Boolean checkIfAdmin (User user){
        return Objects.equals(user.getFirstName(), "Roby") || Objects.equals(user.getFirstName(), "Sami");
    }

    private void checkInUsers(User user1, User user2){
        String error = "";
        if(user1 == null)
            error += "There is no user with the first ID in the database!\n";
        if(user2 == null)
            error += "There is no user with the second ID in the database!\n";
        if(!error.equals(""))
            throw new ValidatorException(error);
    }

    /**
     * adds a frienship to the repo
     * @param id1 long
     * @param id2 long
     * @return friendship if it existed, null otherwise
     */
    public Friendship addFriendship(long id1, long id2, LocalDateTime date) {
        User user1 = repoUser.findOne(id1);
        User user2 = repoUser.findOne(id2);

        checkInUsers(user1, user2);

        Friendship friendship = new Friendship(new Tuple<>(user1, user2), date, "approved");
        return repoFriendship.save(friendship);
    }

    /**
     * removes a friendship from the repo
     * @param id1 long
     * @param id2 long
     * @return Friendship
     */
    public Friendship removeFriendship(long id1, long id2){
        User user1 = repoUser.findOne(id1);
        User user2 = repoUser.findOne(id2);

        checkInUsers(user1, user2);

        return repoFriendship.delete(new Tuple<>(user1, user2));
    }


    /**
     * Returneaza a list of all users in all frienships
     * @return Vector
     */
    public List<Friendship> getFriendships(){
        List<Friendship> list = new ArrayList<>();

        for (Friendship x : this.repoFriendship.findAll()){
            list.add(new Friendship(
                    x.getId().getLeft(),
                    x.getId().getRight(),
                    x.getDate(),x.getStatus()));
        }
        return list;
    }

    public List<FriendshipDTO> getFriendshipsForUsers(Long id){
        List<FriendshipDTO> list = new ArrayList<>();
        StreamSupport.stream(repoFriendship.findAll().spliterator(), false).    // din lista in stream
                filter(x->Objects.equals(x.getId().getLeft().getId(), id) ||
                Objects.equals(x.getId().getRight().getId(), id)).
                forEach(x->{
                    list.add(new FriendshipDTO(x.getId().getLeft().getId(), x.getId().getRight().getId(), x.getDate(), x.getStatus()));
                });
        return list;
    }

    public List<FriendshipDTO> getSentFriendRequests(Long id){
        List<FriendshipDTO> list = new ArrayList<>();
        StreamSupport.stream(repoFriendship.findAll().spliterator(), false).    // din lista in stream
                filter(x->id.equals(x.getId().getLeft().getId())).
                forEach(x->{
                    User sender = x.getId().getLeft();
                    User receiver = x.getId().getRight();

                    String fullName = receiver.getFirstName() + " " + receiver.getLastName();
                    list.add(new FriendshipDTO(sender.getId(), receiver.getId(), x.getDate(), x.getStatus(), fullName));
                });

        Comparator<FriendshipDTO> friendshipDTOComparator = Comparator.comparing(FriendshipDTO::getDate).reversed();
        list.sort(friendshipDTOComparator);

        return list;
    }

    public List<FriendshipDTO> getReceivedFriendRequests(Long id){
        List<FriendshipDTO> list = new ArrayList<>();
        StreamSupport.stream(repoFriendship.findAll().spliterator(), false).    // din lista in stream
                filter(x-> id.equals(x.getId().getRight().getId())).
                forEach(x->{
                    User sender = x.getId().getLeft();
                    User receiver = x.getId().getRight();

                    String fullName = sender.getFirstName() + " " + sender.getLastName();
                    list.add(new FriendshipDTO(sender.getId(), receiver.getId(), x.getDate(), x.getStatus(), fullName));
                });

        Comparator<FriendshipDTO> friendshipDTOComparator = Comparator.comparing(FriendshipDTO::getDate).reversed();
        list.sort(friendshipDTOComparator);

        return list;
    }

    public List<Tuple<User, LocalDateTime>> findFriendships(Long id){
        List<Tuple<User, LocalDateTime>> list = new ArrayList<>();
        StreamSupport.stream(repoFriendship.findAll().spliterator(), false).    // from list so stream
                filter(x->Objects.equals(x.getId().getLeft().getId(), id) ||
                Objects.equals(x.getId().getRight().getId(), id)).
                forEach(x->{
                    if(x.getStatus().equals("approved"))
                        if (!Objects.equals(x.getId().getLeft().getId(), id))
                            list.add(new Tuple<>(x.getId().getLeft(), x.getDate()));
                        else
                            list.add(new Tuple<>(x.getId().getRight(), x.getDate()));
                });
        return list;
    }

    public List<UserDTO> findFriendshipsWithDate(Long id, Boolean paged, long limit, long offset){
        List<UserDTO> list = new ArrayList<>();
        Iterable<Friendship> friendships;
        if (!paged)
            friendships = repoFriendship.findAll();
        else
            friendships = repoFriendship.findAllPaged(id, limit, offset);

        StreamSupport.stream(friendships.spliterator(), false).    // from list so stream
                filter(x->Objects.equals(x.getId().getLeft().getId(), id) ||
                Objects.equals(x.getId().getRight().getId(), id)).
                forEach(x->{
                    if(x.getStatus().equals("approved"))
                        if (!Objects.equals(x.getId().getLeft().getId(), id))
                            list.add(new UserDTO(x.getId().getLeft().getId(), x.getId().getLeft().getFirstName(), x.getId().getLeft().getLastName(),
                                    x.getDate(), getImage(x.getId().getLeft().getId())));
                        else
                            list.add(new UserDTO(x.getId().getRight().getId(), x.getId().getRight().getFirstName(), x.getId().getRight().getLastName(),
                                    x.getDate(), getImage(x.getId().getRight().getId())));
                });
        return list;
    }


    public String getStatus(long id1, long id2){
        for (Friendship fr : repoFriendship.findAll())
            if(fr.getId().getLeft().getId() == id1 && fr.getId().getRight().getId() == id2
                || fr.getId().getLeft().getId() == id2 && fr.getId().getRight().getId() == id1)
                return fr.getStatus();
        return "";
    }

    public Friendship sendFriendRequest(long id1, long id2, LocalDateTime date) {
        User user1 = repoUser.findOne(id1);
        User user2 = repoUser.findOne(id2);

        checkInUsers(user1, user2);

        Friendship friendship = new Friendship(new Tuple<>(user1, user2), date, "pending");
        return repoFriendship.save(friendship);
    }

    public Friendship respondFriendship(long receiver, long sender, String response){
        User user1 = repoUser.findOne(receiver);
        User user2 = repoUser.findOne(sender);

        checkInUsers(user1, user2);

        Friendship newFriendship = new Friendship(user1, user2, LocalDateTime.now(), response);
        return repoFriendship.update(newFriendship);
    }

    public Message addMessage(long idFrom, List<Long> toIDList, String message, long idReply){
        User from = repoUser.findOne(idFrom);

        List<User> to = new ArrayList<>();
        for(long toID : toIDList){
            User toUser = repoUser.findOne(toID);
            to.add(toUser);
        }
        LocalDateTime date = LocalDateTime.now();
        Message reply = repoMessage.findOne(idReply, false);

        return repoMessage.save(new Message(from, to, message, date, reply));
    }

    public List<Message> getConversation(long id1, long id2, boolean decorate, boolean getAll){

        List<Message> filtered = new ArrayList<>();

        for(Message m : repoMessage.findAll())
            if (m.getFrom().getId() == id1 || m.getFrom().getId() == id2)
                for (User toU : m.getTo())
                    if (toU.getId() == id1 || toU.getId() == id2 || getAll) {
                        if (decorate){
                            String decorated = "  >  ";
                            if (m.getReply() != null)
                                decorated += "replying to: " + m.getReply().getMessage() + "  >>>  ";
                            m.setMessage(decorated + m.getMessage());
                        }
                        filtered.add(m);
                        break;
                    }

        Comparator<Message> messageComparator = Comparator.comparing(Message::getDate);
        filtered.sort(messageComparator);
        return filtered;
    }

    public List<Message> getIncomingMessages(long id, boolean decorate){

        List<Message> filtered = new ArrayList<>();

        for(Message m : repoMessage.findAll())
            for (User toU : m.getTo())
                if (toU.getId() == id) {
                    if (decorate){
                        String decorated = "  >  ";
                        if (m.getReply() != null)
                            decorated += "replying to: " + m.getReply().getMessage() + "  >>>  ";
                        m.setMessage(decorated + m.getMessage());
                    }
                    filtered.add(m);
                    break;
                }

        Comparator<Message> messageComparator = Comparator.comparing(Message::getDate);
        filtered.sort(messageComparator);
        return filtered;
    }

    public List<Message> getConversationInDate(long id1, long id2, boolean decorate, boolean getAll, LocalDateTime date1, LocalDateTime date2){

        List<Message> messagesUnfiltered = getConversation(id1, id2, decorate, getAll);
        return messagesUnfiltered.stream()
                .filter(x->x.getDate().isAfter(date1) && x.getDate().isBefore(date2))
                .collect(Collectors.toList());
    }

    public List<Message> getIncomingMessagesInDate(long id, boolean decorate, LocalDateTime date1, LocalDateTime date2){


        List<Message> messagesUnfiltered = getIncomingMessages(id, decorate);
        System.out.println(messagesUnfiltered.size());
        return messagesUnfiltered.stream()
                .filter(x->x.getDate().isAfter(date1) && x.getDate().isBefore(date2))
                .collect(Collectors.toList());
    }

    public List<User> getUsersAddedInDate(long id, LocalDateTime date1, LocalDateTime date2){

        List<User> usersAdded = new ArrayList<>();

        for (Friendship f : repoFriendship.findAll()){
            if (f.getDate().isAfter(date1) && f.getDate().isBefore(date2))
                if (f.getId().getLeft().getId() == id)
                    usersAdded.add(f.getId().getRight());
                else if (f.getId().getRight().getId() == id)
                    usersAdded.add(f.getId().getLeft());
        }

        return usersAdded;
    }

    public List<MessageDTO> getConversationDTO(long myID, long friendID){
        Image fromImage = getImage(friendID);
        Image myImage = getImage(myID);

        List<MessageDTO> filtered = new ArrayList<>();

        for(Message m : repoMessage.findAll())
            if (m.getFrom().getId() == myID && m.getTo().get(0).getId() == friendID ||
                    m.getFrom().getId() == friendID && m.getTo().get(0).getId() == myID){
                    String replyString = "";
                    if(m.getReply() != null){
                        replyString = "replying to: \"" + m.getReply().getMessage() + "\":\n";
                    }
                    MessageDTO messageDTO;
                    if (m.getFrom().getId() == myID)
                        messageDTO = new MessageDTO(fromImage,new Message(null,null,"",null,null),m,myImage, replyString);
                    else
                        messageDTO = new MessageDTO(fromImage,m,new Message(null,null,"",null,null),myImage, replyString);
                    filtered.add(messageDTO);
                }

        Comparator<MessageDTO> messageComparator = Comparator.comparing(MessageDTO::getMessageDate);
        filtered.sort(messageComparator);
        return filtered;
    }

    public Iterable<Message> getMessages(){
        return repoMessage.findAll();
    }

    public Image getImage(Long idUser) {
        String default_path = "local/MainScreens/HomeScreen/default.png";
        File currentDirFile = new File("");
        Image image;
        try {
            String path = currentDirFile.toURI() + "/ProfilePictures/" + idUser;
            image = new Image(path,256,256,false,true);
            if(image.isError())
                image = new Image(default_path,256,256,false,true);
        }
        catch(Exception ex) {
            image = new Image(default_path,256,256,false,true);
        }
        return image;
    }

    public EventMeet addEvent(EventMeet event){
        return repoEvent.save(event);
    }

    public EventMeet removeEvent(Long id){
        return repoEvent.delete(id);
    }

    public EventMeet updateEvent(EventMeet newEvent){
        return repoEvent.update(newEvent);
    }

    public List<EventMeet> getEvents(){
        List<EventMeet> events = new ArrayList<>();
        for(EventMeet e : repoEvent.findAll())
            events.add(e);
        return events;
    }

}
