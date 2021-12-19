package swd.team11.coviddatabase.utils;

/**
 * Class for representing the required methods for implementing a buffer
 * @param <T>   Type of data stored in buffer
 */
public interface Buffer<T> {

    /**
     * Method for adding objects to buffer
     * @param value                 Object to add
     * @throws InterruptedException Thrown when buffer interrupted
     */
    public void blockingPut(T value) throws InterruptedException;

    /**
     * Method for getting objects from buffer
     * @return                      Next object in buffer
     * @throws InterruptedException Thrown when buffer interrupted
     */
    public T blockingGet() throws InterruptedException;

}
