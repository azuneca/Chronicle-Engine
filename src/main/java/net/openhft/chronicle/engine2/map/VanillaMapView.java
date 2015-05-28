package net.openhft.chronicle.engine2.map;

import net.openhft.chronicle.engine2.api.Asset;
import net.openhft.chronicle.engine2.api.RequestContext;
import net.openhft.chronicle.engine2.api.View;
import net.openhft.chronicle.engine2.api.map.KeyValueStore;
import net.openhft.chronicle.engine2.api.map.MapView;
import net.openhft.chronicle.engine2.session.LocalSession;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.Set;

/**
 * Created by peter on 22/05/15.
 */
public class VanillaMapView<K, MV, V> extends AbstractMap<K, V> implements MapView<K, MV, V> {
    private final boolean putReturnsNull;
    private final boolean removeReturnsNull;
    private Asset asset;
    private KeyValueStore<K, MV, V> kvStore;

    public VanillaMapView(RequestContext<KeyValueStore<K, MV, V>> context) {
        this(context.parent(), context.item(), context.putReturnsNull(), context.removeReturnsNull());
    }

    public VanillaMapView(Asset asset, KeyValueStore<K, MV, V> kvStore, boolean putReturnsNull, boolean removeReturnsNull) {
        this.asset = asset;
        this.kvStore = kvStore;
        this.putReturnsNull = putReturnsNull;
        this.removeReturnsNull = removeReturnsNull;
    }

    @Override
    public View forSession(LocalSession session, Asset asset) {
        return new VanillaMapView<K, MV, V>(asset, View.forSession(kvStore, session, asset), putReturnsNull, removeReturnsNull);
    }

    @Override
    public void asset(Asset asset) {
        this.asset = asset;
    }

    @Override
    public Asset asset() {
        return asset;
    }

    @Override
    public void underlying(KeyValueStore<K, MV, V> underlying) {
        this.kvStore = underlying;
    }

    @Override
    public KeyValueStore<K, MV, V> underlying() {
        return kvStore;
    }

    @Override
    public V get(Object key) {
        return kvStore.getUsing((K) key, null);
    }

    @Override
    public V put(K key, V value) {
        if (putReturnsNull) {
            kvStore.put(key, value);
            return null;

        } else {
            return kvStore.getAndPut(key, value);
        }
    }

    @Override
    public V remove(Object key) {
        if (removeReturnsNull) {
            kvStore.remove((K) key);
            return null;

        } else {
            return kvStore.getAndRemove((K) key);
        }
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        //noinspection unchecked
        return asset.acquireView(Set.class, Entry.class, "");
    }

    @Override
    public void clear() {
        kvStore.clear();
    }

    @Override
    public V putIfAbsent(@NotNull K key, V value) {
        throw new UnsupportedOperationException("todo");
    }

    @Override
    public boolean remove(@NotNull Object key, Object value) {
        throw new UnsupportedOperationException("todo");
    }

    @Override
    public boolean replace(@NotNull K key, @NotNull V oldValue, @NotNull V newValue) {
        throw new UnsupportedOperationException("todo");
    }

    @Override
    public V replace(@NotNull K key, @NotNull V value) {
        return kvStore.replace(key, value);
    }
}
