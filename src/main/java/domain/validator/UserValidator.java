package domain.validator;

import domain.User;
import exceptions.ValidatorException;

import java.util.Objects;

public class UserValidator implements Validator<User> {

    @Override
    public void validate(User user) throws ValidatorException {
        String error = "";


        if (Objects.equals(user.getFirstName(), ""))
            error += "Invalid First Name!\n";
        if (Objects.equals(user.getLastName(), ""))
            error += "Invalid Last Name!\n";

        if (Objects.equals(error, ""))
            return;
        throw new ValidatorException(error);

    }
}
