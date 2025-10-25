package sv.edu.udb.www.desafio2.validators;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("svPhoneValidator")
public class SVPhoneValidator implements Validator<String> {

    private static final String REGEX = "^[2367]\\d{3}-\\d{4}$";

    @Override
    public void validate(FacesContext context, UIComponent component, String value) throws ValidatorException {
        if (value == null || value.trim().isEmpty()) {
            return;
        }

        if (!value.matches(REGEX)) {
            FacesMessage msg = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Teléfono inválido",
                    "Formato requerido ####-#### (debe comenzar con 2, 3, 6 o 7)."
            );
            throw new ValidatorException(msg);
        }
    }
}
