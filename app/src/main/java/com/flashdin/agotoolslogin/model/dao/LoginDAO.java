package com.flashdin.agotoolslogin.model.dao;

public interface LoginDAO<T> {

    T loginTo(T params);
}
