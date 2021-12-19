package swd.team11.coviddatabase.utils;

/**
 * Class designed to queue data to be processed by QueryProcessor threads
 * @param <T>   Type of data stored
 * @see swd.team11.coviddatabase.server.mysql.QueryProcessor
 */
public class CircularBuffer<T> implements Buffer<T> {

    /**
     * The buffer elements are stored in
     */
    public final T[] buffer;
    /**
     * Number of cells occupied in the buffer
     */
    private int occupiedCells = 0;
    /**
     * Next index write from
     */
    private int writeIndex = 0;
    /**
     * Next index to read from
     */
    private int readIndex = 0;

    /**
     * Creates a circular buffer with specified size.
     * @param size  Size of buffer
     */
    public CircularBuffer(int size) {
        buffer = (T[]) new Object[size];
    }

    /**
     * Adds an element to the buffer, if buffer is full it will wait until an cell
     * is available.
     * @param value                     Value to add
     * @throws InterruptedException     If buffer unable to function
     */
    @Override
    public synchronized void blockingPut(T value) throws InterruptedException {
        while (occupiedCells == buffer.length) {
            wait();
        }

        buffer[writeIndex] = value;

        writeIndex = (writeIndex + 1) % buffer.length;

        ++occupiedCells;
        notifyAll();
    }

    /**
     * Gets the next occupied cell and returns it, if no occupied cells available waits
     * until a new object has been added.
     * @return                          Object in queue
     * @throws InterruptedException     If buffer non functional
     */
    @Override
    public synchronized T blockingGet() throws InterruptedException {
        while (occupiedCells == 0) {
            wait();
        }

        T readValue = buffer[readIndex];

        readIndex = (readIndex + 1) % buffer.length;
        --occupiedCells;

        notifyAll();

        return readValue;
    }


}
