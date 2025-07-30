package com.cxj.collections;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/** Concurrent identity hash set.
 *
 * <p>Similar in role to the ConcurrentHashSet, implements the Set interface
 * using reference equality instead of object equality.</p>
 *
 * @param <E> The element type.
 */
@SuppressWarnings("unused")
@ThreadSafe
public class ConcurrentIdentityHashSet<E>
        extends AbstractSet<E>
{
    /** {@inheritDoc}
     */
    @Override
    public boolean add(final E object)
    {
        return _map.put(new _Key(object), object) == null;
    }

    /** {@inheritDoc}
     */
    @Override
    public void clear()
    {
        _map.clear();
    }

    /** {@inheritDoc}
     */
    @Override
    public boolean contains(final Object object)
    {
        return _map.containsKey(new _Key(object));
    }

    /** {@inheritDoc}
     */
    @Nonnull
    @Override
    public Iterator<E> iterator()
    {
        return _map.values()
                .iterator();
    }

    /** {@inheritDoc}
     */
    @Override
    public boolean remove(final Object object)
    {
        return _map.remove(new _Key(object)) != null;
    }

    /** {@inheritDoc}
     */
    @Override
    public int size()
    {
        return _map.size();
    }

    private final Map<_Key, E> _map = new ConcurrentHashMap<>();

    /** Key.
     */
    @Immutable
    private static final class _Key
    {
        /** Constructs an instance.
         *
         * @param object The object represented by the key.
         */
        _Key(@Nonnull final Object object)
        {
            _object = object;
        }

        /** {@inheritDoc}
         */
        @Override
        public boolean equals(final Object object) {
            return object instanceof _Key && ((_Key) object)._object == _object;
        }

        /** {@inheritDoc}
         */
        @Override
        public int hashCode()
        {
            return System.identityHashCode(_object);
        }

        private final Object _object;
    }
}