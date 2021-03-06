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

package org.suikasoft.jOptions.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import pt.up.fe.specs.util.parsing.StringCodec;

public class EnumCodec<T extends Enum<T>> implements StringCodec<T> {

    private final Class<T> anEnum;
    private final Map<String, T> decodeMap;
    private final Function<T, String> encoder;

    public EnumCodec(Class<T> anEnum) {
        // this(anEnum, value -> value.name());
        this(anEnum, value -> value.toString());
    }

    public EnumCodec(Class<T> anEnum, Function<T, String> encoder) {
        this.anEnum = anEnum;
        this.decodeMap = new HashMap<>();
        this.encoder = encoder;

        for (T enumValue : anEnum.getEnumConstants()) {
            // decodeMap.put(enumValue.toString(), enumValue);
            decodeMap.put(encoder.apply(enumValue), enumValue);
        }
    }

    @Override
    public T decode(String value) {
        if (value == null) {
            return anEnum.getEnumConstants()[0];
        }

        T enumValue = decodeMap.get(value);

        if (enumValue == null) {
            throw new RuntimeException("Could not find enum '" + value + "' in class '" + anEnum
                    + "'. Available values: " + decodeMap.keySet());
        }

        return enumValue;
    }

    @Override
    public String encode(T value) {
        return encoder.apply(value);
        // return value.toString();
    }

}
