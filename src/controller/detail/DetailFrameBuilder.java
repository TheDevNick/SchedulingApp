package controller.detail;

import controller.BaseController;
import model.base.BaseModel;

import java.util.function.Function;

/**
 * Instantiates a form controller of type T for model R
 *
 * @param <R> a Record subclass
 * @param <T> a Form subclass
 */
public abstract class DetailFrameBuilder<R extends BaseModel, T extends DetailFrame<R>> extends BaseController {
    private final Class<R> modelClass;

    public DetailFrameBuilder(Class<R> modelClass) {
        this.modelClass = modelClass;
    }

    /**
     * get title of frame
     *
     * @param frameType the mode the frame opens in
     * @return the title for the frame window
     */
    protected String getTitle(FrameType frameType) {
        return resourceBundle.getString(String.format("form.%s.%s", frameType.toString().toLowerCase(), modelClass.getSimpleName().toLowerCase()));
    }

    /**
     * Returns an instance of the Form for the Record
     *
     * @param frameType the mode to open the frame in
     * @param row   the row to create/read/update
     * @param function the callback that will act on the row after editing has finished
     * @return the form controller instance
     */
    abstract public T getInstance(FrameType frameType, R row, Function<R, Boolean> function);
}
