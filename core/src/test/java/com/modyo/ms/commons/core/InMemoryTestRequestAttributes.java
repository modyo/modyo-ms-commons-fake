package com.modyo.ms.commons.core;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.context.request.AbstractRequestAttributes;

public class InMemoryTestRequestAttributes extends AbstractRequestAttributes {
    protected Map<String, Object> attributes = new HashMap();

    public InMemoryTestRequestAttributes() {
    }

    public Object getAttribute(String name, int scope) {
      return this.attributes.get(name);
    }

    public void setAttribute(String name, Object value, int scope) {
      this.attributes.put(name, value);
    }

    public void removeAttribute(String name, int scope) {
      this.attributes.remove(name);
    }

    public String[] getAttributeNames(int scope) {
      String[] result = new String[this.attributes.keySet().size()];
      this.attributes.keySet().toArray(result);
      return result;
    }

    public void registerDestructionCallback(String name, Runnable callback, int scope) {
      synchronized(this.requestDestructionCallbacks) {
        this.requestDestructionCallbacks.put(name, callback);
      }
    }

    public Object resolveReference(String key) {
      return this.attributes;
    }

    public String getSessionId() {
      return null;
    }

    public Object getSessionMutex() {
      return null;
    }

    protected void updateAccessedSessionAttributes() {
    }
  }
