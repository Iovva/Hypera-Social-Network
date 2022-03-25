package domain.validator;

import domain.Friendship;
import exceptions.ValidatorException;

import java.util.Objects;

public class FriendshipValidator implements Validator<Friendship> {

    @Override
    public void validate(Friendship friendship) throws ValidatorException {
        String error = "";
        if (friendship.getId().getLeft().getId() < 0)
            error += "Invalid left ID!\n";

        if (friendship.getId().getRight().getId() < 0)
            error += "Invalid right ID!\n";


        if (Objects.equals(friendship.getId().getRight(), friendship.getId().getLeft()))
            error += "You can't friend request yourself!\n";

        if (!(friendship.getStatus().equals("approved") || friendship.getStatus().equals("pending") || friendship.getStatus().equals("rejected")))
            error += "Invalid status!\n";

        if (Objects.equals(error, ""))
            return;

        throw new ValidatorException(error);

    }
}

