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

package pt.up.fe.specs.util.enums;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import pt.up.fe.specs.util.SpecsEnums;
import pt.up.fe.specs.util.lazy.Lazy;
import pt.up.fe.specs.util.lazy.ThreadSafeLazy;
import pt.up.fe.specs.util.providers.StringProvider;

public class EnumHelper<T extends Enum<T> & StringProvider> {

    private final Class<T> enumClass;
    private final Map<String, T> translationMap;

    public EnumHelper(Class<T> enumClass) {
        this(enumClass, Collections.emptyList());
    }

    public EnumHelper(Class<T> enumClass, Collection<T> excludeList) {
        this.enumClass = enumClass;
        this.translationMap = SpecsEnums.buildMap(enumClass);

        excludeList.stream()
                .map(exclude -> exclude.getString())
                .forEach(key -> translationMap.remove(key));
    }

    public Map<String, T> getTranslationMap() {
        return translationMap;
    }

    public T valueOf(String name) {
        return valueOfTry(name).orElseThrow(() -> new IllegalArgumentException(
                "Enum '" + enumClass.getSimpleName() + "' does not contain an enum with the name '" + name
                        + "'. Available enums: " + translationMap));
    }

    public Optional<T> valueOfTry(String name) {
        T value = translationMap.get(name);

        return Optional.ofNullable(value);
    }

    public List<T> valueOf(List<String> names) {
        return names.stream()
                .map(name -> valueOf(name))
                .collect(Collectors.toList());
    }

    public String getAvailableOptions() {
        return translationMap.keySet().stream()
                .collect(Collectors.joining(", "));
    }

    public static <T extends Enum<T> & StringProvider> Lazy<EnumHelper<T>> newLazyHelper(Class<T> anEnum) {
        return newLazyHelper(anEnum, Collections.emptyList());
    }

    public static <T extends Enum<T> & StringProvider> Lazy<EnumHelper<T>> newLazyHelper(Class<T> anEnum,
            T exclude) {
        return newLazyHelper(anEnum, Arrays.asList(exclude));
    }

    public static <T extends Enum<T> & StringProvider> Lazy<EnumHelper<T>> newLazyHelper(Class<T> anEnum,
            Collection<T> excludeList) {
        return new ThreadSafeLazy<>(() -> new EnumHelper<>(anEnum, excludeList));
    }

}