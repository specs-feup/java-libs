/**
 * Copyright 2016 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package org.suikasoft.jOptions.Datakey;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.suikasoft.jOptions.gui.KeyPanelProvider;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;

public abstract class ADataKey<T> implements DataKey<T> {

    private final String id;
    private transient final Supplier<? extends T> defaultValueProvider;
    private transient final StringCodec<T> decoder;
    private transient final CustomGetter<T> customGetter;
    private transient final KeyPanelProvider<T> panelProvider;
    private transient final String label;
    private transient final StoreDefinition definition;
    private transient final Function<T, T> copyFunction;

    protected ADataKey(String id, Supplier<? extends T> defaultValueProvider, StringCodec<T> decoder,
            CustomGetter<T> customGetter, KeyPanelProvider<T> panelProvider, String label,
            StoreDefinition definition, Function<T, T> copyFunction) {

        assert id != null;

        this.id = id;
        this.defaultValueProvider = defaultValueProvider;
        this.decoder = decoder;
        this.customGetter = customGetter;
        this.panelProvider = panelProvider;
        this.label = label;
        this.definition = definition;
        this.copyFunction = copyFunction;
    }

    protected ADataKey(String id, Supplier<T> defaultValue) {
        this(id, defaultValue, null, null, null, null, null, null);
    }

    @Override
    public String getName() {
        return id;
    }

    @Override
    public String toString() {
        return DataKey.toString(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        ADataKey<?> other = (ADataKey<?>) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    abstract protected DataKey<T> copy(String id, Supplier<? extends T> defaultValueProvider, StringCodec<T> decoder,
            CustomGetter<T> customGetter, KeyPanelProvider<T> panelProvider, String label,
            StoreDefinition definition, Function<T, T> copyFunction);

    @Override
    public Optional<StringCodec<T>> getDecoder() {
        return Optional.ofNullable(decoder);
    }

    @Override
    public DataKey<T> setDecoder(StringCodec<T> decoder) {

        // Adding interface 'Serializable', so that it can save lambda expressions
        StringCodec<T> serializableDecoder = getSerializableDecoder(decoder);
        // StringCodec<T> serializableDecoder = (StringCodec<T> & Serializable) value -> decoder.decode(value);
        // return copy(id, defaultValueProvider, serializableDecoder, customGetter,
        return copy(id, defaultValueProvider, serializableDecoder, customGetter,
                panelProvider, label, definition, copyFunction);
    }

    private static <T> StringCodec<T> getSerializableDecoder(StringCodec<T> decoder) {
        if (decoder instanceof Serializable) {
            return decoder;
        }

        return StringCodec.newInstance(decoder::encode, decoder::decode);
        // return (StringCodec<T> & Serializable) value -> decoder.decode(value);
    }

    @Override
    public Optional<T> getDefault() {
        if (defaultValueProvider != null) {
            return Optional.ofNullable(defaultValueProvider.get());
        }

        return Optional.empty();
    }

    @Override
    public DataKey<T> setDefault(Supplier<? extends T> defaultValueProvider) {
        return copy(id, defaultValueProvider, decoder, customGetter, panelProvider, label, definition, copyFunction);
    }

    @Override
    public DataKey<T> setCustomGetter(CustomGetter<T> customGetter) {
        // Adding interface 'Serializable', so that it can save lambda expressions
        CustomGetter<T> serializableGetter = (CustomGetter<T> & Serializable) (value, dataStore) -> customGetter
                .get(value, dataStore);

        return copy(id, defaultValueProvider, decoder, serializableGetter, panelProvider, label, definition,
                copyFunction);
    }

    @Override
    public Optional<CustomGetter<T>> getCustomGetter() {
        return Optional.ofNullable(customGetter);
    }

    @Override
    public DataKey<T> setKeyPanelProvider(KeyPanelProvider<T> panelProvider) {
        return copy(id, defaultValueProvider, decoder, customGetter, panelProvider, label, definition, copyFunction);
    }

    @Override
    public Optional<KeyPanelProvider<T>> getKeyPanelProvider() {
        return Optional.ofNullable(panelProvider);
    }

    @Override
    public DataKey<T> setLabel(String label) {
        return copy(id, defaultValueProvider, decoder, customGetter, panelProvider, label, definition, copyFunction);
    }

    /**
     * As default, returns the name of the key if a label is not set.
     *
     * @return
     */
    @Override
    public String getLabel() {
        if (label == null) {
            return getName();
        }

        return label;
    }

    @Override
    public DataKey<T> setStoreDefinition(StoreDefinition definition) {
        return copy(id, defaultValueProvider, decoder, customGetter, panelProvider, label, definition, copyFunction);
    }

    @Override
    public Optional<StoreDefinition> getStoreDefinition() {
        return Optional.ofNullable(definition);
    }

    @Override
    public DataKey<T> setCopyFunction(Function<T, T> copyFunction) {
        return copy(id, defaultValueProvider, decoder, customGetter, panelProvider, label, definition, copyFunction);
    }

    @Override
    public Optional<Function<T, T>> getCopyFunction() {
        return Optional.ofNullable(copyFunction);
    }

}
