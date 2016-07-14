package io.capman.common;

/**
 * Created by flying on 7/7/16.
 */
public class RequestContexts {

    private static final ThreadLocal<RequestContext> threadLocalContext = new ThreadLocal<RequestContext>();

    private RequestContexts(){
    }

    public static RequestContext current(){
        return threadLocalContext.get();
    }

    public static void setRequestContext(RequestContext context) {
        threadLocalContext.set(context);
    }

    public static void removeCurrentContext(){
        threadLocalContext.remove();
    }
}
