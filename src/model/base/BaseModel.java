package model.base;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class BaseModel {
    /**
     * resource bundle
     */
    public static ResourceBundle resourceBundle;

    /**
     * locale
     */
    public static Locale locale;

    /**
     * id of model
     */
    protected long ID;

    /**
     * Constructor
     * @param ID - model id
     */
    public BaseModel(long ID) {
        this.ID = ID;
    }

    /**
     * Get id of model
     * @return id
     */
    public long getID() {
        return ID;
    }

    /**
     * Set id of model
     * @param ID - model id
     */
    public void setID(long ID) {
        this.ID = ID;
    }

    /**
     * error message
     * @param field the field that is empty
     * @return the message for the error
     */
    private String fieldNotEmptyMsg(String field) {
        return String.format(resourceBundle.getString("error.empty")
                .replace("%{field}",
                        resourceBundle.getString(String.format("%s.%s",
                                getClass().getSimpleName().toLowerCase(),
                                field)))
                .replace("%{issue}", resourceBundle.getString("issue.empty")),
                field,
                resourceBundle.getString("issue.empty"));
    }

    /**
     * validate fields
     * @throws ValidateException the invalid field error
     */
    public void validate() throws ValidateException {
        for (final Field declaredField : getClass().getDeclaredFields()) {
            try {
                declaredField.setAccessible(true);
                Object value = declaredField.get(this);

                if (value instanceof String) {
                    if (((String) value).length() == 0)
                        throw new ValidateException(fieldNotEmptyMsg(declaredField.getName()));
                } else if (value instanceof Long) {
                    if ((Long) value == 0)
                        throw new ValidateException(fieldNotEmptyMsg(declaredField.getName()));
                } else if (!(value instanceof LocalDateTime))
                    throw new ValidateException(resourceBundle.getString("issue.invalid"));
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }

        customValidate();
    }

    /**
     * custom validate
     * @throws ValidateException the invalid field error
     */
    protected abstract void customValidate() throws ValidateException;
}
