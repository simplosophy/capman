package io.capman.common.client.handler;

import io.capman.common.exception.CMException;
import io.capman.protobuf.Internal;

/**
 * Created by flying on 7/9/16.
 */
public interface RequestSender {
    /**
     * Sends a single message asynchronously, and notifies the {@link Listener}
     * when the request is finished sending, when the response has arrived, and/or when an error
     * occurs.
     *
     * @param request
     * @param listener
     * @throws io.capman.common.exception.CMException
     */
    void sendAsynchronousRequest(
            Internal.InternalRequest.Builder request,
            final Listener listener
    )
            throws CMException;
    /**
     * Closes the channel
     */
    void close();

    /**
     * Checks whether the channel has encountered an error. This method is a shortcut for:
     *
     * <code>
     * return (getError() != null);
     * </code>
     *
     * @return {@code true} if the {@link RequestSender} is broken
     */
    boolean hasError();

    /**
     * Returns the {@link CMException} representing the error the channel encountered, if any.
     *
     * @return An instance of {@link CMException} or {@code null} if the channel is still good.
     */
    CMException getError();

    /**
     * The listener interface that must be implemented for callback objects passed to
     * {@link #sendAsynchronousRequest}
     */
    public interface Listener {
        /**
         * This will be called when the request has successfully been written to the transport
         * layer (e.g. socket)
         */
        void onRequestSent();

        /**
         * This will be called when a full response to the request has been received
         *
         * @param message The response buffer
         */
        void onResponseReceived(Internal.InternalResponse message);

        /**
         * This will be called if the channel encounters an error before the request is sent or
         * before a response is received
         *
         * @param requestException A {@link CMException} describing the problem that was encountered
         */
        void onChannelError(CMException requestException);
    }
}
