package com.axlan.gdxtactics;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class for making Lists parsed from JSON unmodifiable
 *
 * @see TypeAdapterFactory
 */
@SuppressWarnings("unchecked")
class ImmutableListTypeAdapterFactory implements TypeAdapterFactory {

  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
    Type type = typeToken.getType();
      if (typeToken.getRawType() != List.class || !(type instanceof ParameterizedType)) {
      return null;
    }

    Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
    TypeAdapter<?> elementAdapter = gson.getAdapter(TypeToken.get(elementType));
    return (TypeAdapter<T>) newImmutableListAdapter(elementAdapter);
  }

    private <E> TypeAdapter<List<E>> newImmutableListAdapter(final TypeAdapter<E> elementAdapter) {
    return new TypeAdapter<List<E>>() {
      @Override
      public void write(JsonWriter out, List<E> value) throws IOException {
        if (value == null) {
          out.nullValue();
          return;
        }
        out.beginArray();
        for (E entry : value) {
          elementAdapter.write(out, entry);
        }
        out.endArray();
      }

      @Override
      public List<E> read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        }

        ArrayList<E> result = new ArrayList<>();
        in.beginArray();
        while (in.hasNext()) {
          E element = elementAdapter.read(in);
          result.add(element);
        }
        in.endArray();
        return Collections.unmodifiableList(result);
      }
    };
  }
}
