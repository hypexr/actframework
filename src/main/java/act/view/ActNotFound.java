package act.view;

import act.Act;
import act.app.SourceInfo;
import act.util.ActError;
import org.osgl.mvc.result.NotFound;

import java.lang.reflect.Method;
import java.util.List;

public class ActNotFound extends NotFound implements ActError {

    private SourceInfo sourceInfo;

    public ActNotFound() {
        super();
        if (Act.isDev()) {
            loadSourceInfo();
        }
    }

    public ActNotFound(String message, Object... args) {
        super(message, args);
        if (Act.isDev()) {
            loadSourceInfo();
        }
    }

    public ActNotFound(Method method) {
        super("null value returned from %s.%s()", method.getDeclaringClass().getName(), method.getName());
        if (Act.isDev()) {
            loadSourceInfo(method);
        }
    }

    public ActNotFound(Throwable cause, String message, Object ... args) {
        super(cause, message, args);
        if (Act.isDev()) {
            loadSourceInfo();
        }
    }

    public ActNotFound(Throwable cause) {
        super(cause);
        if (Act.isDev()) {
            loadSourceInfo();
        }
    }

    private void loadSourceInfo() {
        doFillInStackTrace();
        Throwable cause = getCause();
        sourceInfo = Util.loadSourceInfo(null == cause ? getStackTrace() : cause.getStackTrace(), ActNotFound.class);
    }

    private void loadSourceInfo(Method method) {
        sourceInfo = Util.loadSourceInfo(method);
    }

    @Override
    public Throwable getCauseOrThis() {
        Throwable cause = super.getCause();
        return null == cause ? this : cause;
    }

    public SourceInfo sourceInfo() {
        return sourceInfo;
    }

    public List<String> stackTrace() {
        Throwable cause = getCause();
        ActError root = this;
        if (null == cause) {
            cause = this;
            root = null;
        }
        return Util.stackTraceOf(cause, root);
    }

    @Override
    public boolean isErrorSpot(String traceLine, String nextTraceLine) {
        return false;
    }

    public static NotFound create() {
        return Act.isDev() ? new ActNotFound() : NotFound.get();
    }

    public static NotFound create(String msg, Object... args) {
        return Act.isDev() ? new ActNotFound(msg, args) : NotFound.of(msg, args);
    }

    public static NotFound create(Method method) {
        return Act.isDev() ? new ActNotFound(method) : NotFound.get();
    }

    public static NotFound create(Throwable cause, String msg, Object ... args) {
        return Act.isDev() ? new ActNotFound(cause, msg, args) : NotFound.of(cause, msg, args);
    }

    public static NotFound create(Throwable cause) {
        return Act.isDev() ? new ActNotFound(cause) : NotFound.of(cause);
    }
}
