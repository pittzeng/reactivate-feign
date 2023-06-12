package com.yigou.common.feign.bean;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Data
@Builder
public class MyParameterizedTypeImpl implements ParameterizedType {
    private final Type raw;
    private final Type[] args;
    private final Type owner;
    public MyParameterizedTypeImpl(Type raw, Type[] args, Type owner){
        this.raw = raw;
        this.args = args != null ? args : new Type[0];
        this.owner = owner;
    }
    @Override
    public Type[] getActualTypeArguments() {
        return args;
    }

    @Override
    public Type getRawType() {
        return raw;
    }

    @Override
    public Type getOwnerType() {
        return owner;
    }
}
