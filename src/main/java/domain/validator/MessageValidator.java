package domain.validator;

import domain.Message;
import domain.User;
import exceptions.RepositoryException;
import exceptions.ValidatorException;

public class MessageValidator implements Validator<Message>{
    @Override
    public void validate(Message entity) throws ValidatorException {
        String error = "";
        if(entity.getFrom() == null)
            error += "Sender not found !\n";
        boolean v1 = false;
        boolean v2 = false;
        for(User toUser : entity.getTo()) {
            if (toUser == null && !v1) {
                error += "A reciver was not found !\n";
                v1 = true;
            }
            if(toUser != null && !v2 && entity.getFrom() != null)
                if (entity.getFrom().getId().equals(toUser.getId())) {
                    error += "You can't send a message to yourself dummy !\n";
                    v2 = true;
                }
        }
        if(entity.getDate() == null)
            error += "Invalid date !\n";
        if(entity.getMessage().strip().equals(""))
            error += "Invalid text message !\n";

        Message reply = entity.getReply();
        if(reply != null){      // if reply != null => entity.getTo contine doar un element
            if(!(reply.getTo().contains(entity.getFrom()) &&
                    entity.getTo().get(0).equals(reply.getFrom())))
                if (!(entity.getFrom().equals(reply.getFrom()) &&
                        reply.getTo().contains( entity.getTo().get(0))))
                    error += "You can't reply to other's messages!";

        }

        if(!error.equals(""))
            throw new RepositoryException(error);
    }
}
